package com.duang.easyecard.Activities;

import com.duang.easyecard.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ManageTradingInquiryHistoryFragment extends Fragment {
	
	
	private MyCallback mCallback;
	
	// 定义一个调用接口
	public interface MyCallback	 {
		public void onBtnClick(View v);
	}
	
	private View viewFragment;
	
	private Button btn;

	public static int LAYOUT_FLAG = 1;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(!(activity instanceof MyCallback)) {
			throw new IllegalStateException(
					"fragment所在的Activity必须实现Callbacks接口");
		}
		//把绑定的activity当成callback对象
		mCallback = (MyCallback)activity;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		viewFragment =  inflater.inflate(
				R.layout.fragment_trading_inquiry_history, container, false);
		return viewFragment;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 绑定控件
		btn = (Button) viewFragment.findViewById(R.id.btn);
		// 一定要在定义好视图后才来初始化控件，不能放在onCreat()里面
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.onBtnClick(v);
			}
		});
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mCallback = null;  // 移除前赋值为空
	}

}
