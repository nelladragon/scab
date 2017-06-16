// Copyright (C) 2017 Peter Robinson
package com.nelladragon.sss.util;



public class Arrays {

    /**
     * Compares two byte arrays.
     *
     * @param a The first byte array.
     * @param b The second byte array.
     * @return <code>true</code> if byte arrays are equal.
     */
    public static boolean equals(final byte[] a, final byte[] b) {
        if (a == null || b == null) {
            return a == null && b == null;
        }
        return equals(a, 0, a.length, b, 0, b.length);
    }

    /**
     * Compares two byte arrays.
     *
     * @param a The first byte array.
     * @param aOffset Offset into the first byte array where the data starts.
     * @param aLen The length of the data in the first byte array.
     * @param b The second byte array.
     * @param bOffset Offset into the second byte array where the data starts.
     * @param bLen The length of the data in the second byte array.
     * @return <code>true</code> if byte arrays are equal.
     */
    public static boolean equals(final byte[] a, final int aOffset, int aLen, final byte[] b, final int bOffset, final int bLen) {

        // If both references are null they match.
        if (a == null || b == null) {
            return a == null && b == null;
        }
        // If lengths differ they aren't equal.
        if (aLen != bLen) {
            return false;
        }
        for (aLen--; aLen >= 0; aLen--) {
            if (a[aOffset + aLen] != b[bOffset + aLen]) {
                return false;
            }
        }
        // All bytes matched.
        return true;
    }

    /**
     * Create a copy of the specified array object with offset and length.
     *
     * @param bytes The array to be copied.
     * @param offset The offset to be copied.
     * @param length The length to be copied.
     * @return The new array.
     */
    public static int[] copyOf(int[] bytes, int offset, int length) {
        final int[] result;
        if (bytes != null) {
            result = new int[length];
            System.arraycopy(bytes, offset, result, 0, length);
        } else {
            result = null;
        }
        return result;
    }

    /**
     * Create a copy of the specified array object.
     *
     * @param chars The array to be copied.
     * @return The new array.
     */
    public static char[] copyOf(char[] chars) {
        if (chars == null) {
            return null;
        }
        return copyOf(chars, 0, chars.length);
    }

    /**
     * Create a copy of the specified array object.
     *
     * @param bytes The array to be copied.
     * @return The new array.
     */
    public static int[] copyOf(int[] bytes) {
        if (bytes == null) {
            return null;
        }
        return copyOf(bytes, 0, bytes.length);
    }

    /**
     * Create a copy of the specified array object.
     *
     * @param bytes The array to be copied.
     * @return The new array.
     */
    public static long[] copyOf(long[] bytes) {
        if (bytes == null) {
            return null;
        }
        return copyOf(bytes, 0, bytes.length);
    }
    /**
     * Create a copy of the specified array object with offset and length.
     *
     * @param bytes The array to be copied.
     * @param offset The offset to be copied.
     * @param length The length to be copied.
     * @return The new array.
     */
    public static long[] copyOf(long[] bytes, int offset, int length) {
        if (bytes == null) {
            return null;
        }
        final long[] result = new long[length];
        System.arraycopy(bytes, offset, result, 0, length);
        return result;
    }

    /**
     * Create a copy of the specified array object with offset and length.
     *
     * @param bytes The array to be copied.
     * @param offset The offset to be copied.
     * @param length The length to be copied.
     * @return The new array.
     */
    public static byte[] copyOf(byte[] bytes, int offset, int length) {
        if (bytes == null) {
            return null;
        }
        final byte[] result = new byte[length];
        System.arraycopy(bytes, offset, result, 0, length);
        return result;
    }

    public static char[] copyOf(char[] chars, int offset, int length) {
        if (chars == null) {
            return null;
        }
        final char[] result = new char[length];
        System.arraycopy(chars, offset, result, 0, length);
        return result;
    }

    /**
     * Create a copy of the specified array object.
     *
     * @param bytes The array to be copied.
     * @return The new array.
     */
    public static byte[] copyOf(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return copyOf(bytes, 0, bytes.length);
    }

    public static void fill(final byte[] input, final int start, final int len, final byte b) {
        for (int i = start; i < len; i++) {
            input[i] = b;
        }
    }

