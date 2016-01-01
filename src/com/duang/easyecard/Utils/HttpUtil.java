package com.duang.easyecard.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

public class HttpUtil {
	// 发送GET请求
	public static void sendGetRequest(final HttpClient httpClient,
			final String address,
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 创建一个HttpGet对象
				HttpGet httpGetRequest = new HttpGet(address);
				try {
					// 发送GET请求
					HttpResponse httpResponse = 
							httpClient.execute(httpGetRequest);
					StringBuffer responseString = new StringBuffer();
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = httpResponse.getEntity();
						if (entity != null) {
							// 读取服务器响应
							BufferedReader br = new BufferedReader(
								new InputStreamReader(entity.getContent()));
							String line = null;
							while ((line = br.readLine()) != null) {
								responseString.append(line);
							}
						}
					}
					if (listener != null) {
						// Callback onFinish()
						listener.onFinish(responseString.toString());
					}
				} catch (Exception e) {
					if (listener != null) {
						// Callback onError()
						listener.onError(e);
					}
				}
			}
		}).start();
	}
	// 发送POST请求
	public static void sendPostRequest(final HttpClient httpClient, 
		final String address,
		final List<NameValuePair> params,
		final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 创建一个HttpPost对象
				HttpPost httpPost = new HttpPost(address);
				try {
					UrlEncodedFormEntity entity =
							new UrlEncodedFormEntity(params, "utf-8");
					httpPost.setEntity(entity);
					// 发送POST请求
					HttpResponse httpResponse = httpClient.execute(httpPost);
					// 如果服务器成功地返回响应
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						// 请求和响应都成功了
		        		String responseString = EntityUtils
		        				.toString(httpResponse.getEntity());
		        		// Callback onFinish
		        		if (listener != null) {
		        			listener.onFinish(responseString);
		        		}
		        	}
				} catch (Exception e) {
					if (listener != null) {
						// Callback onError()
						listener.onError(e);
					}
				}
			}
		}).start();
	}
	// 回调接口
	public interface HttpCallbackListener {
		
		void onFinish(String response);
		
		void onError(Exception e);
	}
	

}
