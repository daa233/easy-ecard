package com.duang.easyecard.Activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.rey.material.widget.ProgressView;

import cz.msebera.android.httpclient.Header;
import us.feras.mdv.MarkdownView;

public class SettingsAboutActivity extends BaseActivity {

    private static final String TAG = "SettingsAboutActivity";
    private static final String ABOUT_URL =
            "https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/about_in_app.md";
    private MarkdownView markdownview;
    private ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_about);
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_about_toolbar);
        setSupportActionBar(toolbar);
        // 显示Back按钮
        setDisplayHomeButton();
        // 实例化控件
        markdownview = (MarkdownView) findViewById(R.id.settings_about_markdown_view);
        progressView = (ProgressView) findViewById(R.id.settings_about_progress_view);
    }

    private void initData() {
        // 获取Markdown文本
        new AsyncHttpClient().get(ABOUT_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 响应成功
                LogUtil.d(TAG, "Success to get response.");
                if (responseBody != null && responseBody.length > 0) {
                    // 显示Markdown文本
                    markdownview.loadMarkdown(new String(responseBody));
                    markdownview.setVisibility(View.VISIBLE);
                } else {
                    // 显示未找到图片
                    Glide
                            .with(MyApplication.getContext())
                            .load(R.drawable.nothing_founded_404)
                            .into((ImageView) findViewById(
                                    R.id.settings_about_nothing_founded_image_view));
                }
                progressView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // 网络错误
                Toast.makeText(MyApplication.getContext(), getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
                LogUtil.e(TAG, "Network error.");
                error.printStackTrace();
                // 显示未找到图片
                Glide
                        .with(MyApplication.getContext())
                        .load(R.drawable.nothing_founded_404)
                        .into((ImageView) findViewById(
                                R.id.settings_about_nothing_founded_image_view));
            }
        });
    }

}
