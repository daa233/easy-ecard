package com.duang.easyecard.Activities;

import com.duang.easyecard.R;

import android.os.Bundle;
import android.view.MenuItem;

public class LostAndFoundRegistrationActivity extends BaseActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_and_found_registration);
		// 显示返回按钮
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	// 返回键的点击
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return false;
	}
}
