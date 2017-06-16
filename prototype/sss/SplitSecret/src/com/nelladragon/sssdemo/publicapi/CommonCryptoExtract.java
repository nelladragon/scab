package com.rsa.peter.testsplitsecret.publicapi;

import com.rsa.peter.testsplitsecret.split.SecretShare;
import com.rsa.peter.testsplitsecret.split.SplitKey;
import com.rsa.peter.testsplitsecret.split.SplitSecretScheme;
import com.rsa.peter.testsplitsecret.util.SHA256Digest;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * The code in this class is demonstrating approximately what the Common
 * Crypto API and associated classes would do with respect to split keys.
 *
 */
public class CommonCryptoExtract {
    // Share derived from the password.
    public SecretShare passwordShare;

    // Random share stored on the device.
    public SecretShare deviceShare;

    // Share which should be stored in parts of the system other than the device.
    // These shares can be exported by passing in an array offset.
    SecretShare[] extShares;

    // Secret key which could be used in the operational code to wrap a private key.
    // Key which is the secret protected by the split key algorihtm.
    BigInteger key;

    // A new share. This share can be exported by passing -1 to the export wrapped share method.
    SecretShare newShare;


    SplitSecretScheme scheme;


    public CommonCryptoExtract() throws Exception {
        this.scheme = new SplitSecretScheme();
    }


    /**
     * Derive a share from a password. Store the share in memory.
     *
     * @param password A byte array based password.
     * @throws Exception
     */
    public void createPasswordShare(byte[] password) throws Exception {
        // Process password using scrypt. For the purposes of this code, let's just SHA256.
        this.passwordShare = new SecretShare(SHA256Digest.digest(password));
    }



    /**
     * Generate a split key and random shares based on a password share.
     * @throws Exception
     */

    public void generateSplitKey() throws Exception {
        SplitKey splitKey = scheme.generate(this.passwordShare);
        this.deviceShare = splitKey.getDeviceShare();
        this.extShares = splitKey.getExternalShares();
        this.key = splitKey.getKey();
    }


    /**
     * Export a share.
     *
     * In operational code, the share would be wrapped by a public key.
     *
     * @return The share to be exported.
     */
    public SecretShare exportWrappedShare(int externalShareNumber) throws Exception {
        if (externalShareNumber == -1) {
            return this.newShare;
        }

        if (externalShareNumber >= this.extShares.length) {
            throw new Exception("Invalid share number");
        }
        return this.extShares[externalShareNumber];
    }


    /**
     * Zeroize all shares except for the device share and the reconstructed key.
     * The device share would be persisted to local encrypted persistent storage.
     */
    public void clearSplitSecret() {
        this.passwordShare = null;
        this.extShares = null;
        this.newShare = null;
        this.key = null;
    }


    /**
     * Import a share.
     *
     * In operational code, the share would be wrapped.
     *
     * @param wrappedShares The share to import.
     * @throws Exception
     */
    public void importWrappedShares(SecretShare[] wrappedShares) throws Exception {
        this.extShares = wrappedShares;
    }


    /**
     * Reconstruct a split key based on the shares available.
     *
     * @throws Exception
     */
    public void reconstructSplitKey() throws Exception {
        SecretShare[] shares = assembleShares();
        this.key = this.scheme.recoverSecret(shares);
    }


    /**
     * Create a new share.
     *
     * @throws Exception
     */

    public void addShare() throws Exception {
        SecretShare[] shares = assembleShares();
        this.newShare = this.scheme.addShare(shares);
    }


    /**
     * Create a new device share.
     *
     * @throws Exception
     */
    public void recreateDeviceShare() throws Exception {
        SecretShare[] shares = assembleShares();
        SecretShare newDeviceShare = this.scheme.addShare(shares);
        this.deviceShare = new SecretShare(ShareType.DEVICE, newDeviceShare.getShareY(), newDeviceShare.getShareX());
    }



    private SecretShare[] assembleShares() throws Exception {
        ArrayList<SecretShare> sharesO = new ArrayList<SecretShare>();
        if (this.passwordShare != null) {
            sharesO.add(this.passwordShare);
        }
        if (this.deviceShare != null) {
            sharesO.add(this.deviceShare);
        }
        if (this.extShares != null) {
            for (SecretShare aShare: this.extShares) {
                sharesO.add(aShare);
            }
        }
        return sharesO.toArray(new SecretShare[0]);
    }


    /**
     * Proactivize the device share and external shares.
     *
     * Note that the shares could just be the x values for the shares, with the
     * y values set to zero. In this way, the shares can be exported, and the existing
     * value and the proactivized value can be combined remotely.
     *
     * @throws Exception
     */
    public void proactivize() throws Exception {
        if (this.deviceShare == null) {
            throw new Exception("Trying to proactivize whilst device share is null. It makes sense to create the device share before proactivizing");
        }
        if (this.extShares == null) {
            throw new Exception("Trying to proactivize whilst no ext shares. With just the device share, there won't be enough shares to proactivize");
        }

        ArrayList<SecretShare> sharesO = new ArrayList<SecretShare>();
        sharesO.add(this.deviceShare);
        for (SecretShare aShare: this.extShares) {
            sharesO.add(aShare);
        }
        SecretShare[] shares = sharesO.toArray(new SecretShare[0]);

        SecretShare[] proactShares = this.scheme.proactivizeShares(shares);
        this.deviceShare = proactShares[0];

        for (int i = 1; i < proactShares.length; i++) {
            this.extShares[i-1] = proactShares[i];
        }
    }


    /**
     * Update the password. Keep the key the same. Update all other shares.
     *
     * @param newPassword The new password.
     * @throws Exception
     */

    public void changePassword(byte[] newPassword) throws Exception {
        // Process password using scrypt. For the purposes of this code, let's just SHA256.
        SecretShare newPasswordShare = new SecretShare(SHA256Digest.digest(newPassword));

        if (this.deviceShare == null) {
            throw new Exception("Trying to change password whilst device share is null. It makes sense to create the device share before change password");
        }
        if (this.extShares == null) {
            throw new Exception("Trying to change password whilst no ext shares. With just the device share, there won't be enough shares to change password");
        }

        ArrayList<SecretShare> sharesO = new ArrayList<SecretShare>();
        sharesO.add(this.deviceShare);
        for (SecretShare aShare: this.extShares) {
            sharesO.add(aShare);
        }
        SecretShare[] shares = sharesO.toArray(new SecretShare[0]);

        SecretShare[] proactShares = this.scheme.changePasswordAndProactivizeShares(shares, this.passwordShare, newPasswordShare);
        this.deviceShare = proactShares[0];

        for (int i = 1; i < proactShares.length; i++) {
            this.extShares[i-1] = proactShares[i];
        }

        this.passwordShare = newPasswordShare;
    }



    /**
     * For the purposes of the PoC only, export the reconstructed key.
     *
     * @return The recovered key.
     */
    public BigInteger testOnlyExportKey() {
        return this.key;
    }


    /**
     * For the purposes of the PoC only, wipe the device share, to simulate a lost phone.
     */
    public void testOnlyWipeDeviceShare() {
        this.deviceShare = null;
    }

}
