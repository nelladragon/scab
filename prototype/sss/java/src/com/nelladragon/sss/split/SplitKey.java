// Copyright (C) 2017 Peter Robinson
package com.nelladragon.sss.split;

import java.math.BigInteger;
import java.util.Map;

/**
 * Represents a generated secret and its shares.
 */
public class SplitKey {
    private final BigInteger secret;
    private final Map<ShareType, SecretShare> shares;


    public SplitKey(final BigInteger secret, final Map<ShareType, SecretShare> shares) {
        this.secret = secret;
        this.shares = shares;
    }


    public SecretShare getDeviceShare() {
        return this.shares.get(ShareType.DEVICE);
    }

    public BigInteger getSecret() {
        return this.secret;
    }


    public SecretShare getShare(final ShareType type) {
        return this.shares.get(type);
    }

}
