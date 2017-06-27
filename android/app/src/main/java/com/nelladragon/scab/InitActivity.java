// Copyright (C) 2017 Peter Robinson and the Smart Contract Application Browser contributors.
package com.nelladragon.scab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Show coach marks.
 */
public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.relativeLayoutInit);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
