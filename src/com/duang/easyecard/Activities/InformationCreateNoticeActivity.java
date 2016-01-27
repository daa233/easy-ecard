package com.duang.easyecard.Activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.duang.easyecard.R;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Utils.HttpUtil;
import com.duang.easyecard.Utils.HttpUtil.HttpCallbackListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class InformationCreateNoticeActivity extends BaseActivity {

	private EditText contactsEditText;
	private EditText titleEditText;
	private EditText contextEditText;
	private Button sendButton;
	private LinearLayout contactsInputParent;

	private final int RESPONSE_SUCCESS = 200;
	private final int NETWORK_ERROR = 404;
	
	private String noticeContacts;
	private String noticeTitle;
	private String noticeContext;
	private String userSplit = "|";
	private HttpClient httpClient;
	private String responseString;
	private String queryCondition;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information_create_notice);
		initView();
	}

	private void initView() {
		// 实例化控件
		contactsEditText = (EditText) findViewById(
				R.id.information_create_notice_contacts_input);
		titleEditText = (EditText) findViewById(
				R.id.information_create_notice_title_input);
		contextEditText = (EditText) findViewById(
				R.id.information_create_notice_context_input);
		sendButton = (Button) findViewById(
				R.id.information_create_notice_send_button);
		contactsInputParent = (LinearLayout) findViewById(
				R.id.information_create_notice_contacts_linear_layout);
		
		// 添加两个控件
		contactsInputParent.addView(addContactButton("吴彦祖(13020021005)"));
		contactsInputParent.addView(addContactButton("吴彦祖(13020021005)"));
		
	}
	
	private void initData() {
		// 获得全局变量httpClient
		MyApplication myApp = (MyApplication) getApplication();
		httpClient = myApp.getHttpClient();
		// 发送POST请求
		sendPOSTRequestToSend();
	}

	// 处理从线程中传递出来的消息
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			}
		}
	};
	
	// 发送POST请求，来创建消息
	private void sendPOSTRequestToSend() {
		// 装填POST数据
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userids", noticeContacts));
		params.add(new BasicNameValuePair("title", noticeTitle));
		params.add(new BasicNameValuePair("context", noticeContext));
		HttpUtil.sendPostRequest(httpClient, UrlConstant.CREATE_NOTICE, params,
				new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				// 成功响应
				responseString = response;
				Message message = new Message();
				message.what = RESPONSE_SUCCESS;
				handler.sendMessage(message);
			}
			@Override
			public void onError(Exception e) {
				// 网络错误
				Message message = new Message();
				message.what = NETWORK_ERROR;
				handler.sendMessage(message);
			}
		});
	}
	
	// 发送POST请求，查找用户
	private void sendPOSTRequestToQuery() {
		// 装填POST数据
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("condition", queryCondition));
		HttpUtil.sendPostRequest(httpClient, UrlConstant.QUERY_USER, params,
				new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				// 成功响应
				responseString = response;
				Message message = new Message();
				message.what = RESPONSE_SUCCESS;
				handler.sendMessage(message);
			}
			@Override
			public void onError(Exception e) {
				// 网络错误
				Message message = new Message();
				message.what = NETWORK_ERROR;
				handler.sendMessage(message);
			}
		});
	}
	
	// 动态添加Button
	private View addContactButton(String str) {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		LinearLayout view = new LinearLayout(this);
		ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		Button button = new Button(this);
		button.setLayoutParams(vlp);
		button.setText(str);
		view.addView(button);
		return view;
	}
}
