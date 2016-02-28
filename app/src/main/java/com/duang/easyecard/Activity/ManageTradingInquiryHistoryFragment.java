package com.duang.easyecard.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.rey.material.widget.Button;

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
public class ManageTradingInquiryHistoryFragment extends Fragment implements View.OnClickListener,
        ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener,
        PinnedHeaderListView.OnHeaderUpdateListener {

    protected static ScrollView pickDateView;
    protected static LinearLayout resultView;

    private PinnedHeaderListView mListView;
    private LinearLayout setStartTimeLayout;
    private LinearLayout setEndTimeLayout;
    private TextView startDateTextView;
    private TextView startDayTextView;
    private TextView endDateTextView;
    private TextView endDayTextView;
    private Button queryButton;

    private TradingInquiryDateUtil myDateUtil;
    private DatePickerDialog mDatePickerDialog;
    private ProgressDialog mProgressDialog;
    private List<Group> mGroupList;
    private List<List<TradingInquiry>> mChildList;
    private TradingInquiryExpandableListAdapter mAdapter;

    private String TAG = "ManageTradingInquiryHistoryFragment";

    private String response;
    private int maxPageIndex = 1;  // 最大页码，默认为1
    private int pageIndex = 1;
    private boolean FIRST_TIME_TO_PARSE_FLAG = true;  // 首次解析标志

    public ManageTradingInquiryHistoryFragment() {
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
        return inflater.inflate(R.layout.fragment_manage_trading_inquiry, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.d(TAG, "onActivityCreated");
        initView();
    }

    private void initView() {
        // 实例化控件
        pickDateView = (ScrollView) getActivity().findViewById(
                R.id.manage_trading_inquiry_history_pick_date);
        resultView = (LinearLayout) getActivity().findViewById(
                R.id.manage_trading_inquiry_history_result);

        mListView = (PinnedHeaderListView) getActivity().findViewById(
                R.id.manage_trading_inquiry_list_view);
        setStartTimeLayout = (LinearLayout) getActivity().findViewById(
                R.id.manage_trading_inquiry_set_start_time);
        setEndTimeLayout = (LinearLayout) getActivity().findViewById(
                R.id.manage_trading_inquiry_set_end_time);
        startDateTextView = (TextView) getActivity().findViewById(
                R.id.manage_trading_inquiry_start_date);
        startDayTextView = (TextView) getActivity().findViewById(
                R.id.manage_trading_inquiry_start_day);
        endDateTextView = (TextView) getActivity().findViewById(
                R.id.manage_trading_inquiry_end_date);
        endDayTextView = (TextView) getActivity().findViewById(
                R.id.manage_trading_inquiry_end_day);
        queryButton = (Button) getActivity().findViewById(
                R.id.manage_trading_inquiry_query_button);

        // 获得从Activity传递过来的DateUtil
        myDateUtil = ManageTradingInquiryActivity.myDateUtil;

        // 根据TAB的选择状态来显示布局
        chooseViewByState(ManageTradingInquiryActivity.HISTORY_TAB_INIT_FLAG);
        // 监听控件的点击事件
        setStartTimeLayout.setOnClickListener(this);
        setEndTimeLayout.setOnClickListener(this);
        queryButton.setOnClickListener(this);
        // 更新时间列表
        updateTimeTable();
    }

    // 初始化数据，开始“历史流水”查询
    public void initData() {
        // 组装Url地址
        UrlConstant.trjnListStartTime = myDateUtil.getHistoryStartYear() + "-" +
                myDateUtil.getHistoryStartMonth() + "-" + myDateUtil.getHistoryStartDayOfMonth();
        UrlConstant.trjnListEndTime = myDateUtil.getHistoryEndYear() + "-" +
                myDateUtil.getHistoryEndMonth() + "-" + myDateUtil.getHistoryEndDayOfMonth();
        UrlConstant.trjnListPageIndex = pageIndex;
        LogUtil.d(TAG, UrlConstant.getTrjnListHistroy());
        // 发送GET请求
        ManageTradingInquiryActivity.httpClient.get(UrlConstant.getTrjnListHistroy(),
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
                        LogUtil.e(TAG, new String(responseBody));
                        Toast.makeText(getContext(), R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void chooseViewByState(boolean historyTabInitFlag) {
        if (historyTabInitFlag) {
            // 加载结果界面
            pickDateView.setVisibility(View.GONE);
            resultView.setVisibility(View.VISIBLE);
            matchDataWithAdapterLists();
            setupWithAdapter();
        } else {
            // 加载时间选择界面
            pickDateView.setVisibility(View.VISIBLE);
            resultView.setVisibility(View.GONE);
        }
    }

    // 设置起始时间
    public void setStartTime() {
        mDatePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myDateUtil.setHistoryStartDate(year, monthOfYear + 1, dayOfMonth);
                        updateTimeTable();
                    }
                }, myDateUtil.getHistoryStartYear(), myDateUtil.getHistoryStartMonth() - 1,
                myDateUtil.getHistoryStartDayOfMonth());
        mDatePickerDialog.setTitle(getString(R.string.set_start_time));
        mDatePickerDialog.show();
    }

    // 设置结束时间
    public void setEndTime() {
        mDatePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myDateUtil.setHistoryEndDate(year, monthOfYear + 1, dayOfMonth);
                        updateTimeTable();
                    }
                }, myDateUtil.getHistoryEndYear(), myDateUtil.getHistoryEndMonth() - 1,
                myDateUtil.getHistoryEndDayOfMonth());
        mDatePickerDialog.setTitle(getString(R.string.set_end_time));
        mDatePickerDialog.show();
    }

    // 更新时间列表
    public void updateTimeTable() {
        startDateTextView.setText(myDateUtil.getHistoryStartDate());
        startDayTextView.setText(myDateUtil.getHistoryStartDayOfWeek());
        endDateTextView.setText(myDateUtil.getHistoryEndDate());
        endDayTextView.setText(myDateUtil.getHistoryEndDayOfWeek());
    }

    // queryButton的点击事件
    public void onQueryButtonClick() {
        // 将pickDateView隐藏，显示resultView
        pickDateView.setVisibility(View.GONE);
        resultView.setVisibility(View.VISIBLE);
        // 初始化historyDataList
        ManageTradingInquiryActivity.historyDataList = new ArrayList<>();
        // 将首次解析标志FIRST_TIME_TO_PARSE_FLAG置为true
        FIRST_TIME_TO_PARSE_FLAG = true;
        // 开始“历史流水”查询
        initData();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                int childPosition, long id) {
        // 点击子项显示卡余额
        Toast.makeText(MyApplication.getContext(),
                mChildList.get(groupPosition).get(childPosition).getTradingDate()
                        + "-" +
                        mChildList.get(groupPosition).get(childPosition).getTradingTime()
                        + "  " + "交易后余额  " +
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

    /**
     * 通过网站返回的html文本解析数据
     * 首次解析会得到最大页码maxIndex
     * 当存在更多页码（pageIndex < maxIndex）时，再次发送GET请求，并进行解析
     * 结果保存在ManageTradingInquiryActivity中的historyDataList
     * <p/>
     * 注意：在解析到最大页码（即最后一页 maxIndex）时，html文本中最大页码maxIndex会被替代为“尾页”，
     * 所以要通过FIRST_TIME_TO_PARSE_FLAG进行标识，仅在首次解析时获取maxIndex
     */
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // 只有首次解析时才新建 progressDialog
            if (FIRST_TIME_TO_PARSE_FLAG) {
                // Create a progressDialog
                mProgressDialog = new ProgressDialog(getActivity());
                // Set progressDialog message
                mProgressDialog.setMessage(getString(R.string.loading) + " o(>﹏<)o");
                mProgressDialog.setIndeterminate(false);
                // Show progressDialog
                mProgressDialog.show();
            }
        }

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
                        ManageTradingInquiryActivity.historyDataList.add(map);
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
                initData();
            } else {
                /**
                 * 如果当前页码是最大页码，说明已准备好historyDataList加载完成
                 * 通过matchDataWithAdapterLists，准备mAdapter的数据
                 */
                matchDataWithAdapterLists();
                setupWithAdapter();
                // 耗时操作基本完成呢，关闭mProgressDialog
                mProgressDialog.dismiss();
            }
        }
    }

    /**
     * 从historyDataList中获取数据，配置mGroupList和mChildList
     */
    private void matchDataWithAdapterLists() {
        // 初始化mGroupList和mChildList
        mGroupList = new ArrayList<>();
        mChildList = new ArrayList<>();
        // 没有搜索到数据
        if (ManageTradingInquiryActivity.historyDataList.isEmpty()) {
            // 添加默认数据
            Group group = new Group();
            group.setTitle(" --- 这里空空的，一定不是因为我穷。--- ");
            mGroupList.add(group);
            ArrayList<TradingInquiry> childTempList = new ArrayList<>();
            TradingInquiry tradingInquiry = new TradingInquiry();
            tradingInquiry.setTradingTime("如果  选");
            tradingInquiry.setMerchantName("对了时间  结果 可能");
            tradingInquiry.setTradingName("就会   不  一");
            tradingInquiry.setTransactionAmount(" 样");
            childTempList.add(tradingInquiry);
            mChildList.add(childTempList);
            return;
        }

        // 导入mGroupList
        String tempDate = null;
        String tempDateFromHashMapList;  // 直接从HashMapList中获取的日期
        // 通过循环遍历historyDataList来得到mGroupList
        for (int i = 0; i < ManageTradingInquiryActivity.historyDataList.size(); i++) {
            tempDateFromHashMapList = ManageTradingInquiryActivity
                    .historyDataList.get(i).get("TradingDate");
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
            for (int j = 0; j < ManageTradingInquiryActivity.historyDataList.size(); j++) {
                String childDate = ManageTradingInquiryActivity
                        .historyDataList.get(j).get("TradingDate");
                // 如果日期相同（包含组名）则属于该组
                if (childDate.contains(tempDate)) {
                    TradingInquiry tradingInquiry = new TradingInquiry();
                    tradingInquiry.setTradingDate(ManageTradingInquiryActivity
                            .historyDataList.get(j).get("TradingDate"));
                    tradingInquiry.setTradingTime(ManageTradingInquiryActivity
                            .historyDataList.get(j).get("TradingTime"));
                    tradingInquiry.setMerchantName(ManageTradingInquiryActivity
                            .historyDataList.get(j).get("MerchantName"));
                    tradingInquiry.setTradingName(ManageTradingInquiryActivity
                            .historyDataList.get(j).get("TradingName"));
                    tradingInquiry.setTransactionAmount(ManageTradingInquiryActivity
                            .historyDataList.get(j).get("TransactionAmount"));
                    tradingInquiry.setBalance(ManageTradingInquiryActivity
                            .historyDataList.get(j).get("Balance"));
                    childTempList.add(tradingInquiry);
                }
            }
            // 把这一组的childTempList添加到mChildList
            mChildList.add(childTempList);
        }
    }

    /**
     * 设置Adapter及监听ListView相关事件
     */
    private void setupWithAdapter() {
        mAdapter = new TradingInquiryExpandableListAdapter(getContext(), mGroupList,
                R.layout.manage_trading_inquiry_group_item, mChildList,
                R.layout.manage_trading_inquiry_child_item);
        mListView.setAdapter(mAdapter);

        // 如果有数据，展开所有group
        if (!ManageTradingInquiryActivity.historyDataList.isEmpty()) {
            for (int i = 0, count = mListView.getCount(); i < count; i++) {
                mListView.expandGroup(i);
            }
        }
        // 设置监听事件
        mListView.setOnHeaderUpdateListener(ManageTradingInquiryHistoryFragment.this);
        mListView.setOnGroupClickListener(ManageTradingInquiryHistoryFragment.this);
        mListView.setOnChildClickListener(ManageTradingInquiryHistoryFragment.this);
        // 将HISTORY_TAB_INIT_FLAG置为true
        ManageTradingInquiryActivity.HISTORY_TAB_INIT_FLAG = true;
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
                onQueryButtonClick();
                break;
            default:
                break;
        }
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
