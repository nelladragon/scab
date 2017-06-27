// Copyright (C) 2017 Peter Robinson and the Smart Contract Application Browser contributors.
package com.nelladragon.scml.util;

/**
 * We need our own Pair class because the Java 7 version and the Android version of the
 * class are not available (this code needs to run on both Java 8 and on Android).
 */
public class Pair<T1, T2> {
    private T1 key;
    private T2 value;

    public Pair(T1 key, T2 value) {
        this.key = key;
        this.value = value;
    }


    public T1 getKey() {
        return this.key;
    }

    public T2 getValue() {
        return this.value;
    }


}
