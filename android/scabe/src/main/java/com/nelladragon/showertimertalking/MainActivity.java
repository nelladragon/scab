// Copyright (C) 2015 Peter Robinson
package com.nelladragon.showertimertalking;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.nelladragon.common.DonateActivity;
import com.nelladragon.common.FeedbackActivity;
import com.nelladragon.common.HelpActivity;
import com.nelladragon.common.RateActivity;
import com.nelladragon.showertimertalking.data.NonPersistentGlobalData;
import com.nelladragon.showertimertalking.guistate.ChronometerControl;
import com.nelladragon.showertimertalking.users.UserController;
import com.nelladragon.showertimertalking.users.UserProfile;
import com.nelladragon.common.util.Fonts;
import com.nelladragon.common.AboutActivity;

/**
 * Drawer main activity.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_SETTINGS = 0;
    public static final int REQUEST_SWITCH = 1;
    public static final int REQUEST_SHOWER = 2;

    UserController controller;
    UserProfile currentProfile;

    // Drawer based UI
    TextView txtUserName;
    ImageView userPhoto;
    TextView txtProfileDesc;
    ImageView iSwitchUser, iAddUser;

    // Main screen UI
    ChronometerControl chronometerControl;

    Button startStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateDrawer(R.layout.activity_main);

        Typeface typeFace = Fonts.getLetsGoDigital(this.getApplicationContext());
        Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.setTypeface(typeFace);
        this.chronometerControl = new ChronometerControl(this, chronometer);

        // Wipe the Chronometer state when the app first starts up.
        if (NonPersistentGlobalData.first) {
            NonPersistentGlobalData.first = false;
            this.chronometerControl.deleteState();
        }


        this.startStop = (Button) findViewById(R.id.buttonStartStop);
        this.startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chronometerControl.deleteState();
                startActivityForResult(new Intent(MainActivity.this, ShowerOnActivity.class), REQUEST_SHOWER);
            }
        });
    }



    protected void onCreateDrawer(int layoutId) {
        setContentView(layoutId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0); // 0-index header

        this.controller = UserController.getInstance(this);
        this.currentProfile = this.controller.getActiveProfile();
        this.txtUserName = (TextView) headerLayout.findViewById(R.id.textViewAccountName);
        this.userPhoto = (ImageView) headerLayout.findViewById(R.id.imageViewAccountIcon);
        this.txtProfileDesc = (TextView) headerLayout.findViewById(R.id.textViewAcountDescription);
        this.iSwitchUser = (ImageView) headerLayout.findViewById(R.id.buttonSwitch);
        this.iAddUser = (ImageView) headerLayout.findViewById(R.id.buttonAdd);
        setupDrawerUI();

        this.iSwitchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, SwitchProfileActivity.class), REQUEST_SWITCH);
            }
        });
        this.iAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.addNewProfile();
                startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), REQUEST_SETTINGS);
            }
        });
        this.userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), REQUEST_SETTINGS);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.chronometerControl.restoreState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                i.putExtra(SettingsActivity.TAB_TO_OPEN, SettingsActivity.SETTINGS_TAB_CONFIG);
                startActivityForResult(i, REQUEST_SETTINGS);
                break;
//            case R.id.nav_share:
//                startActivity(new Intent(this, ShareActivity.class));
//                break;
            case R.id.nav_rate:
                startActivity(new Intent(this, RateActivity.class));
                break;
            case R.id.nav_feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;
            case R.id.nav_donate:
                startActivity(new Intent(this, DonateActivity.class));
                break;
            case R.id.nav_tutorial:
                startActivity(new Intent(this, TutorialActivity.class));
                break;
            case R.id.nav_help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        switch (requestCode) {
            case REQUEST_SETTINGS:
                if (resultCode == RESULT_OK) {
                    // Get the profile object again - perhaps the old profile has been deleted.
                    this.currentProfile = this.controller.getActiveProfile();
                    setupDrawerUI();
                }
                break;
            case REQUEST_SWITCH:
                if (resultCode == RESULT_OK) {
                    // Get the profile object again - we have probably switched user.
                    this.currentProfile = this.controller.getActiveProfile();
                    setupDrawerUI();
                }
                break;
            case REQUEST_SHOWER:
                // Nothing to do.
                break;
            default:
                break;
        }
    }


    private void setupDrawerUI() {
        this.txtUserName.setText(currentProfile.getProfileName());
        this.userPhoto.setImageDrawable(currentProfile.getProfilePhoto());
        this.txtProfileDesc.setText(currentProfile.getProfileDescription());

        if (this.controller.userCount() == 1) {
            iSwitchUser.setVisibility(View.INVISIBLE);
        } else {
            iSwitchUser.setVisibility(View.VISIBLE);
        }

    }
}
