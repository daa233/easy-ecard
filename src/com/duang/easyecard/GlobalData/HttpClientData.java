package com.duang.easyecard.GlobalData;

import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;
import android.app.Application;

public class HttpClientData extends Application {

	public HttpClient httpClient;
	public List<Cookie> cookies;
}
