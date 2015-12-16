package com.duang.easyecard.Utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.CoreConnectionPNames;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

public class ImageUtil {

	public static void onLoadImage(final String bitmapUrl,
			final HttpClient httpClient,
			final OnLoadImageListener onLoadImageListener) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				onLoadImageListener.OnLoadImage((Bitmap) msg.obj, null);
			}
		};
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String imageUrl = bitmapUrl;
				HttpGet request = null;
				HttpResponse response = null;
				
				try {
					request = new HttpGet(imageUrl);
					httpClient.getParams().setParameter(CoreConnectionPNames.
							CONNECTION_TIMEOUT, 1000 * 5);  // 链接超时
					httpClient.getParams().setParameter(CoreConnectionPNames.
							SO_TIMEOUT, 1000 * 5);  // 读取超时
					response = httpClient.execute(request);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						InputStream inputStream = response.getEntity().getContent();
						Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
						Message msg = new Message();
						msg.obj = bitmap;
						handler.sendMessage(msg);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public interface OnLoadImageListener {
		public void OnLoadImage(Bitmap bitmap, String bitmapPath);
	}
}
