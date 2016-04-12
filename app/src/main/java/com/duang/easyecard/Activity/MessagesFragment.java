package com.duang.easyecard.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.Model.MessagesListViewItem;
import com.duang.easyecard.Model.Notice;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.duang.easyecard.Util.MessagesListViewAdapter;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private View viewFragment;
    private PullToRefreshView mPullToRefreshView;
    private ListView mListView;

    private MessagesListViewAdapter mAdapter;
    private List<MessagesListViewItem> dataList;
    private int[] iconImageArray = {
            R.drawable.messages_inbox,
            R.drawable.messages_sent,
            R.drawable.messages_send,
            R.drawable.messages_board,
            R.drawable.messages_question
    };
    private String[] titleArray;
    private String newMessagesCount = "";
    private int newMessageVisibility;
    private final int arrowResId = R.drawable.ic_keyboard_arrow_right_black_24dp;

    private final String TAG = "MessagesFragment";

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewFragment = inflater.inflate(R.layout.fragment_messages, container, false);
        initView();
        initData();
        return viewFragment;
    }

    private void initView() {
        // 实例化控件
        mPullToRefreshView = (PullToRefreshView) viewFragment.findViewById(
                R.id.messages_pull_to_refresh_view);
        mListView = (ListView) viewFragment.findViewById(R.id.messages_list_view);
    }

    // 初始化数据
    private void initData() {
        // ItemTitle
        titleArray = new String[]{
                getString(R.string.messages_inbox),
                getString(R.string.messages_sent),
                getString(R.string.messages_send),
                getString(R.string.messages_board),
                getString(R.string.messages_questions)
        };

        dataList = new ArrayList<>();
        MessagesListViewItem item;
        if (iconImageArray.length == titleArray.length) {
            for (int i = 0; i < titleArray.length; i++) {
                item = new MessagesListViewItem(titleArray[i]);
                item.setIconResId(iconImageArray[i]);
                item.setArrowResId(arrowResId);
                if (i == 0 && !newMessagesCount.isEmpty()) {
                    // 如果是首项——收件箱，且有新消息，显示新消息按钮
                    item.setNewMessagesCount(newMessagesCount);
                    item.setNewMessageVisibility(View.VISIBLE);
                } else {
                    item.setNewMessageVisibility(View.INVISIBLE);
                }
                dataList.add(item);
            }
        } else {
            // 数据数目不匹配
            LogUtil.e(TAG, "Error: Arrays' lengths don't match.");
        }

        mAdapter = new MessagesListViewAdapter(MyApplication.getContext(), dataList,
                R.layout.item_messages_fragment_list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                // 打开收件箱
                LogUtil.d(TAG, "onItemClick: " + 0);
                startActivity(new Intent(MyApplication.getContext(),
                        MessagesInboxAndSentActivity.class).putExtra("TYPE", Notice.RECEIVED_TYPE));
                break;
            case 1:
                // 已发送
                LogUtil.d(TAG, "onItemClick: " + 1);
                startActivity(new Intent(MyApplication.getContext(),
                        MessagesInboxAndSentActivity.class).putExtra("TYPE", Notice.SENT_TYPE));
                break;
            case 2:
                // 发消息
                LogUtil.d(TAG, "onItemClick: " + 2);
                break;
            case 3:
                // 留言板
                LogUtil.d(TAG, "onItemClick: " + 3);
                startActivity(new Intent(MyApplication.getContext(), MessagesBoardActivity.class));
                break;
            case 4:
                // 常见问题
                LogUtil.d(TAG, "onItemClick: " + 4);
                startActivity(new Intent(MyApplication.getContext(), MessagesFaqActivity.class));
                break;
            default:
                break;
        }
    }
}
