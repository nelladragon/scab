package com.nelladragon.common.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Returns strings indicating time relative to a base line.
 */
public class RelativeTime {
    private static final long MS_PER_S = 1000;
    private static final long ONE_MINUTE = 60 * MS_PER_S;
    private static final long TWO_MINUTES = 2 * ONE_MINUTE;
    private static final long ONE_HOUR = 60 * ONE_MINUTE;
    private static final long TWO_HOURS = 2 * ONE_HOUR;
    private static final long SIX_HOURS = 6 * ONE_HOUR;


    private static final String JUST_NOW =      "just now";
    private static final String MINTES_AGO =    " minutes ago";
    private static final String AN_HOUR_AGO =   "an hour ago";
    private static final String HOURS_AGO =     " hours ago";
    private static final String EARLIER_TODAY = "earlier today";
    private static final String YESTERDAY =     "yesterday";
    private static final String DAYS_AGO =      " days ago";


    private static final String[] MONTHS = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };



    // Time in ms since Jan 1, 1970.
    private long baseTime;
    private Calendar calBaseTime;
    private Calendar otherCal;

    /**
     * Initialize relate time to now.
     */
    public RelativeTime() {
        this((new Date()).getTime());
    }

    /**
     * Specify the relative time.
     *
     * @param time Time in ms since Jan 1, 1970.
     */
    public RelativeTime(long time) {
        this.baseTime = time;

        this.calBaseTime = Calendar.getInstance();
        this.calBaseTime.setTimeInMillis(this.baseTime);

        this.otherCal = Calendar.getInstance();
    }


    public String getTime(long time) {
        long timeDiff = this.baseTime - time;
        if (timeDiff < TWO_MINUTES) {
            return JUST_NOW;
        }
        if (timeDiff < ONE_HOUR) {
            long numMin = timeDiff / ONE_MINUTE;
            return numMin + MINTES_AGO;
        }
        if (timeDiff < TWO_HOURS) {
            return AN_HOUR_AGO;
        }
        if (timeDiff < SIX_HOURS) {
            long numHours = timeDiff / ONE_HOUR;
            return numHours + HOURS_AGO;
        }

        int bYear = this.calBaseTime.get(Calendar.YEAR);
        int bDayOfYear = this.calBaseTime.get(Calendar.DAY_OF_YEAR);

        this.otherCal.setTimeInMillis(time);
        int oYear = this.otherCal.get(Calendar.YEAR);

        int oDayOfYear = this.otherCal.get(Calendar.DAY_OF_YEAR);

        if (bYear == oYear) {
            if (bDayOfYear == oDayOfYear) {
                return EARLIER_TODAY;
            }
            if (bDayOfYear - 1 == oDayOfYear) {
                return YESTERDAY;
            }
            int diff = bDayOfYear - oDayOfYear;
            if (diff < 7) {
                return diff + DAYS_AGO;
            }
        }

        int oMonth = this.otherCal.get(Calendar.MONTH);
        int oDay = this.otherCal.get(Calendar.DATE);
        String month = MONTHS[oMonth];
        return month + ' ' + oDay + ", " + oYear;
    }








}
