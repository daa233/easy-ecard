package com.duang.easyecard.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.duang.easyecard.R;

public class MessagesCreateNoticeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_create_notice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.messages_create_notice_toolbar);
        setSupportActionBar(toolbar);

    }

}
