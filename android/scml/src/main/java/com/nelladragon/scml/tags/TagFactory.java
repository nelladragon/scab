// Copyright (C) 2017 Peter Robinson and the Smart Contract Application Browser contributors.
package com.nelladragon.scml.tags;

import com.nelladragon.scml.util.Pair;

import java.util.Set;
import java.util.TreeSet;

/**
 * Factory to create tag objects based on ether tag names and parameters or TODO
 */

public class TagFactory {
    public static final Set<Pair<TagParameter, String>> NO_PARAMETERS = new TreeSet<>();



    private static TagFactory instance;

    public static synchronized final TagFactory getSingleInstance() {
        if (instance == null) {
            instance = new TagFactory();
        }
        return instance;
    }


    private TagFactory() {}



    public ScmlTag getScmlTag(String tagNane, Set<Pair> parameters) {
        // TODO
        return null;



    }





}
