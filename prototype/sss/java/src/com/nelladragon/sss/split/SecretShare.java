// Copyright (C) 2017 Peter Robinson
package com.nelladragon.sss.split;


import com.nelladragon.sss.util.Arrays;
import com.nelladragon.sss.util.Util;

import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Represents a single share.
 */
public class SecretShare {
    private ShareType type;
    private BigInteger shareY;
    private BigInteger shareX;


    /**
     * Create shares from byte arrays.
     * @param type Share type. Can be password or backup.
     * @param value value to be converted to Y value.
     */
    public SecretShare(ShareType type, byte[] value) {
        BigInteger temp = new BigInteger(value);
        this.shareY = temp.mod(Primes.PRIME);

        this.type = type;

        switch (type) {
            case PASSWORD:
                this.shareX = FixedShareThresholdScheme.X_BIG_PASSWORD;
                break;
            case BACKUP:
                this.shareX = FixedShareThresholdScheme.X_BIG_BACKUP;
                break;
            default:
                throw new RuntimeException("Unexpected share type");
        }
    }

    public SecretShare(ShareType type, BigInteger shareX, BigInteger shareY) {
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

    public void updateY(BigInteger y) {
        this.shareY = y;
    }


    private static final int INT_LEN = 4;

    public byte[] toBytes() {
        byte[] shareXBytes = this.shareX.toByteArray();
        int lenShareXBytes = shareXBytes.length;
        byte[] lenShareXBytesBytes = ByteBuffer.allocate(INT_LEN).putInt(lenShareXBytes).array();

        byte[] shareYBytes = this.shareY.toByteArray();
        int lenShareYBytes = shareYBytes.length;
        byte[] lenShareYBytesBytes = ByteBuffer.allocate(INT_LEN).putInt(lenShareYBytes).array();

        int type = this.type.xValue;
        byte[] typeBytes = ByteBuffer.allocate(INT_LEN).putInt(type).array();

        int totalLen = typeBytes.length + lenShareXBytesBytes.length + shareXBytes.length + lenShareYBytesBytes.length + shareYBytes.length;
        byte[] result = new byte[totalLen];

        int offset = Arrays.copyInto(typeBytes, result, 0);
        offset = Arrays.copyInto(lenShareXBytesBytes, result, offset);
        offset = Arrays.copyInto(shareXBytes, result, offset);
        offset = Arrays.copyInto(lenShareYBytesBytes, result, offset);
        offset = Arrays.copyInto(shareYBytes, result, offset);

        if (offset != totalLen) {
            throw new RuntimeException("Didn't copy all data into array");
        }
        return result;
    }


    public static SecretShare fromBytes(byte[] from) {
        int offset = 0;
        byte[] typeBytes = new byte[INT_LEN];
        offset = Arrays.copyFrom(from, typeBytes, offset);
        int typeInt = ByteBuffer.wrap(typeBytes).getInt();
        ShareType type = ShareType.fromInt(typeInt);

        byte[] lenShareXBytesBytes = new byte[INT_LEN];
        offset = Arrays.copyFrom(from, lenShareXBytesBytes, offset);
        int lenShareXBytes = ByteBuffer.wrap(lenShareXBytesBytes).getInt();

        byte[] shareXBytes = new byte[lenShareXBytes];
        offset = Arrays.copyFrom(from, shareXBytes, offset);
        BigInteger shareX = new BigInteger(shareXBytes);

        byte[] lenShareYBytesBytes = new byte[INT_LEN];
        offset = Arrays.copyFrom(from, lenShareYBytesBytes, offset);
        int lenShareYBytes = ByteBuffer.wrap(lenShareYBytesBytes).getInt();

        byte[] shareYBytes = new byte[lenShareYBytes];
        offset = Arrays.copyFrom(from, shareYBytes, offset);
        BigInteger shareY = new BigInteger(shareYBytes);

        return new SecretShare(type, shareX, shareY);
    }


    public String toString() {
        StringBuilder builder = new StringBuilder();
        return builder.append("SecretShare: Type: " + this.type)
                .append(", SecretShare: X: " + this.shareX)
                .append(", SecretShare: Y: " + this.shareY)
                .append("\n")
                .toString();
    }
}
