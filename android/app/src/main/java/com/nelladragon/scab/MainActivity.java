// Copyright (C) 2015 Peter Robinson
package com.nelladragon.scab;

import android.content.Intent;
import android.graphics.Typeface;
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
import android.view.Menu;
import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.nelladragon.common.FeedbackActivity;
        import com.nelladragon.common.HelpActivity;
        import com.nelladragon.common.RateActivity;
        import com.nelladragon.common.util.Fonts;
import com.nelladragon.scab.users.UserController;
import com.nelladragon.scab.users.UserProfile;
import com.viewpagerindicator.CirclePageIndicator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Values used for starting activities with a result.
    public static final int REQUEST_SETTINGS = 0;
    public static final int REQUEST_SWITCH = 1;
    public static final int TAB_TO_VIEW = 2;

    // Used with TAB_TO_VIEW
    public static final int TAB_WALLET = 0;
    public static final int TAB_MYCONTRACTS = TAB_WALLET + 1;
    public static final int TAB_CONTRACTSTORE = TAB_MYCONTRACTS + 1;

    UserController controller;
    UserProfile currentProfile;

    // Drawer based UI
    TextView txtUserName;
    ImageView userPhoto;
    TextView txtProfileDesc;
    ImageView iSwitchUser, iAddUser;


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
    private ViewPager viewPager;

    Typeface font;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onCreateDrawer();

        this.font = Fonts.getDegaws(null);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                // do transformation here
                final float normalizedposition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalizedposition / 2 + 0.5f);
                page.setScaleY(normalizedposition / 2 + 0.5f);
            }
        });

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.viewpagerindicator);
        indicator.setViewPager(this.viewPager);


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
            case R.id.nav_help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.nav_about:
//                startActivity(new Intent(this, AboutActivity.class));
                startActivity(new Intent(this, TestActivity.class));


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
        private static final int WALLET = TAB_WALLET;
        private static final int MYCONTRACTS = TAB_MYCONTRACTS;
        private static final int CONTRACTSTORE = TAB_CONTRACTSTORE;
        private static final int NUM_SCREENS = CONTRACTSTORE + 1;


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case WALLET:
                    return new WalletFragment();
                case MYCONTRACTS:
                    return new MyContractsFragment();
                case CONTRACTSTORE:
                    return new ContractStoreFragment();
                default:
                    throw new Error("Unknown page: " + position);
            }
        }

        @Override
        public int getCount() {
            return NUM_SCREENS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case WALLET:
                    return "Wallet";
                case MYCONTRACTS:
                    return "My Contracts";
                case CONTRACTSTORE:
                    return "Contract Store";
            }
            return null;
        }


        @Override
        public void setPrimaryItem(ViewGroup viewGroup, int position, Object obj) {
            setTitle(getPageTitle(position));

            // TODO what else might I be interested in doing here....?
            switch (position) {
                case WALLET:
                    break;
                case MYCONTRACTS:
                    break;
                case CONTRACTSTORE:
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
            case TAB_TO_VIEW:
                // The result code is the tab to view.
                this.viewPager.setCurrentItem(resultCode);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);

    //    // If there is only one profile, disable the Remove Profile option.
      //  if (UserController.getInstance(this).userCount() == 1) {
     //       MenuItem item = menu.getItem(2);
       //     item.setEnabled(false);
       // }
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
               // controller = UserController.getInstance(this);
              //  controller.deleteCurrentProfile();

  //              if (controller.userCount() == 1) {
    //                item.setEnabled(false);
      //          }

        //        setResult(RESULT_OK);
          //      finish();
                return true;

            case R.id.action_add:
                // Sync any changed settings.
    //            controller = UserController.getInstance(this);
      //          controller.syncActiveProfile();
                // Add the new profile, changing the current profile.
        //        controller.addNewProfile();
                // Restart this activity, to force a re-load by all frames.
//                startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
                // Close this instance of this activity.
  //              finish();
                return true;

            case R.id.action_switch:
                // Sync any changed settings.
          //      controller = UserController.getInstance(this);
            //    controller.syncActiveProfile();

                // Goto the switch activity
              //  startActivity(new Intent(SettingsActivity.this, SwitchProfileActivity.class));

                // Close this  activity.
                //finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}