// Copyright (C) 2015 Peter Robinson
package com.nelladragon.common;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.nelladragon.common.thankyou.FireworksView;

/**
 * Display "thank you" screen.
 */
public class ThankyouActivity extends AppCompatActivity {
    FireworksView fireworksView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        int amount = DonateActivity.UNKNOWN_DEFAULT_AMOUNT;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            amount = extras.getInt(DonateActivity.DONATION_AMOUNT);
        }

        this.fireworksView = (FireworksView) findViewById(R.id.fireworksView);
        this.fireworksView.setAmount(amount);
        this.fireworksView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireworksView.fireAgain();
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
