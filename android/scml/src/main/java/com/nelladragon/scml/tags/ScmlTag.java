// Copyright (C) 2017 Peter Robinson and the Smart Contract Application Browser contributors.
package com.nelladragon.scml.tags;

import com.nelladragon.scml.util.Pair;

import java.util.Set;

/**
 * Interface for SCML tags.
 */

public interface ScmlTag {

    // Get name of tag.
    String getName();

    // Get id which represents the tag.
    int getId();

    // Get parameters of the tag.
    Set<Pair<TagParameter, String>> getParameters();

    Set<Pair<TagParameter, String>> getDefaultParameters();



    String toString();

    byte[] toBytes();



}
