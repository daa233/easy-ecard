package com.duang.easyecard.Activities;

import com.duang.easyecard.R;
import com.duang.easyecard.GlobalData.MyApplication;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ManageTradingInquiryHistoryFragment extends Fragment implements 
OnClickListener{
	
	
	private MyCallback mCallback;
	
	// 定义一个调用接口
	public interface MyCallback	 {
		public void onBtnClick(View v);
	}
	
	private View viewFragment;
	
	private Button confirmButton;
	private TextView startTimeTitleText;
	private TextView startTimeText;
	private TextView endTimeTitleText;
	private TextView endTimeText;
	private ImageView startTimeArrowImage;
	private ImageView endTimeArrowImage;

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
		
		// 初始化控件，一定要在定义好视图后才来初始化控件，不能放在onCreat()里面
		confirmButton = (Button) viewFragment.findViewById(
				R.id.history_inquiry_confirm_btn);
		startTimeTitleText = (TextView) viewFragment.findViewById(
				R.id.history_inquiry_start_time_title);
		startTimeText = (TextView) viewFragment.findViewById(
				R.id.history_inquiry_start_time);
		startTimeArrowImage = (ImageView) viewFragment.findViewById(
				R.id.history_inquiry_start_time_img);
		endTimeTitleText = (TextView) viewFragment.findViewById(
				R.id.history_inquiry_end_time_title);
		endTimeText = (TextView) viewFragment.findViewById(
				R.id.history_inquiry_end_time);
		endTimeArrowImage = (ImageView) viewFragment.findViewById(
				R.id.history_inquiry_end_time_img);
		// 监听控件的点击
		confirmButton.setOnClickListener(this);
		startTimeTitleText.setOnClickListener(this);
		startTimeText.setOnClickListener(this);
		startTimeArrowImage.setOnClickListener(this);
		endTimeTitleText.setOnClickListener(this);
		endTimeText.setOnClickListener(this);
		endTimeArrowImage.setOnClickListener(this);
	}

	// 控件的点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.history_inquiry_confirm_btn:
			mCallback.onBtnClick(v);
			break;
		case R.id.history_inquiry_start_time_title:
		case R.id.history_inquiry_start_time:
		case R.id.history_inquiry_start_time_img:
			Toast.makeText(MyApplication.getContext(), "StartTime",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.history_inquiry_end_time_title:
		case R.id.history_inquiry_end_time:
		case R.id.history_inquiry_end_time_img:
			Toast.makeText(MyApplication.getContext(), "EndTime",
					Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mCallback = null;  // 移除前赋值为空
	}

}