    public static void fill(final int[] array, final int val) {
        for (int i = 0; i < array.length; i++) {
            array[i] = val;
        }
    }

    /**
     * Concatenate the byte[] in <code>inputs</code> into one byte[].
     * <p>
     * Length of <code>inputs</code> must be equals to length of <code>lens</code>.
     * <p>
     * An array in <code>inputs</code>  may be <code>null</code>, and its corresponding length must be zero.
     *
     * @param inputs Array containing byte arrays to concatenate.
     * @param lens Array containing the number of bytes in each of inputs to concatenate.
     * @return The concatenated byte array.
     */
    public static byte[] concatenateBytes(byte[][] inputs, int[] lens) {

        int len = 0;
        for (int i = 0; i < lens.length; i++) {
            len += lens[i];
        }
        byte[] output = new byte[len];

        len = 0;
        for (int i = 0; i < inputs.length; i++) {
            if (lens[i] != 0) {
                System.arraycopy(inputs[i], 0, output, len, lens[i]);
                len += lens[i];
            }
        }
        return output;
    }

    /**
     * This method concatenates two byte arrays.
     *
     * @param data The original byte array.
     * @param in The byte array to concatenate to <code>data</code>.
     * @param offset The offset into <code>in</code> to tart the copy.
     * @param len The total number of bytes to copy from <code>in</code>.
     * @return The byte array resulting from the concatenation.
     */
    public static byte[] addData(byte[] data, byte[] in, int offset, int len, boolean clearData) {
        if (in == null) {
            return data;
        }
        byte[] returnValue;
        if (data == null) {
            returnValue = new byte[len];
            System.arraycopy(in, offset, returnValue, 0, len);
        } else {
            returnValue = new byte[data.length + len];
            System.arraycopy(data, 0, returnValue, 0, data.length);
            System.arraycopy(in, offset, returnValue, data.length, len);
        }
        return returnValue;
    }

    /**
     * Sorts an array of ints into ascending order using an insertions sort.
     *
     * @param x The array to sort.
     * @param off The offset into x.
     * @param len The number of bytes in x to sort.
     */
    public static void sortSmallArray(int[] x, int off, int len) {
        for (int i = off; i< len + off; i++) {
            for (int j = i; j > off && x[j - 1] > x[j]; j--) {
                swap(x, j, j - 1);
            }
        }
    }

    /*
     * Swaps x[a] with x[b].
     */
    private static void swap(int[] x, int a, int b) {
        int t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    /**
     * Searches a range of the specified array for the specified value using the binary search algorithm.
     * <p>
     * The range must be sorted (as by the {@link #sortSmallArray(int[], int, int)} method)
     * prior to making this call.  The range should also contain unique values.
     *
     * @param a Array to be searched
     * @param fromIndex Index of the first element to be searched
     * @param toIndex Index of the last element (exclusive) to be searched
     * @param key Value to be searched for
     * @return index of the search key, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.  The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>toIndex</tt> if all
     *         elements in the range are less than the specified key.  Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     */
    public static int binarySearch(int[] a, int fromIndex, int toIndex, int key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = low + high >>> 1;
            int midVal = a[mid];

            if (midVal < key) {
                low = mid + 1;
            } else if (midVal > key) {
                high = mid - 1;
            } else {
                return mid;
            } // key found
        }
        return -(low + 1);  // key not found.
    }


    /**
     * Copy a byte array into another byte array.
     *
     * @param from Array to copy from.
     * @param to Array to copy to.
     * @param offset Offset within "to" array.
     * @return New offset.
     */
    public static int copyInto(byte[] from, byte[] to, int offset) {
        System.arraycopy(from, 0, to, offset, from.length);
        return offset + from.length;
    }

    /**
     * Copy from an array at a certain offset into an array.
     *
     * @param from Array to copy from.
     * @param to Array to copy to. Offset to copy to is 0, and copy to.length bytes.
     * @param offset Offset within from array.
     * @return New offset.
     */
    public static int copyFrom(byte[] from, byte[] to, int offset) {
        System.arraycopy(from, offset, to, 0, to.length);
        return offset + to.length;
    }


}
