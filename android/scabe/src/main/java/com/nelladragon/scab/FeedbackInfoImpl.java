package com.nelladragon.scab;

import android.content.Context;

import com.nelladragon.common.FeedbackInfo;
import com.nelladragon.scab.data.DataHolder;
import com.nelladragon.scab.data.DatabaseHandler;
import com.nelladragon.common.inapp.DonationCache;
import com.nelladragon.scab.users.UserConfig;
import com.nelladragon.scab.users.UserProfile;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Feedback specific to Shower Timer Talking.
 */
public class FeedbackInfoImpl implements FeedbackInfo {

    Context appContext;


    public FeedbackInfoImpl(Context c) {
        this.appContext = c.getApplicationContext();
    }



    @Override
    public String getProfileInfo() {
        StringBuffer buf = new StringBuffer();

        DataHolder data = DataHolder.getInstance(this.appContext);
        String userIdStr = data.getCurrent();
        buf.append("Current: ");
        buf.append((userIdStr == null) ? "none. " : userIdStr);
        buf.append('\n');

        DatabaseHandler db = new DatabaseHandler(this.appContext);
        buf.append("Profiles: \n");
        Map<UUID, UserProfile> profiles = db.getAllUsers();
        if (profiles.isEmpty()) {
            buf.append("none");
        } else {
            Collection<UserProfile> profiles1 = profiles.values();
            for (UserProfile profile: profiles1) {
                UserConfig conf = profile.getConfig();
                buf.append(profile.getId().toString());
                buf.append(',');
                buf.append(conf.getLanguage());
                buf.append(',');
                buf.append(conf.getShowerLenInSeconds());
                buf.append(',');
                buf.append(conf.isWithAttitude());
                buf.append(',');
                buf.append(conf.isWithHumour());
                buf.append(',');
                buf.append(conf.isWithThoughts());
                buf.append('\n');
            }
        }
        return buf.toString();
    }


    @Override
    public String getAutoAnalysis() {
        StringBuffer buf = new StringBuffer();

        // Dump a list donations the app perceives have been made.
        DonationCache cache = DonationCache.getInstance(this.appContext);
        List<DonationCache.DonateItem> donations = cache.loadDonationInfo();
        buf.append("D: ");
        for (DonationCache.DonateItem donation: donations) {
            if (donation.purchased) {
                buf.append(donation.amount);
                buf.append(',');
            }
        }

        buf.append('\n');


        return buf.toString();
    }


}
