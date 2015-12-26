package com.duang.easyecard.Activities;

import com.duang.easyecard.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ManageTradingInquiryDayFragment extends Fragment{

	private View viewFragment;
	
	public static int INIT_FLAG = 1;  // 首次初始化标志
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (viewFragment == null) {
			viewFragment =  inflater.inflate(
					R.layout.fragment_trading_inquiry_day,container, false);
		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误
		ViewGroup parent = (ViewGroup) viewFragment.getParent();
		if (parent != null) {
			parent.removeView(viewFragment);
		}
		return viewFragment;
	}
}
