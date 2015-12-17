package com.duang.easyecard.GlobalData;

import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Application;

public class HttpClientData extends Application {

	private HttpClient httpClient;
	// private List<Cookie> cookies;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		httpClient = new DefaultHttpClient();
		// cookies = new List<Cookie>();
		super.onCreate();
	}
	
	public HttpClient getHttpClient() {
		return httpClient;
	}
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
}
