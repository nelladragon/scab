// Copyright (C) 2017 Peter Robinson and the Smart Contract Application Browser contributors.
package com.nelladragon.common;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nelladragon.common.util.AppVersion;
import com.nelladragon.scab.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FeedbackActivity extends AppCompatActivity {
    CheckBox checkBoxDevice;
    CheckBox checkBoxUser;
    CheckBox checkBoxLog;
    EditText editTextFeedback;

    FeedbackInfo feedbackInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CommonLib commonLib = CommonLib.getInstance();
        this.feedbackInfo = commonLib.getFeedbackInfo();

        this.checkBoxDevice = (CheckBox) findViewById(R.id.checkboxDeviceInfo);
        this.checkBoxUser = (CheckBox) findViewById(R.id.checkboxUserInfo);
        this.checkBoxLog = (CheckBox) findViewById(R.id.checkboxLog);
        this.editTextFeedback = (EditText) findViewById(R.id.editTextFeedback);

        this.editTextFeedback.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    handled = true;
                }
                return handled;
            }
        });

        Button buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }



    private void sendMessage() {
        Resources res = getResources();
        String emailIntro1 = res.getString(R.string.feedback_email_intro1);
        String emailIntro2 = res.getString(R.string.feedback_email_intro2);
        String emailRedacted = res.getString(R.string.feedback_email_redacted);
        String emailDeviceInfo = res.getString(R.string.feedback_email_device_info);
        String emailUserInfo = res.getString(R.string.feedback_email_user_info);
        String emailLog = res.getString(R.string.feedback_email_log);
        String emailProblem = res.getString(R.string.feedback_email_problem);

        String emailAddress = res.getString(R.string.feedback_email_address);
        String emailSubject1 = res.getString(R.string.feedback_email_subject1);
        String emailSubject2 = res.getString(R.string.feedback_email_subject2);
        String appName = res.getString(R.string.app_name);

        String emailIntentInfo = res.getString(R.string.feedback_email_intentinfo);
        String emailIntentError = res.getString(R.string.feedback_email_intenterror);

        String verstionText = String.format(res.getString(R.string.version), AppVersion.getVersion(this));

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd':'HHmm z");
        String date = dateFormat.format(now);

        String emailSubject = emailSubject1 + appName + emailSubject2 + date;
        String emailIntro = emailIntro1 + appName + emailIntro2 + verstionText;

        StringBuffer buf = new StringBuffer();
        buf.append(emailIntro);
        buf.append('\n');
        buf.append('\n');

        buf.append(emailProblem);
        buf.append(editTextFeedback.getText().toString());
        buf.append('\n');
        buf.append('\n');

        buf.append(emailDeviceInfo);
        if (checkBoxDevice.isChecked()) {
            buf.append(getDeviceInfo());
        } else {
            buf.append(emailRedacted);
        }
        buf.append('\n');
        buf.append('\n');

        buf.append(emailUserInfo);
        if (checkBoxUser.isChecked()) {
            buf.append(this.feedbackInfo.getProfileInfo());
        } else {
            buf.append(emailRedacted);
        }
        buf.append('\n');
        buf.append('\n');

        buf.append(emailLog);
        if (checkBoxLog.isChecked()) {
            buf.append(this.feedbackInfo.getAutoAnalysis());
        } else {
            buf.append(emailRedacted);
        }
        buf.append('\n');
        buf.append('\n');

        String emailContents = buf.toString();

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{emailAddress});
        i.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        i.putExtra(Intent.EXTRA_TEXT   , emailContents);
        try {
            startActivity(Intent.createChooser(i, emailIntentInfo));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(FeedbackActivity.this, emailIntentError, Toast.LENGTH_SHORT).show();
        }
    }


    public String getDeviceInfo() {
        StringBuffer buf = new StringBuffer();
        // Canonical version: 6.0, XYZ123
        buf.append(android.os.Build.VERSION.RELEASE);
        buf.append(',');
        buf.append(android.os.Build.ID);

        // Brand and model: Google, Nexus9
        buf.append(' ');
        buf.append(android.os.Build.BRAND);
        buf.append(',');
        buf.append(android.os.Build.MODEL);

        // Display information
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        int densityDpi = metrics.densityDpi;
        buf.append(' ');
        buf.append(heightPixels);
        buf.append('x');
        buf.append(widthPixels);
        buf.append(',');
        buf.append(densityDpi);
        return buf.toString();
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
