package com.duang.easyecard.Activities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import com.duang.easyecard.R;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Models.Group;
import com.duang.easyecard.Models.TradingInquiry;
import com.duang.easyecard.UI.PinnedHeaderExpandableListView;
import com.duang.easyecard.UI.PinnedHeaderExpandableListView.OnHeaderUpdateListener;
import com.duang.easyecard.Utils.LogUtil;
import com.duang.easyecard.Utils.MyexpandableListAdapter;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.LayoutParams;

public class ManageTradingInquiryHistoryResultFragment extends Fragment implements 
ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener,
OnHeaderUpdateListener {
	
	private PinnedHeaderExpandableListView mExpandableListView;
	private ArrayList<Group> groupList;
	private ArrayList<List<TradingInquiry>> childList;
	private HorizontalScrollView mHorizontalScrollView;
	private ProgressDialog mProgressDialog;

	private MyexpandableListAdapter adapter;

	private View viewFragment;  // 缓存Fragment的View
	
	private final int GET_SUCCESS_RESPONSE = 200;
	private int width;
	
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
		// 获取屏幕的像素宽度
		WindowManager wm = getActivity().getWindowManager();
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		width = outMetrics.widthPixels;
		// 动态设置footLayout的宽度
		LinearLayout footLayout = (LinearLayout) getActivity().findViewById(R.id.history_trading_inquiry_foot_linear);
		footLayout.setMinimumWidth(width * 2);
		// 实例化底部HorizontalScrollListView
		mHorizontalScrollView = (HorizontalScrollView) getActivity().findViewById(R.id.history_trading_inquiry_foot_scroll);
		
		mExpandableListView = (PinnedHeaderExpandableListView) getActivity().findViewById(R.id.histroy_trading_inquiry_expandablelist);
		initData();
		adapter = new MyexpandableListAdapter(getActivity(), groupList, childList);
        mExpandableListView.setAdapter(adapter);

        // 展开所有group
        for (int i = 0, count = mExpandableListView.getCount(); i < count; i++) {
            mExpandableListView.expandGroup(i);
        }

        mExpandableListView.setOnHeaderUpdateListener(this);
        mExpandableListView.setOnChildClickListener(this);
        mExpandableListView.setOnGroupClickListener(this);
	}
	
	private Runnable scrollRunable = new Runnable() {
		
		@Override
		public void run() {
			// 将HorizontalScrollView移动到最右边
			mHorizontalScrollView.smoothScrollTo(width * 2, 0);
		}
	};
	// 处理GET请求的结果
	Handler readResponseHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_SUCCESS_RESPONSE:
				// 已成功得到响应数据responseString
				LogUtil.d("responseString", responseString);
				break;
			default:
				break;
			}
		}
	};
	
	private void initData() {
		Handler scrollFootHandler = new Handler();
		scrollFootHandler.postDelayed(scrollRunable, 2000);
		
		// 发送GET请求
		sendGetRequest();
		
		// TODO Auto-generated method stub
		groupList = new ArrayList<Group>();
        Group group = null;
        for (int i = 0; i < 3; i++) {
            group = new Group();
            group.setTitle("group-" + i);
            groupList.add(group);
        }

        childList = new ArrayList<List<TradingInquiry>>();
        for (int i = 0; i < groupList.size(); i++) {
            ArrayList<TradingInquiry> childTemp;
            if (i == 0) {
                childTemp = new ArrayList<TradingInquiry>();
                for (int j = 0; j < 13; j++) {
                	TradingInquiry tradingInquiry = new TradingInquiry();
                    tradingInquiry.setmTradingTime("yy-" + j);
                    childTemp.add(tradingInquiry);
                }
            } else if (i == 1) {
                childTemp = new ArrayList<TradingInquiry>();
                for (int j = 0; j < 8; j++) {
                	TradingInquiry tradingInquiry = new TradingInquiry();
                    tradingInquiry.setmTradingName("ff-" + j);
                    childTemp.add(tradingInquiry);
                }
            } else {
                childTemp = new ArrayList<TradingInquiry>();
                for (int j = 0; j < 23; j++) {
                	TradingInquiry tradingInquiry = new TradingInquiry();
                    tradingInquiry.setmTradingTime("hh-" + j);
                    childTemp.add(tradingInquiry);
                }
            }
            childList.add(childTemp);
        }
	}
	// 发送GET请求
	private void sendGetRequest() {
		// 组装Url
		UrlConstant.trjnListStartTime = ManageTradingInquiryActivity.startTime;
		UrlConstant.trjnListEndTime = ManageTradingInquiryActivity.endTime;
		UrlConstant.trjnListPageIndex = 1;
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
							readResponseHandler.sendMessage(message);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	// 解析响应数据
	private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
		    LogUtil.d("JsouphtmlData", "onPreExecute");
			super.onPreExecute();
			// Create a progressDialog
			mProgressDialog = new ProgressDialog(MyApplication.getContext());
			// Set progressDialog title
			mProgressDialog.setTitle("从校园一卡通网站获取相关信息");
			// Set progressDialog message
			mProgressDialog.setMessage("正在加载并解析……");
			mProgressDialog.setIndeterminate(false);
			// Show progressDialog
			mProgressDialog.show();
		}
		@Override
		protected Void doInBackground(Void... params) {
			// 解析返回的responseString
			Document doc = null;
			try {
				doc = Jsoup.parse(responseString);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			LogUtil.d("JsouphtmlData", "onPostExecute");
			// Close the progressDialog
			mProgressDialog.dismiss();
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

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
            int groupPosition, int childPosition, long id) {
        Toast.makeText(MyApplication.getContext(),
                childList.get(groupPosition).get(childPosition).getmTradingTime(), 1)
                .show();

        return false;
    }


    @Override
    public View getPinnedHeader() {
        View headerView = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.trading_inquiry_group, null);
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
