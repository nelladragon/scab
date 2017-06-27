// Copyright (C) 2017 Peter Robinson and the Smart Contract Application Browser contributors.
package com.nelladragon.scab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nelladragon.scab.users.UserController;
import com.nelladragon.scab.users.UserProfile;


/**
 * Sets up profile.
 */
public class SettingsProfileFragment extends Fragment {
    private static final int REQUEST_PHOTO = 0x125;
    private static final int REQUEST_FACEBOOK = 0x126;
    private static final int REQUEST_GOOGLE = 0x127;

    private UserController userController;
    private UserProfile currentProfile;

    ImageView imageViewPhoto;
    TextView textViewPhotoDesc;
    EditText editTextProfileName;
    EditText editTextProfileDesc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings_profile, container, false);

        this.userController = UserController.getInstance(getContext());
        this.currentProfile = this.userController.getActiveProfile();

        this.editTextProfileName = (EditText) rootView.findViewById(R.id.editTextProfileName);
        this.editTextProfileName.setText(this.currentProfile.getProfileName());

        this.editTextProfileDesc = (EditText) rootView.findViewById(R.id.editTextProfileDesc);
        this.editTextProfileDesc.setText(this.currentProfile.getProfileDescription());

        this.imageViewPhoto = (ImageView) rootView.findViewById(R.id.imageViewProfilePhoto);
        this.imageViewPhoto.setImageDrawable(this.currentProfile.getProfilePhoto());

        this.textViewPhotoDesc = (TextView) rootView.findViewById(R.id.textViewProfilePhotoSummary);
        this.textViewPhotoDesc.setText(this.currentProfile.getPhotoDesc());

        RelativeLayout layoutProfilePhoto = (RelativeLayout) rootView.findViewById(R.id.layoutProfilePhoto);
        layoutProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), ChoosePhotoActivity.class), REQUEST_PHOTO);
            }
        });

        Button buttonFromFacebook = (Button) rootView.findViewById(R.id.buttonProfileFromFacebook);
        buttonFromFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), InfoFromFacebookActivity.class), REQUEST_FACEBOOK);
            }
        });

        Button buttonFromGoogle = (Button) rootView.findViewById(R.id.buttonProfileFromGoogle);
        buttonFromGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), InfoFromGoogleActivity.class), REQUEST_GOOGLE);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        switch (requestCode) {
            case REQUEST_PHOTO:
                // Make sure the request was successful
                if (resultCode == Activity.RESULT_OK) {
                    // The user picked a photo. The object will have been updated.
                    this.imageViewPhoto.setImageDrawable(this.currentProfile.getProfilePhoto());
                    this.textViewPhotoDesc.setText(this.currentProfile.getPhotoDesc());
                }
                break;
            case REQUEST_FACEBOOK:
            case REQUEST_GOOGLE:
                // Make sure the request was successful
                if (resultCode == Activity.RESULT_OK) {
                    // The user picked a photo. The object will have been updated.
                    this.imageViewPhoto.setImageDrawable(this.currentProfile.getProfilePhoto());
                    this.textViewPhotoDesc.setText(this.currentProfile.getPhotoDesc());
                    this.editTextProfileName.setText(this.currentProfile.getProfileName());
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        this.currentProfile.setProfileName(this.editTextProfileName.getText().toString());
        this.currentProfile.setProfileDescription(this.editTextProfileDesc.getText().toString());
    }
}
