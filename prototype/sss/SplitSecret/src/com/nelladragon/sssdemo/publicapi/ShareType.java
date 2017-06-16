package com.rsa.peter.testsplitsecret.publicapi;

/**
 * Types of secret shares.
 */
public enum ShareType {
    PASSWORD,   // Derived from a password.
                // Never exported or imported.
                // Never persisted.

    DEVICE,     // To be persisted on device.
                // Never exported or imported.


    EXTERNAL,   // ONLINE share or BACKUP share or some other share.
                // Can be exported and imported.
                // Never persisted.
}
