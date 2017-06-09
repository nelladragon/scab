package com.nelladragon.common;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.nelladragon.common.util.AppVersion;
import com.nelladragon.scab.BuildConfig;
import com.nelladragon.scab.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Displays copyright information, EULA, and open source
 * license info.
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the navigation "UP" button.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        Resources res = getResources();
        String version = AppVersion.getVersion(this);
        String appName = res.getString(R.string.app_name);
        String authorName =  res.getString(R.string.author);


        TextView txtCopyrightTitle = (TextView) findViewById(R.id.textViewCopyrightTitle);
        String copyrightTitleUnpatched = res.getString(R.string.copyright_title);
        String copyrightTitlePatched = String.format(copyrightTitleUnpatched, appName);
        txtCopyrightTitle.setText(copyrightTitlePatched);

        TextView txtCopyright = (TextView) findViewById(R.id.textViewCopyrightDetail);
        Date buildDate = new Date(BuildConfig.TIMESTAMP);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String date = dateFormat.format(buildDate);
        String copyrightDetailUnpatched = res.getString(R.string.copyright_detail);
        String copyrightDetailPatched = String.format(copyrightDetailUnpatched, authorName, version, date);
        txtCopyright.setText(copyrightDetailPatched);

        TextView txtTermsOfUse = (TextView) findViewById(R.id.textViewTermsOfUse);
        String termsOfUseUnpatched = res.getString(R.string.terms_of_use);
        String termsOfUsePatched = String.format(termsOfUseUnpatched, authorName, version, appName);
        txtTermsOfUse.setText(termsOfUsePatched);

        TextView txtOpenSource = (TextView) findViewById(R.id.textViewOpenSource);
        String openSourceCommon = res.getString(R.string.open_source_common);
        String openSourceSpecific = res.getString(R.string.open_source_specific);
        txtOpenSource.setText(openSourceCommon + openSourceSpecific);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            // Rather than trying to navigate to the "parent", just finish this activity, which will
            // return control to the "parent" activity.
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
