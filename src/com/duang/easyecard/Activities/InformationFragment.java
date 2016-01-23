package com.duang.easyecard.Activities;

import com.duang.easyecard.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class InformationFragment extends Fragment {
	
	private View viewFragment;
	private ExpandableListView expandableListView;
	// private MyExpandableListViewAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewFragment = inflater.inflate(
				R.layout.fragment_information, null);
		initView();
		return viewFragment;
	}

	private void initView() {
		// TODO Auto-generated method stub
		
	}

	
}
