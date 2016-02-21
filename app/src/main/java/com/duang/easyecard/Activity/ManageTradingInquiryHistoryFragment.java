package com.duang.easyecard.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.duang.easyecard.R;
import com.duang.easyecard.Util.TradingInquiryDateUtil;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.Button;
import com.rey.material.widget.LinearLayout;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageTradingInquiryHistoryFragment extends Fragment implements View.OnClickListener {

    private View viewFragment;

    private ScrollView pickDateView;
    private LinearLayout resultView;
    private LinearLayout setStartTimeLayout;
    private LinearLayout setEndTimeLayout;
    private TextView startDateTextView;
    private TextView startDayTextView;
    private TextView endDateTextView;
    private TextView endDayTextView;
    private Button queryButton;

    private TradingInquiryDateUtil myDateUtil = null;

    public ManageTradingInquiryHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewFragment = inflater.inflate(R.layout.fragment_manage_trading_inquiry,
                container, false);
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
        // 初始化DateUtil
        myDateUtil = new TradingInquiryDateUtil(getActivity());
        // 监听控件的点击事件
        setStartTimeLayout.setOnClickListener(this);
        setEndTimeLayout.setOnClickListener(this);
        queryButton.setOnClickListener(this);
        // 更新时间列表
        updateTimeTable();
    }

    // 设置起始时间
    public void setStartTime() {
        boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;
        Dialog.Builder builder = new DatePickerDialog.Builder(isLightTheme ?
                R.style.Material_App_Dialog_DatePicker_Light :
                R.style.Material_App_Dialog_DatePicker) {
            // 确定
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                // 更新HistoryStartDate
                DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                Calendar calendar = dialog.getCalendar();
                myDateUtil.setHistoryStartDate(calendar);
                updateTimeTable();
                super.onPositiveActionClicked(fragment);
            }

            // 取消选择
            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }  // 设置日期选择范围
        }.dateRange(1, 1, 2006,
                myDateUtil.getTodayDayOfMonth(), myDateUtil.getTodayMonth(),
                myDateUtil.getTodayYear())
                // 设置初始时间
                .date(myDateUtil.getHistoryStartDayOfMonth(),
                        myDateUtil.getHistoryStartMonth(),
                        myDateUtil.getHistoryStartYear());

        builder.positiveAction(getString(R.string.OK))
                .negativeAction(getString(R.string.Cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    // 设置结束时间
    public void setEndTime() {
        boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;
        Dialog.Builder builder = new DatePickerDialog.Builder(isLightTheme ?
                R.style.Material_App_Dialog_DatePicker_Light :
                R.style.Material_App_Dialog_DatePicker) {
            // 确定
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                // 更新HistoryEndDate
                DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                Calendar calendar = dialog.getCalendar();
                myDateUtil.setHistoryEndDate(calendar);
                updateTimeTable();
                super.onPositiveActionClicked(fragment);
            }

            // 取消选择
            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }  // 设置日期选择范围
        }.dateRange(myDateUtil.getHistoryStartDayOfMonth(), myDateUtil.getHistoryStartMonth(),
                myDateUtil.getHistoryStartYear(),
                myDateUtil.getTodayDayOfMonth(), myDateUtil.getTodayMonth(),
                myDateUtil.getTodayYear())
                // 设置初始时间
                .date(myDateUtil.getHistoryEndDayOfMonth(),
                        myDateUtil.getHistoryEndMonth(),
                        myDateUtil.getHistoryEndYear());
        builder.positiveAction(getString(R.string.OK))
                .negativeAction(getString(R.string.Cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
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
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
}
