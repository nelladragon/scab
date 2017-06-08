// Copyright (C) 2015 Peter Robinson
package com.nelladragon.scab;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nelladragon.common.util.Fonts;

/**
 * Show coach marks.
 */
public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        Typeface typeFace = Fonts.getHandlee(this.getApplicationContext());
        TextView txt = (TextView) findViewById(R.id.textViewTutIntro);
        txt.setTypeface(typeFace);
        txt = (TextView) findViewById(R.id.textViewTutMenu);
        txt.setTypeface(typeFace);
        txt = (TextView) findViewById(R.id.textViewTutStart);
        txt.setTypeface(typeFace);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.relativeLayoutTutorial);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
