package com.duang.easyecard.Activity;

import android.os.Bundle;

import com.duang.easyecard.R;

public class ManageReportLossActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_report_loss);
        // 显示home按钮
        setupActionBar();
    }
}
