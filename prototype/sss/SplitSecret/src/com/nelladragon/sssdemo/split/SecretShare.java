package com.rsa.peter.testsplitsecret.split;


import com.rsa.peter.testsplitsecret.publicapi.ShareType;

import java.math.BigInteger;

/**
 * Represents a single share.
 */
public class SecretShare {
    private ShareType type;
    private BigInteger shareY;
    private BigInteger shareX;


    /**
     * Constructor to be used for password share only.
     * @param share value to be converted to Y value.
     */
    public SecretShare(byte[] share) {
        this.type = ShareType.PASSWORD;
        this.shareX = BigInteger.ONE;
        BigInteger temp = new BigInteger(share);
        this.shareY = temp.mod(Primes.PRIME);
    }

    public SecretShare(ShareType type, BigInteger shareY, BigInteger shareX) {
        this.type = type;
        this.shareX = shareX;
        this.shareY = shareY;
    }


    public BigInteger getShareY() {
        return shareY;
    }

    public BigInteger getShareX() {
        return shareX;
    }


    public ShareType getType() {
        return this.type;
    }
}
