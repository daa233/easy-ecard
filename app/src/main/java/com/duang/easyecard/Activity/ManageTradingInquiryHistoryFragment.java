package com.duang.easyecard.Activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.duang.easyecard.Util.TradingInquiryDateUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.rey.material.widget.Button;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageTradingInquiryHistoryFragment extends Fragment implements View.OnClickListener {

    protected static ScrollView pickDateView;
    protected static LinearLayout resultView;
    private LinearLayout setStartTimeLayout;
    private LinearLayout setEndTimeLayout;
    private TextView startDateTextView;
    private TextView startDayTextView;
    private TextView endDateTextView;
    private TextView endDayTextView;
    private Button queryButton;

    private TradingInquiryDateUtil myDateUtil = null;
    private DatePickerDialog mDatePickerDialog;

    private String TAG = "ManageTradingInquiryHistoryFragment";

    private String response;
    private int maxPageIndex = 1;  // 最大页码，默认为1
    private int pageIndex = 1;

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

        // 根据TAB的选择状态来显示布局
        chooseViewByState(ManageTradingInquiryActivity.HISTORY_TAB_INIT_FLAG);

        // 初始化DateUtil
        myDateUtil = new TradingInquiryDateUtil(getActivity());
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
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                          Throwable error) {
                        // 网络错误
                        LogUtil.d(TAG, new String(responseBody));
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
                        myDateUtil.setHistoryStartDate(year, monthOfYear, dayOfMonth);
                        updateTimeTable();
                    }
                }, myDateUtil.getHistoryStartYear(), myDateUtil.getHistoryStartMonth(),
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
                        myDateUtil.setHistoryEndDate(year, monthOfYear, dayOfMonth);
                        updateTimeTable();
                    }
                }, myDateUtil.getHistoryEndYear(), myDateUtil.getHistoryEndMonth(),
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
        // 将HistoryTabInitFlag置为true
        ManageTradingInquiryActivity.HISTORY_TAB_INIT_FLAG = true;
        // 开始“历史流水”查询
        initData();
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
