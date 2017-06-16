package com.nelladragon.sss;

import com.nelladragon.sss.crypto.AESEncDec;
import com.nelladragon.sss.password.PasswordHardening;
import com.nelladragon.sss.split.SecretShare;
import com.nelladragon.sss.split.ShareType;
import com.nelladragon.sss.split.SplitKey;
import com.nelladragon.sss.split.SplitSecretScheme;
import com.nelladragon.sss.crypto.SHA256Digest;
import com.nelladragon.sss.util.Util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * This class emulates the actions of an app on a device / what is stored on a device.
 */
public class Device {
    // Random share stored on the device.
    public SecretShare deviceShare;

    // Secret key which could be used in the operational code to wrap a private key.
    // Key which is the secret protected by the split key algorihtm.
    BigInteger key;

    // A new share. This share can be exported by passing -1 to the export wrapped share method.
    SecretShare newShare;


    SplitSecretScheme scheme;

    AESEncDec aes = new AESEncDec();


    // Parameters used as part of password hardening.
    byte[] passwordParamters;
    byte[] backupParamters;


    // Authenticated encrypted data stored on the device.
    byte[] authEncryptedDeviceData;
    byte[] authEncryptedDeviceDataNonce;


    public Device() throws Exception {
        this.scheme = new SplitSecretScheme();
    }


    public void register(byte[] password, byte[] backupPassword, byte[] deviceData) throws Exception {
        byte[][] hardenedPasswordAndSalt = PasswordHardening.mainPasswordHardenGenerate(password);
        SecretShare passwordShare = new SecretShare(ShareType.PASSWORD, hardenedPasswordAndSalt[0]);
        Util.print("PasswordShare: " + passwordShare);
        this.passwordParamters = hardenedPasswordAndSalt[1];

//        byte[][] hardenedBackupPasswordAndSalt = PasswordHardening.backupPasswordHardenGenerate(backupPassword);
//        SecretShare backupShare = new SecretShare(ShareType.BACKUP, hardenedBackupPasswordAndSalt[0]);
//        Util.print("BackupShare: " + backupShare);
//        this.backupParamters = hardenedBackupPasswordAndSalt[1];


        SplitKey splitKey = this.scheme.generate(passwordShare);
//        SplitKey splitKey = scheme.generate(passwordShare, backupShare);
        this.deviceShare = splitKey.getDeviceShare();
        Util.print("DeviceShare: " + this.deviceShare);

        // Share which should be stored in parts of the system other than the device.
        // These shares can be exported by passing in an array offset.
        SecretShare[] cloudShares = splitKey.getExternalShares();
        for (SecretShare share: cloudShares) {
            Util.print("CloudShare: " + share);
        }

        Cloud cloud = Cloud.getSingleInstance();
        cloud.setData(Cloud.CLOUD_SHARE1, cloudShares[0].toBytes());
        cloud.setData(Cloud.CLOUD_SHARE2, cloudShares[1].toBytes());
        cloud.setData(Cloud.PASSWORD_HARDENING_PARAMETERS, this.passwordParamters);
        cloud.setData(Cloud.BACKUPPASSWORD_HARDENING_PARAMETERS, this.backupParamters);


        BigInteger rawSecret = splitKey.getSecret();



        SecretKey derivedKey = aes.generateKey(rawSecret);

        byte[][] encNonce = aes.authEncrypt(derivedKey, deviceData);
        this.authEncryptedDeviceData = encNonce[0];
        this.authEncryptedDeviceDataNonce = encNonce[1];
        cloud.setData(Cloud.DEVICE_DATA, this.authEncryptedDeviceData);
        cloud.setData(Cloud.DEVICE_DATA_PARAMETERS, this.authEncryptedDeviceDataNonce);
    }







    public byte[] getDataPasswordCloudDevice(byte[] password) throws Exception {
        byte[] hardenedPassword = PasswordHardening.mainPasswordHardenRegenerate(password, this.passwordParamters);
        SecretShare passwordShare = new SecretShare(ShareType.PASSWORD, hardenedPassword);
        Util.print("PasswordShare: " + passwordShare);


        Cloud cloud = Cloud.getSingleInstance();
        byte[] cloudShare1Bytes = cloud.getData(Cloud.CLOUD_SHARE1);
        SecretShare cloudShare1 = SecretShare.fromBytes(cloudShare1Bytes);
        Util.print("CloudShare1: " + cloudShare1);
        byte[] cloudShare2Bytes = cloud.getData(Cloud.CLOUD_SHARE2);
        SecretShare cloudShare2 = SecretShare.fromBytes(cloudShare2Bytes);
        Util.print("CloudShare2: " + cloudShare2);
        SecretShare[] shares = new SecretShare[] {passwordShare, cloudShare1, cloudShare2, this.deviceShare};
        Util.print("DeviceShare: " + this.deviceShare);

        BigInteger rawSecret = this.scheme.recoverSecret(shares);
        SecretKey derivedKey = aes.generateKey(rawSecret);

        return aes.authDecrypt(derivedKey, this.authEncryptedDeviceData, this.authEncryptedDeviceDataNonce);
    }

//
//
//
//    /**
//     * Export a share.
//     *
//     * In operational code, the share would be wrapped by a public key.
//     *
//     * @return The share to be exported.
//     */
//    public SecretShare exportWrappedShare(int externalShareNumber) throws Exception {
//        if (externalShareNumber == -1) {
//            return this.newShare;
//        }
//
//        if (externalShareNumber >= this.extShares.length) {
//            throw new Exception("Invalid share number");
//        }
//        return this.extShares[externalShareNumber];
//    }
//
//
//    /**
//     * Zeroize all shares except for the device share and the reconstructed key.
//     * The device share would be persisted to local encrypted persistent storage.
//     */
//    public void clearSplitSecret() {
//        this.passwordShare = null;
//        this.extShares = null;
//        this.newShare = null;
//        this.key = null;
//    }


