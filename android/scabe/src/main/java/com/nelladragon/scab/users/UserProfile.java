// Copyright (C) 2015 Peter Robinson
package com.nelladragon.scab.users;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.nelladragon.scab.R;
import com.nelladragon.scab.data.DataHolder;

import java.util.UUID;

/**
 * User who is local to this device.
 */
public class UserProfile {


    String profileName;
    String profileDesc;
    Drawable photo;
    int photoId;
    String photoDesc;
    UUID id;

    UserConfig config;

    Context appContext;

    /**
     * Used for setting default.
     *
     * @param appContext
     * @param userName
     * @param profileDesc
     * @param photoId
     * @param photoDescId
     */
    public UserProfile(Context appContext, String userName, String profileDesc, int photoId, int photoDescId, UserConfig conf) {
        this(appContext, userName, profileDesc, photoId, appContext.getResources().getString(photoDescId), conf);
    }

    /**
     * Used for creating a new user - generate a random UUID
     *
     * @param appContext
     * @param userName
     * @param profileDesc
     * @param photoId
     * @param photoDesc
     */
    public UserProfile(Context appContext, String userName, String profileDesc, int photoId, String photoDesc, UserConfig conf) {
        this(UUID.randomUUID(), appContext, userName, profileDesc, photoId, photoDesc, conf);
    }

    /**
     * Load a user.
     *
     * @param id
     * @param appContext
     * @param userName
     * @param profileDesc
     * @param photoId
     * @param photoDesc
     */
    public UserProfile(UUID id, Context appContext, String userName, String profileDesc, int photoId, String photoDesc, UserConfig conf) {
        this.appContext = appContext;
        this.id = id;
        this.profileName = userName;
        this.profileDesc = profileDesc;
        this.photoDesc = photoDesc;

        this.photoId = photoId;
        switch (photoId) {
            case UserPhotoUtil.PHOTO01:
            case UserPhotoUtil.PHOTO02:
            case UserPhotoUtil.PHOTO03:
            case UserPhotoUtil.PHOTO04:
            case UserPhotoUtil.PHOTO05:
            case UserPhotoUtil.PHOTO06:
                int drawableId = UserPhotoUtil.photoIdToDrawableId(photoId);
                this.photo = ContextCompat.getDrawable(this.appContext, drawableId);
                break;
            default:
                this.photo = DataHolder.getInstance(this.appContext).getUserPhoto(photoId);
                break;
        }
        if (this.photo == null) {
            this.photo = ContextCompat.getDrawable(this.appContext, R.drawable.user_icon_6);
        }

        this.config = conf;
    }

    public UUID getId() {
        return this.id;
    }

    public String getProfileName() {
        return this.profileName;
    }

    public void setProfileName(String name) {
        this.profileName = name;
    }

    public String getProfileDescription() {
        return this.profileDesc;
    }

    public void setProfileDescription (String description) {
        this.profileDesc = description;
    }

    public Drawable getProfilePhoto() {
        return this.photo;
    }


    public String getPhotoDesc() {
        return this.photoDesc;
    }

    // Used after photo has been chosen from list.
    public void setPhotoId(int id) {
        this.photoId = id;
        int drawableId = UserPhotoUtil.photoIdToDrawableId(id);
        this.photo = ContextCompat.getDrawable(this.appContext, drawableId);
    }
    public void setPhotoDescription(String description) {
        this.photoDesc = description;
    }

    // Used when user chooses a social photo (Facebook or Google) or one from their galley or take a photo.
    public void setPhotoDrawable(Drawable photo) {
        int newPhotoId = UserPhotoUtil.getRandomUserPhotoId();
        DataHolder dataHolder = DataHolder.getInstance(appContext);
        dataHolder.setUserPhoto(newPhotoId, photo);
        if (UserPhotoUtil.isUserPhotoId(this.photoId)) {
            dataHolder.deleteUserPhoto(this.photoId);   // Remove the old photo.
        }
        dataHolder.persist();

        this.photoId = newPhotoId;
        this.photo = photo;
    }

    public int getPhotoId() {
        return this.photoId;
    }

    public UserConfig getConfig() {
        return config;
    }

    public void setConfig(UserConfig config) {
        this.config = config;
    }

    public UserProfile deriveNewProfile() {
        String newDescription = this.profileDesc + "*";

        UserProfile profile = new UserProfile(this.appContext, this.profileName, newDescription, UserPhotoUtil.PHOTO01,
                this.photoDesc, this.config.clone());
        // Force a new photo id to be generated and a copy of the photo to be saved under
        // the new photo id.
        profile.setPhotoDrawable(this.photo);
        return profile;
    }
}
