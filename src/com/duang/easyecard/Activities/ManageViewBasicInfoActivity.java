package com.duang.easyecard.Activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.duang.easyecard.R;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class ManageViewBasicInfoActivity extends BaseActivity {
	
	private TextView nameText;
	private TextView stuIdText;
	private TextView ecardIdText;
	private TextView balanceText;
	private TextView transitionText;
	private TextView reportLossStateText;
	private TextView freezeStateText;

	private String name;
	private String stuId;
	private String ecardId;
	private String balance;
	private String transition;
	private String reportLossState;
	private String freezeState;
	private String responseHtml;
	
	private List<String> stringList;
	
	private static final int RESPONSE_SUCCESS = 1;
	private static final int FINISH_STRING_LIST = 2;
	
	private ProgressDialog mProgressDialog;
	
	private HttpClient httpClient;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_view_basic_info);
		
		// 实例化控件
		nameText = (TextView) findViewById(R.id.view_basic_info_name_text);
		stuIdText = (TextView) findViewById(R.id.view_basic_info_stu_id_text);
		ecardIdText = (TextView) findViewById(R.id.view_basic_info_ecard_id_text);
		balanceText = (TextView) findViewById(R.id.view_basic_info_balance_text);
		transitionText = (TextView) findViewById(R.id.view_basic_info_transition_text);
		reportLossStateText = (TextView) findViewById(R.id.view_basic_info_report_loss_state_text);
		freezeStateText = (TextView) findViewById(R.id.view_basic_info_freeze_state_text);
		// 初始化数据
		initData();
	}
	
	// 初始化数据
	private void initData() {
		// 获得全局变量httpClient
		MyApplication myApp = (MyApplication) getApplication();
		httpClient = myApp.getHttpClient();
		// 发送请求
		sendRequest();
	}
	// 处理从线程中传递出来的消息
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RESPONSE_SUCCESS:
				// 确保responseHtml已成功赋值
				new JsoupHtmlData().execute();
				break;
			case FINISH_STRING_LIST:
				// 将数据填充到布局
				initView();
				break;
			default:
				break;
			}
		}
	};
	// 发送POST请求
	private void sendRequest() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 创建一个HttpPost对象
				HttpPost httpPost = new HttpPost(UrlConstant.BASIC_INFO);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				try {
					// 装填POST数据
					params.add(new BasicNameValuePair("needHeader", "false"));
					
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
					httpPost.setEntity(entity);
					// 执行POST请求
					HttpResponse httpResponse = httpClient.execute(httpPost);
					// 如果服务器成功地返回响应
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						responseHtml = EntityUtils.toString(httpResponse.getEntity());
						Log.d("responseHtml", responseHtml);
						Message message = new Message();
						message.what = RESPONSE_SUCCESS;
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}).start();
		
	}
	// 通过网站返回的html文本解析数据
	private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
		    Log.d("JsouphtmlData", "onPreExecute");
			super.onPreExecute();
			// Create a progressDialog
			mProgressDialog = new ProgressDialog(ManageViewBasicInfoActivity.this);
			// Set progressDialog title
			mProgressDialog.setTitle("从校园一卡通网站获取相关信息");
			// Set progressDialog message
			mProgressDialog.setMessage("正在加载并解析……");
			mProgressDialog.setIndeterminate(false);
			// Show progressDialog
			mProgressDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			Log.d("JsouphtmlData", "doInBackground.");
			// 解析返回的responseHtml
			Document doc = null;
			try {
				stringList = new ArrayList<String>();
				doc = Jsoup.parse(responseHtml);
				Elements es = doc.getElementsByTag("em");
				for (Element e : es) {
					stringList.add(e.text());
					Log.d("e", e.text());
				}
				Message message = new Message();
				message.what = FINISH_STRING_LIST;
				handler.sendMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Log.d("JsouphtmlData", "onPostExecute");
			// Close the progressDialog
			mProgressDialog.dismiss();
		}
	}
	
	// 填充布局
	private void initView() {
		Log.d("ViewBasicInfo", "initView");
		
		if (!stringList.isEmpty()) {
			// 将stringList的数据与变量对应
			name = stringList.get(0);
			stuId = stringList.get(1);
			ecardId = stringList.get(2);
			balance = stringList.get(3);
			transition = stringList.get(4);
			reportLossState = stringList.get(5);
			freezeState = stringList.get(6);
			// 对应变量值填充到控件
			nameText.setText(name);
			stuIdText.setText(stuId);
			ecardIdText.setText(ecardId);
			balanceText.setText(balance);
			transitionText.setText(transition);
			reportLossStateText.setText(reportLossState);
			freezeStateText.setText(freezeState);
		} else {
			Toast.makeText(this, "获取失败！", Toast.LENGTH_SHORT).show();
		}
	}

}
