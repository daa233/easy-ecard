package com.duang.easyecard.Activities;

import com.duang.easyecard.R;
import com.duang.easyecard.Utils.TabListener;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;

public class ManageTradingInquiryActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_trading_inquiry);
		
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// 添加Tab选项
		Tab tab = actionBar.newTab()
				.setText("历史流水")
				.setTabListener(
						new TabListener<ManageTradingInquiryHistoryFragment>(
								this, "历史流水", ManageTradingInquiryHistoryFragment.class));
		actionBar.addTab(tab);
		
		tab = actionBar.newTab()
				.setText("当日流水")
				.setTabListener(
						new TabListener<ManageTradingInquiryDayFragment>(
								this, "当日流水", ManageTradingInquiryDayFragment.class));
		actionBar.addTab(tab);
		
		tab = actionBar.newTab()
				.setText("当月流水")
				.setTabListener(
						new TabListener<ManageTradingInquiryMonthFragment>(
								this, "当月流水", ManageTradingInquiryMonthFragment.class));
		actionBar.addTab(tab);
	}

}