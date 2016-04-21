package com.duang.easyecard.Activity;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Model.MessagesListViewItem;
import com.duang.easyecard.Model.Notice;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.duang.easyecard.Util.MessagesListViewAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yalantis.phoenix.PullToRefreshView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private View viewFragment;
    private PullToRefreshView mPullToRefreshView;
    private ListView mListView;

    private AsyncHttpClient httpClient;
    private String response;
    private MessagesListViewAdapter mAdapter;
    private List<MessagesListViewItem> dataList;
    private String sysUserID = "";
    private boolean refreshingFlag = false;
    private int[] iconImageArray = {
            R.drawable.messages_inbox,
            R.drawable.messages_sent,
            R.drawable.messages_send,
            R.drawable.messages_board,
            R.drawable.messages_question
    };
    private String[] titleArray;
    private String newMessagesCount = "";
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
        return viewFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        initData();
    }

    private void initView() {
        // 实例化控件
        mPullToRefreshView = (PullToRefreshView) viewFragment.findViewById(
                R.id.messages_pull_to_refresh_view);
        mListView = (ListView) viewFragment.findViewById(R.id.messages_list_view);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 设置refreshingFlag为true
                refreshingFlag = true;
                // 发送GET请求到Index，以解析sysUserID，然后获取未读消息数目
                sendGETRequestToIndex();
            }
        });
    }

    // 初始化数据
    private void initData() {
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getActivity().getApplication();
        httpClient = myApp.getHttpClient();
        // ItemTitle
        titleArray = new String[]{
                getString(R.string.messages_inbox),
                getString(R.string.messages_sent),
                getString(R.string.messages_send),
                getString(R.string.messages_board),
                getString(R.string.messages_questions)
        };
        // 发送GET请求到Index，以解析sysUserID，然后获取未读消息数目
        sendGETRequestToIndex();
    }

    // 组建列表
    private void createList() {
        dataList = new ArrayList<>();
        MessagesListViewItem item;
        if (iconImageArray.length == titleArray.length) {
            for (int i = 0; i < titleArray.length; i++) {
                item = new MessagesListViewItem(titleArray[i]);
                item.setIconResId(iconImageArray[i]);
                item.setArrowResId(arrowResId);
                if (i == 0 && !newMessagesCount.isEmpty()
                        && Integer.valueOf(newMessagesCount) != 0) {
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

    // 发送GET请求到主页，解析sysUserID
    private void sendGETRequestToIndex() {
        httpClient.get(UrlConstant.INDEX, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 响应成功
                response = new String(responseBody);
                // 解析响应数据
                new JsoupHtmlData().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // 网络错误
                LogUtil.e(TAG, getString(R.string.network_error) + "at sendGETRequestToIndex()");
                Toast.makeText(MyApplication.getContext(), getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
                // 停止PullToRefreshView的刷新
                stopRefreshing();
                error.printStackTrace();
            }
        });
    }

    // 停止PullToRefreshView的刷新
    private void stopRefreshing() {
        // 停止PullToRefreshView的刷新
        mPullToRefreshView.setRefreshing(false);
        // 设置refreshingFlag为fasle
        refreshingFlag = false;
    }

    // 发送GET请求，获取未读消息数目
    private void sendGETRequestToGetAllMyUnreadCount(String id) {
        httpClient.get(UrlConstant.getAllMyUnreadCount(id), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 响应成功，获取未读消息数目
                newMessagesCount = new String(responseBody);
                // 组建列表
                createList();
                if (refreshingFlag) {
                    Toast.makeText(MyApplication.getContext(), getString(R.string.refresh_complete),
                            Toast.LENGTH_SHORT).show();
                }
                // 停止PullToRefreshView的刷新
                stopRefreshing();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 网络错误
                LogUtil.e(TAG, getString(R.string.network_error) + "at getting unread count.");
                Toast.makeText(MyApplication.getContext(), getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
                // 停止PullToRefreshView的刷新
                stopRefreshing();
                error.printStackTrace();
            }
        });
    }

    // 解析响应数据
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            LogUtil.d(TAG, "Start Jsoup");
            Document doc;
            try {
                doc = Jsoup.parse(response);
                Elements inputs = doc.select("input");
                for (Element input : inputs) {
                    if (input.attr("id").equals("hidSysUserID")) {
                        sysUserID = input.attr("value");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (sysUserID.isEmpty()) {
                // 没有解析出sysUserID
            } else {
                // 已经解析出sysUserID，发送GET请求获取未读消息数目
                sendGETRequestToGetAllMyUnreadCount(sysUserID);
            }
        }
    }
}
