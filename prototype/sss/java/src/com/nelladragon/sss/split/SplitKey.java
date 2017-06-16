// Copyright (C) 2017 Peter Robinson
package com.nelladragon.sss.split;

import java.math.BigInteger;

/**
 * Represents a generated secret and its shares.
 */
public class SplitKey {
    BigInteger secret;
    SecretShare passwordShare;
    SecretShare deviceShare;
    SecretShare[] externShares;

    public SplitKey(BigInteger secret, SecretShare passwordShare, SecretShare deviceShare, SecretShare[] externalShares) {
        this.secret = secret;
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

    public BigInteger getSecret() {
        return this.secret;
    }

}
