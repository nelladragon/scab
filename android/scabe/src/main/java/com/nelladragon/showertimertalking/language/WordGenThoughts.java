// Copyright (C) 2015 Peter Robinson
package com.nelladragon.showertimertalking.language;

import com.nelladragon.util.crypto.Rand;

/**
 * List of thoughts.
 */
public class WordGenThoughts {

    private static String[] words = new String[] {
            "Do not argue with an idiot. He will drag you down to his level and beat you with experience.",
            "A day without smiling is a day wasted.",
            "Sometimes we expect more from others because we would be willing to do that much more for them.",
            "When tempted to fight fire with fire, remember that Fire Fighters usually uses water.",
            "Burial shrouds have no pockets.",
            "Live modestly.",
            "Where will you be next year?",
            "Live for today. Plan for tomorrow.",
            "It’s no use going back to yesterday, because I was a different person then.",
            "Hardship often prepares an ordinary person for an extraordinary destiny.",
            "We are what we repeatedly do. Greatness then, is not an act, but a habit.",
            "The more I learn, the more I realize how much I don't know.",
            "Smile, it is good for you",
            "Take time to live life",
            "Smile",
            "Do the best you can",
            "Be the best person you can be",
            "what will you be doing in one year's time?",
            "less is more",
            "if in doubt, wait out"

    };

    public static String getRandomThoughts() {
        int len = words.length;
        int rand = Rand.generateRandomPositiveInt();
        int ofs = rand % len;
        return words[ofs];
    }

}
