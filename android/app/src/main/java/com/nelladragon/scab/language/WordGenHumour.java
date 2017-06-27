// Copyright (C) 2017 Peter Robinson and the Smart Contract Application Browser contributors.
package com.nelladragon.scab.language;

import com.nelladragon.common.crypto.Rand;

/**
 * List of jokes.
 */
public class WordGenHumour {

    private static String[] words = new String[] {
        "What did one ocean say to the other ocean? Nothing, they just waved.",
        "A day without sunshine is night.",
        "For Sale: Parachute. Only used once, never opened.",
        "A bank is a place that will lend you money, if you can prove that you don’t need it.",
        "What is faster Hot or cold? Hot, because you can catch a cold.",
        "Why did the scientist install a knocker on his door? He wanted to win the No-bell prize!",
        "When everything’s coming your way, you’re in the wrong lane.",
        "If you can’t convince them, confuse them.",
        "Why did the bee get married? Because he found his honey.",
        "Forget hydrogen, you're my number one element.",
        "Are you made of beryllium, gold, and titanium? You must be because you are BeAuTi-ful.",
        "Knock, knock; who's there; cow; cow who? Cow is on your head!",
        "What runs around a grave yard but never moves? A fence!",
        "You have mass, you take up space; you matter!",
        "Last time I got caught stealing a calendar I got 12 months.",
        "Knock, knock. Who is there? Isabelle. Isabelle who? Is a bell necessary on a bicycle.",
        "What happens to a frog's car when it breaks down? It gets toad away.",
        "Why was six scared of seven? Because seven ate nine.",
        "How do astronomers organize a party? They planet",
        "How my did Santa pay for his sleigh? Nothing. It was on the house.",
        "I wasn't going to get a brain transplant, but then I changed my mind.",
        "I'm reading a book about anti-gravity. It's impossible to put down."
    };

    public static String getRandomHumour() {
        int len = words.length;
        int rand = Rand.generateRandomPositiveInt();
        int ofs = rand % len;
        return words[ofs];
    }

}
