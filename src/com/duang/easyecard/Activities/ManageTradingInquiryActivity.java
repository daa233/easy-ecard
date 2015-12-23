package com.duang.easyecard.Activities;

import com.duang.easyecard.R;
import com.duang.easyecard.Activities.ManageTradingInquiryHistoryFragment.MyCallback;
import com.duang.easyecard.Utils.TabListener;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ManageTradingInquiryActivity extends BaseActivity 
implements MyCallback{
	
	// HISTORY_STATE用于区别Tab(0)加载的Fragment类型 {0:select time; 1:result}
	private static int HISTORY_STATE = 0;  // 初始为选择时间Fragment
	
	protected static String startTime;
	protected static String endTime;
	
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
								this, "历史流水",
								ManageTradingInquiryHistoryFragment.class));
		actionBar.addTab(tab);
		
		tab = actionBar.newTab()
				.setText("当日流水")
				.setTabListener(
						new TabListener<ManageTradingInquiryDayFragment>(
								this, "当日流水",
								ManageTradingInquiryDayFragment.class));
		actionBar.addTab(tab);
		
		tab = actionBar.newTab()
				.setText("当月流水")
				.setTabListener(
						new TabListener<ManageTradingInquiryMonthFragment>(
								this, "当月流水",
								ManageTradingInquiryMonthFragment.class));
		actionBar.addTab(tab);
	}

	/*
	 * (non-Javadoc)
	 * @see com.duang.easyecard.Activities.ManageTradingInquiryHistoryFragment
	 * .MyCallback#onBtnClick(android.view.View)
	 * 在ManageTradingInquiryHistoryFragment中定义的Callback接口
	 * 用于返回Fragment中的按钮点击事件，并重新加载该Tab
	 */
	@Override
	public void onBtnClick(View v) {
		// 向搜索结果Fragment传递startTime和endTime
		TextView startTimeText = (TextView) findViewById(R.id.history_inquiry_start_time);
		TextView endTimeText = (TextView) findViewById(R.id.history_inquiry_end_time);
		startTime = startTimeText.getText().toString();
		endTime = endTimeText.getText().toString();
		// 更换Tab(0)的布局
		ActionBar actionBar = getActionBar();
		Tab tab = actionBar.getTabAt(0);
		actionBar.removeTabAt(0);
		tab.setText("历史流水")
		.setTabListener(new TabListener<ManageTradingInquiryHistoryResultFragment>(
				this, "历史流水", ManageTradingInquiryHistoryResultFragment.class));
		actionBar.addTab(tab, 0);
		actionBar.selectTab(tab);
		
		HISTORY_STATE = 1;  // 状态置为搜索结果Fragment
	}
	
	// 监听Back按钮的点击
	public boolean onKeyDown(int keyCode, KeyEvent event)	{
		if (keyCode == KeyEvent.KEYCODE_BACK)	{
			doBack();
			return false;
		}
		return false;
	}
	// 根据判断决定返回键的响应
	private void doBack() {
		// 先判断当前Tab位置
		int tabPosition;
		ActionBar actionBar = getActionBar();
		Tab tab = actionBar.getSelectedTab();
		tabPosition = tab.getPosition();
		// 位于“历史流水”Tab， 视其状态选择是否退出
		if (tabPosition == 0) {
			// 位于选择时间界面，直接退出
			if (HISTORY_STATE == 0) {
				finish();
			} else {
				// 位于搜索结果界面，回退到选择时间界面
				actionBar.removeTabAt(0);
				tab.setText("历史流水")
				.setTabListener(new TabListener
						<ManageTradingInquiryHistoryFragment>(this, "历史流水",
						ManageTradingInquiryHistoryFragment.class));
				actionBar.addTab(tab, 0);
				actionBar.selectTab(tab);
				// 将搜索界面选择的时间赋给startTime和endTime
				// Button btn = (Button) findViewById(R.id.fragment_trading_inquiry_history_result_btn);
				// String str = btn.getText().toString();
				// Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
				HISTORY_STATE = 0;  // 状态置为选择时间Fragment
			}
		} else {
			// 不位于“历史流水”Tab，也直接退出
			finish();
		}
	}

	// 监听MenuItem的点击事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			doBack();
			break;
		default:
			break;
		}
		return false;
	}
}