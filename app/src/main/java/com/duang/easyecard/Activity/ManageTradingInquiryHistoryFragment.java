package com.duang.easyecard.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.duang.easyecard.R;
import com.duang.easyecard.Util.TradingInquiryDateUtil;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.Button;

import java.util.Calendar;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageTradingInquiryHistoryFragment extends Fragment {

    private View viewFragment;
    private UITableView setStartTimeTableView;
    private UITableView setEndTimeTableView;
    private Button queryButton;

    private ScrollView pickDateView;
    private ListView resultListView;

    public ManageTradingInquiryHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewFragment = inflater.inflate(R.layout.fragment_manage_trading_inquiry,
                container, false);
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
        resultListView = (ListView) getActivity().findViewById(
                R.id.manage_trading_inquiry_history_result_list_view);

        setStartTimeTableView = (UITableView) getActivity().findViewById(
                R.id.manage_trading_inquiry_set_start_time);
        setEndTimeTableView = (UITableView) getActivity().findViewById(
                R.id.manage_trading_inquiry_set_end_time);
        queryButton = (Button) getActivity().findViewById(R.id.manage_trading_inquiry_query_button);
        /// 初始化“起始时间”和“结束时间”
        initTimeTableView();
        final TradingInquiryDateUtil myDateUtil = new TradingInquiryDateUtil(getActivity());
        // 监听设置起始时间的点击事件
        setStartTimeTableView.setClickListener(new UITableView.ClickListener() {
            boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;
            @Override
            public void onClick(int i) {
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
                        updateTableItem(setStartTimeTableView, getString(R.string.start_time),
                                myDateUtil.getHistoryStartDate(),
                                myDateUtil.getHistoryStartDayOfWeek());
                        super.onPositiveActionClicked(fragment);
                    }
                    // 取消选择
                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                }       // 设置日期选择范围
                        .dateRange(1, 1, 2006,
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
        });
        // 监听设置结束时间的点击事件
        setEndTimeTableView.setClickListener(new UITableView.ClickListener() {
            boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;
            @Override
            public void onClick(int i) {
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
                        updateTableItem(setEndTimeTableView, getString(R.string.end_time),
                                myDateUtil.getHistoryEndDate(),
                                myDateUtil.getHistoryEndDayOfWeek());
                        super.onPositiveActionClicked(fragment);
                    }
                    // 取消选择
                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                }       // 设置日期选择范围
                        .dateRange(1, 1, 2006,
                                myDateUtil.getTodayDayOfMonth(), myDateUtil.getTodayMonth(),
                                myDateUtil.getTodayYear())
                        // 设置默认日期
                        .date(myDateUtil.getHistoryEndDayOfMonth(),
                                myDateUtil.getHistoryEndMonth(),
                                myDateUtil.getHistoryEndYear());
                builder.positiveAction(getString(R.string.OK))
                        .negativeAction(getString(R.string.Cancel));
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getFragmentManager(), null);
            }
        });
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDateView.setVisibility(View.INVISIBLE);
                resultListView.setVisibility(View.VISIBLE);
            }
        });
    }
    // 初始化时间选择列表
    private void initTimeTableView() {
        TradingInquiryDateUtil myDateUtil = new TradingInquiryDateUtil(getActivity());
        // 显示到UITableView
        generateTableItem(setStartTimeTableView, getString(R.string.start_time),
                myDateUtil.getHistoryStartDate(),
                myDateUtil.getHistoryStartDayOfWeek());
        generateTableItem(setEndTimeTableView, getString(R.string.end_time),
                myDateUtil.getHistoryEndDate(),
                myDateUtil.getHistoryEndDayOfWeek());
    }

    // 构造UItableView的列表项，传入title和content
    private void generateTableItem(UITableView tableView, String title, String date, String day) {
        LayoutInflater mLayoutInflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = (LinearLayout) mLayoutInflater.inflate(
                R.layout.table_view_custom_item_2, null);
        TextView titleText = (TextView) linearLayout.getChildAt(0);
        titleText.setText(title);
        TextView dateText = (TextView) linearLayout.getChildAt(1);
        dateText.setText(date);
        TextView dayText = (TextView) linearLayout.getChildAt(2);
        dayText.setText(day);
        ViewItem v = new ViewItem(linearLayout);
        v.setClickable(true);
        tableView.addViewItem(v);
        tableView.commit();
    }
    // 更新UITableView的列表项
    private void updateTableItem(UITableView tableView, String title, String date, String day) {
        tableView.clear();
        generateTableItem(tableView, title, date, day);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
