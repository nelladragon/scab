// Copyright (C) 2017 Peter Robinson and the Smart Contract Application Browser contributors.
package com.nelladragon.scab.language;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Describes all supported languages.
 */
public class Languages {
    public static final int DEFAULT = 0;
    public static final Language australian = new Language(DEFAULT, "English: Australia", new Locale("en", "AU"));
    public static final Language canadian = new Language(1, "English: Canada", new Locale("en", "CA"));
    public static final Language indian = new Language(2, "English: India", new Locale("en", "IN"));
    public static final Language irish = new Language(3, "English: Ireland", new Locale("en", "EI"));
    public static final Language newzealand = new Language(4, "English: New Zealand", new Locale("en", "NZ"));
    public static final Language southafrica = new Language(5, "English: South Africa", new Locale("en", "ZA"));
    public static final Language uk = new Language(6, "English: UK", new Locale("en", "GB"));
    public static final Language usa = new Language(7, "English: USA", new Locale("en", "US"));

    public static Map<Integer, Language> languages = new TreeMap<>();
    static {
        languages.put(australian.id, australian);
        languages.put(canadian.id, canadian);
        languages.put(indian.id, indian);
        languages.put(irish.id, irish);
        languages.put(newzealand.id, newzealand);
        languages.put(southafrica.id, southafrica);
        languages.put(uk.id, uk);
        languages.put(usa.id, usa);
    }



    public static Locale getLocale(int lang) {
        Language language = languages.get(lang);
        return language.locale;
    }

    public static String getName(int lang) {
        Language language = languages.get(lang);
        return language.name;
    }

}


