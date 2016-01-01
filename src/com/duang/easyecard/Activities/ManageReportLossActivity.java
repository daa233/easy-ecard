package com.duang.easyecard.Activities;

import com.duang.easyecard.R;

import android.os.Bundle;

public class ManageReportLossActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_loss);
		// 显示返回按钮
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
}