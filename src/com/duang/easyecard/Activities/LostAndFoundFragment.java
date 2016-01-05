package com.duang.easyecard.Activities;

import com.duang.easyecard.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class LostAndFoundFragment extends Fragment implements OnClickListener{
	
	private View viewFragment;
	private LinearLayout lostLinearLayout;
	private LinearLayout foundLinearLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewFragment = inflater.inflate(
				R.layout.fragment_lost_and_found, null);
		initView();
		return viewFragment;
	}
	// 初始化View
	private void initView() {
		lostLinearLayout = (LinearLayout) viewFragment.findViewById(
				R.id.lost_and_found_fragment_lost_linear_layout);
		foundLinearLayout = (LinearLayout) viewFragment.findViewById(
				R.id.lost_and_found_fragment_found_linear_layout);
		lostLinearLayout.setOnClickListener(this);
		foundLinearLayout.setOnClickListener(this);
	}
	// 监听控件的点击事件
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.lost_and_found_fragment_lost_linear_layout:
			// 跳转到失卡信息浏览界面
			Intent intent = new Intent(getActivity(),
					LostAndFoundInformationBrowsingActivity.class);
			startActivity(intent);
			break;
		case R.id.lost_and_found_fragment_found_linear_layout:
			// 跳转到丢失卡登记界面
			intent = new Intent(getActivity(),
					LostAndFoundRegistrationActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}

