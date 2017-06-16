package com.nelladragon.sss.split;

/**
 * Types of secret shares.
 */
public enum ShareType {
    PASSWORD(0),   // Derived from a password.
                // Never exported or imported.

    DEVICE(1),     // To be persisted on device.
                // Never exported or imported.

    BACKUP(2),     // To be persisted offline. Derived from a password.
                // Exported for storage, imported to allow recovery.

    TRANSFER(3),   // Exported and displayed on device1, scanned and imported on device2.
                // Never persisted.

    CLOUD(4);      // Can be exported and imported.
                // Never persisted on device.


    int value;

    private ShareType(int val) {
        this.value = val;
    }

    public static ShareType fromInt(int val) {
        switch (val) {
            case 0:
                return PASSWORD;
            case 1:
                return DEVICE;
            case 2:
                return BACKUP;
            case 3:
                return TRANSFER;
            case 4:
                return CLOUD;
            default:
                throw new RuntimeException("Unknown Share Type");
        }
    }


}
