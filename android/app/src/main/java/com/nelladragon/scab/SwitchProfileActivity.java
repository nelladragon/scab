// Copyright (C) 2017 Peter Robinson and the Smart Contract Application Browser contributors.
package com.nelladragon.scab;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nelladragon.scab.users.UserController;
import com.nelladragon.scab.users.UserProfile;

import java.util.List;

/**
 * Activity to allow the user to choose the active profile from a list.
 *
 */
public class SwitchProfileActivity extends AppCompatActivity {

    /**
     * Adapter class to allow data to be transferred from the list and the activity.
     */
    private class ProfileListAdapter extends BaseAdapter {
        private class ViewHolder {
            protected ImageView profileImage;
            protected TextView profileName;
            protected TextView profileDescription;
            protected RadioButton radioButton;
        }

        private List<UserProfile> profiles;
        private LayoutInflater inflater;
        private Context context;

        public ProfileListAdapter(Context context, List<UserProfile> profileItems) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            this.profiles = profileItems;
        }


        public int getCount() {
            return this.profiles.size();
        }

        // Get the item associated with the position in the dataset.
        public Object getItem(int position) {
            return this.profiles.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.list_item_profile, parent, false);

                holder = new ViewHolder();
                holder.profileName = (TextView) convertView.findViewById(R.id.textViewProfileName);
                holder.profileImage = (ImageView) convertView.findViewById(R.id.profileImage);
                holder.profileDescription = (TextView) convertView.findViewById(R.id.textViewProfileDescription);
                holder.radioButton = (RadioButton) convertView.findViewById(R.id.iconRadio);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            UserProfile item = this.profiles.get(position);
            holder.profileName.setText(item.getProfileName());
            holder.profileDescription.setText(item.getProfileDescription());
            holder.profileImage.setImageDrawable(item.getProfilePhoto());

            if (item.getId().equals(activeProfile.getId())) {
                holder.radioButton.setChecked(true);
            }
            return convertView;
        }
    }



    ListView listView;
    ProfileListAdapter adapter;

    UserProfile activeProfile;
    List<UserProfile> profiles;
    UserController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.controller = UserController.getInstance(SwitchProfileActivity.this);
        this.activeProfile = this.controller.getActiveProfile();
        this.profiles = this.controller.getAllProfiles();

        this.adapter = new ProfileListAdapter(this, this.profiles);
        this.listView = (ListView) findViewById(R.id.listViewSwitchProfile);
        this.listView.setAdapter(this.adapter);

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                UserProfile selected = profiles.get(position);
                controller.setActiveUser(selected.getId());

                SwitchProfileActivity.this.setResult(RESULT_OK);
                SwitchProfileActivity.this.finish();
            }
        });
    }
}
