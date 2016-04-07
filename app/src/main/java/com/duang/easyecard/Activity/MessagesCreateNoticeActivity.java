package com.duang.easyecard.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MessagesCreateNoticeActivity extends BaseActivity {

    private AsyncHttpClient httpClient;
    private String resposne;
    private String msgTitle;
    private String msgContent;
    private List<String> receiverList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_create_notice);
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.messages_create_notice_toolbar);
        setSupportActionBar(toolbar);
        setDisplayHomeButton();  // 显示Back按钮
    }

    private void initData() {
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
    }

    /**
     * 添加接收人
     * sendPOSTRequestToAddReceiver
     * @param condition 用户搜索条件
     * {"ret":true,"account":"1302xxxx005","name":"吴彦祖"}
     */
    private void sendPOSTRequestToAddReceiver(String condition) {
        RequestParams params = new RequestParams();
        params.add("condition", condition);
        httpClient.post(UrlConstant.QUERY_USER, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // 响应成功
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                // 网络错误
            }
        });
    }

    /**
     * 发送消息
     * sendPOSTRequestToCreateNotice
     * {"ret":true,"msg":"发送成功"}
     */
    private void sendPOSTRequestToCreateNotice() {
        RequestParams params = new RequestParams();
        params.add("context", "");
        params.add("revicerDept", "");
        params.add("sendDept", "");
        params.add("title", "");
        params.add("typeID", "");
        params.add("userids", "");
        params.add("UserSno", "");
        httpClient.post(UrlConstant.CREATE_NOTICE, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // 响应成功
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                // 网络错误
            }
        });
    }
}
