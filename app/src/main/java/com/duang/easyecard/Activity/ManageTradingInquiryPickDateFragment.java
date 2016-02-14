package com.duang.easyecard.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duang.easyecard.R;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ManageTradingInquiryPickDateFragment.OnQureyButtonClickListener} interface
 * to handle interaction events.
 */
public class ManageTradingInquiryPickDateFragment extends Fragment {

    private View viewFragment;
    private UITableView setStartTimeTableView;
    private UITableView setEndTimeTableView;

    private OnQureyButtonClickListener mCallbackListener;

    public ManageTradingInquiryPickDateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (viewFragment == null) {
            viewFragment = inflater.inflate(R.layout.fragment_manage_trading_inquiry_pick_date,
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
        initView();
    }

    private void initView() {
        // 实例化控件
        setStartTimeTableView = (UITableView) getActivity().findViewById(
                R.id.manage_trading_inquiry_set_start_time);
        setEndTimeTableView = (UITableView) getActivity().findViewById(
                R.id.manage_trading_inquiry_set_end_time);
        generateTableItem(setStartTimeTableView, "起始时间", "2016-12-23", "周五");
        generateTableItem(setEndTimeTableView, "结束时间", "2016-12-23", "周五");
        updateTableItem(setStartTimeTableView, "起始时间哈哈", "2323-23-22", "粥吧");
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
        if (context instanceof OnQureyButtonClickListener) {
            mCallbackListener = (OnQureyButtonClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbackListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnQureyButtonClickListener {
        void onQueryBtnClick(View v);
    }
}
