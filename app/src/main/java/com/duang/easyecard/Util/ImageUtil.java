package com.duang.easyecard.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class ImageUtil {

    public static void onLoadImage(final String imageUrl,
                                   final AsyncHttpClient httpClient,
                                   final OnLoadImageListener onLoadImageListener) {
        httpClient.get(imageUrl, new AsyncHttpResponseHandler() {
            @Override
            // 获取成功
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody.length != 0) {
                    onLoadImageListener.OnLoadImage(responseBody, null);
                }
            }

            // 获取失败
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                LogUtil.e("ImageUtil", "Fail to get image.");
            }
        });
    }

    // 获取Bitmap后的回调接口
    public interface OnLoadImageListener {
        public void OnLoadImage(byte[] imageBytes, String bitmapPath);
    }
}
