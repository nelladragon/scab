// Copyright (C) 2015 Peter Robinson
package com.nelladragon.scab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentPagerAdapter;
        import android.support.v4.view.GravityCompat;
        import android.support.v4.view.ViewPager;
        import android.os.Bundle;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.Gravity;
        import android.view.LayoutInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;

        import android.view.Window;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.PopupWindow;
        import android.widget.TextView;

import com.nelladragon.common.AboutActivity;
import com.nelladragon.common.DonateActivity;
        import com.nelladragon.common.FeedbackActivity;
        import com.nelladragon.common.HelpActivity;
        import com.nelladragon.common.RateActivity;
        import com.nelladragon.common.util.Fonts;
import com.nelladragon.scab.users.UserController;
import com.nelladragon.scab.users.UserProfile;
import com.viewpagerindicator.CirclePageIndicator;

public class DappActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_SETTINGS = 0;
    public static final int REQUEST_SWITCH = 1;
    public static final int REQUEST_SWITCH_LEVEL = 2;

    UserController controller;
    UserProfile currentProfile;

    // Drawer based UI
    TextView txtUserName;
    ImageView userPhoto;
    TextView txtProfileDesc;
    ImageView iSwitchUser, iAddUser;

    PopupWindow popup;

    DappFragment dappFrag1 = new DappFragment();
    DappFragment dappFrag2 = new DappFragment();

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

    Typeface font;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dapp);
        onCreateDrawer();

        this.font = Fonts.getDegaws(null);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                // do transformation here
                final float normalizedposition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalizedposition / 2 + 0.5f);
                page.setScaleY(normalizedposition / 2 + 0.5f);
            }
        });

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.viewpagerindicator);
        indicator.setViewPager(this.mViewPager);


    }


    private void onCreateDrawer() {
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
                startActivityForResult(new Intent(DappActivity.this, SwitchProfileActivity.class), REQUEST_SWITCH);
            }
        });
        this.iAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.addNewProfile();
                startActivityForResult(new Intent(DappActivity.this, SettingsActivity.class), REQUEST_SETTINGS);
            }
        });
        this.userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(DappActivity.this, SettingsActivity.class), REQUEST_SETTINGS);
            }
        });

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


    @SuppressWarnings("StatementWithEmptyBody")
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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private static final int MULT = 0;
        private static final int DIV = 1;


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case MULT:
                    return dappFrag1;
                case DIV:
                    return dappFrag2;
                default:
                    throw new Error("Unknown page: " + position);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case MULT:
                    return "Prime Factor Quest: Multiply";
                case DIV:
                    return "Prime Factor Quest: Divide";
            }
            return null;
        }


        @Override
        public void setPrimaryItem(ViewGroup viewGroup, int position, Object obj) {
            setTitle(getPageTitle(position));

            switch (position) {
                case MULT:
                    break;
                case DIV:
                    break;
            }
        }
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
            case REQUEST_SWITCH_LEVEL:
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


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();

    }


}