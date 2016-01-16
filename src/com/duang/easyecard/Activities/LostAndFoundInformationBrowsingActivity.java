package com.duang.easyecard.Activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.duang.easyecard.R;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Models.LostInfo;
import com.duang.easyecard.UI.XListView;
import com.duang.easyecard.UI.XListView.IXListViewListener;
import com.duang.easyecard.Utils.HttpUtil;
import com.duang.easyecard.Utils.LogUtil;
import com.duang.easyecard.Utils.HttpUtil.HttpCallbackListener;
import com.duang.easyecard.Utils.LostInfoAdapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class LostAndFoundInformationBrowsingActivity extends BaseActivity
implements IXListViewListener, OnItemClickListener{
	
	private XListView xListView;
	private CheckBox notFoundedCheckBox;
	private CheckBox foundedCheckBox;
	
	private LostInfoAdapter mAdapter;
	private Handler mHandler;
	
	private final int READY_TO_SEND_GET_REQUEST = 100;
	private final int GET_SUCCESS_RESPONSE = 200;
	private final int FINISH_TEMP_LIST = 201;
	private final int NEED_MORE_DATA = 300;
	private final int NETWORK_ERROR = 404;
	private int FIRST_JSOUP_FLAG = 1;  // 首次解析标志
	
	private HttpClient httpClient;
	private List<LostInfo> lostInfoList = new ArrayList<LostInfo>();
	private List<LostInfo> tempLostInfoList = new ArrayList<LostInfo>();
	private String responseString;
	private int pageIndex = 1;  // 访问的网页信息的页码
	private int maxPageIndex = 1;  // 最大页码，默认为1
	private int allLostInfoNumber;  // 当前累计丢失卡信息条数
	private int foundedLostInfoNumber;  // 已招领条数
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_and_found_information_browsing);
		
		initData();
		initView();
	}
	// 初始化View
	private void initView() {
		xListView = (XListView) findViewById(
				R.id.lost_and_found_info_browsing_xListView);
		xListView.setPullLoadEnable(true);
		mAdapter = new LostInfoAdapter(this, lostInfoList,
				R.layout.lost_and_found_info_browsing_list_item);
		xListView.setAdapter(mAdapter);
		xListView.setXListViewListener(this);
		xListView.setOnItemClickListener(this);
		mHandler = new Handler();
	}

	// 初始化数据
	private void initData() {
		// 获得全局变量httpClient
		MyApplication myApp = (MyApplication) getApplication();
		httpClient = myApp.getHttpClient();
		// 需要先访问到“应用中心”界面，准备好后发送GET请求
		sendPreGETRequest();
	}
	// 处理各种Message请求
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case READY_TO_SEND_GET_REQUEST:
				sendGETRequest();
				break;
			case GET_SUCCESS_RESPONSE:
				// 已成功得到响应数据responseString
				LogUtil.d("responseString", responseString);
				new JsoupHtmlData().execute();
				break;
			case FINISH_TEMP_LIST:
				Toast.makeText(LostAndFoundInformationBrowsingActivity.this,
						"总页数" + maxPageIndex + "  总记录数" + allLostInfoNumber
						+ "  招领数" + foundedLostInfoNumber,
						Toast.LENGTH_LONG).show();
				break;
			case NEED_MORE_DATA:
				break;
			case NETWORK_ERROR:
				// 网络错误
				Toast.makeText(LostAndFoundInformationBrowsingActivity.this,
						"网络错误",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};
	// 发送GET请求
	private void sendGETRequest() {
		UrlConstant.cardLossPageIndex = pageIndex;  // 组装Url
		HttpUtil.sendGetRequest(httpClient,
				UrlConstant.getCardLossInfoBrowsing(),
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
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}
	
	// 解析响应数据
	private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			// 解析返回的responseString
			Document doc = null;
			try {
				if (responseString == null) {
					LogUtil.e("resposeString", "responseString is null.");
				}
				doc = Jsoup.parse(responseString);
				// 获取总页数，当前累计丢失信息条数，已招领条数
				if (FIRST_JSOUP_FLAG == 1) {
					// 首次解析时得到最大页码，避免maxPageIndex在解析到最后一页时减小
					String remainString = "";
					for (Element page : doc.select("a[data-ajax=true]")) {
						remainString = page.attr("href");
					}
					// 当记录页数少于1时，remainString为空
					if (!remainString.isEmpty()) {
						// remainString不为空
						remainString = remainString.substring(
								remainString.indexOf("pageindex=") + 10);
						maxPageIndex = Integer.valueOf(remainString);
						LogUtil.d("JsoupHtmlData  maxPageIndex", maxPageIndex + "");
					} else {
						// remainString为空, maxIndex值保持不变
						LogUtil.d("JsoupHtmlData  maxPageIndex", maxPageIndex + "");
					}
					// 解析当前累计丢失信息条数和已招领条数
					for (Element div : doc.select("div[class=content]")) {
						Element span = div.getElementById("lblLostCount");
						allLostInfoNumber = Integer.valueOf(span.text());
						span = div.getElementById("lblClaimCount");
						foundedLostInfoNumber = Integer.valueOf(span.text());
					}
					FIRST_JSOUP_FLAG = 0;
				}
				// 找到表格
				for (Element table : doc.select(
						"table[class=table_show widthtable]")) {
					// 找到表格的所有行
					for (Element row : table.select("tr:gt(0)")) {
						LostInfo lostInfo = new LostInfo();
						// 找到每一行所包含的td
						Elements tds = row.select("td");
						// 将数据按照顺序填入LostInfo对象
						lostInfo.setName(tds.get(0).text());
						lostInfo.setStuId(tds.get(1).text());
						lostInfo.setAccount(tds.get(2).text());
						lostInfo.setPublishTime(tds.get(3).text());
						lostInfo.setContact(tds.get(4).text());
						lostInfo.setState(tds.get(5).text());
						lostInfo.setFoundTime(tds.get(6).text());
						tempLostInfoList.add(lostInfo);
					}
				}
				// 发送完成信息
				Message message = new Message();
				message.what = FINISH_TEMP_LIST;
				handler.sendMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	// 先访问应用中心界面，准备发送GET请求
	private void sendPreGETRequest() {
		HttpUtil.sendGetRequest(httpClient, UrlConstant.MANAGEMENT,
				new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				// 成功响应，发送消息到线程，已准备好发送GET请求
				Message message = new Message();
				message.what = READY_TO_SEND_GET_REQUEST;
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
}
