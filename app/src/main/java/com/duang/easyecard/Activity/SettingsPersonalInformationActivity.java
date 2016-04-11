package com.duang.easyecard.Activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.rey.material.widget.ProgressView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import cz.msebera.android.httpclient.Header;

public class SettingsPersonalInformationActivity extends BaseActivity
        implements UITableView.ClickListener {

    FloatingActionButton mFab;
    ProgressView mProgressView;
    UITableView mTableView;

    private AsyncHttpClient httpClient;
    private String response;
    private List<String> dataList;

    private final String TAG = "SettingsPersonalInformationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_personal_information);
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_personal_information_toolbar);
        setSupportActionBar(toolbar);
        // 显示Back按钮
        setDisplayHomeButton();
        // 实例化控件
        mFab = (FloatingActionButton) findViewById(R.id.settings_personal_information_fab);
        mProgressView = (ProgressView) findViewById(
                R.id.settings_personal_information_progress_view);
        mTableView = (UITableView) findViewById(R.id.settings_personal_information_table_view);
        // 监听点击事件
        mTableView.setClickListener(this);
    }

    private void initData() {
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        // 初始化数据列表
        dataList = new ArrayList<>();
        // 发送GET请求
        sendGETRequest();
    }

    // 发送GET请求
    private void sendGETRequest() {
        httpClient.get(UrlConstant.USER_INFO, new AsyncHttpResponseHandler() {
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
            }
        });
    }

    // UITableView的点击事件
    @Override
    public void onClick(int i) {
        LogUtil.d(TAG, "UITableView onClick at " + i);
        // 跳转到修改个人信息Activity，或者弹出修改信息对话框
    }

    // 解析响应数据
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            LogUtil.d(TAG, "Start Jsoup: In doInBackground.");
            // 解析返回的responseString
            Document doc;
            try {
                doc = Jsoup.parse(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // 构造UItableView的列表项，传入title和content和clickable
    private void generateCustomItem(UITableView tableView, String title, String content,
                                    boolean clickable) {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout relativeLayout = (RelativeLayout) mLayoutInflater.inflate(
                R.layout.item_table_view_custom, null);
        TextView titleText = (TextView) relativeLayout.getChildAt(0);
        titleText.setText(title);
        TextView contentText = (TextView) relativeLayout.getChildAt(1);
        contentText.setText(content);
        ViewItem v = new ViewItem(relativeLayout);
        v.setClickable(clickable);  // Set clickable
        tableView.addViewItem(v);
    }
}
