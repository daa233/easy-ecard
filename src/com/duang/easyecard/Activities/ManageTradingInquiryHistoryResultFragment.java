package com.duang.easyecard.Activities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.duang.easyecard.R;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Models.Group;
import com.duang.easyecard.Models.TradingInquiry;
import com.duang.easyecard.UI.PinnedHeaderExpandableListView;
import com.duang.easyecard.UI.PinnedHeaderExpandableListView.OnHeaderUpdateListener;
import com.duang.easyecard.Utils.LogUtil;
import com.duang.easyecard.Utils.MyExpandableListAdapter;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

public class ManageTradingInquiryHistoryResultFragment extends Fragment
implements ExpandableListView.OnChildClickListener,
ExpandableListView.OnGroupClickListener, OnHeaderUpdateListener {
	
	private PinnedHeaderExpandableListView mExpandableListView;
	private ProgressDialog mProgressDialog;
	private TextView footStartTimeText;
	private TextView footEndTimeText;
	private TextView footOutputText;
	
	private View viewFragment;  // 缓存Fragment的View
	private MyExpandableListAdapter adapter;

	private ArrayList<Group> groupList = new ArrayList<Group>();
	private ArrayList<List<TradingInquiry>> childList =
			new ArrayList<List<TradingInquiry>>();
	
	private final int GET_SUCCESS_RESPONSE = 200;
	private final int FINISH_HISTORY_ARRAY_LIST = 201;
	private final int NEED_MORE_DATA = 202;
	private int FIRST_JSOUP_FLAG = 1;  // 首次解析标志
	
	private int maxPageIndex = 1;  // 最大页码，默认为1
	private int pageIndex = 1;
	
	private String responseString;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (viewFragment == null) {
			viewFragment =  inflater.inflate(
					R.layout.fragment_trading_inquiry_history_result,
					container, false);
		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误
		ViewGroup parent = (ViewGroup) viewFragment.getParent();
		if (parent != null) {
			parent.removeView(viewFragment);
		}
		return viewFragment;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initView();
		initData();
	}
	// 处理各种Message请求
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_SUCCESS_RESPONSE:
				// 已成功得到响应数据responseString
				LogUtil.d("responseString", responseString);
				new JsoupHtmlData().execute();
				break;
			case NEED_MORE_DATA:
				// 当前页码不是最大页码，需要获取更多数据
				pageIndex++;
				sendGetRequest();
				break;
			case FINISH_HISTORY_ARRAY_LIST:
				// 已得到处理好的historyArrayList
				loadDataToFootView();
				loadDataToLists();  // 导入groupList和ChildList
				// 显示搜索到的记录总数
				Toast.makeText(getActivity(), "共搜索到" 
						+ ManageTradingInquiryActivity
						.historyArrayList.size() + "条记录",
						Toast.LENGTH_SHORT).show();
				// 绑定适配器
				adapter = new MyExpandableListAdapter(getActivity(),
						groupList, childList);
		        mExpandableListView.setAdapter(adapter);
		        // 如果有数据，展开所有group
		        if (!ManageTradingInquiryActivity.historyArrayList.isEmpty()) {
		        	for (int i = 0, count = mExpandableListView.getCount();
			        		i < count; i++) {
			            mExpandableListView.expandGroup(i);
			        }
		        }
		        // 设置监听事件
		        mExpandableListView.setOnHeaderUpdateListener(
		        		ManageTradingInquiryHistoryResultFragment.this);
		        mExpandableListView.setOnChildClickListener(
		        		ManageTradingInquiryHistoryResultFragment.this);
		        mExpandableListView.setOnGroupClickListener(
		        		ManageTradingInquiryHistoryResultFragment.this);
				break;
			default:
				break;
			}
		}
	};
	
	// 初始化布局
	private void initView() {
		// 实例化Foot中的TextView
		footStartTimeText = (TextView) getActivity().findViewById(
				R.id.history_trading_inquiry_result_start_time);
		footEndTimeText = (TextView) getActivity().findViewById(
				R.id.history_trading_inquiry_result_end_time);
		footOutputText = (TextView) getActivity().findViewById(
				R.id.history_trading_inquiry_result_foot_output);
		// 实例化ListView
		mExpandableListView = (PinnedHeaderExpandableListView) getActivity().
				findViewById(R.id.histroy_trading_inquiry_expandablelist);
	}
	
	private void initData() {
		// 刷新historyArrayList
		ManageTradingInquiryActivity.historyArrayList =
				new ArrayList<HashMap<String, String>>();
		// 设置底部TextView显示时间
		footStartTimeText.setText(ManageTradingInquiryActivity.startTime);
		footEndTimeText.setText(ManageTradingInquiryActivity.endTime);
		// 发送GET请求，完成后会转到耗时任务，由Handler继续处理
		sendGetRequest();
	}
	
	// 发送GET请求
	private void sendGetRequest() {
		// 组装Url
		UrlConstant.trjnListStartTime = ManageTradingInquiryActivity.startTime;
		UrlConstant.trjnListEndTime = ManageTradingInquiryActivity.endTime;
		UrlConstant.trjnListPageIndex = pageIndex;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 创建一个HttpGet对象
				HttpGet httpGetRequest = new HttpGet(
						UrlConstant.getTrjnListHistroy());
				LogUtil.d("URL", UrlConstant.TRJN_LIST_HISTORY);
				try {
					// 发送GET请求
					HttpResponse httpResponse = ManageTradingInquiryActivity
							.httpClient.execute(httpGetRequest);
					// 成功响应
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						StringBuffer stringBuffer = new StringBuffer();
						HttpEntity entity = httpResponse.getEntity();
						if (entity != null) {
							
							// 读取服务器响应
							BufferedReader br = new BufferedReader(
								new InputStreamReader(entity.getContent()));
							String line = null;
							while ((line = br.readLine()) != null) {
								stringBuffer.append(line);
							}
							responseString = stringBuffer.toString();
							// 发送消息到线程，已得到响应数据responseString
							Message message = new Message();
							message.what = GET_SUCCESS_RESPONSE;
							handler.sendMessage(message);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	// 解析响应数据        **Thanks for http://www.androidbegin.com/tutorial/.**
	private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
		    LogUtil.d("JsouphtmlData", "onPreExecute");
			super.onPreExecute();
			// 只有首次解析时才新建 progressDialog
			if (FIRST_JSOUP_FLAG == 1) {
				// Create a progressDialog
				mProgressDialog = new ProgressDialog(getActivity());
				// Set progressDialog title
				mProgressDialog.setTitle("从校园一卡通网站获取相关信息");
				// Set progressDialog message
				mProgressDialog.setMessage("正在加载并解析……");
				mProgressDialog.setIndeterminate(false);
				// Show progressDialog
				mProgressDialog.show();
			}
		}
		@Override
		protected Void doInBackground(Void... params) {
			// 解析返回的responseString
			Document doc = null;
			try {
				if (responseString == null) {
					LogUtil.e("resposeString", "responseString is null.");
				}
				doc = Jsoup.parse(responseString);
				// 找到表格
				for (Element table : doc.select("table[class=table_show]")) {
					// 找到表格的所有行
					for (Element row : table.select("tr:gt(0)")) {
						HashMap<String, String> map =
								new HashMap<String, String>();
						// 找到每一行所包含的td
						Elements tds = row.select("td");
						// tempTradingTime用于打断字符串
						String[] tempTradingTime = tds.get(0).text().split(" ");
						// 将td添加到arraylist
						map.put("TradingDate", tempTradingTime[0]);
						map.put("TradingTime", tempTradingTime[1]);
						map.put("MerchantName", tds.get(1).text());
						map.put("TradingName", tds.get(2).text());
						map.put("TransactionAmount", tds.get(3).text());
						map.put("Balance", tds.get(4).text());
						ManageTradingInquiryActivity.historyArrayList.add(map);
					}
				}
				LogUtil.d("JsoupHtmlData  arrayList", ManageTradingInquiryActivity.
						historyArrayList.toString());
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
					FIRST_JSOUP_FLAG = 0;
				}
				
				Message message = new Message();
				if (pageIndex < maxPageIndex) {
					// 如果当前页码不是最大页码，发送请求，获取更多数据
					message.what = NEED_MORE_DATA;
					handler.sendMessage(message);
				} else {
					// 如果当前页码是最大页码，发送已准备好histroyArrayList的请求
					message.what = FINISH_HISTORY_ARRAY_LIST;
					handler.sendMessage(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			LogUtil.d("JsouphtmlData", "onPostExecute");
			// Close the progressDialog
			if (pageIndex == maxPageIndex) {
				// 当接近结束时再关闭mProgressDialog
				mProgressDialog.dismiss();
			}
		}
	}
	// 将数据导入底部的布局
	private void loadDataToFootView() {
		double sum = 0;
		for (int i = 0; i < ManageTradingInquiryActivity
				.historyArrayList.size(); i++) {
			if(Double.valueOf(ManageTradingInquiryActivity
					.historyArrayList.get(i).get("TransactionAmount")) < 0){
				sum = sum + Double.valueOf(ManageTradingInquiryActivity
						.historyArrayList.get(i).get("TransactionAmount"));
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		footOutputText.setText("本段时间共支出  " + String.valueOf(df.format(-sum)) + " 元");
	}
	// 将historyArrayList的数据导入groupList和childList
	private void loadDataToLists() {
		// 没有搜索到数据
		if (ManageTradingInquiryActivity.historyArrayList.isEmpty()) {
			// 添加默认数据
			Group group = new Group();
			group.setTitle("   -------- 这里空空的，一定不是因为我穷。 --------");
			groupList.add(group);
			ArrayList<TradingInquiry> childTempList =
					new ArrayList<TradingInquiry>();
			TradingInquiry tradingInquiry = new TradingInquiry();
			tradingInquiry.setTradingTime("如果    选");
			tradingInquiry.setMerchantName("对了时间  结果 可能");
			tradingInquiry.setTradingName("就会    不   一");
			tradingInquiry.setTransactionAmount("样");
			childTempList.add(tradingInquiry);
			childList.add(childTempList);
			return;
		}
		// 导入groupList
		String tempDate = null;
		String tempDateFromHashMapList = null;  // 直接从HashMapList中获取的日期
		// 通过循环遍历historyArrayList来得到groupList
		for (int i = 0; i < ManageTradingInquiryActivity
				.historyArrayList.size(); i++) {
			tempDateFromHashMapList = ManageTradingInquiryActivity
					.historyArrayList.get(i).get("TradingDate");
			if (groupList.size() == 0) {
				tempDate = tempDateFromHashMapList;
				LogUtil.d("tempDate", tempDate);
				Group group = new Group();
				group.setTitle(tempDate);
				groupList.add(group);
			} else if (groupList.size() > 0) {
				// 如果日期不同，则把新日期添加到groupList
				if (!tempDate.equals(tempDateFromHashMapList)) {
					tempDate = tempDateFromHashMapList;
					LogUtil.d("tempDate", tempDate);
					Group group = new Group();
					group.setTitle(tempDate);
					groupList.add(group);
				}
			}
		}
		// 打印groupList结果
		for (int k = 0; k < groupList.size(); k ++) {
			LogUtil.d("groupTitle", groupList.get(k).getTitle());
		}
		// 导入childList
		ArrayList<TradingInquiry> childTempList;
		for (int i = 0; i < groupList.size(); i++) {
			tempDate = groupList.get(i).getTitle();
			// 进入一个新的组，要有一个新的childTempList
			childTempList = new ArrayList<TradingInquiry>();
			for (int j = 0;
					j < ManageTradingInquiryActivity.historyArrayList.size();
					j++) {
				String childDate = ManageTradingInquiryActivity
						.historyArrayList.get(j).get("TradingDate");
				// 如果日期相同（包含组名）则属于该组
				if (childDate.contains(tempDate)) {
					TradingInquiry tradingInquiry = new TradingInquiry();
					tradingInquiry.setTradingDate(ManageTradingInquiryActivity
							.historyArrayList.get(j).get("TradingDate"));
					tradingInquiry.setTradingTime(ManageTradingInquiryActivity
							.historyArrayList.get(j).get("TradingTime"));
					tradingInquiry.setMerchantName(ManageTradingInquiryActivity
							.historyArrayList.get(j).get("MerchantName"));
					tradingInquiry.setTradingName(ManageTradingInquiryActivity
							.historyArrayList.get(j).get("TradingName"));
					tradingInquiry.setTransactionAmount(ManageTradingInquiryActivity
							.historyArrayList.get(j).get("TransactionAmount"));
					tradingInquiry.setBalance(ManageTradingInquiryActivity
							.historyArrayList.get(j).get("Balance"));
					childTempList.add(tradingInquiry);
				}
			}
			// 把这一组的childTempList添加到childList
			childList.add(childTempList);
		}
	}
	
	/**
	 * 想用Pull解析XML一直没解析对，还是用Jsoup吧
	 * @param xmlData
	 *//*
	// 解析响应数据 *失败*
	private void parseXMLWithPull(String xmlData) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = factory.newPullParser();
			xmlPullParser.setInput(new StringReader(xmlData));
			int eventType = xmlPullParser.getEventType();
			List<String> td = new ArrayList<String>();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String nodeName = xmlPullParser.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG: {
					if ("td".equals(nodeName)) {
						td.add(xmlPullParser.nextText());
						LogUtil.d("XML", "td is" + xmlPullParser.nextText());
					}
					break;
				}
				case XmlPullParser.END_TAG: {
					if ("tr".equals(nodeName)) {
						LogUtil.d("td List", td.toString());
					}
					break;
				}
				default:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/

    @Override
    public boolean onGroupClick(final ExpandableListView parent, final View v,
            int groupPosition, final long id) {

        return false;
    }
    // 子项点击事件
    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
            int groupPosition, int childPosition, long id) {
    	// 点击子项显示卡余额
        Toast.makeText(MyApplication.getContext(),
        	childList.get(groupPosition).get(childPosition).getTradingDate()
        	+ "-" +
        	childList.get(groupPosition).get(childPosition).getTradingTime()
        	+ "  " + "交易后余额  " + 
            childList.get(groupPosition).get(childPosition).getBalance(),
            Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public View getPinnedHeader() {
        View headerView = (ViewGroup) getActivity().getLayoutInflater().inflate(
        		R.layout.trading_inquiry_group, null);
        headerView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        return headerView;
    }

    @Override
    public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
        Group firstVisibleGroup = (Group) adapter.getGroup(firstVisibleGroupPos);
        TextView textView = (TextView) headerView.findViewById(R.id.group);
        textView.setText(firstVisibleGroup.getTitle());
    }

}
