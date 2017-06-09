// Copyright (C) 2015 Peter Robinson
package com.nelladragon.common;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.nelladragon.scab.R;

/**
 * Activity to show the "Rate This App" screen.
 */
public class RateActivity extends AppCompatActivity {
    private static final String TAG = RateActivity.class.getSimpleName();


    String googlePlayLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Resources res = getResources();
        this.googlePlayLink = res.getString(R.string.rate_google_play_link);
        Log.d(TAG, "Google Play Link: " + this.googlePlayLink);


        Button rateApp = (Button) findViewById(R.id.buttonRateApp);
        rateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(googlePlayLink));
                startActivity(intent);
                finish();
            }
        });

        Button feedback = (Button) findViewById(R.id.buttonFeedback);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RateActivity.this, FeedbackActivity.class));
                finish();
            }
        });

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
