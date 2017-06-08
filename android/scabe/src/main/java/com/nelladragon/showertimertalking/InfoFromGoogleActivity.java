// Copyright (C) 2015 Peter Robinson
package com.nelladragon.showertimertalking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.nelladragon.showertimertalking.users.UserController;
import com.nelladragon.showertimertalking.users.UserProfile;

/**
 * Load user name and photo from Google.
 */
public class InfoFromGoogleActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleSignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;


    ImageView imageViewGooglePhoto;
    TextView textViewGoogleName;
    Button buttonAck;
    Button buttonSwitch;

    boolean triedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_from_google);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.imageViewGooglePhoto = (ImageView) findViewById(R.id.imageViewGooglePhoto);
        this.textViewGoogleName = (TextView) findViewById(R.id.textViewGoogleName);

        // Google signing. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                // .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        this.buttonAck = (Button) findViewById(R.id.buttonProfileFromGoogleAck);
        this.buttonAck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable photo = imageViewGooglePhoto.getDrawable();
                String userName = textViewGoogleName.getText().toString();
                if (photo != null) {
                    UserController controller = UserController.getInstance(InfoFromGoogleActivity.this);
                    UserProfile currentProfile = controller.getActiveProfile();
                    currentProfile.setProfileName(userName);
                    currentProfile.setPhotoDescription("Google");
                    currentProfile.setPhotoDrawable(photo);

                    // Force the active profile to be stored to the database so all changes are
                    // reflected in the database.
                    controller.syncActiveProfile();
                }

                InfoFromGoogleActivity.this.setResult(Activity.RESULT_OK);
                InfoFromGoogleActivity.this.finish();
            }
        });
        this.buttonAck.setEnabled(false);

        this.buttonSwitch = (Button) findViewById(R.id.buttonGoogleSwitch);
        this.buttonSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
                signIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Reset this - maybe the user has gone to another app to turn on wifi / network.
        this.triedOnce = false;

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Uri accountPhotoURI = acct.getPhotoUrl();

            this.textViewGoogleName.setText(acct.getDisplayName());
            Glide.with(this).load(accountPhotoURI).asBitmap().into(imageViewGooglePhoto);

            this.buttonAck.setEnabled(true);
        } else {
            // Signed out, or not yet signed in. Show unauthenticated UI.
            String errorMsg = getResources().getString(R.string.google_error);
            this.textViewGoogleName.setText(errorMsg);

            // Kick-off sign-in if auto login didn't work. If this fails, then don't try it agian.
            if (!this.triedOnce) {
                this.triedOnce = true;
                signIn();
            }
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        //updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        //updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}


