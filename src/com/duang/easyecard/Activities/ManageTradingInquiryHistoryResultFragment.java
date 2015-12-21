package com.duang.easyecard.Activities;

import com.duang.easyecard.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ManageTradingInquiryHistoryResultFragment extends Fragment {

	private View viewFragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		viewFragment =  inflater.inflate(
				R.layout.fragment_trading_inquiry_history_result,
				container, false);
		
		return viewFragment;
	}

}
