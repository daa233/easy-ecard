package com.duang.easyecard.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.duang.easyecard.R;

public class MessagesNoticeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_notice_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.messages_notice_detail_toolbar);
        setSupportActionBar(toolbar);
    }

}
