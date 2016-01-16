package com.duang.easyecard.Activities;

import com.duang.easyecard.R;
import com.duang.easyecard.UI.XListView;
import com.duang.easyecard.Utils.LostInfoAdapter;

import android.os.Bundle;
import android.widget.CheckBox;

public class LostAndFoundInformationBrowsingActivity extends BaseActivity {
	
	private XListView xListView;
	private CheckBox notFoundedCheckBox;
	private CheckBox foundedCheckBox;
	
	private LostInfoAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_and_found_information_browsing);
		
		initView();
		
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		
	}

	private void initData() {
		// TODO Auto-generated method stub
		
	}
}
