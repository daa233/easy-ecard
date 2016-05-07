package com.duang.easyecard.Activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pgyersdk.crash.PgyCrashManager;
import com.rey.material.widget.ProgressView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * To reuse the ManageTradingInquiryFragment,
 * make the history, today and week in one Fragment.
 * Created by MrD on 2016/4/1.
 */
public class ManageTradingInquiryFragment extends Fragment implements View.OnClickListener,
        ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener,
        PinnedHeaderListView.OnHeaderUpdateListener {

    private final String TAG = "ManageTradingInquiryFragment";
    private final int CONSTANT_HISTORY = 0;
    private final int CONSTANT_TODAY = 1;
    private final int CONSTANT_WEEK = 2;
    private View viewFragment;  // 缓存Fragment的View
    // 四个主要视图
    private ScrollView mPickDateView;
    private ProgressView mProgressView;
    private PinnedHeaderListView mListView;
    private ImageView mImageView;
    // 一些小的控件
    private LinearLayout setStartTimeLayout;
    private LinearLayout setEndTimeLayout;
    private DatePickerDialog mDatePickerDialog;
    private TextView startDateTextView;
    private TextView startDayTextView;
    private TextView endDateTextView;
    private TextView endDayTextView;
    private Button queryButton;
    private TradingInquiryExpandableListAdapter mAdapter;
    private CommunicateListener communicateListener;
    private int type;
    private List<HashMap<String, String>> dataList;
    private List<Group> mGroupList;
    private List<List<TradingInquiry>> mChildList;
    private TradingInquiryDateUtil dateUtil;
    private AsyncHttpClient httpClient;
    private String response;
    private int maxPageIndex;  // 最大页码
    private int pageIndex;
    private double sumTransaction = 0;
    private boolean firstTimeToParseFlag = true;  // 首次解析标志，用于解析页码

    // Constructor
    public ManageTradingInquiryFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtil.d(TAG, "onAttach");
        if (!(context instanceof CommunicateListener)) {
            throw new IllegalStateException("The host activity must implement the" +
                    "CommunicateListener");
        }
        // 把绑定的activity当成callback对象
        communicateListener = (CommunicateListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreateView");
        if (viewFragment == null) {
            viewFragment = inflater.inflate(R.layout.fragment_manage_trading_inquiry,
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.d(TAG, "onActivityCreated");
        initView();
        initData();
    }

    private void initView() {
        // 实例化控件
        mPickDateView = (ScrollView) viewFragment.findViewById(
                R.id.manage_trading_inquiry_pick_date);
        mListView = (PinnedHeaderListView) viewFragment.findViewById(
                R.id.manage_trading_inquiry_list_view);
        mProgressView = (ProgressView) viewFragment.findViewById(
                R.id.manage_trading_inquiry_progress_view);
        mImageView = (ImageView) viewFragment.findViewById(
                R.id.manage_trading_inquiry_nothing_founded_image_view);
        setStartTimeLayout = (LinearLayout) viewFragment.findViewById(
                R.id.manage_trading_inquiry_set_start_time);
        setEndTimeLayout = (LinearLayout) viewFragment.findViewById(
                R.id.manage_trading_inquiry_set_end_time);
        startDateTextView = (TextView) viewFragment.findViewById(
                R.id.manage_trading_inquiry_start_date);
        startDayTextView = (TextView) viewFragment.findViewById(
                R.id.manage_trading_inquiry_start_day);
        endDateTextView = (TextView) viewFragment.findViewById(
                R.id.manage_trading_inquiry_end_date);
        endDayTextView = (TextView) viewFragment.findViewById(
                R.id.manage_trading_inquiry_end_day);
        queryButton = (Button) getActivity().findViewById(
                R.id.manage_trading_inquiry_query_button);
    }

    // 初始化数据。先判断类型，然后开始流水查询
    public void initData() {
        // 通过POSITION确定Fragment的类型，加载相应的数据
        if (getArguments().containsKey("POSITION")) {
            switch (getArguments().getInt("POSITION")) {
                case 0:
                    type = CONSTANT_HISTORY;
                    // 监听控件的点击事件
                    setStartTimeLayout.setOnClickListener(this);
                    setEndTimeLayout.setOnClickListener(this);
                    queryButton.setOnClickListener(this);
                    break;
                case 1:
                    type = CONSTANT_TODAY;
                    break;
                case 2:
                    type = CONSTANT_WEEK;
                    break;
                default:
                    break;
            }
        } else {
            LogUtil.e(TAG, "Can't get arguments: position.");
        }
        // 获得全局变量httpClient，新建dateUtil
        MyApplication myApp = (MyApplication) getActivity().getApplication();
        httpClient = myApp.getHttpClient();
        dateUtil = new TradingInquiryDateUtil(MyApplication.getContext());
        // 初始化数据列表
        dataList = new ArrayList<>();
        // 初始化页码
        pageIndex = 1;
        maxPageIndex = 1;
        // 传递Tag给Activity
        communicateListener.getFragmentTag(type, getTag());
        // 创建Fragment时判断此Fragment之前是否已经加载（初始化）过
        if (communicateListener.getDataListInitFlag(type)) {
            // 已经初始化，无需操作
            LogUtil.d(TAG, "Has inited.");
        } else if (type == CONSTANT_HISTORY) {
            // 历史流水，未经初始化，显示选择日期界面
            mPickDateView.setVisibility(View.VISIBLE);
            // 隐藏ListView，防止抢夺焦点
            mListView.setVisibility(View.INVISIBLE);
            // 隐藏ImageView
            mImageView.setVisibility(View.INVISIBLE);
            // 显示默认时间
            updateTimeTable();
        } else {
            // 没有初始化过，发送GET请求
            mProgressView.setVisibility(View.VISIBLE);
            // 隐藏ImageView
            mImageView.setVisibility(View.INVISIBLE);
            sendGETRequest();
        }
    }

    // 发送GET请求
    private void sendGETRequest() {
        // 置位正在加载标志
        communicateListener.setLoadingFlag(type, true);
        // 历史流水
        if (type == CONSTANT_HISTORY) {
            // 组装Url地址
            UrlConstant.trjnListStartTime = dateUtil.getHistoryStartYear() + "-" +
                    dateUtil.getHistoryStartMonth() + "-" +
                    dateUtil.getHistoryStartDayOfMonth();
            UrlConstant.trjnListEndTime = dateUtil.getHistoryEndYear() + "-" +
                    dateUtil.getHistoryEndMonth() + "-" +
                    dateUtil.getHistoryEndDayOfMonth();
            // 刷新页码
            UrlConstant.historyTrjnListPageIndex = pageIndex;
            LogUtil.d(TAG, "History Url = " + UrlConstant.getTrjnListHistory());
            // 发送GET请求
            httpClient.get(UrlConstant.getTrjnListHistory(),
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            // 成功响应
                            response = new String(responseBody);
                            new JsoupHtmlData().execute();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                              Throwable error) {
                            // 网络错误
                            LogUtil.e(TAG, "Network error. " + new String(responseBody));
                            Toast.makeText(MyApplication.getContext(),
                                    getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    });
        } else if (type == CONSTANT_TODAY) {  // 当日流水
            // 刷新页码
            UrlConstant.todayTrjnListPageIndex = pageIndex;
            LogUtil.d(TAG, "Today Url = " + UrlConstant.getTrjnListToday());
            httpClient.get(UrlConstant.getTrjnListToday(), new AsyncHttpResponseHandler() {
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
                    LogUtil.e(TAG, "Network error. " + new String(responseBody));
                    Toast.makeText(MyApplication.getContext(),
                            getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            });
        } else if (type == CONSTANT_WEEK) {  // 一周流水
            // 组装Url地址
            UrlConstant.trjnListStartTime = dateUtil.getWeekStartYear() + "-" +
                    dateUtil.getWeekStartMonth() + "-" + dateUtil.getWeekStartDayOfMonth();
            UrlConstant.trjnListEndTime = dateUtil.getWeekEndYear() + "-" +
                    dateUtil.getWeekEndMonth() + "-" + dateUtil.getWeekEndDayOfMonth();
            UrlConstant.historyTrjnListPageIndex = pageIndex;
            LogUtil.d(TAG, "Week Url = " + UrlConstant.getTrjnListHistory());
            // 发送GET请求
            httpClient.get(UrlConstant.getTrjnListHistory(),
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            // 成功响应
                            response = new String(responseBody);
                            new JsoupHtmlData().execute();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                              Throwable error) {
                            // 网络错误
                            LogUtil.e(TAG, "Network error. " + new String(responseBody));
                            Toast.makeText(MyApplication.getContext(),
                                    getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    });
        } else {
            LogUtil.e(TAG, "Unexpect type.");
        }
    }

    // 设置起始时间
    public void setStartTime() {
        mDatePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateUtil.setHistoryStartDate(year, monthOfYear + 1, dayOfMonth);
                        updateTimeTable();
                    }
                }, dateUtil.getHistoryStartYear(), dateUtil.getHistoryStartMonth() - 1,
                dateUtil.getHistoryStartDayOfMonth());
        mDatePickerDialog.setTitle(getString(R.string.set_start_time));
        mDatePickerDialog.show();
    }

    // 设置结束时间
    public void setEndTime() {
        mDatePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateUtil.setHistoryEndDate(year, monthOfYear + 1, dayOfMonth);
                        updateTimeTable();
                    }
                }, dateUtil.getHistoryEndYear(), dateUtil.getHistoryEndMonth() - 1,
                dateUtil.getHistoryEndDayOfMonth());
        mDatePickerDialog.setTitle(getString(R.string.set_end_time));
        mDatePickerDialog.show();
    }

    // 更新时间列表
    public void updateTimeTable() {
        startDateTextView.setText(dateUtil.getHistoryStartDate());
        startDayTextView.setText(dateUtil.getHistoryStartDayOfWeek());
        endDateTextView.setText(dateUtil.getHistoryEndDate());
        endDayTextView.setText(dateUtil.getHistoryEndDayOfWeek());
    }

    // 监听控件的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.manage_trading_inquiry_set_start_time:
                setStartTime();
                break;
            case R.id.manage_trading_inquiry_set_end_time:
                setEndTime();
                break;
            case R.id.manage_trading_inquiry_query_button:
                // 点击确定按钮，切换视图，开始流水查询。
                mPickDateView.setVisibility(View.INVISIBLE);
                mProgressView.setVisibility(View.VISIBLE);
                sendGETRequest();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                int childPosition, long id) {
        TradingInquiry tradingInquiry = mChildList.get(groupPosition).get(childPosition);
        Toast.makeText(MyApplication.getContext(), getString(R.string.trading_time) +
                        tradingInquiry.getTradingDate() + " " + tradingInquiry.getTradingTime() + "\n" +
                        getString(R.string.merchant_name) + tradingInquiry.getMerchantName() + "\n" +
                        getString(R.string.trading_name) + tradingInquiry.getTradingName() + "\n" +
                        getString(R.string.transaction_amount) + tradingInquiry.getTransactionAmount() +
                        "\n" + getString(R.string.balance_after_trading) + tradingInquiry.getBalance(),
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
                R.layout.item_manage_trading_inquiry_group, null);
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

    /**
     * 从historyDataList中获取数据，配置mGroupList和mChildList
     */
    private void matchDataWithAdapterLists() {
        LogUtil.d(TAG, "Type :" + type + " matchDataWithAdapterLists.");
        if (dataList.isEmpty()) {
            // 没有搜索到数据，显示未找到数据图像
            mImageView.setVisibility(View.VISIBLE);
            Glide
                    .with(MyApplication.getContext())
                    .load(R.drawable.nothing_founded_404)
                    .into(mImageView);
            // 总交易额设为0
            sumTransaction = 0;
            // 设置初始化标志
            communicateListener.setDataListInitFlag(type, true);
            // 隐藏ProgressView
            mProgressView.setVisibility(View.GONE);
            // 清除正在加载标志
            communicateListener.setLoadingFlag(type, false);
        } else {
            // 有数据
            // 初始化mGroupList和mChildList
            mGroupList = new ArrayList<>();
            mChildList = new ArrayList<>();
            // 导入mGroupList
            String tempDate = "";
            String tempDateFromHashMapList;  // 直接从HashMapList中获取的日期
            // 通过循环遍历historyDataList来得到mGroupList
            for (int i = 0; i < dataList.size(); i++) {
                tempDateFromHashMapList = dataList.get(i).get("TradingDate");
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
            sumTransaction = 0;
            for (int i = 0; i < mGroupList.size(); i++) {
                tempDate = mGroupList.get(i).getTitle();
                // 进入一个新的组，要有一个新的childTempList
                childTempList = new ArrayList<>();
                for (int j = 0; j < dataList.size(); j++) {
                    String childDate = dataList.get(j).get("TradingDate");
                    // 如果日期相同（包含组名）则属于该组
                    if (childDate.contains(tempDate)) {
                        TradingInquiry tradingInquiry = new TradingInquiry();
                        tradingInquiry.setTradingDate(dataList.get(j).get("TradingDate"));
                        tradingInquiry.setTradingTime(dataList.get(j).get("TradingTime"));
                        tradingInquiry.setMerchantName(dataList.get(j).get("MerchantName"));
                        tradingInquiry.setTradingName(dataList.get(j).get("TradingName"));
                        tradingInquiry.setTransactionAmount(dataList.get(j).get("TransactionAmount"));
                        // 计算总消费额
                        if (Double.valueOf(dataList.get(j).get("TransactionAmount")) < 0) {
                            sumTransaction = sumTransaction +
                                    Double.valueOf(dataList.get(j).get("TransactionAmount"));
                        }
                        tradingInquiry.setBalance(dataList.get(j).get("Balance"));
                        childTempList.add(tradingInquiry);
                    }
                }
                // 把这一组的childTempList添加到mChildList
                mChildList.add(childTempList);
            }
            setupWithAdapter();
        }
    }

    /**
     * 设置Adapter及监听ListView相关事件
     */
    private void setupWithAdapter() {
        LogUtil.d(TAG, "Type :" + type + " setupWithAdapter.");
        mAdapter = new TradingInquiryExpandableListAdapter(MyApplication.getContext(),
                mGroupList, R.layout.item_manage_trading_inquiry_group,
                mChildList, R.layout.item_manage_trading_inquiry_child);
        mListView.setAdapter(mAdapter);
        // 如果有数据，展开所有group
        if (!dataList.isEmpty()) {
            for (int i = 0, count = mListView.getCount(); i < count; i++) {
                mListView.expandGroup(i);
            }
        }
        // 设置监听事件
        mListView.setOnHeaderUpdateListener(ManageTradingInquiryFragment.this);
        mListView.setOnGroupClickListener(ManageTradingInquiryFragment.this);
        mListView.setOnChildClickListener(ManageTradingInquiryFragment.this);
        // 设置初始化标志
        communicateListener.setDataListInitFlag(type, true);
        // 显示ListView
        mListView.setVisibility(View.VISIBLE);
        // 隐藏ProgressView
        mProgressView.setVisibility(View.GONE);
        // 清除正在加载标志
        communicateListener.setLoadingFlag(type, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        communicateListener = null;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    // 显示sumTransaction
    public void showSumTransaction() {
        DecimalFormat df = new DecimalFormat("0.00");
        if (type == CONSTANT_HISTORY) {
            Snackbar.make(getActivity().findViewById(
                    R.id.activity_trading_inquiry_coordinator_layout),
                    getString(R.string.start_and_end_date) + dateUtil.getHistoryStartDate() + "—" +
                            dateUtil.getHistoryEndDate() + "\n" +
                            getString(R.string.total_transaction_amount) +
                            String.valueOf(df.format(-sumTransaction)),
                    Snackbar.LENGTH_SHORT).show();
        } else if (type == CONSTANT_TODAY) {
            Snackbar.make(getActivity().findViewById(
                    R.id.activity_trading_inquiry_coordinator_layout),
                    getString(R.string.today_total_transaction_amount) +
                            String.valueOf(df.format(-sumTransaction)),
                    Snackbar.LENGTH_SHORT).show();
        } else if (type == CONSTANT_WEEK) {
            Snackbar.make(getActivity().findViewById(
                    R.id.activity_trading_inquiry_coordinator_layout),
                    getString(R.string.week_total_transaction_amount) +
                            String.valueOf(df.format(-sumTransaction)),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    // 返回选择日期界面
    public void backToPickDateView() {
        if (type == CONSTANT_HISTORY) {
            mPickDateView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.INVISIBLE);
            mImageView.setVisibility(View.INVISIBLE);
            // 刷新页码和数据列表
            pageIndex = 1;
            maxPageIndex = 1;
            firstTimeToParseFlag = true;
            dataList.clear();
            mGroupList.clear();
            mChildList.clear();
            mAdapter.notifyDataSetChanged();
            // 将其设置为未初始化
            communicateListener.setDataListInitFlag(type, false);
        }
    }

    // 用于判断对应类型的Fragment是否初始化过
    public interface CommunicateListener {
        // 用于获得初始化状态
        boolean getDataListInitFlag(int type);

        // 设置初始化状态
        void setDataListInitFlag(int type, boolean flag);

        // 设置加载状态
        void setLoadingFlag(int type, boolean flag);

        // 传递Tag
        void getFragmentTag(int type, String tag);
    }

    /**
     * 通过网站返回的html文本解析数据
     * 首次解析会得到最大页码maxIndex
     * 当存在更多页码（pageIndex < maxIndex）时，再次发送GET请求，并进行解析
     * 结果保存在ManageTradingInquiryActivity中的historyDataList
     * <p/>
     * 注意：在解析到最大页码（即最后一页 maxIndex）时，html文本中最大页码maxIndex会被替代为“尾页”，
     * 所以要通过firstTimeToParseFlag进行标识，仅在首次解析时获取maxIndex
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
                        dataList.add(map);
                    }
                }
                // 首次解析时，获得maxIndex
                if (firstTimeToParseFlag) {
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
                    firstTimeToParseFlag = false;
                }

            } catch (Exception e) {
                PgyCrashManager.reportCaughtException(MyApplication.getContext(), e);
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
                 * 如果当前页码是最大页码，说明已准备好dataList加载完成
                 * 通过matchDataWithAdapterLists，准备mAdapter的数据
                 */
                matchDataWithAdapterLists();
            }
        }
    }
}
