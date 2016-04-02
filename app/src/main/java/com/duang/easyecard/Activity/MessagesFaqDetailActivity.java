package com.duang.easyecard.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.Model.FaqItem;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.rey.material.widget.ProgressView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cz.msebera.android.httpclient.Header;

public class MessagesFaqDetailActivity extends BaseActivity {

    private TextView titleTextView;
    private TextView publishTimeTextView;
    private TextView contentTextView;
    private ProgressView progressView;

    private FaqItem faqItem;
    private String publishTime;
    private String content;
    private AsyncHttpClient httpClient;
    private String address;
    private String response;
    private final String TAG = "MessagesFaqDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_faq_detail);
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.messages_faq_detail_toolbar);
        setSupportActionBar(toolbar);
        setDisplayHomeButton();
        // 实例化控件
        titleTextView = (TextView) findViewById(R.id.messages_faq_detail_title);
        publishTimeTextView = (TextView) findViewById(R.id.messages_faq_detail_publish_time);
        contentTextView = (TextView) findViewById(R.id.messages_faq_detail_content);
        progressView = (ProgressView) findViewById(R.id.messages_faq_detail_progress_view);
    }

    private void initData() {
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        // 获得Intent传递的FaqItem对象
        Intent intent = this.getIntent();
        faqItem = (FaqItem) intent.getSerializableExtra("FaqItem");
        address = faqItem.getDetailAddress();
        // 设置问题标题
        titleTextView.setText(faqItem.getTitle());
        // 根据类型设置Activity标题
        switch (faqItem.getType()) {
            case 0:
                setTitle(getString(R.string.title_activity_messages_faq_detail) +
                        getString(R.string.card_manage));
                break;
            case 1:
                setTitle(getString(R.string.title_activity_messages_faq_detail) +
                        getString(R.string.application_center));
                break;
            case 2:
                setTitle(getString(R.string.title_activity_messages_faq_detail) +
                        getString(R.string.account_secure));
                break;
            case 3:
                setTitle(getString(R.string.title_activity_messages_faq_detail) +
                        getString(R.string.online_pay));
                break;
            default:
                LogUtil.e(TAG, "Unexpect type.");
                break;
        }
        // 发送GET请求，获取问题发布时间和内容
        sendGETRequest();
    }

    // 发送GET请求，获取问题发布时间和内容
    private void sendGETRequest() {
        httpClient.get(address, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 响应成功
                response = new String(responseBody);
                // 解析响应数据
                new JsoupHtmlData().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // 网络错误
                LogUtil.e(TAG, "Network error.");
                Toast.makeText(MyApplication.getContext(), getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
                // 隐藏ProgressView
                progressView.setVisibility(View.GONE);
                error.printStackTrace();
            }
        });
    }

    // 解析响应数据
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // 解析返回的responseString
            Document doc;
            try {
                doc = Jsoup.parse(response);
                // 发布时间
                for (Element span : doc.select("span")) {
                    publishTime = span.text();
                }
                // 问题内容
                for (Element p : doc.select("p")) {
                    content = p.text();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // 在UI中显示数据
            publishTimeTextView.setText(publishTime);
            contentTextView.setText(content);
            // 隐藏ProgressView
            progressView.setVisibility(View.GONE);
        }
    }
}
