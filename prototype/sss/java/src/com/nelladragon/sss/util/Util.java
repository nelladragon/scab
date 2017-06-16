// Copyright (C) 2017 Peter Robinson
package com.nelladragon.sss.util;

import java.security.SecureRandom;

/**
 * Utilities to print and compare data.
 */
public class Util {

    /*
     * Print out some byte data, with a given label.
     */
    public void printData(String label, byte[] bytes) {
        System.out.println(label);
        printBuffer(bytes);
    }

    /*
     * Print the specified text to standard output.
     *
     * @param textToPrint The text to be printed.
     */
    public static void print(String textToPrint) {
        System.out.print(textToPrint);
    }

    /*
     * Print the specified value to standard output.
     *
     * @param textToPrint The text to be printed.
     */
    public static void print(long val) {
        System.out.print(val);
    }

    /*
     * Print a blank line to standard output.
     */
    public static void println() {
        System.out.println();
    }

    /*
     * Print the specified text to standard output.
     *
     * @param textToPrint The text to print.
     */
    public static void println(String textToPrint) {
        System.out.println(textToPrint);
    }

    /*
     * Given a <code>byte</code> array, print out the hex-encoding of that data,
     * in a visually pleasing format.
     *
     * @param byteArray The <code>byte</code> array to be printed.
     */
    public static void printBuffer(byte[] byteArray) {
        printBuffer(byteArray, 0, byteArray.length);
    }

    /*
     * Given a <code>byte</code> array, print the first length bytes of
     * that <code>byte</code> array.
     *
     * @param byteArray The <code>byte</code> array containing the data to be printed.
     * @param length    The amount of data to be printed.
     */
    public static void printBuffer(byte[] byteArray, int length) {
        printBuffer(byteArray, 0, length);
    }

    /*
     * Given some part of a <code>byte</code> array, print the
     * hex-encoding of that data in a visually pleasing format.
     *
     * @param byteArray The <code>byte</code> array containing data to print.
     * @param offset    The starting location of the data to print.
     * @param length    The amount of data to print.
     */
    public static void printBuffer(byte[] byteArray, int offset, int length) {
        StringBuffer textLine = new StringBuffer("                ");
        print("  0000: ");
        for (int i = 0; i < length; i++) {
            if (i % 16 == 0 && i != 0) {
                println("[" + textLine + "]");
                print("  " + hexString(i, 4) + ": ");
                for (int j = 0; j < 16; j++) {
                    textLine.setCharAt(j, ' ');
                }
            }
            print(hexString(byteArray[i + offset], 2) + " ");
            if (byteArray[i + offset] < 32 || byteArray[i + offset] > 127 ||
                    byteArray[i + offset] == 0x7f) {
                textLine.setCharAt(i % 16, '.');
            } else {
                textLine.setCharAt(i % 16, (char) byteArray[i + offset]);
            }
        }
        if (length % 16 != 0 || length == 0) {
            for (int i = 0; i < 16 - length % 16; i++) {
                print("   ");
            }
        }
        println("[" + textLine + "]");
    }

