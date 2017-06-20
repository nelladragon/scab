// Copyright (C) 2017 Peter Robinson
package com.nelladragon.sss;


import com.nelladragon.sss.split.SecretShare;
import com.nelladragon.sss.util.*;

import java.math.BigInteger;
import java.util.Date;

import static com.nelladragon.sss.util.Util.compare;

/**
 * Runner class to execute proof of concept code.
 */
public class Test {
    public static Device device1;
    public static Device device2;
    public static Cloud cloud;
    public static BackupDevice backup;

    /**
     * Walk through all of the scenarios.
     *
     */
    public static void main(String[] args) throws Exception {
        // Make stdout and stderr one stream. Have them both non-buffered.
        // What this means is that if an error or exception stack trace is thrown,
        // it will be shown in the context of the other output.
        System.setOut(System.err);

        Util.println("Split Secret Scheme Prototype: Start");
        Util.println(" Date: " + (new Date().toString()));

        Util.println();


        // Have four separate passwords.
        byte[] passwordOriginal = new byte[30];
        passwordOriginal[0] = 1;

        byte[] passwordNew = new byte[30];
        passwordNew[0] = 2;

        byte[] backupPasswordOriginal = new byte[30];
        backupPasswordOriginal[0] = 3;

        byte[] backupPasswordNew = new byte[30];
        backupPasswordNew[0] = 4;

        byte[] deviceData = new byte[] {1,2,3,4};

        Util.println("*****App First Time Registration: Device 1");
        device1 = new Device();
        device1.register(passwordOriginal, backupPasswordOriginal, deviceData);

        Util.println("*****Re-create the key using the password share, cloud share and the device share");
        byte[] recoveredDeviceData = device1.getDataPasswordCloudDevice(passwordOriginal);
        if (!Util.compare(deviceData, recoveredDeviceData)) {
            System.exit(-1);
        }


        Util.println("*******Proactivize cloud");
        device1.proactivizeTheCloud();
        Util.println("******Re-create the key using the password share, cloud share and the device share");
        recoveredDeviceData = device1.getDataPasswordCloudDevice(passwordOriginal);
        if (!Util.compare(deviceData, recoveredDeviceData)) {
            System.exit(-1);
        }

        Util.println("*******Change password");
        device1.changePassword(passwordOriginal, passwordNew);
        Util.println("******Re-create the key using the password share, cloud share and the device share");
        recoveredDeviceData = device1.getDataPasswordCloudDevice(passwordNew);
        if (!Util.compare(deviceData, recoveredDeviceData)) {
            System.exit(-1);
        }



        Util.println("*******Get transfer share");
        byte[] transferShare1 = device1.getTransferShare(passwordNew);

        Util.println("*******Set-up new device using transfer share");
        device2 = new Device();
        device2.registerSecondaryDevice(passwordNew, transferShare1);

        Util.println("*******Logging into device 2 with password, cloud and device shares only");
        recoveredDeviceData = device2.getDataPasswordCloudDevice(passwordNew);
        if (!Util.compare(deviceData, recoveredDeviceData)) {
            System.exit(-1);
        }





/*        Util.println(" Re-create the key using the backup share, cloud share and the device share");
        recoveredDeviceData = device1.getDataBackupCloudDevice(backupOriginal);
        if (!Util.compare(deviceData, recoveredDeviceData)) {
            System.exit(-1);
        }

        /*



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
//        crypto..
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
*/

        Util.println();
        Util.println();
        Util.println();
        Util.println(" Date: " + (new Date().toString()));
        Util.println("Split Secret Scheme Prototype: End");
    }

}
