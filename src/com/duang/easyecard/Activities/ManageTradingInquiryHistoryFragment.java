package com.duang.easyecard.Activities;

import java.util.Calendar;

import com.duang.easyecard.R;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

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
	
	private DatePickerDialog datePickerDialog;
	
	private int FIRST_INIT_FLAG = 0;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(!(activity instanceof MyCallback)) {
			throw new IllegalStateException(
					"fragment所在的Activity必须实现Callbacks接口");
		}
		// 把绑定的activity当成callback对象
		mCallback = (MyCallback)activity;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (viewFragment == null) {
			viewFragment =  inflater.inflate(
					R.layout.fragment_trading_inquiry_history,
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
		// 初始化数据
		initData();
		// 监听控件的点击
		confirmButton.setOnClickListener(this);
		startTimeTitleText.setOnClickListener(this);
		startTimeText.setOnClickListener(this);
		startTimeArrowImage.setOnClickListener(this);
		endTimeTitleText.setOnClickListener(this);
		endTimeText.setOnClickListener(this);
		endTimeArrowImage.setOnClickListener(this);
	}
	
	// 初始化数据
	private void initData() {
		// 如果是首次加载，默认结束时间为
		if (FIRST_INIT_FLAG == 0) {
			// 初始化Time
			Calendar calendar = Calendar.getInstance();
			// 结束时间默认值为前一天
			ManageTradingInquiryActivity.endYear = calendar.get(Calendar.YEAR);
			ManageTradingInquiryActivity.endMonthOfYear =
					calendar.get(Calendar.MONTH) + 1;
			ManageTradingInquiryActivity.endDayOfMonth =
					calendar.get(Calendar.DAY_OF_MONTH) - 1;
			ManageTradingInquiryActivity.endDayOfWeek = 
					calendar.get(Calendar.DAY_OF_WEEK) - 1;
			// 在endTime中连接起来，格式为"2015-12-20"
			ManageTradingInquiryActivity.endTime =
					ManageTradingInquiryActivity.endYear + "-"
					+ ManageTradingInquiryActivity.endMonthOfYear + "-"
					+ ManageTradingInquiryActivity.endDayOfMonth;
			// 开始时间默认值为结束时间前90天，借助Calendar来处理
			ManageTradingInquiryActivity.startYear =
					ManageTradingInquiryActivity.endYear;
			ManageTradingInquiryActivity.startMonthOfYear =
					ManageTradingInquiryActivity.endMonthOfYear;
			ManageTradingInquiryActivity.startDayOfMonth =
					ManageTradingInquiryActivity.endDayOfMonth - 90;
			calendar.set(ManageTradingInquiryActivity.startYear,
					ManageTradingInquiryActivity.startMonthOfYear - 1,
					ManageTradingInquiryActivity.startDayOfMonth);
			ManageTradingInquiryActivity.startYear =
					calendar.get(Calendar.YEAR);
			ManageTradingInquiryActivity.startMonthOfYear =
					calendar.get(Calendar.MONTH) + 1;
			ManageTradingInquiryActivity.startDayOfMonth =
					calendar.get(Calendar.DAY_OF_MONTH);
			ManageTradingInquiryActivity.startDayOfWeek = 
					calendar.get(Calendar.DAY_OF_WEEK);
			// 在startTime中连接起来，格式为"2015-12-20"
			ManageTradingInquiryActivity.startTime =
					ManageTradingInquiryActivity.startYear + "-"
					+ ManageTradingInquiryActivity.startMonthOfYear + "-"
					+ ManageTradingInquiryActivity.startDayOfMonth;
			FIRST_INIT_FLAG = 1; // 将首次初始化标志置1
		}
		// 显示在Text中
		endTimeText.setText(ManageTradingInquiryActivity.endTime + "  " +
				ManageTradingInquiryActivity.dayOfWeekToString(
						ManageTradingInquiryActivity.endDayOfWeek));
		
		startTimeText.setText(ManageTradingInquiryActivity.startTime + "  " +
				ManageTradingInquiryActivity.dayOfWeekToString(
						ManageTradingInquiryActivity.startDayOfWeek));
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
			// 显示选择日期对话框
			datePickerDialog = new DatePickerDialog(this.getActivity(),
					new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int month,
						int dayOfMonth) {
					// 引入Calendar是为了得到周几
					Calendar calendar = Calendar.getInstance();
					calendar.set(year, month, dayOfMonth);
					// 传值给startTime，并设置startTimeText显示的时间
					ManageTradingInquiryActivity.startYear = year;
					ManageTradingInquiryActivity.startMonthOfYear = month + 1;
					ManageTradingInquiryActivity.startDayOfMonth = dayOfMonth;
					ManageTradingInquiryActivity.startDayOfWeek =
							calendar.get(Calendar.DAY_OF_WEEK);
					ManageTradingInquiryActivity.startTime =
						ManageTradingInquiryActivity.startYear + "-"
						+ ManageTradingInquiryActivity.startMonthOfYear + "-"
						+ ManageTradingInquiryActivity.startDayOfMonth;
					startTimeText.setText(
							ManageTradingInquiryActivity.startTime +
						"  " + ManageTradingInquiryActivity.dayOfWeekToString(
								ManageTradingInquiryActivity.startDayOfWeek));
				}
			}, ManageTradingInquiryActivity.startYear,
			   ManageTradingInquiryActivity.startMonthOfYear - 1,
			   ManageTradingInquiryActivity.startDayOfMonth);
			datePickerDialog.setTitle("设置起始时间");
			datePickerDialog.show();
			break;
		case R.id.history_inquiry_end_time_title:
		case R.id.history_inquiry_end_time:
		case R.id.history_inquiry_end_time_img:
			// 显示选择日期对话框
			datePickerDialog = new DatePickerDialog(this.getActivity(),
					new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int month,
						int dayOfMonth) {
					// 引入Calendar是为了得到周几
					Calendar calendar = Calendar.getInstance();
					calendar.set(year, month, dayOfMonth);
					// 传值给endTime，并设置endTimeText显示的时间
					ManageTradingInquiryActivity.endYear = year;
					ManageTradingInquiryActivity.endMonthOfYear = month + 1;
					ManageTradingInquiryActivity.endDayOfMonth = dayOfMonth;
					ManageTradingInquiryActivity.endDayOfWeek =
							calendar.get(Calendar.DAY_OF_WEEK);
					ManageTradingInquiryActivity.endTime =
						ManageTradingInquiryActivity.endYear + "-"
						+ ManageTradingInquiryActivity.endMonthOfYear + "-"
						+ ManageTradingInquiryActivity.endDayOfMonth;
					endTimeText.setText(ManageTradingInquiryActivity.endTime +
						"  " + ManageTradingInquiryActivity.dayOfWeekToString(
								ManageTradingInquiryActivity.endDayOfWeek));
				}
			}, ManageTradingInquiryActivity.endYear,
			   ManageTradingInquiryActivity.endMonthOfYear - 1,
	           ManageTradingInquiryActivity.endDayOfMonth);
			datePickerDialog.setTitle("设置结束时间");
			datePickerDialog.show();
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
