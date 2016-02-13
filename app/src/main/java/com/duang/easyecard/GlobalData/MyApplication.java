package com.duang.easyecard.GlobalData;

import android.app.Application;
import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

public class MyApplication extends Application {
	
	private static Context context;
	private static AsyncHttpClient httpClient;
	
	@Override
	public void onCreate() {
		context = getApplicationContext();
		super.onCreate();
	}

	public static Context getContext() {
		return context;
	}
	
	public AsyncHttpClient getHttpClient() {
		return httpClient;
	}
	public void setHttpClient(AsyncHttpClient httpClient) {
		MyApplication.httpClient = httpClient;
	}

}