    /**
     * Import a share.
     *
     * In operational code, the share would be wrapped.
     *
     * @param wrappedShares The share to import.
     * @throws Exception
     */
//    public void importWrappedShares(SecretShare[] wrappedShares) throws Exception {
//        this.extShares = wrappedShares;
//    }
//
//
//    /**
//     * Reconstruct a split key based on the shares available.
//     *
//     * @throws Exception
//     */
//    public void reconstructSplitKey() throws Exception {
//        SecretShare[] shares = assembleShares();
//        this.key = this.scheme.recoverSecret(shares);
//    }
//
//
//    /**
//     * Create a new share.
//     *
//     * @throws Exception
//     */
//
//    public void addShare() throws Exception {
//        SecretShare[] shares = assembleShares();
//        this.newShare = this.scheme.addShare(shares);
//    }
//
//
//    /**
//     * Create a new device share.
//     *
//     * @throws Exception
//     */
//    public void recreateDeviceShare() throws Exception {
//        SecretShare[] shares = assembleShares();
//        SecretShare newDeviceShare = this.scheme.addShare(shares);
//        this.deviceShare = new SecretShare(ShareType.DEVICE, newDeviceShare.getShareY(), newDeviceShare.getShareX());
//    }
//
//
//
//    private SecretShare[] assembleShares() throws Exception {
//        ArrayList<SecretShare> sharesO = new ArrayList<SecretShare>();
//        if (this.passwordShare != null) {
//            sharesO.add(this.passwordShare);
//        }
//        if (this.deviceShare != null) {
//            sharesO.add(this.deviceShare);
//        }
//        if (this.extShares != null) {
//            for (SecretShare aShare: this.extShares) {
//                sharesO.add(aShare);
//            }
//        }
//        return sharesO.toArray(new SecretShare[0]);
//    }
//
//
//    /**
//     * Proactivize the device share and external shares.
//     *
//     * Note that the shares could just be the x values for the shares, with the
//     * y values set to zero. In this way, the shares can be exported, and the existing
//     * value and the proactivized value can be combined remotely.
//     *
//     * @throws Exception
//     */
//    public void proactivize() throws Exception {
//        if (this.deviceShare == null) {
//            throw new Exception("Trying to proactivize whilst device share is null. It makes sense to create the device share before proactivizing");
//        }
//        if (this.extShares == null) {
//            throw new Exception("Trying to proactivize whilst no ext shares. With just the device share, there won't be enough shares to proactivize");
//        }
//
//        ArrayList<SecretShare> sharesO = new ArrayList<SecretShare>();
//        sharesO.add(this.deviceShare);
//        for (SecretShare aShare: this.extShares) {
//            sharesO.add(aShare);
//        }
//        SecretShare[] shares = sharesO.toArray(new SecretShare[0]);
//
//        SecretShare[] proactShares = this.scheme.proactivizeShares(shares);
//        this.deviceShare = proactShares[0];
//
//        for (int i = 1; i < proactShares.length; i++) {
//            this.extShares[i-1] = proactShares[i];
//        }
//    }
//
//
//    /**
//     * Update the password. Keep the key the same. Update all other shares.
//     *
//     * @param newPassword The new password.
//     * @throws Exception
//     */
//
//    public void changePassword(byte[] newPassword) throws Exception {
//        // Process password using scrypt. For the purposes of this code, let's just SHA256.
//        SecretShare newPasswordShare = new SecretShare(digest(newPassword));
//
//        if (this.deviceShare == null) {
//            throw new Exception("Trying to change password whilst device share is null. It makes sense to create the device share before change password");
//        }
//        if (this.extShares == null) {
//            throw new Exception("Trying to change password whilst no ext shares. With just the device share, there won't be enough shares to change password");
//        }
//
//        ArrayList<SecretShare> sharesO = new ArrayList<SecretShare>();
//        sharesO.add(this.deviceShare);
//        for (SecretShare aShare: this.extShares) {
//            sharesO.add(aShare);
//        }
//        SecretShare[] shares = sharesO.toArray(new SecretShare[0]);
//
//        SecretShare[] proactShares = this.scheme.changePasswordAndProactivizeShares(shares, this.passwordShare, newPasswordShare);
//        this.deviceShare = proactShares[0];
//
//        for (int i = 1; i < proactShares.length; i++) {
//            this.extShares[i-1] = proactShares[i];
//        }
//
//        this.passwordShare = newPasswordShare;
//    }
//
//
//
//
//
//    /**
//     * Derive a share from a password. Store the share in memory.
//     *
//     * @param password A byte array based password.
//     * @throws Exception
//     */
//    public void createPasswordShare(byte[] password) throws Exception {
//        // Process password using scrypt. For the purposes of this code, let's just SHA256.
//        this.passwordShare = new SecretShare(digest(password));
//    }
//
//
//
//
//    /**
//     * For the purposes of the PoC only, export the reconstructed key.
//     *
//     * @return The recovered key.
//     */
//    public BigInteger testOnlyExportKey() {
//        return this.key;
//    }
//
//
//    /**
//     * For the purposes of the PoC only, wipe the device share, to simulate a lost phone.
//     */
//    public void testOnlyWipeDeviceShare() {
//        this.deviceShare = null;
//    }
//
//
//
//

}
