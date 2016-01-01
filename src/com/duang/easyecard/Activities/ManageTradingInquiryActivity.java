package com.duang.easyecard.Activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import com.duang.easyecard.Activities.ManageTradingInquiryHistoryFragment.MyCallback;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Utils.HttpUtil;
import com.duang.easyecard.Utils.HttpUtil.HttpCallbackListener;
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
import android.widget.Toast;

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
	protected static ArrayList<HashMap<String, String>> weekArrayList;

	private final int POST_SUCCESS_RESPONSE = 200;
	private final int NETWORK_ERROR = 0x404;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
				.setText("最近一周")
				.setTabListener(
						new TabListener<ManageTradingInquiryWeekFragment>(
								this, "最近一周",
								ManageTradingInquiryWeekFragment.class));
		actionBar.addTab(tab);
	}

	private void initData() {
		// 获得全局变量httpClient
		MyApplication myApp = (MyApplication) getApplication();
		httpClient = myApp.getHttpClient();
		// 将所有Fragment的首次初始化标志置1
		ManageTradingInquiryDayFragment.INIT_FLAG = 1;
		ManageTradingInquiryWeekFragment.INIT_FLAG = 1;
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
			case NETWORK_ERROR:
				// 网络错误
				Toast.makeText(ManageTradingInquiryActivity.this, "网络错误",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};
	// 发送POST请求到TRJN_QUERY，开始流水查询
	private void sendPostRequest() {
		// 装填POST数据
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("needHeader", "false"));
		HttpUtil.sendPostRequest(httpClient, UrlConstant.TRJN_QUERY, params,
				new HttpCallbackListener() {
					@Override
					public void onFinish(String response) {
						// 响应成功
						Message message = new Message();
						message.what = POST_SUCCESS_RESPONSE;
						message.obj = response;
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

	/*
	 * 在ManageTradingInquiryHistoryFragment中定义的Callback接口
	 * 用于返回Fragment中的按钮点击事件，并重新加载该Tab
	 */
	@Override
	public void onBtnClick(View v) {
		// 更新startTime和endTime
		startTime = startYear + "-" + startMonthOfYear + "-" + startDayOfMonth;
		endTime = endYear + "-" + endMonthOfYear + "-" + endDayOfMonth;
		// 更换Tab(0)的布局
		ManageTradingInquiryHistoryResultFragment.INIT_FLAG = 1;  // 首次初始化
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
		/*
		 * 发生了直接查询“历史流水”后查询“当日流水”没有结果的Bug，原因在这里
		 * 这种Tab的移除方法会导致“当日流水”的意外加载，所以要将其首次初始化标志重新置1
		 * 解决方法，在DayResultFragment中将其INIT_FLAG置1的时间后移
		 */
		// LogUtil.d("DayResultFragment",
		//		ManageTradingInquiryDayFragment.INIT_FLAG + "");
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
		case 0:
			strDayOfWeek = "周六";
			break;
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