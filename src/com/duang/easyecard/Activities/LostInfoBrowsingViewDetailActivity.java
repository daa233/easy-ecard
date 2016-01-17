package com.duang.easyecard.Activities;

import org.apache.http.client.HttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.duang.easyecard.R;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Models.LostInfo;
import com.duang.easyecard.Utils.HttpUtil;
import com.duang.easyecard.Utils.LogUtil;
import com.duang.easyecard.Utils.HttpUtil.HttpCallbackListener;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LostInfoBrowsingViewDetailActivity extends BaseActivity {
	
	private TextView nameText;
	private TextView stuIdText;
	private TextView accountText;
	private TextView contactText;
	private TextView publishTimeText;
	private TextView lostPlaceText;
	private TextView descriptionText;
	private TextView stateText;
	private TextView foundTimeText;
	private Button sendMessageButton;
	
	private final int GET_SUCCESS_RESPONSE = 200;
	private final int GOT_DETAIL_DATA = 201;
	private final int NETWORK_ERROR = 404;
	
	private LostInfo lostInfo;
	private HttpClient httpClient;
	private String responseString;
	private String lostPlace;
	private String description;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(
				R.layout.activity_lost_and_found_info_browsing_view_detail);
		// 显示返回按钮
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Intent intent = this.getIntent();
		lostInfo = (LostInfo) intent.getSerializableExtra("LostInfo");
		setTitle("失卡招领详细信息——No." + lostInfo.getId());
		initView();
		initData();
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_SUCCESS_RESPONSE:
				// 已成功得到响应数据responseString
				LogUtil.d("responseString", responseString);
				new JsoupHtmlData().execute();
				break;
			case GOT_DETAIL_DATA:
				// 获得了“丢失地点”和“说明”
				lostPlaceText.setText(lostPlace);
				descriptionText.setText(description);
				break;
			case NETWORK_ERROR:
				// 网络错误
				Toast.makeText(LostInfoBrowsingViewDetailActivity.this,
						"网络错误",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};
	private void initView() {
		nameText = (TextView) findViewById(
				R.id.lost_info_browsing_view_detail_name);
		stuIdText = (TextView) findViewById(
				R.id.lost_info_browsing_view_detail_stu_id);
		accountText = (TextView) findViewById(
				R.id.lost_info_browsing_view_detail_account);
		contactText = (TextView) findViewById(
				R.id.lost_info_browsing_view_detail_contact);
		publishTimeText = (TextView) findViewById(
				R.id.lost_info_browsing_view_detail_publish_time);
		lostPlaceText = (TextView) findViewById(
				R.id.lost_info_browsing_view_detail_lost_place);
		descriptionText = (TextView) findViewById(
				R.id.lost_info_browsing_view_detail_description);
		stateText = (TextView) findViewById(
				R.id.lost_info_browsing_view_detail_state);
		foundTimeText = (TextView) findViewById(
				R.id.lost_info_browsing_view_detail_found_time);
		sendMessageButton = (Button) findViewById(
				R.id.lost_info_browsing_view_detail_send_msg_btn);
		// 按钮的点击事件
		sendMessageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳转到发送信息界面
				
			}
		});
	}
	// 初始化数据
	private void initData() {
		// 获得全局变量httpClient
		MyApplication myApp = (MyApplication) getApplication();
		httpClient = myApp.getHttpClient();
		// 从Intent传递的lostInfo对象获取信息
		nameText.setText(lostInfo.getName());
		stuIdText.setText(lostInfo.getStuId());
		accountText.setText(lostInfo.getAccount());
		contactText.setText(lostInfo.getContact());
		publishTimeText.setText(lostInfo.getPublishTime());
		stateText.setText(lostInfo.getState());
		foundTimeText.setText(lostInfo.getFoundTime());
		sendGETRequest();
	}
	// 发送GET请求
	private void sendGETRequest() {
		// 组装Url
		UrlConstant.cardLossViewDetailId = lostInfo.getId();
		HttpUtil.sendGetRequest(httpClient,
			UrlConstant.getCardLossInfoViewDetail(),
			new HttpCallbackListener() {
				@Override
				public void onFinish(String response) {
					// 成功响应
					responseString = response;
					// 发送消息到线程，已得到响应数据responseString
					Message message = new Message();
					message.what = GET_SUCCESS_RESPONSE;
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
	// 解析响应数据
	private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			// 解析返回的responseString
			Document doc = null;
			try {
				doc = Jsoup.parse(responseString);
				for (Element ps : doc.select("p[class=heightauto]")) {
					lostPlace = ps.ownText();
				}
				for (Element ps : doc.select("p[class=heightauto clear]")) {
					description = ps.ownText();
				}
				Message message = new Message();
				message.what = GOT_DETAIL_DATA;
				handler.sendMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	// 返回键的点击
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return false;
	}
}
