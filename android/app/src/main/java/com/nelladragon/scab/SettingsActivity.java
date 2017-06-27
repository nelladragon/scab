// Copyright (C) 2017 Peter Robinson and the Smart Contract Application Browser contributors.
package com.nelladragon.scab;

import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.nelladragon.scab.users.UserController;

/**
 * Holds the two settings fragments.
 */
public class SettingsActivity extends AppCompatActivity {
    public static String TAB_TO_OPEN = "TAB_TO_OPEN";
    private static final int NUM_SETTINGS_TABS = 2;
    public static final int SETTINGS_TAB_PROFILE = 0;
    public static final int SETTINGS_TAB_CONFIG = 1;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        int tabToOpen = SETTINGS_TAB_PROFILE;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tabToOpen = extras.getInt(TAB_TO_OPEN);
        }

        this.resources = getResources();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(tabToOpen);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Force the active profile to be stored to the database so all changes are
        // reflected in the database.
        UserController.getInstance(this).syncActiveProfile();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_activity, menu);

        // If there is only one profile, disable the Remove Profile option.
        if (UserController.getInstance(this).userCount() == 1) {
            MenuItem item = menu.getItem(2);
            item.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        UserController controller;
        switch (id) {
            case R.id.action_delete:
                controller = UserController.getInstance(this);
                controller.deleteCurrentProfile();

                if (controller.userCount() == 1) {
                    item.setEnabled(false);
                }

                setResult(RESULT_OK);
                finish();
                return true;

            case R.id.action_add:
                // Sync any changed settings.
                controller = UserController.getInstance(this);
                controller.syncActiveProfile();
                // Add the new profile, changing the current profile.
                controller.addNewProfile();
                // Restart this activity, to force a re-load by all frames.
                startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
                // Close this instance of this activity.
                finish();
                return true;

            case R.id.action_switch:
                // Sync any changed settings.
                controller = UserController.getInstance(this);
                controller.syncActiveProfile();

                // Goto the switch activity
                startActivity(new Intent(SettingsActivity.this, SwitchProfileActivity.class));

                // Close this  activity.
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case SETTINGS_TAB_PROFILE:
                    return new SettingsProfileFragment();
                case SETTINGS_TAB_CONFIG:
                    return new SettingsConfigFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total tabs.
            return NUM_SETTINGS_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case SETTINGS_TAB_PROFILE:
                    return resources.getString(R.string.settings_header_profile);
                case SETTINGS_TAB_CONFIG:
                    return resources.getString(R.string.settings_header_conf);
            }
            return null;
        }
    }

}
