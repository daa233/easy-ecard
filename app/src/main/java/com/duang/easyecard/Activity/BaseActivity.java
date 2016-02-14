package com.duang.easyecard.Activity;

import com.duang.easyecard.Util.ActivityCollector;
import com.duang.easyecard.Util.LogUtil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("BaseActivity", getClass().getSimpleName());
		ActivityCollector.addActivity(this);
	}
	
	@Override
	protected void onDestroy()	{
		super.onDestroy();
		ActivityCollector.removeActivity(this);
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
