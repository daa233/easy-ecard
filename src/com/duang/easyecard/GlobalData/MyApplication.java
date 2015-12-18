package com.duang.easyecard.GlobalData;

import org.apache.http.client.HttpClient;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
	
	private static Context context;
	private static HttpClient httpClient;
	
	@Override
	public void onCreate() {
		context = getApplicationContext();
		super.onCreate();
	}

	public static Context getContext() {
		return context;
	}
	
	public HttpClient getHttpClient() {
		return httpClient;
	}
	public void setHttpClient(HttpClient httpClient) {
		MyApplication.httpClient = httpClient;
	}

}
