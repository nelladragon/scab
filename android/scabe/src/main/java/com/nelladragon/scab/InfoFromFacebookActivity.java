// Copyright (C) 2015 Peter Robinson
package com.nelladragon.scab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.ProfilePictureView;
import com.nelladragon.scab.users.UserController;
import com.nelladragon.scab.users.UserProfile;

/**
 * Activity to get user name and photo from Facebook.
 */
public class InfoFromFacebookActivity extends AppCompatActivity {

    CallbackManager facebookCallbackManager;
    LoginManager facebookLoginManager;

    ProfilePictureView facebookProfilePhoto;
    TextView textViewFacebookName;
    Button buttonAck;
    Button buttonSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_from_facebook);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.facebookProfilePhoto = (ProfilePictureView) findViewById(R.id.imageViewFacebookProfilePhoto);
        this.textViewFacebookName = (TextView) findViewById(R.id.textViewFacebookName);
        this.buttonSwitch = (Button) findViewById(R.id.buttonFacebookSwitch);
        this.buttonSwitch.setVisibility(Button.GONE);
        this.buttonAck = (Button) findViewById(R.id.buttonProfileFromFacebookAck);
        this.buttonAck.setEnabled(false);

        this.facebookCallbackManager = CallbackManager.Factory.create();
        this.facebookLoginManager = LoginManager.getInstance();
        this.facebookLoginManager.registerCallback(this.facebookCallbackManager, new MyFacebookCallback<LoginResult>());

        // If we aren't logged in, then login now.
        if (AccessToken.getCurrentAccessToken() == null) {
            facebookLoginManager.logInWithReadPermissions(this, null);
        } else {
            // We are logged in.
            Profile facebookProfile = Profile.getCurrentProfile();
            if (facebookProfile == null) {
                String errorMsg = InfoFromFacebookActivity.this.getResources().getString(R.string.facebook_error);
                errorMsg = errorMsg + "!";
                Snackbar.make(textViewFacebookName, errorMsg, Snackbar.LENGTH_LONG).show();
                textViewFacebookName.setText(errorMsg);
            } else {
                this.facebookProfilePhoto.setProfileId(facebookProfile.getId());
                this.textViewFacebookName.setText(facebookProfile.getName());
            }
            buttonSwitch.setVisibility(Button.VISIBLE);
            buttonAck.setEnabled(true);
        }

        this.buttonAck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView profileImageView = ((ImageView) facebookProfilePhoto.getChildAt(0));
                Drawable photo = profileImageView.getDrawable();
                String userName = textViewFacebookName.getText().toString();
                if (photo != null) {
                    UserController controller = UserController.getInstance(InfoFromFacebookActivity.this);
                    UserProfile currentProfile = controller.getActiveProfile();
                    currentProfile.setProfileName(userName);
                    currentProfile.setPhotoDescription("Facebook");
                    currentProfile.setPhotoDrawable(photo);

                    // Force the active profile to be stored to the database so all changes are
                    // reflected in the database.
                    controller.syncActiveProfile();
                }

                InfoFromFacebookActivity.this.setResult(Activity.RESULT_OK);
                InfoFromFacebookActivity.this.finish();
            }
        });

        this.buttonSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Logout and then login as another user.
                facebookLoginManager.logOut();
                facebookLoginManager.logInWithReadPermissions(InfoFromFacebookActivity.this, null);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public static String dumpLoginResult(LoginResult loginResult) {
        StringBuffer buf = new StringBuffer();
        if (loginResult == null) {
            buf.append("Login Result is null");
        } else {
            String userId = loginResult.getAccessToken().getUserId();
            buf.append("UserId: " + userId + "\n");
            String accessToken = loginResult.getAccessToken().getToken();
            buf.append("Token: " + accessToken + "\n");
        }
        return buf.toString();
    }

    public String dump() {
        StringBuffer buf = new StringBuffer();

        buf.append("FacebookSDK is initialized: " + FacebookSdk.isInitialized() + "\n");
        buf.append("FacebookSDK app id: " + FacebookSdk.getApplicationId() + "\n");
        buf.append("FacebookSDK app name: " + FacebookSdk.getApplicationName() + "\n");
        buf.append("FacebookSDK app signature: " + FacebookSdk.getApplicationSignature(this) + "\n");
        buf.append("FacebookSDK client token: " + FacebookSdk.getClientToken() + "\n");
        buf.append("FacebookSDK facebook domain: " + FacebookSdk.getFacebookDomain() + "\n");
        buf.append("FacebookSDK version: " + FacebookSdk.getSdkVersion() + "\n");
        buf.append("FacebookSDK debug enabled: " + FacebookSdk.isDebugEnabled() + "\n");

        Profile profile = Profile.getCurrentProfile();
        if (profile == null) {
            buf.append("Profile is null\n");
        } else {
            buf.append("Profile: First Name: " + profile.getFirstName() + "\n");
            buf.append("Profile: Last Name: " + profile.getLastName() + "\n");
            buf.append("Profile: Name: " + profile.getName() + "\n");
            buf.append("Profile: Id: " + profile.getId() + "\n");
            buf.append("Profile: LinkURI: " + profile.getLinkUri() + "\n");
            buf.append("Profile: Pic URI: " + profile.getProfilePictureUri(100, 100) + "\n");
        }

        AccessToken currentToken = AccessToken.getCurrentAccessToken();
        if (currentToken == null) {
            buf.append("Current Access Token is null\n");
        } else {
            buf.append("CurToken: tok: " + currentToken.getToken() + "\n");
            buf.append("CurToken: appid: " + currentToken.getApplicationId() + "\n");
            buf.append("CurToken: expires: " + currentToken.getExpires().toString() + "\n");
        }
        return buf.toString();
    }

    class MyFacebookCallback<LoginResult> implements FacebookCallback<com.facebook.login.LoginResult>{
        @Override
        public void onSuccess(com.facebook.login.LoginResult loginResult) {
            Profile facebookProfile = Profile.getCurrentProfile();
            if (facebookProfile == null) {
                String errorMsg = InfoFromFacebookActivity.this.getResources().getString(R.string.facebook_error);
                errorMsg = errorMsg + ".";
                Snackbar.make(textViewFacebookName, errorMsg, Snackbar.LENGTH_LONG).show();
                textViewFacebookName.setText(errorMsg);
            } else {
                facebookProfilePhoto.setProfileId(facebookProfile.getId());
                textViewFacebookName.setText(facebookProfile.getName());

                buttonAck.setEnabled(true);
                buttonSwitch.setVisibility(Button.VISIBLE);
            }

            // TODO: Do not store access token - it is a security issue storing this
            // TODO and appears to be of no benefit.
//                AccessToken token = loginResult.getAccessToken();
//                DataHolder data = DataHolder.getInstance(InfoFromFacebookActivity.this.getApplicationContext());
//                data.setFacebookToken(token.getToken());
//                data.persist();
        }

        @Override
        public void onCancel() {
            InfoFromFacebookActivity.this.setResult(Activity.RESULT_CANCELED);
            InfoFromFacebookActivity.this.finish();
        }

        @Override
        public void onError(FacebookException exception) {
            String errorMsg = InfoFromFacebookActivity.this.getResources().getString(R.string.facebook_error);
            Snackbar.make(textViewFacebookName, errorMsg, Snackbar.LENGTH_LONG).show();
            textViewFacebookName.setText(errorMsg);
            buttonSwitch.setVisibility(Button.VISIBLE);
        }
    }

}
