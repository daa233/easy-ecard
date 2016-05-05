package com.duang.easyecard.Activity;

import com.duang.easyecard.Util.ActivityCollector;
import com.duang.easyecard.Util.LogUtil;
import com.pgyersdk.crash.PgyCrashManager;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("BaseActivity", getClass().getSimpleName());
		PgyCrashManager.register(this);  // 蒲公英Crash分析
		ActivityCollector.addActivity(this);
	}
	
	@Override
	protected void onDestroy()	{
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	protected void setDisplayHomeButton() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			// Show the Up button in the action bar.
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}
	// 菜单项选择
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
