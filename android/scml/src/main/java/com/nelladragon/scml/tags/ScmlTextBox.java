// Copyright (C) 2017 Peter Robinson and the Smart Contract Application Browser contributors.
package com.nelladragon.scml.tags;

import com.nelladragon.scml.util.Pair;

import java.util.Set;


/**
 * Textbox mark-up tag.
 */

public class ScmlTextBox implements ScmlTag {

    public static final String NAME = "TextBox";
    public static final int ID = 1;


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public Set<Pair<TagParameter, String>> getParameters() {
        return TagFactory.NO_PARAMETERS;
    }

    @Override
    public Set<Pair<TagParameter, String>> getDefaultParameters() {
        return TagFactory.NO_PARAMETERS;
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @Override
    public String toString() {
        return NAME;
    }
}
