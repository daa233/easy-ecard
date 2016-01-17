package com.duang.easyecard.Activities;

import java.util.ArrayList;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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
	private final int FINISH_ALL_LIST = 500;
	private final int NEED_MORE_DATA = 300;
	private final int NETWORK_ERROR = 404;
	
	private int FIRST_JSOUP_FLAG = 1;  // 首次解析标志
	private int ALL_DATA_GOT_FLAG = 0;  // 已获取全部数据标志
	private int ALL_LOADED_FLAG = 0;  // 已全部加载标志
	private int DISPLAY_FOUNDED_FLAG = 1;  // 显示已招领信息标志
	private int DISPLAY_NOT_FOUNDED_FLAG = 1;  // 显示未招领信息
	
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
	}
	
	// 初始化View
	private void initView() {
		notFoundedCheckBox = (CheckBox) findViewById(
				R.id.lost_info_browsing_check_box_not_founded);
		foundedCheckBox = (CheckBox) findViewById(
				R.id.lost_info_browsing_check_box_founded);
		notFoundedCheckBox.setText("未招领 （" + 
				(allLostInfoNumber - foundedLostInfoNumber) + "）");
		foundedCheckBox.setText("已招领 （" + foundedLostInfoNumber + "）");
		
		xListView = (XListView) findViewById(
				R.id.lost_and_found_info_browsing_xListView);
		xListView.setPullLoadEnable(true);
		mAdapter = new LostInfoAdapter(this, lostInfoList,
				R.layout.lost_and_found_info_browsing_list_item);
		xListView.setAdapter(mAdapter);
		xListView.setXListViewListener(this);
		xListView.setOnItemClickListener(this);
		// CheckBox的选择事件
		notFoundedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					DISPLAY_NOT_FOUNDED_FLAG = 1;
				} else {
					DISPLAY_NOT_FOUNDED_FLAG = 0;
				}
				updateView();
			}
		});
		foundedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					DISPLAY_FOUNDED_FLAG = 1;
				} else {
					DISPLAY_FOUNDED_FLAG = 0;
				}
				updateView();
			}
		});
		
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
				lostInfoList.addAll(tempLostInfoList);
				// 先显示第一页的内容
				if (FIRST_JSOUP_FLAG == 1) {
					initView();
					FIRST_JSOUP_FLAG = 0;
				}
				break;
			case NEED_MORE_DATA:
				// 获取更多数据
				pageIndex++;
				sendGETRequest();
				break;
			case FINISH_ALL_LIST:
				// 已获取全部数据
				ALL_DATA_GOT_FLAG = 1;
				onLoadMore();
				ALL_LOADED_FLAG = 1;
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
	// 子项点击事件
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		
	}
	// 更新View
	public void updateView() {
		List<LostInfo> lostInfoRefreshList = new ArrayList<LostInfo>();
		if (DISPLAY_FOUNDED_FLAG == 1) {
			if (DISPLAY_NOT_FOUNDED_FLAG == 1) {
				// 均显示
				mAdapter = new LostInfoAdapter(MyApplication.getContext(),
						lostInfoList,
						R.layout.lost_and_found_info_browsing_list_item);
			}
			else {
				// 仅显示已招领
				for (int i = 0; i < lostInfoList.size(); i++) {
					if (lostInfoList.get(i).getState().contains("已招领")) {
						lostInfoRefreshList.add(lostInfoList.get(i));
					}
				}
				mAdapter = new LostInfoAdapter(MyApplication.getContext(),
						lostInfoRefreshList,
						R.layout.lost_and_found_info_browsing_list_item);
			}
		} else {
			if (DISPLAY_NOT_FOUNDED_FLAG == 1) {
				// 仅显示未招领
				for (int i = 0; i < lostInfoList.size(); i++) {
					if (lostInfoList.get(i).getState().contains("丢失卡")) {
						lostInfoRefreshList.add(lostInfoList.get(i));
					}
				}
				mAdapter = new LostInfoAdapter(MyApplication.getContext(),
						lostInfoRefreshList,
						R.layout.lost_and_found_info_browsing_list_item);
			} else {
				// 均不显示
				mAdapter = new LostInfoAdapter(MyApplication.getContext(),
						lostInfoRefreshList,
						R.layout.lost_and_found_info_browsing_list_item);
			}
		}
		xListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	// 刷新
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				FIRST_JSOUP_FLAG = 1;
				pageIndex = 1;
				lostInfoList.clear();
				ALL_DATA_GOT_FLAG = 0;
				ALL_LOADED_FLAG = 0;
				foundedCheckBox.setChecked(true);
				notFoundedCheckBox.setChecked(true);
				sendGETRequest();
				mAdapter = new LostInfoAdapter(MyApplication.getContext(),
						lostInfoList,
						R.layout.lost_and_found_info_browsing_list_item);
				xListView.setAdapter(mAdapter);
				onLoad();
			}
		}, 1500);
	}
	// 加载更多
	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (ALL_LOADED_FLAG == 1) {
					if (ALL_DATA_GOT_FLAG == 1) {
						Toast.makeText(
								LostAndFoundInformationBrowsingActivity.this,
								"全部加载完成",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								LostAndFoundInformationBrowsingActivity.this,
								"正在获取更多数据",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					if (ALL_DATA_GOT_FLAG == 1) {
						mAdapter = new LostInfoAdapter(
								MyApplication.getContext(),
								lostInfoList,
								R.id.lost_and_found_info_browsing_xListView);
						mAdapter.notifyDataSetChanged();
					}
				}
				onLoad();
			}
		}, 1500);
	}
	private void onLoad() {
		xListView.stopRefresh();
		xListView.stopLoadMore();
		xListView.setRefreshTime("刚刚");
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
						LogUtil.d("JsoupHtmlData  maxPageIndex",
								maxPageIndex + "");
					} else {
						// remainString为空, maxIndex值保持不变
						LogUtil.d("JsoupHtmlData  maxPageIndex",
								maxPageIndex + "");
					}
					// 解析当前累计丢失信息条数和已招领条数
					for (Element div : doc.select("div[class=content]")) {
						Element span = div.getElementById("lblLostCount");
						allLostInfoNumber = Integer.valueOf(span.text());
						span = div.getElementById("lblClaimCount");
						foundedLostInfoNumber = Integer.valueOf(span.text());
					}
				}
				// 找到表格
				for (Element table : doc.select(
						"table[class=table_show widthtable]")) {
					tempLostInfoList = new ArrayList<LostInfo>();
					Elements tbody = table.select("tbody");
					// 找到表格的所有行
					for (Element row : tbody.select("tr")) {
						LostInfo lostInfo = new LostInfo();
						// 找到每一行所包含的td
						Elements tds = row.select("td");
						// 将数据按照顺序填入LostInfo对象
						if (!tds.get(0).text().isEmpty()) {
							// 通过字符串截取获得丢失信息ID
							String lostInfoIdString = tds.get(0).toString();
							lostInfoIdString = lostInfoIdString.substring(
									lostInfoIdString.indexOf("(") + 1);
							lostInfoIdString = lostInfoIdString.substring(
									0, lostInfoIdString.indexOf(")"));
							lostInfo.setId(Integer.valueOf(lostInfoIdString));
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
				}
				// 发送完成一页信息
				Message message = new Message();
				message.what = FINISH_TEMP_LIST;
				handler.sendMessage(message);
				// 判断是否还有信息
				message = new Message();
				if (pageIndex < maxPageIndex) {
					// 如果当前页码不是最大页码，发送请求，获取更多数据
					message.what = NEED_MORE_DATA;
					handler.sendMessage(message);
				} else {
					// 如果当前页码是最大页码，已获取到全部数据
					message.what = FINISH_ALL_LIST;
					handler.sendMessage(message);
				}
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
