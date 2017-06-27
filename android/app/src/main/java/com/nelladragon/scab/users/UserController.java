// Copyright (C) 2017 Peter Robinson and the Smart Contract Application Browser contributors.
package com.nelladragon.scab.users;

import android.content.Context;

import com.nelladragon.scab.R;
import com.nelladragon.scab.SettingsConfigFragment;
import com.nelladragon.scab.data.DataHolder;
import com.nelladragon.scab.data.DatabaseHandler;
import com.nelladragon.scab.language.Languages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Controls interactions with users
 */
public class UserController {
    private Context appContext;

    private static UserController instance;

    private UUID currentProfile;

    private Map<UUID, UserProfile> profiles = new TreeMap<>();

    private DataHolder dataHolder;
    private DatabaseHandler db;

    private UserController(Context c) {
        this.appContext = c.getApplicationContext();
        this.dataHolder = DataHolder.getInstance(this.appContext);

        this.db = new DatabaseHandler(c);
        this.profiles = this.db.getAllUsers();
        if (this.profiles.isEmpty()) {
            // Create the default user profile.
            UserConfig conf = new UserConfig(SettingsConfigFragment.DEFAULT_TIME, true, false, false, Languages.DEFAULT);
            UserProfile def = new UserProfile(
                    this.appContext, "Sue Smith", "Standard Shower",
                    R.drawable.user_icon_6, R.string.profile_photo_desc_6,
                    conf);
            // Put the default profile into the list of profiles.
            this.currentProfile = def.getId();
            this.profiles.put(this.currentProfile, def);
            this.db.addOrUpdateUser(def);
            this.dataHolder.setCurrent(this.currentProfile.toString());
            this.dataHolder.persist();
        } else {
            String userIdStr = this.dataHolder.getCurrent();
            this.currentProfile = UUID.fromString(userIdStr);
        }
    }
    
    public static UserController getInstance(Context c) {
        if (instance == null) {
            instance = new UserController(c);
        }
        return instance;
    }




    public UserProfile getActiveProfile() {
        return this.profiles.get(this.currentProfile);
    }


    public void setActiveUser(UUID userId) {
        if (this.profiles.get(userId) != null) {
            this.currentProfile = userId;
            this.dataHolder.setCurrent(this.currentProfile.toString());
            this.dataHolder.persist();
        }
    }


    public int userCount() {
        return this.profiles.size();
    }


    public void addNewProfile() {
        UserProfile current = this.profiles.get(this.currentProfile);
        UserProfile newProfile = current.deriveNewProfile();
        this.currentProfile = newProfile.getId();
        this.profiles.put(this.currentProfile, newProfile);
        this.db.addOrUpdateUser(newProfile);
        this.dataHolder.setCurrent(this.currentProfile.toString());
        this.dataHolder.persist();
    }

    public List<UserProfile> getAllProfiles() {
        return new ArrayList<>(this.profiles.values());
    }

    public void deleteCurrentProfile() {
        int photoId = getActiveProfile().photoId;
        if (UserPhotoUtil.isUserPhotoId(photoId)) {
            this.dataHolder.deleteUserPhoto(photoId);
        }
        this.profiles.remove(this.currentProfile);
        this.db.deleteUser(this.currentProfile);
        Set<UUID> ids = this.profiles.keySet();
        this.currentProfile = ids.iterator().next();
        this.dataHolder.setCurrent(this.currentProfile.toString());
        this.dataHolder.persist();
    }




    public void syncActiveProfile() {
        this.db.addOrUpdateUser(getActiveProfile());
    }

}
