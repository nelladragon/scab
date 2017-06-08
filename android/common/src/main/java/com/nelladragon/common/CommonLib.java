package com.nelladragon.common;

import android.content.Context;

/**
 * Control class for common library.
 */
public final class CommonLib {
    private static CommonLib commonLib;

    Context appContext;


    private CommonLib(Context context) {
        this.appContext = context.getApplicationContext();
    }


    public static CommonLib getInstance(Context context) {
        if (commonLib == null) {
            commonLib = new CommonLib(context);
        }
        return commonLib;

    }

    public static CommonLib getInstance() {
        if (commonLib == null) {
            throw new Error("Common Lib not set-up");
        }
        return commonLib;
    }



    private FeedbackInfo feedbackInfo;
    public FeedbackInfo getFeedbackInfo() {
        return (this.feedbackInfo == null) ? new FeedbackInfoDefaultImpl() : this.feedbackInfo;
    }
    public void setFeedbackInfo(FeedbackInfo feedbackInfo) {
        this.feedbackInfo = feedbackInfo;
    }


    private String inAppBillPubkey;
    public String getInAppBillPubkey() {
        return inAppBillPubkey;
    }
    public void setInAppBillPubkey(String key) {
        this.inAppBillPubkey = key;
    }

}
