package com.rsa.peter.testsplitsecret.split;

import java.math.BigInteger;

/**
 * Represents a generated key and its shares.
 */
public class SplitKey {
    BigInteger key;
    SecretShare passwordShare;
    SecretShare deviceShare;
    SecretShare[] externShares;

    public SplitKey(BigInteger key, SecretShare passwordShare, SecretShare deviceShare, SecretShare[] externalShares) {
        this.key = key;
        this.passwordShare = passwordShare;
        this.deviceShare = deviceShare;
        this.externShares = externalShares;
    }


    public SecretShare[] getExternalShares() {
        return this.externShares;
    }

    public SecretShare getDeviceShare() {
        return this.deviceShare;
    }

    public BigInteger getKey() {
        return this.key;
    }

}
