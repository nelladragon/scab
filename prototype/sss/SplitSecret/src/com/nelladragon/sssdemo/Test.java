/**
 * Copyright 2014 EMC Corporation. All rights reserved.
 *
 * EMC, RSA, the RSA Logo, and BSAFE are either registered trademarks
 * or trademarks of EMC Corporation in the United States and/or other
 * countries. All other products and services mentioned are trademarks
 * of their respective companies.
 *
 * This work contains information proprietary to EMC Corporation. Distribution
 * is limited to licensees authorized by EMC Corporation. Any unauthorized
 * reproduction, distribution, or modification of this work is strictly
 * prohibited.
 *
 */
package com.rsa.peter.testsplitsecret;


import com.rsa.jsafe.crypto.CryptoJ;
import com.rsa.jsafe.crypto.CryptoJVersion;
import com.rsa.jsafe.crypto.PolicyChecker;
import com.rsa.peter.testsplitsecret.publicapi.CommonCryptoExtract;
import com.rsa.peter.testsplitsecret.split.SecretShare;
import com.rsa.peter.testsplitsecret.util.*;

import java.math.BigInteger;
import java.util.Date;

/**
 * Runner class to execute split key proof of concept code.
 */
public class Test {

    private static void printEnvironmentInfo() {
        Util.println("JDK/JRE");
        Util.println("-------");
        Util.println(" Version: " + System.getProperty("java.version"));
        Util.println(" Java Home: " + System.getProperty("java.home"));
        Util.println(" Vendor: " + System.getProperty("java.vendor"));
        Util.println(" OS: " + System.getProperty("os.name"));

        Util.println();
        Util.println("Unlimited strength jurisdiction policy files:");
        Util.println("--------------------------------------------");

        if (PolicyChecker.getJurisdictionPolicyLevel() == PolicyChecker.JurisdictionPolicyLevel.UNLIMITED) {
            Util.println(" Installed");
        } else {
            Util.println("Not installed. Please download it from" +
                    " the following URL: \n" +
                    "JDK 5.0   - " +
                    "http://java.sun.com/javase/downloads/index_jdk5.jsp\n" +
                    "JDK 6.0   - " +
                    "http://java.sun.com/javase/downloads/index.jsp\n\n" +
                    "To install the Unlimited Jurisdiction Policy Files:\n" +
                    "  - Extract the local_policy.jar and US_export_policy.jar files" +
                    " from the downloaded zip file.\n" +
                    "  - Copy local_policy.jar and US_export_policy.jar to the " +
                    "<jdk install directory>/jre/lib/security directory, " +
                    "over-writing the files of the same name.");
            System.exit(-1);
        }

        Util.println();
        Util.println("Crypto-J Information");
        Util.println("--------------------");
        // Print product information (name, version and build date).
        Util.println(" " + CryptoJVersion.getProductID());
        // Print FIPS140 info.
        if (CryptoJ.isFIPS140Compliant()) {
            Util.println(" FIPS 140 toolkit variant");
        } else {
            Util.println(" Non-FIPS 140 toolkit variant");
        }
        // Print evaluation info.
        if (!CryptoJVersion.isEval()) {
            Util.println(" Production (non-evaluation) toolkit");
        } else {
            Util.println(" Evaluation License Information");
            Util.println(CryptoJVersion.getLicenseInfo());
        }
        Util.println(" Crypto-J able to access Crypto-C ME crypto implementation: " + CryptoJ.isNativeAvailable());
        Util.println();
    }


    /**
     * Display the state of each of the keys and shares.
     */
    public static void dumpState() {
        Util.print("  Original Key: ");
        Util.println(originalKey.toString(16));

        Util.print("  RecoveredKey: ");
        if (recoveredKey == null) {
            Util.println("null");
        } else {
            Util.println(recoveredKey.toString(16));
        }

        Util.print("  Pass Share: ");
        if (crypto.passwordShare == null) {
            Util.println("null");
        } else {
            SecretShare pass = crypto.passwordShare;
            Util.print(pass.getType().toString());
            Util.print(", ");
            Util.print(pass.getShareX().toString(16));
            Util.print(", ");
            Util.println(pass.getShareY().toString(16));
        }

        Util.print("  Device Share: ");
        if (crypto.deviceShare == null) {
            Util.println("null");
        } else {
            SecretShare dev = crypto.deviceShare;
            Util.print(dev.getType().toString());
            Util.print(", ");
            Util.print(dev.getShareX().toString(16));
            Util.print(", ");
            Util.println(dev.getShareY().toString(16));
        }

        Util.print("  Online Share: ");
        if (onlineShare == null) {
            Util.println("null");
        } else {
            Util.print(onlineShare.getType().toString());
            Util.print(", ");
            Util.print(onlineShare.getShareX().toString(16));
            Util.print(", ");
            Util.println(onlineShare.getShareY().toString(16));
        }

        Util.print("  Backup Share: ");
        if (backupShare == null) {
            Util.println("null");
        } else {
            Util.print(backupShare.getType().toString());
            Util.print(", ");
            Util.print(backupShare.getShareX().toString(16));
            Util.print(", ");
            Util.println(backupShare.getShareY().toString(16));
        }

    }





