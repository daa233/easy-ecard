package com.duang.easyecard.Activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.duang.easyecard.R;
import com.duang.easyecard.Activities.ManageTradingInquiryHistoryFragment.MyCallback;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Utils.LogUtil;
import com.duang.easyecard.Utils.TabListener;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

public class ManageTradingInquiryActivity extends BaseActivity 
implements MyCallback{
	
	// HISTORY_STATE用于区别Tab(0)加载的Fragment类型 {0:select time; 1:result}
	private static int HISTORY_STATE = 0;  // 初始为选择时间Fragment
	
	protected static HttpClient httpClient;
	
	protected static int startYear, startMonthOfYear, startDayOfMonth;
	protected static int startDayOfWeek;
	protected static int endYear, endMonthOfYear, endDayOfMonth;
	protected static int endDayOfWeek;
	protected static String startTime;
	protected static String endTime;
	
	protected static ArrayList<HashMap<String, String>> historyArrayList;
	protected static ArrayList<HashMap<String, String>> dayArrayList;
	protected static ArrayList<HashMap<String, String>> monthArrayList;

	private final int POST_SUCCESS_RESPONSE = 200;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_trading_inquiry);

		initView();
		
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// 添加Tab选项
		Tab tab = actionBar.newTab()
				.setText("历史流水")
				.setTabListener(
						new TabListener<ManageTradingInquiryHistoryFragment>(
								this, "历史流水",
								ManageTradingInquiryHistoryFragment.class));
		actionBar.addTab(tab);
		
		tab = actionBar.newTab()
				.setText("当日流水")
				.setTabListener(
						new TabListener<ManageTradingInquiryDayFragment>(
								this, "当日流水",
								ManageTradingInquiryDayFragment.class));
		actionBar.addTab(tab);
		
		tab = actionBar.newTab()
				.setText("当月流水")
				.setTabListener(
						new TabListener<ManageTradingInquiryMonthFragment>(
								this, "当月流水",
								ManageTradingInquiryMonthFragment.class));
		actionBar.addTab(tab);
	}

	private void initData() {
		// 获得全局变量httpClient
		MyApplication myApp = (MyApplication) getApplication();
		httpClient = myApp.getHttpClient();
		// 发送POST信息，开启流水查询
		sendPostRequest();
	}
	// 处理从线程中传递出来的消息
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case POST_SUCCESS_RESPONSE:
				LogUtil.d("POST_SUCCESS_RESPONSE", msg.obj.toString());
				// 刷新全局httpClient
				MyApplication myApp = (MyApplication) getApplication();
				myApp.setHttpClient(httpClient);
				break;
			default:
				break;
			}
		}
	};
	// 发送POST请求
	private void sendPostRequest() {
		// 发送POST信息到TRJN_QUERY，开始流水查询
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 创建一个HttpPost对象
				HttpPost httpPost = new HttpPost(UrlConstant.TRJN_QUERY);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				try {
					params.add(new BasicNameValuePair("needHeader", "false"));
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
							params, "utf-8");
					httpPost.setEntity(entity);
					// 发送POST请求
					HttpResponse httpResponse = httpClient.execute(httpPost);
					// 如果服务器成功地返回响应
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						// 请求和响应都成功了
						LogUtil.d("POST", "Success!");
						// 获取返回的cookies
				        String httpResponseString = EntityUtils.toString(
				        		httpResponse.getEntity());
				        Message message = new Message();
						message.what = POST_SUCCESS_RESPONSE;
						message.obj = httpResponseString;
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/*
	 * (non-Javadoc)
	 * @see com.duang.easyecard.Activities.ManageTradingInquiryHistoryFragment
	 * .MyCallback#onBtnClick(android.view.View)
	 * 在ManageTradingInquiryHistoryFragment中定义的Callback接口
	 * 用于返回Fragment中的按钮点击事件，并重新加载该Tab
	 */
	@Override
	public void onBtnClick(View v) {
		// 更新startTime和endTime
		startTime = startYear + "-" + startMonthOfYear + "-" + startDayOfMonth;
		endTime = endYear + "-" + endMonthOfYear + "-" + endDayOfMonth;
		// 更换Tab(0)的布局
		ActionBar actionBar = getActionBar();
		Tab tab = actionBar.getTabAt(0);
		actionBar.removeTabAt(0);
		tab.setText("历史流水")
		.setTabListener(new TabListener<
				ManageTradingInquiryHistoryResultFragment>(
				this, "历史流水", ManageTradingInquiryHistoryResultFragment.class));
		actionBar.addTab(tab, 0);
		actionBar.selectTab(tab);
		
		HISTORY_STATE = 1;  // 状态置为搜索结果Fragment
	}
	
	// 监听Back按钮的点击
	public boolean onKeyDown(int keyCode, KeyEvent event)	{
		if (keyCode == KeyEvent.KEYCODE_BACK)	{
			doBack();
			return false;
		}
		return false;
	}
	// 根据判断决定返回键的响应
	private void doBack() {
		// 先判断当前Tab位置
		int tabPosition;
		ActionBar actionBar = getActionBar();
		Tab tab = actionBar.getSelectedTab();
		tabPosition = tab.getPosition();
		// 位于“历史流水”Tab， 视其状态选择是否退出
		if (tabPosition == 0) {
			// 位于选择时间界面，直接退出
			if (HISTORY_STATE == 0) {
				finish();
			} else {
				// 位于搜索结果界面，回退到选择时间界面
				actionBar.removeTabAt(0);
				tab.setText("历史流水")
				.setTabListener(new TabListener
						<ManageTradingInquiryHistoryFragment>(this, "历史流水",
						ManageTradingInquiryHistoryFragment.class));
				actionBar.addTab(tab, 0);
				actionBar.selectTab(tab);
				// 将搜索界面选择的时间赋给startTime和endTime
				HISTORY_STATE = 0;  // 状态置为选择时间Fragment
			}
		} else {
			// 不位于“历史流水”Tab，也直接退出
			finish();
		}
	}

	// 监听MenuItem的点击事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			doBack();
			break;
		default:
			break;
		}
		return false;
	}
	
	// 将数字转化为“周X”字符串
	public static String dayOfWeekToString(int dayOfWeek) {
		String strDayOfWeek;
		switch (dayOfWeek) {
		case 1:
			strDayOfWeek = "周日";
			break;
		case 2:
			strDayOfWeek = "周一";
			break;
		case 3:
			strDayOfWeek = "周二";
			break;
		case 4:
			strDayOfWeek = "周三";
			break;
		case 5:
			strDayOfWeek = "周四";
			break;
		case 6:
			strDayOfWeek = "周五";
			break;
		case 7:
			strDayOfWeek = "周六";
			break;
		default:
			strDayOfWeek = "未知";
			break;
		}
		return strDayOfWeek;
	}
}