package com.duang.easyecard.Activities;

import com.duang.easyecard.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ManageTradingInquiryHistoryResultFragment extends Fragment {

	private View viewFragment;
	private TextView tv;
	private Button btn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		viewFragment =  inflater.inflate(
				R.layout.fragment_trading_inquiry_history_result,
				container, false);
		
		return viewFragment;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		tv = (TextView) getActivity().findViewById(
				R.id.fragment_trading_inquiry_history_result);
		btn = (Button) getActivity().findViewById(R.id.fragment_trading_inquiry_history_result_btn);
		tv.setText(ManageTradingInquiryActivity.startTime + ManageTradingInquiryActivity.endTime);
		btn.setText(ManageTradingInquiryActivity.startTime + ManageTradingInquiryActivity.endTime);
	}

}
