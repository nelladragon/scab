// Copyright (C) 2015 Peter Robinson
package com.nelladragon.scab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nelladragon.scab.language.Languages;
import com.nelladragon.scab.users.UserConfig;
import com.nelladragon.scab.users.UserController;
import com.nelladragon.scab.users.UserProfile;
import com.nelladragon.common.util.Fonts;
import com.nelladragon.common.util.SoundGenerator;


import java.util.Locale;


/**
 * Configuration of app specific parameters.
 */
public class SettingsConfigFragment extends Fragment {
    public static final int REQUEST_LANG = 0;

    // Times in seconds.
    public static final int MAX_TIME = 8 * 60;
    public static final int MIN_TIME = 1 * 60;
    public static final int DEFAULT_TIME = 4 * 60;
    private int showerLen;

    TextToSpeech tts;

    SoundGenerator soundGenerator;

    TextView textViewChronometer;
    SeekBar seekBarTime;
    TextView textViewLanguage;

    UserConfig config;

    String toSpeak = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings_config, container, false);

        UserController controller = UserController.getInstance(this.getActivity());
        UserProfile user = controller.getActiveProfile();
        this.config = user.getConfig();
        this.showerLen = this.config.getShowerLenInSeconds();

        this.soundGenerator = new SoundGenerator();

        Typeface typeFace = Fonts.getLetsGoDigital(this.getContext().getApplicationContext());
        this.textViewChronometer = (TextView) rootView.findViewById(R.id.textViewChronometer);
        this.textViewChronometer.setTypeface(typeFace);
        setChronometer();

        this.seekBarTime = (SeekBar) rootView.findViewById(R.id.seekBar);
        this.seekBarTime.setMax(MAX_TIME);
        this.seekBarTime.setProgress(this.showerLen);
        this.seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                showerLen = progresValue;
                setChronometer();

                double relativeProgress = (double) progresValue / MAX_TIME;
                soundGenerator.setNewFrequency(relativeProgress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                soundGenerator.startPlaying();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                soundGenerator.stopPlaying();
            }
        });

        CheckBox checkBoxAttitude = (CheckBox) rootView.findViewById(R.id.checkBoxAttitude);
        checkBoxAttitude.setChecked(this.config.isWithAttitude());
        checkBoxAttitude.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                config.setWithAttitude(isChecked);
            }
        });

        CheckBox checkBoxHumour = (CheckBox) rootView.findViewById(R.id.checkBoxHumour);
        checkBoxHumour.setChecked(this.config.isWithHumour());
        checkBoxHumour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                config.setWithHumour(isChecked);
            }
        });

        CheckBox checkBoxThoughts = (CheckBox) rootView.findViewById(R.id.checkBoxThoughts);
        checkBoxThoughts.setChecked(this.config.isWithThoughts());
        checkBoxThoughts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                config.setWithThoughts(isChecked);
            }
        });

        RelativeLayout layoutProfileLang = (RelativeLayout) rootView.findViewById(R.id.layoutLanguage);
        layoutProfileLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), ChooseLanguageActivity.class), REQUEST_LANG);
            }
        });

        this.textViewLanguage = (TextView) rootView.findViewById(R.id.textViewLanguage);
        int lang = this.config.getLanguage();
        this.textViewLanguage.setText(Languages.getName(lang));

        return rootView;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        switch (requestCode) {
            case REQUEST_LANG:
                // Make sure the request was successful
                if (resultCode == Activity.RESULT_OK) {
                    // The user picked a language. The object will have been updated.
                    final int lang = this.config.getLanguage();
                    final String langName = Languages.getName(lang);
                    this.textViewLanguage.setText(langName);
                    this.toSpeak = langName;
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onResume(){
        // Disable keyboard
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        this.tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // onResume can be called due to returning from selecting the language.
                // Play this selected language.
                if (toSpeak != null) {
                    int lang = config.getLanguage();
                    Locale locale = Languages.getLocale(lang);
                    tts.setLanguage(locale);
                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    toSpeak = null;
                }
            }
        });
        super.onResume();
    }

    @Override
    public void onPause() {
        if (this.tts != null) {
            this.tts.stop();
            this.tts.shutdown();
            this.tts = null;
        }
        super.onPause();
    }


    /**
     * Convert from seconds to seconds rounded to the nearest 30 seconds.
     * 0 to 15 is always rounded up to 30 seconds.
     *
     * @return rounded seconds.
     */
    private int roundTime() {
        int temp = this.showerLen / 15;
        temp++;
        temp /= 2;
        temp *= 30;
        if (temp < MIN_TIME) {
            temp = MIN_TIME;
        }
        return temp;
    }

    private void setChronometer() {
        int roundedTime = roundTime();
        int minutes = roundedTime / 60;
        int seconds = roundedTime - minutes * 60;
        this.textViewChronometer.setText(String.format("%02d:%02d", minutes, seconds));

        this.config.setShowerLenInSeconds(roundedTime);
    }
}



//StringBuffer buf = new StringBuffer();
//                Set<Locale> localeSet = tts.getAvailableLanguages();
//                Set<Voice> voicesSet = tts.getVoices();
//                if (localeSet == null) {
//                    buf.append("No locales\n");
//                } else {
//                    for (Locale locale: localeSet) {
//                        buf.append("Locale: " + locale.toString() + "\n");
//                    }
//                }
//                if (voicesSet == null) {
//                    buf.append("No voices\n");
//                } else {
//                    for (Voice voice : voicesSet) {
//                        buf.append("Voice: " + voice.toString() + "\n");
//                    }
//                }


//        Intent checkTTSIntent = new Intent();
//        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
//        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);


// let's assume tts is already inited at this point:
//        Locale[] locales = Locale.getAvailableLocales();
//        if (locales.length == 0) {
//            buf.append("No locales\n");
//        }
//        List<Locale> localeList = new ArrayList<>();
//        for (Locale loc: locales) {
//            int availability = tts.isLanguageAvailable(loc);
//            switch (availability) {
//                case TextToSpeech.LANG_COUNTRY_AVAILABLE:
//                    buf.append("Locale " + loc.toString() + " LANG_COUNTRY_AVAILABLE\n");
//                    localeList.add(loc);
//                    break;
//                case TextToSpeech.LANG_AVAILABLE:
//                    buf.append("Locale " + loc.toString() + " LANG_AVAILABLE\n");
//                    break;
//                case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
//                    buf.append("Locale " + loc.toString() + " LANG_COUNTRY_VAR_AVAILABLE\n");
//                    break;
//                case TextToSpeech.LANG_MISSING_DATA:
//                    buf.append("Locale " + loc.toString() + " LANG_MISSING_DATA\n");
//                    break;
//                case TextToSpeech.LANG_NOT_SUPPORTED:
//                    //buf.append("Locale " + loc.toString() + " LANG_NOT_SUPPORTED\n");
//                    break;
//                default:
//                    buf.append("Locale " + loc.toString() + " Unknown: " + availability + "\n");
//                    break;
//            }
//        }
//
//// at this point the localeList object will contain
//// all available languages for Text to Speech
//        textView.setText(buf.toString());
