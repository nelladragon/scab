// Copyright (C) 2017 Peter Robinson and the Smart Contract Application Browser contributors.
package com.nelladragon.scab.language;

import java.util.Locale;

/**
 * Holder of language related information.
 */
public class Language {
    public int id;
    public boolean enabled;
    public String name;
    public Locale locale;

    public Language(int id, String name, Locale locale) {
        this.id = id;
        this.name = name;
        this.locale = locale;
        this.enabled = true;
    }
}
