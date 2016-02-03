package com.duang.easyecard.Activity;

import com.duang.easyecard.Util.ActivityCollector;
import com.duang.easyecard.Util.LogUtil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
}
