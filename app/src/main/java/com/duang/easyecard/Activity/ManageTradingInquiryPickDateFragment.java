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
 * {@link ManageTradingInquiryPickDateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ManageTradingInquiryPickDateFragment extends Fragment {

    private UITableView setStartTimeTableView;
    private UITableView setEndTimeTableView;

    // private OnFragmentInteractionListener mListener;

    public ManageTradingInquiryPickDateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_trading_inquiry_pick_date,
                container, false);
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
        setStartTimeTableView.commit();
        setEndTimeTableView.commit();
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
    }
/*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
*/
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *//*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
