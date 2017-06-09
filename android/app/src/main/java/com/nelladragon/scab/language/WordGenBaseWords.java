// Copyright (C) 2015 Peter Robinson
package com.nelladragon.scab.language;

import java.util.Map;
import java.util.TreeMap;

/**
 * Simple time based words.
 */
public class WordGenBaseWords {

    public static Map<Integer, String> baseWordsMap = createBaseMap();
    public static Map<Integer, String> createBaseMap() {
        Map<Integer, String> map = new TreeMap<>();
        map.put(30, "thirty seconds");
        map.put(60, "one minute");
        map.put(90, "ninety seconds");
        map.put(120, "two minutes");
        map.put(150, "two and a half minutes");
        map.put(180, "three minutes");
        map.put(210, "three and a half minutes");
        map.put(240, "four minutes");
        map.put(270, "four and a half minutes");
        map.put(300, "five minutes");
        map.put(330, "five and a half minutes");
        map.put(360, "six minutes");
        map.put(390, "six and a half minutes");
        map.put(420, "seven minutes");
        map.put(450, "seven and a half minutes");
        map.put(480, "eight minutes");
        map.put(510, "eight and a half minutes");
        map.put(540, "nine minutes");
        map.put(570, "nine and a half minutes");
        map.put(600, "ten minutes");
        return map;
    }

}
