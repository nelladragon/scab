// Copyright (C) 2015 Peter Robinson
package com.nelladragon.common.util;

import java.nio.charset.Charset;

/**
 * General utility.
 */
public final class Util {
    public static final Charset UTF8 = Charset.forName("UTF8");


    /**
     * Wait a length of time.
     */
    public static void pause(int durationInMs) {
        try {
            Thread.sleep(durationInMs);
        } catch (Throwable th) {
            // Do nothing if we are woken up early.
        }
    }


    /**
     * Compare two byte arrays.
     *
     * @param a A byte array to compare.
     * @param b A byte array to compare.
     * @return true if the byte arrays match.
     */
    public static boolean compare(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }

        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }


    public static void intToByteArray(int value, byte[] buffer, int offset) {
        buffer[offset + 3] = (byte) value;
        buffer[offset + 2] = (byte) (value >> 8);
        buffer[offset + 1] = (byte) (value >> 16);
        buffer[offset] = (byte) (value >> 24);
    }


    /*
     * Given a <code>byte</code> array, print out the hex-encoding of that data,
     * in a visually pleasing format.
     *
     * @param byteArray The <code>byte</code> array to be printed.
     */
    public static String dumpBuffer(byte[] byteArray) {
        return dumpBuffer(byteArray, 0, byteArray.length);
    }


    /*
     * Given some part of a <code>byte</code> array, print the
     * hex-encoding of that data in a visually pleasing format.
     *
     * @param byteArray The <code>byte</code> array containing data to print.
     * @param offset    The starting location of the data to print.
     * @param length    The amount of data to print.
     */
    public static String dumpBuffer(byte[] byteArray, int offset, int length) {
        StringBuffer buf = new StringBuffer();
        StringBuffer textLine = new StringBuffer("                ");
        buf.append("0000: ");
        for (int i = 0; i < length; i++) {
            if (i % 16 == 0 && i != 0) {
                buf.append("[");
                buf.append(textLine);
                buf.append("]\n");
                buf.append(hexString(i, 4));
                buf.append(": ");
                for (int j = 0; j < 16; j++) {
                    textLine.setCharAt(j, ' ');
                }
            }
            buf.append(hexString(byteArray[i + offset], 2) + " ");
            if (byteArray[i + offset] < 32 || byteArray[i + offset] > 127 ||
                    byteArray[i + offset] == 0x7f) {
                textLine.setCharAt(i % 16, '.');
            } else {
                textLine.setCharAt(i % 16, (char) byteArray[i + offset]);
            }
        }
        if (length % 16 != 0 || length == 0) {
            for (int i = 0; i < 16 - length % 16; i++) {
                buf.append("   ");
            }
        }
        buf.append("[" + textLine + "]");
        return buf.toString();
    }

    private static String hexString(int value, int padding) {
        String hexString = "0123456789ABCDEF";
        StringBuffer tempString = new StringBuffer
                ("                                                                              ".substring(0, padding));
        int offset = padding - 1;

        for (int i = 0; i < padding; i++) {
            tempString.setCharAt(offset - i,
                    hexString.charAt(value >> i * 4 & 0xF));
        }
        return tempString.toString();
    }


}
