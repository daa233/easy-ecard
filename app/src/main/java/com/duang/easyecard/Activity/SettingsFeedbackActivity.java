package com.duang.easyecard.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.duang.easyecard.R;

public class SettingsFeedbackActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_feedback_toolbar);
        setSupportActionBar(toolbar);
        setDisplayHomeButton();
    }

}
