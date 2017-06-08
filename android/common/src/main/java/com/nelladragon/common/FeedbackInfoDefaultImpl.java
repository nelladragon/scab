package com.nelladragon.common;

/**
 * Default implementation if a FeedbackInfo implementation has not been supplied to CommonLib.
 */
public class FeedbackInfoDefaultImpl implements FeedbackInfo {

    @Override
    public String getProfileInfo() {
        return "";
    }

    @Override
    public String getAutoAnalysis() {
        return "";
    }
}