    public static BigInteger originalKey;
    public static BigInteger recoveredKey;
    public static SecretShare onlineShare;
    public static SecretShare backupShare;
    public static CommonCryptoExtract crypto;


    /**
     * Walk through all of the scenarios.
     *
     * Please email me (peter.robinson@rsa.com) if you see a scenario which isn't
     * covered by the code below.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // Make stdout and stderr one stream. Have them both non-buffered.
        // What this means is that if an error or exception stack trace is thrown,
        // it will be shown in the context of the other output.
        System.setOut(System.err);

        Util.println("Split Secret Analysis: Start");
        Util.println(" Date: " + (new Date().toString()));
        printEnvironmentInfo();

        Util.println();


        byte[] password = new byte[12];

        Util.println(" Create the split secret");
        crypto = new CommonCryptoExtract();
        crypto.createPasswordShare(password);
        crypto.generateSplitKey();
        originalKey = crypto.testOnlyExportKey();

        onlineShare = crypto.exportWrappedShare(0);
        dumpState();
        crypto.clearSplitSecret();

        Util.println(" Re-create the key using the online share and the device share");
        crypto.importWrappedShares(new SecretShare[]{onlineShare});
        crypto.reconstructSplitKey();
        recoveredKey = crypto.testOnlyExportKey();
        crypto.clearSplitSecret();
        if (!recoveredKey.equals(originalKey)) {
            throw new Exception("Recovered key doesn't match original. 1");
        }


        Util.println(" Re-create the key using the password share and the device share");
        crypto.createPasswordShare(password);
        crypto.reconstructSplitKey();
        recoveredKey = crypto.testOnlyExportKey();
        crypto.clearSplitSecret();
        if (!recoveredKey.equals(originalKey)) {
            throw new Exception("Recovered key doesn't match original. 2");
        }

        Util.println(" Wipe device share - the phone has been lost!");
        crypto.testOnlyWipeDeviceShare();
        Util.println(" Re-create the key using the password share and the online share");
        crypto.importWrappedShares(new SecretShare[]{onlineShare});
        crypto.createPasswordShare(password);
        crypto.reconstructSplitKey();
        recoveredKey = crypto.testOnlyExportKey();
        crypto.clearSplitSecret();
        if (!recoveredKey.equals(originalKey)) {
            throw new Exception("Recovered key doesn't match original. 3");
        }

        Util.println(" Recreate a new device share");
        crypto.importWrappedShares(new SecretShare[]{onlineShare});
        crypto.createPasswordShare(password);
        crypto.recreateDeviceShare();
        crypto.clearSplitSecret();

        Util.println(" Re-create the key using the online share and the recovered device share");
        crypto.importWrappedShares(new SecretShare[]{onlineShare});
        crypto.reconstructSplitKey();
        recoveredKey = crypto.testOnlyExportKey();
        crypto.clearSplitSecret();
        if (!recoveredKey.equals(originalKey)) {
            throw new Exception("Recovered key doesn't match original. 4");
        }

        Util.println(" Create a new share");
        crypto.importWrappedShares(new SecretShare[]{onlineShare});
        crypto.addShare();
        backupShare = crypto.exportWrappedShare(-1);
        crypto.clearSplitSecret();

        Util.println(" Use the online share and the other external share to recreate the key");
        crypto.importWrappedShares(new SecretShare[]{onlineShare, backupShare});
        crypto.reconstructSplitKey();
        recoveredKey = crypto.testOnlyExportKey();
        crypto.clearSplitSecret();
        if (!recoveredKey.equals(originalKey)) {
            throw new Exception("Recovered key doesn't match original. 5");
        }
        dumpState();


        Util.println(" Proactivize the shares***");
        crypto.importWrappedShares(new SecretShare[]{onlineShare, backupShare});
        crypto.proactivize();
        onlineShare = crypto.exportWrappedShare(0);
        backupShare = crypto.exportWrappedShare(1);
        dumpState();
        crypto.clearSplitSecret();


        Util.println(" Re-create the key using the online share and the device share");
        crypto.importWrappedShares(new SecretShare[]{onlineShare});
        crypto.reconstructSplitKey();
        recoveredKey = crypto.testOnlyExportKey();
        crypto.clearSplitSecret();
        if (!recoveredKey.equals(originalKey)) {
            throw new Exception("Recovered key doesn't match original. 6");
        }

        Util.println(" Re-create the key using the password share and the device share");
        crypto.createPasswordShare(password);
        crypto.reconstructSplitKey();
        recoveredKey = crypto.testOnlyExportKey();
        crypto.clearSplitSecret();
        if (!recoveredKey.equals(originalKey)) {
            throw new Exception("Recovered key doesn't match original. 7");
        }

        Util.println(" Wipe device share - the phone has been lost!");
        crypto.testOnlyWipeDeviceShare();
        Util.println(" Re-create the key using the password share and the online share");
        crypto.importWrappedShares(new SecretShare[]{onlineShare});
        crypto.createPasswordShare(password);
        crypto.reconstructSplitKey();
        recoveredKey = crypto.testOnlyExportKey();
        crypto.clearSplitSecret();
        if (!recoveredKey.equals(originalKey)) {
            throw new Exception("Recovered key doesn't match original. 8");
        }

        Util.println(" Recreate a new device share");
        crypto.importWrappedShares(new SecretShare[]{onlineShare});
        crypto.createPasswordShare(password);
        crypto.recreateDeviceShare();
        dumpState();
        crypto.clearSplitSecret();

        Util.println(" Re-create the key using the online share and the recovered device share");
        crypto.importWrappedShares(new SecretShare[]{onlineShare});
        crypto.reconstructSplitKey();
        recoveredKey = crypto.testOnlyExportKey();
        crypto.clearSplitSecret();
        if (!recoveredKey.equals(originalKey)) {
            throw new Exception("Recovered key doesn't match original. 9");
        }

        Util.println(" Wipe device share - the phone has been lost!");
        crypto.testOnlyWipeDeviceShare();
        Util.println(" Re-create the key using the online share and the backup share");
        crypto.importWrappedShares(new SecretShare[]{onlineShare, backupShare});
        crypto.reconstructSplitKey();
        recoveredKey = crypto.testOnlyExportKey();
        crypto.clearSplitSecret();
        if (!recoveredKey.equals(originalKey)) {
            throw new Exception("Recovered key doesn't match original. 10");
        }
        dumpState();

        Util.println(" Recreate a new device share");
        crypto.importWrappedShares(new SecretShare[]{onlineShare});
        crypto.createPasswordShare(password);
        crypto..
        dumpState();
        crypto.clearSplitSecret();


        Util.println(" Change password***");
        crypto.createPasswordShare(password);
        crypto.importWrappedShares(new SecretShare[]{onlineShare, backupShare});
        password = new byte[] {0x01, 0x02};
        crypto.changePassword(password);
        onlineShare = crypto.exportWrappedShare(0);
        backupShare = crypto.exportWrappedShare(1);
        dumpState();
        crypto.clearSplitSecret();


        Util.println(" Re-create the key using the online share and the device share");
        crypto.importWrappedShares(new SecretShare[]{onlineShare});
        crypto.reconstructSplitKey();
        recoveredKey = crypto.testOnlyExportKey();
        crypto.clearSplitSecret();
        if (!recoveredKey.equals(originalKey)) {
            throw new Exception("Recovered key doesn't match original. 6");
        }

        Util.println(" Re-create the key using the password share and the device share");
        crypto.createPasswordShare(password);
        crypto.reconstructSplitKey();
        recoveredKey = crypto.testOnlyExportKey();
        crypto.clearSplitSecret();
        if (!recoveredKey.equals(originalKey)) {
            throw new Exception("Recovered key doesn't match original. 7");
        }

        Util.println(" Wipe device share - the phone has been lost!");
        crypto.testOnlyWipeDeviceShare();
        Util.println(" Re-create the key using the password share and the online share");
        crypto.importWrappedShares(new SecretShare[]{onlineShare});
        crypto.createPasswordShare(password);
        crypto.reconstructSplitKey();
        recoveredKey = crypto.testOnlyExportKey();
        dumpState();
        crypto.clearSplitSecret();
        if (!recoveredKey.equals(originalKey)) {
            throw new Exception("Recovered key doesn't match original. 8");
        }


        Util.println();
        Util.println();
        Util.println();
        Util.println(" Date: " + (new Date().toString()));
        Util.println("Split Secret Analysis: End");
    }

}
