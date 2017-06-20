package com.nelladragon.sss.split;

/**
 * Types of secret shares.
 */
public enum ShareType {
    PASSWORD(FixedShareThresholdScheme.X_PASSWORD),   // Derived from a password. Never exported or imported.
    DEVICE(FixedShareThresholdScheme.X_DEVICE),     // To be persisted on device. Never exported or imported.
    BACKUP(FixedShareThresholdScheme.X_BACKUP),     // To be persisted offline. Derived from a password.
                // Exported for storage, imported to allow recovery.
    TRANSFER(FixedShareThresholdScheme.X_TRANSFER),   // Exported and displayed on device1, scanned and imported on device2.
                   // Never persisted.
    CLOUD1(FixedShareThresholdScheme.X_CLOUD1),      // Can be exported and imported. Never persisted on device.
    CLOUD2(FixedShareThresholdScheme.X_CLOUD2),      // Can be exported and imported. Never persisted on device.
    CLOUD3(FixedShareThresholdScheme.X_CLOUD3),      // Can be exported and imported. Never persisted on device.
    CLOUD4(FixedShareThresholdScheme.X_CLOUD4);      // Can be exported and imported. Never persisted on device.


    public final int xValue; // X value of share.


    private ShareType(int val) {
        this.xValue = val;
    }

    public static ShareType fromInt(int xVal) {
        switch (xVal) {
            case FixedShareThresholdScheme.X_PASSWORD:
                return PASSWORD;
            case FixedShareThresholdScheme.X_DEVICE:
                return DEVICE;
            case FixedShareThresholdScheme.X_BACKUP:
                return BACKUP;
            case FixedShareThresholdScheme.X_TRANSFER:
                return TRANSFER;
            case FixedShareThresholdScheme.X_CLOUD1:
                return CLOUD1;
            case FixedShareThresholdScheme.X_CLOUD2:
                return CLOUD2;
            case FixedShareThresholdScheme.X_CLOUD3:
                return CLOUD3;
            case FixedShareThresholdScheme.X_CLOUD4:
                return CLOUD4;
            default:
                throw new RuntimeException("Unknown Share Type: " + xVal);
        }
    }


}
