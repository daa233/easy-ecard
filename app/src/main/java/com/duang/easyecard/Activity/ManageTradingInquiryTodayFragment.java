package com.duang.easyecard.Activity;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Model.Group;
import com.duang.easyecard.Model.TradingInquiry;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.duang.easyecard.Util.TradingInquiryDateUtil;
import com.duang.easyecard.Util.TradingInquiryExpandableListAdapter;
import com.duang.mypinnedheaderlistview.PinnedHeaderListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.rey.material.widget.ProgressView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageTradingInquiryTodayFragment extends Fragment implements
        ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener,
        PinnedHeaderListView.OnHeaderUpdateListener {

    private PinnedHeaderListView mListView;
    private ProgressView mProgressView;
    protected ImageView mNothingFoundedImageView;

    private TradingInquiryDateUtil myDateUtil;
    private List<Group> mGroupList;
    private List<List<TradingInquiry>> mChildList;
    private TradingInquiryExpandableListAdapter mAdapter;

    private String TAG = "ManageTradingInquiryTodayFragment";

    private String response;
    private int maxPageIndex = 1;  // 最大页码，默认为1
    private int pageIndex = 1;
    private boolean FIRST_TIME_TO_PARSE_FLAG = true;  // 首次解析标志

    public ManageTradingInquiryTodayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtil.d(TAG, "onAttach");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_manage_trading_inquiry_today, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.d(TAG, "onActivityCreated");
        initView();
        // 仅加载一次
        if (ManageTradingInquiryActivity.TODAY_TAB_INIT_FLAG == 0) {
            initData();
        }
        chooseViewByState(ManageTradingInquiryActivity.TODAY_TAB_INIT_FLAG);
        ManageTradingInquiryActivity.TODAY_TAB_INIT_FLAG = 0;
    }

    private void initView() {
        // 实例化控件
        mListView = (PinnedHeaderListView) getActivity().findViewById(
                R.id.manage_trading_inquiry_today_list_view);
        mProgressView = (ProgressView) getActivity().findViewById(
                R.id.manage_trading_inquiry_today_progress_view);
        mNothingFoundedImageView = (ImageView) getActivity().findViewById(
                R.id.manage_trading_inquiry_today_nothing_founded_image_view);
        // 获得从Activity传递过来的DateUtil
        myDateUtil = ManageTradingInquiryActivity.myDateUtil;
        // 通过Glide加载mNothingFoundedImageView
        Glide
                .with(this)
                .load(R.drawable.nothing_founded_404)
                .into(mNothingFoundedImageView);
    }

    private void initData() {
        // 初始化todayDataList
        ManageTradingInquiryActivity.todayDataList = new ArrayList<>();
        // 将TODAY_TAB_INIT_FLAG置为1，显示mProgressView
        ManageTradingInquiryActivity.TODAY_TAB_INIT_FLAG = 1;
        chooseViewByState(ManageTradingInquiryActivity.TODAY_TAB_INIT_FLAG);
        // 初始化页码
        pageIndex = 1;
        maxPageIndex = 1;
        // 发送GET请求
        sendGETRequest();
    }

    /**
     * 根据传入的FLAG值显示布局
     *
     * @param todayTabInitFlag
     */
    public void chooseViewByState(int todayTabInitFlag) {
        if (todayTabInitFlag == 1) {
            // 正在加载，显示加载按钮
            mProgressView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            mNothingFoundedImageView.setVisibility(View.GONE);
        } else if (todayTabInitFlag == 2) {
            // 加载完成，有数据
            mProgressView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mNothingFoundedImageView.setVisibility(View.GONE);
        } else if (todayTabInitFlag == 3) {
            // 加载完成，没有数据
            mProgressView.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            mNothingFoundedImageView.setVisibility(View.VISIBLE);
        } else {
            LogUtil.e(TAG, "unknown error in chooseViewByState.");
        }
    }

    /**
     * 发送GET请求
     */
    private void sendGETRequest() {
        UrlConstant.trjnListPageIndex = pageIndex;  // 组装Url
        ManageTradingInquiryActivity.httpClient.get(UrlConstant.getTrjnListToday(),
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // 成功响应
                        response = new String(responseBody);
                        LogUtil.d(TAG, new String(responseBody));
                        new JsoupHtmlData().execute();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                          Throwable error) {
                        // 网络错误
                        LogUtil.e(TAG, new String(responseBody));
                        Toast.makeText(getContext(), R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * 通过网站返回的html文本解析数据
     * 首次解析会得到最大页码maxIndex
     * 当存在更多页码（pageIndex < maxIndex）时，再次发送GET请求，并进行解析
     * 结果保存在ManageTradingInquiryActivity中的todayDataList
     * <p/>
     * 注意：在解析到最大页码（即最后一页 maxIndex）时，html文本中最大页码maxIndex会被替代为“尾页”，
     * 所以要通过FIRST_TIME_TO_PARSE_FLAG进行标识，仅在首次解析时获取maxIndex
     */
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Document doc;
            try {
                doc = Jsoup.parse(response);
                // 找到表格
                for (Element table : doc.select("table[class=table_show]")) {
                    // 找到表格的所有行
                    for (Element row : table.select("tr:gt(0)")) {
                        HashMap<String, String> map = new HashMap<>();
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
                        ManageTradingInquiryActivity.todayDataList.add(map);
                    }
                }
                // 首次解析时，获得maxIndex
                if (FIRST_TIME_TO_PARSE_FLAG) {
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
                    // 将首次解析标志置为false
                    FIRST_TIME_TO_PARSE_FLAG = false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // 判断是否已经全部解析完成
            if (pageIndex < maxPageIndex) {
                // 如果当前页码不是最大页码，再次发送GET请求，获取更多数据
                pageIndex++;
                sendGETRequest();
            } else {
                /**
                 * 如果当前页码是最大页码，说明已准备好todayDataList加载完成
                 * 通过matchDataWithAdapterLists，准备mAdapter的数据
                 */
                matchDataWithAdapterLists();
            }
        }
    }

    /**
     * 从todayDataList中获取数据，配置mGroupList和mChildList
     */
    private void matchDataWithAdapterLists() {
        // 初始化mGroupList和mChildList
        mGroupList = new ArrayList<>();
        mChildList = new ArrayList<>();
        // 没有搜索到数据
        if (ManageTradingInquiryActivity.todayDataList.isEmpty()) {
            // 将TODAY_TAB_INIT_FLAG置为3，显示没有数据的默认图片
            ManageTradingInquiryActivity.TODAY_TAB_INIT_FLAG = 3;
            chooseViewByState(ManageTradingInquiryActivity.TODAY_TAB_INIT_FLAG);
            return;
        }

        // 导入mGroupList
        String tempDate = null;
        String tempDateFromHashMapList;  // 直接从HashMapList中获取的日期
        // 通过循环遍历todayDataList来得到mGroupList
        for (int i = 0; i < ManageTradingInquiryActivity.todayDataList.size(); i++) {
            tempDateFromHashMapList = ManageTradingInquiryActivity
                    .todayDataList.get(i).get("TradingDate");
            if (mGroupList.size() == 0) {
                tempDate = tempDateFromHashMapList;
                LogUtil.d("tempDate", tempDate);
                Group group = new Group();
                group.setTitle(tempDate);
                mGroupList.add(group);
            } else if (mGroupList.size() > 0) {
                // 如果日期不同，则把新日期添加到mGroupList
                if (!tempDate.equals(tempDateFromHashMapList)) {
                    tempDate = tempDateFromHashMapList;
                    LogUtil.d("tempDate", tempDate);
                    Group group = new Group();
                    group.setTitle(tempDate);
                    mGroupList.add(group);
                }
            }
        }
        // 打印mGroupList结果
        for (int k = 0; k < mGroupList.size(); k++) {
            LogUtil.d("groupTitle", mGroupList.get(k).getTitle());
        }
        // 导入mChildList
        ArrayList<TradingInquiry> childTempList;
        for (int i = 0; i < mGroupList.size(); i++) {
            tempDate = mGroupList.get(i).getTitle();
            // 进入一个新的组，要有一个新的childTempList
            childTempList = new ArrayList<>();
            for (int j = 0; j < ManageTradingInquiryActivity.todayDataList.size(); j++) {
                String childDate = ManageTradingInquiryActivity
                        .todayDataList.get(j).get("TradingDate");
                // 如果日期相同（包含组名）则属于该组
                if (childDate.contains(tempDate)) {
                    TradingInquiry tradingInquiry = new TradingInquiry();
                    tradingInquiry.setTradingDate(ManageTradingInquiryActivity
                            .todayDataList.get(j).get("TradingDate"));
                    tradingInquiry.setTradingTime(ManageTradingInquiryActivity
                            .todayDataList.get(j).get("TradingTime"));
                    tradingInquiry.setMerchantName(ManageTradingInquiryActivity
                            .todayDataList.get(j).get("MerchantName"));
                    tradingInquiry.setTradingName(ManageTradingInquiryActivity
                            .todayDataList.get(j).get("TradingName"));
                    tradingInquiry.setTransactionAmount(ManageTradingInquiryActivity
                            .todayDataList.get(j).get("TransactionAmount"));
                    tradingInquiry.setBalance(ManageTradingInquiryActivity
                            .todayDataList.get(j).get("Balance"));
                    childTempList.add(tradingInquiry);
                }
            }
            // 把这一组的childTempList添加到mChildList
            mChildList.add(childTempList);
        }
        // 设置Adapter
        setupWithAdapter();
    }

    /**
     * 设置Adapter及监听ListView相关事件
     */
    private void setupWithAdapter() {
        // 将TODAY_TAB_INIT_FLAG置为2，显示mListView
        ManageTradingInquiryActivity.TODAY_TAB_INIT_FLAG = 2;
        // 显示mListView
        mListView.setVisibility(View.VISIBLE);
        mProgressView.setVisibility(View.GONE);
        mAdapter = new TradingInquiryExpandableListAdapter(getContext(), mGroupList,
                R.layout.manage_trading_inquiry_group_item, mChildList,
                R.layout.manage_trading_inquiry_child_item);
        mListView.setAdapter(mAdapter);

        // 如果有数据，展开所有group
        if (!ManageTradingInquiryActivity.todayDataList.isEmpty()) {
            for (int i = 0, count = mListView.getCount(); i < count; i++) {
                mListView.expandGroup(i);
            }
        }
        // 设置监听事件
        mListView.setOnHeaderUpdateListener(ManageTradingInquiryTodayFragment.this);
        mListView.setOnGroupClickListener(ManageTradingInquiryTodayFragment.this);
        mListView.setOnChildClickListener(ManageTradingInquiryTodayFragment.this);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                int childPosition, long id) {
        // 点击子项显示卡余额
        Toast.makeText(MyApplication.getContext(),
                mChildList.get(groupPosition).get(childPosition).getTradingDate()
                        + "-" +
                        mChildList.get(groupPosition).get(childPosition).getTradingTime()
                        + "  " + R.string.balance_after_trading +
                        mChildList.get(groupPosition).get(childPosition).getBalance(),
                Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
    }

    @Override
    public View getPinnedHeader() {
        View headerView = getActivity().getLayoutInflater().inflate(
                R.layout.manage_trading_inquiry_group_item, null);
        headerView.setLayoutParams(new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        return headerView;
    }

    @Override
    public void updatePinnedHeader(View headerView, int firstVisibleGroupPosition) {
        Group firstVisibleGroup = mAdapter.getGroup(firstVisibleGroupPosition);
        TextView textView = (TextView) headerView.findViewById(
                R.id.manage_traing_inquiry_group_item_text);
        textView.setText(firstVisibleGroup.getTitle());
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.d(TAG, "onDetach");
    }
}
