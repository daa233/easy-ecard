package com.duang.easyecard.Activities;

import com.duang.easyecard.Utils.ActivityCollector;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("BaseActivity", getClass().getSimpleName());
		ActivityCollector.addActivity(this);
	}
	
	@Override
	protected void onDestroy()	{
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
}
