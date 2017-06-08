// Copyright (C) 2015 Peter Robinson
package com.nelladragon.showertimertalking;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.nelladragon.showertimertalking.language.Language;
import com.nelladragon.showertimertalking.language.Languages;
import com.nelladragon.showertimertalking.users.UserConfig;
import com.nelladragon.showertimertalking.users.UserController;
import com.nelladragon.showertimertalking.users.UserProfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Displays a list of languages for the user to choose between.
 */
public class ChooseLanguageActivity extends AppCompatActivity {

    /**
     * Adapter class to allow data to be transferred from the list and the activity.
     */
    private class LanguageListAdapter extends BaseAdapter {
        private class ViewHolder {
            protected TextView iconName;
            protected RadioButton radioButton;
        }

        private List<LanguageItem> languages;
        private LayoutInflater inflater;

        public LanguageListAdapter(Context context, List<LanguageItem> lang) {
            this.inflater = LayoutInflater.from(context);
            this.languages = lang;
        }


        public int getCount() {
            return this.languages.size();
        }

        // Get the item associated with the position in the dataset.
        public Object getItem(int position) {
            return this.languages.get(position);
        }

        public long getItemId(int position) {
            return this.languages.get(position).id;

        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.list_item_language, parent, false);

                holder = new ViewHolder();
                holder.iconName = (TextView) convertView.findViewById(R.id.iconName);
                holder.radioButton = (RadioButton) convertView.findViewById(R.id.iconRadio);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final LanguageItem item = this.languages.get(position);
            holder.iconName.setText(item.name);

            holder.radioButton.setChecked(item.isChecked);

//            convertView.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    ViewHolder holder = (ViewHolder) v.getTag();
//                    for (int i = 0; i < icons.size(); i++) {
//                        if (i == position)
//                            icons.get(i).isChecked = true;
//                        else
//                            icons.get(i).isChecked = false;
//                    }
//                    getActiv
//                    //getDialog().dismiss();
//                }
//            });

            return convertView;
        }
    }


    private static class LanguageItem {
        private boolean isChecked;
        private String name;
        private int id;

        public LanguageItem(Language lang, boolean isChecked) {
            this.name = lang.name;
            this.id = lang.id;
            this.isChecked = isChecked;
        }
    }


    ListView listView;
    LanguageListAdapter adapter;

    UserConfig activeConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserProfile activeProfile = UserController.getInstance(ChooseLanguageActivity.this).getActiveProfile();
        this.activeConfig = activeProfile.getConfig();

        this.adapter = new LanguageListAdapter(this, loadLangInfo());
        this.listView = (ListView) findViewById(R.id.listViewChooseLanguage);
        this.listView.setAdapter(this.adapter);

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                activeConfig.setLanguage((int) l);

                ChooseLanguageActivity.this.setResult(RESULT_OK);
                ChooseLanguageActivity.this.finish();
            }
        });
    }


    private List<LanguageItem> loadLangInfo() {
        int currentLang = activeConfig.getLanguage();
        List<LanguageItem> langInfo = new ArrayList<>();
        Collection<Language> langs = Languages.languages.values();
        for (Language l: langs) {
            boolean current = currentLang == l.id;
            langInfo.add(new LanguageItem(l, current));
        }
        return langInfo;
    }



}
