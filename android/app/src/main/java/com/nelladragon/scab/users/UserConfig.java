// Copyright (C) 2017 Peter Robinson and the Smart Contract Application Browser contributors.
package com.nelladragon.scab.users;

/**
 * Holds configuration information for a single profile.
 */
public class UserConfig {
    private int showerLenInSeconds;
    private boolean withAttitude;
    private boolean withHumour;
    private boolean withThoughts;
    private int language;


    public UserConfig(int time, boolean attitude, boolean humour, boolean thoughts, int language) {
        this.showerLenInSeconds = time;
        this.withAttitude = attitude;
        this.withHumour = humour;
        this.withThoughts = thoughts;
        this.language = language;
    }


    public int getShowerLenInSeconds() {
        return showerLenInSeconds;
    }

    public void setShowerLenInSeconds(int showerLenInSeconds) {
        this.showerLenInSeconds = showerLenInSeconds;
    }

    public boolean isWithAttitude() {
        return withAttitude;
    }

    public void setWithAttitude(boolean withAttitude) {
        this.withAttitude = withAttitude;
    }

    public boolean isWithHumour() {
        return withHumour;
    }

    public void setWithHumour(boolean withHumour) {
        this.withHumour = withHumour;
    }

    public boolean isWithThoughts() {
        return withThoughts;
    }

    public void setWithThoughts(boolean withThoughts) {
        this.withThoughts = withThoughts;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public UserConfig clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            // do nothing
        }
        return new UserConfig(this.showerLenInSeconds, this.withAttitude, this.withHumour, this.withThoughts, this.language);
    }
}