    /*
     * Convert int to hexadecimal string with padding
     *
     * @param value
     * @param padding
     * @return hexadecimal string
     */
    protected static String hexString(int value, int padding) {
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

    /*
     * The following methods all compare byte arrays.
     */
    public static boolean compare(byte[] expected, byte[] actual) {
        return compareX(expected, 0, expected.length, actual, 0, actual.length, true);
    }
    public static boolean compare(
            byte[] expected, int expectedOfs, int expectedLen,
            byte[] actual, int actualOfs, int actualLen) {
        return compareX(expected, expectedOfs, expectedLen, actual, actualOfs, actualLen, true);
    }

    /*
     * As above, but do not print out any info.
     */
    public static boolean compareNoPrint(byte[] expected, byte[] actual) {
        return compareX(expected, 0, expected.length, actual, 0, actual.length, false);
    }
    public static boolean compareNoPrint(
            byte[] expected, int expectedOfs, int expectedLen,
            byte[] actual, int actualOfs, int actualLen) {
        return compareX(expected, expectedOfs, expectedLen, actual, actualOfs, actualLen, false);
    }

    private static boolean compareX(
            byte[] expected, int expectedOfs, int expectedLen,
            byte[] actual, int actualOfs, int actualLen,
            boolean show) {

        // Check for null arrays first.
        if (expected == null || actual == null) {
            if (expected == null && actual == null) {
                // If both references are null they match
                return true;
            } else if (expected == null) {
                if (show) {
                    showBufferDiff(expected, expectedOfs, expectedLen, actual, actualOfs, actualLen);
                }
                return false;
            } else {
                if (show) {
                    showBufferDiff(expected, expectedOfs, expectedLen, actual, actualOfs, actualLen);
                }
                return false;
            }
        }

        // Set-up state variables.
        boolean expectedIsLongerOrSame = expectedLen >= actualLen;
        boolean sameLength = expectedLen == actualLen;
        int shortestLength = expectedIsLongerOrSame ? actualLen : expectedLen;

        // Find the first different byte in the buffers.
        int firstDiff = 0;
        while (firstDiff < shortestLength) {
            if (expected[firstDiff + expectedOfs] != actual[firstDiff + actualOfs]) {
                break;
            }
            firstDiff++;
        }

        StringBuffer message = new StringBuffer();
        if (firstDiff == shortestLength) {
            if (sameLength) {
                // The byte arrays are identical. They have the same contents
                // and the same length.
                return true;
            }
            message.append("Expected and Actual are the same, except that ");
            if (expectedIsLongerOrSame) {
                message.append("expected is ");
                message.append(Integer.toString(expectedLen - actualLen));
            } else {
                message.append("actual is ");
                message.append(Integer.toString(actualLen - expectedLen));
            }
            message.append(" byte(s) longer.");
        } else {
            message.append("Expected and Actual differ at offset: ");
            message.append(Integer.toString(firstDiff));
            message.append(" (0x");
            message.append(Integer.toHexString(firstDiff));
            message.append(")");
        }
        if (show) {
            println(message.toString());
            showBufferDiff(expected, expectedOfs, expectedLen, actual, actualOfs, actualLen);
        }
        return false;
    }

    private static void showBufferDiff(
            byte[] expected, int expectedOfs, int expectedLen,
            byte[] actual, int actualOfs, int actualLen) {

        println("Expected: ");
        printBuffer(expected, expectedOfs, expectedLen);
        println("Actual: ");
        printBuffer(actual, actualOfs, actualLen);
    }



    private static final int BYTES_PER_LONG = 8;
    private static final int BYTES_PER_INT = 4;
    // Have a counter which starts at a different value each time the application starts.
    private static long count = System.nanoTime();

    /**
     * Add in additional seeding material to the PRNG.
     *
     * The following is added:
     * * Current time in milli-seconds.
     * * The CPU cycle counter, as returned by nanotime.
     * * The current value of the incrementing counter.
     * * The memory address of a newly created object, as returned by Object.hashCode()
     *
     * @param rand
     */
    public static void addQuickEntropy(SecureRandom rand) {
        long time = System.currentTimeMillis();
        long cycleCount = System.nanoTime();
        count = count + cycleCount;
        long countSnapshot = count;
        Object o = new Object();
        int objHashCode = o.hashCode();


        byte[] quickEntropy  = new byte[3 * BYTES_PER_LONG + BYTES_PER_INT];
        int i = 0;
        for (;i < BYTES_PER_LONG; i++) {
            quickEntropy[i] = (byte) time;
            time >>= 8;
        }
        for (;i < 2 * BYTES_PER_LONG; i++) {
            quickEntropy[BYTES_PER_LONG + i] = (byte) cycleCount;
            cycleCount >>= 8;
        }
        for (; i < 3 * BYTES_PER_LONG; i++) {
            quickEntropy[i] = (byte) countSnapshot;
            countSnapshot >>= 8;
        }
        for (; i < 3 * BYTES_PER_LONG + BYTES_PER_INT; i++) {
            quickEntropy[i] = (byte) objHashCode;
            objHashCode >>= 8;
        }

        rand.setSeed(quickEntropy);
    }
}
