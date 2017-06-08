// Copyright (C) 2015 Peter Robinson
package com.nelladragon.scab.language;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Determines what words should be said at what time, given the configuration.
 */
public class WordGenerator {
    private int len;
    private boolean attitude, humour, thoughts;

    public WordGenerator(int length, boolean attitude, boolean humour, boolean thoughts) {
        this.len = length;
        this.attitude = attitude;
        this.humour = humour;
        this.thoughts = thoughts;
    }


    public Map<Integer, String> generate() {
        Map<Integer, String> words = new TreeMap<>();

        // Get base time offsets.
        Set<Integer> timesOfs = WordGenBaseWords.baseWordsMap.keySet();
        for (Integer timeOfs: timesOfs) {
            if (timeOfs <= this.len) {
                words.put(timeOfs, WordGenBaseWords.baseWordsMap.get(timeOfs));
            }
        }

        // For each ten seconds after the end, add in an additonal indicator.
        String timeToGetOut = this.attitude ? "get out now" : "ding";
        int TEN_SECONDS = 10;
        for (int i=1; i < 100; i++) {
            words.put(len + i * TEN_SECONDS, timeToGetOut);
        }

        if (this.attitude) {
            words.put(1, "start");
            words.put(this.len/2-TEN_SECONDS, "almost half way");
            words.put(this.len, "time to get out");
            if (this.len > 90) {
                words.put(this.len-30, "almost time");
            }
            if (this.len > 120) {
                words.put(this.len-60, "are you almost clean?");
            }
        }

        if (this.humour) {
            if (this.thoughts) {
                words.put(this.len / 2 + 15, WordGenHumour.getRandomHumour());
                if (this.len > 120) {
                    words.put(30, WordGenThoughts.getRandomThoughts());
                }
            } else {
                words.put(this.len / 2 + 15, WordGenHumour.getRandomHumour());
                if (this.len > 120) {
                    words.put(30, WordGenHumour.getRandomHumour());
                }
            }
        } else if (this.thoughts) {
            words.put(this.len/2 + 15, WordGenThoughts.getRandomThoughts());
            if (this.len > 120) {
                words.put(30, WordGenThoughts.getRandomThoughts());
            }
        }
        return words;
    }

}
