// Copyright (C) 2015 Peter Robinson
package com.nelladragon.showertimertalking;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;

import com.nelladragon.showertimertalking.guistate.ChronometerControl;
import com.nelladragon.showertimertalking.language.Languages;
import com.nelladragon.showertimertalking.language.WordGenerator;
import com.nelladragon.showertimertalking.users.UserConfig;
import com.nelladragon.showertimertalking.users.UserController;
import com.nelladragon.common.util.Fonts;

import java.util.Map;

/**
 * Have a separate view from Main Activity so we can keep the
 * phone screen switched on, and to separate the complexity of this
 * activity from the Main Activity.
 */
public class ShowerOnActivity extends AppCompatActivity {
    TextToSpeech tts;
    ChronometerControl chronometerControl;

    Button startStop;

    UserConfig config;

    public Map<Integer, String> phrases;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showeron);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserController controller = UserController.getInstance(this);
        this.config = controller.getActiveProfile().getConfig();
        WordGenerator gen = new WordGenerator(this.config.getShowerLenInSeconds(),
                this.config.isWithAttitude(), this.config.isWithHumour(), this.config.isWithThoughts());
        this.phrases = gen.generate();

        Typeface typeFace = Fonts.getLetsGoDigital(this.getApplicationContext());
        Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.setTypeface(typeFace);
        this.chronometerControl = new ChronometerControl(this, chronometer);

        this.startStop = (Button) findViewById(R.id.buttonStartStop);
        this.startStop.setBackgroundResource(R.drawable.button_selected);
        this.startStop.setText("Stop");
        this.startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(ShowerOnActivity.this, MainActivity.class);
                //startActivity(i);
                finish();
            }
        });

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long timeMs = SystemClock.elapsedRealtime() - chronometer.getBase();
                int time = (int) timeMs / 1000;

                String toSpeak = phrases.get(time);
                if (toSpeak != null && tts != null) {
                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
            }

        });
    }

    public void onResume(){
        this.tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Languages.getLocale(config.getLanguage()));
                }
            }
        });

        this.chronometerControl.restoreState();
        this.chronometerControl.start();


        super.onResume();
    }


    public void onPause(){
        if(this.tts !=null){
            this.tts.stop();
            this.tts.shutdown();
            this.tts = null;
        }

        this.chronometerControl.storeState();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
