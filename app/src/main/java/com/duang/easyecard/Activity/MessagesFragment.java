package com.duang.easyecard.Activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duang.easyecard.R;
import com.rey.material.widget.ListView;
import com.yalantis.phoenix.PullToRefreshView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {

    private View viewFragment;
    private PullToRefreshView mPullToRefreshView;
    private ListView mListView;

    public MessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewFragment = inflater.inflate(R.layout.fragment_messages, container, false);
        // 实例化控件
        mPullToRefreshView = (PullToRefreshView) viewFragment.findViewById(
                R.id.messages_pull_to_refresh_view);
        mListView = (ListView) viewFragment.findViewById(R.id.messages_list_view);
        return viewFragment;
    }

}
