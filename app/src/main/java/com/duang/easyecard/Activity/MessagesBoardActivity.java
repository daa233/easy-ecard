package com.duang.easyecard.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Model.MessageBoardItem;
import com.duang.easyecard.Model.MessagesListViewItem;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.duang.easyecard.Util.MessagesBoardListAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pgyersdk.crash.PgyCrashManager;
import com.rey.material.widget.ProgressView;
import com.yalantis.phoenix.PullToRefreshView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MessagesBoardActivity extends BaseActivity {

    private FloatingActionButton fab;
    private ProgressView mProgressView;
    private PullToRefreshView mPullToRefreshView;
    private ListView mListView;

    private MessagesBoardListAdapter mAdapter;
    private List<MessageBoardItem> dataList;
    private AsyncHttpClient httpClient;
    private String response;
    private String user;
    private String title;
    private String type;
    private String time;
    private String reply;
    private boolean refreshingFlag = false;

    private final String TAG = "MessagesBoardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_board);
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.messages_board_toolbar);
        setSupportActionBar(toolbar);
        setDisplayHomeButton();  // 显示Back按钮
        // 实例化控件
        mProgressView = (ProgressView) findViewById(R.id.messages_board_progress_view);
        mPullToRefreshView = (PullToRefreshView) findViewById(
                R.id.messages_board_pull_to_refresh_view);
        mListView = (ListView) findViewById(R.id.messages_board_list_view);
    }

    private void initData() {
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        // 初始化数据列表
        dataList = new ArrayList<>();
        // 发送预请求
        sendPreGETRequest();
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshingFlag = true;
                // 清空（刷新）数据列表
                dataList.clear();
                sendGETRequest();
            }
        });
    }

    // 预请求
    private void sendPreGETRequest() {
        httpClient.get(UrlConstant.NOTICE_INDEX, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 响应成功，发送GET请求
                LogUtil.d(TAG, "Ready to send GET request.");
                sendGETRequest();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 网络错误
                LogUtil.e(TAG, "Network error.");
                // 使PullToRefreshView退出刷新状态
                mPullToRefreshView.setRefreshing(false);
                mProgressView.setVisibility(View.GONE);
                Toast.makeText(MyApplication.getContext(), getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    // 发送GET请求
    private void sendGETRequest() {
        httpClient.get(UrlConstant.MESSAGE_BOARD, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 响应成功
                response = new String(responseBody);
                new JsoupHtmlData().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // 网络错误
                LogUtil.e(TAG, "Network error.");
                // 使PullToRefreshView退出刷新状态
                mPullToRefreshView.setRefreshing(false);
                mProgressView.setVisibility(View.GONE);
                Toast.makeText(MyApplication.getContext(), getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    // Fab的点击事件
    public void onLeaveMessageFabClick(View v) {
        LogUtil.d(TAG, "onLeaveMessageFabClicked.");
    }

    private void setAdapter() {
        if (refreshingFlag) {
            // 处于刷新状态，表示不是首次加载
            mAdapter.notifyDataSetChanged();
            mPullToRefreshView.setRefreshing(false);
            Toast.makeText(MyApplication.getContext(), getString(R.string.refresh_complete),
                    Toast.LENGTH_SHORT).show();
            refreshingFlag = false;
        } else {
            // 不是刷新，表示首次加载，需要新建适配器
            mAdapter = new MessagesBoardListAdapter(MyApplication.getContext(), dataList,
                    R.layout.item_messages_board_list);
            mListView.setAdapter(mAdapter);
        }
    }

    // 解析响应数据
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            LogUtil.d(TAG, "Start Jsoup: In doInBackground.");
            // 解析返回的responseString
            Document doc;
            try {
                doc = Jsoup.parse(response);
                // 留言列表
                for (Element divMsg : doc.getElementsByClass("divmsg")) {
                    // 留言用户
                    for (Element em : divMsg.select("em")) {
                        user = em.text();
                        LogUtil.d(TAG, "em = " + em.text());
                        break;
                    }
                    // 标题
                    for (Element p : divMsg.select("p")) {
                        title = p.text();
                        LogUtil.d(TAG, "p = " + p.text());
                    }
                    int i = 0;
                    for (Element span : divMsg.select("span")) {
                        if (i == 0) {
                            type = span.text();
                        } else if (i == 1) {
                            time = span.text();
                        } else if (i == 2) {
                            reply = span.text();
                        } else {
                            break;
                        }
                        i++;
                    }
                    MessageBoardItem item = new MessageBoardItem();
                    item.setTime(time);
                    item.setType(type);
                    item.setUser(user);
                    item.setTitle(title);
                    item.setReply(reply);
                    dataList.add(item);
                }
            } catch (Exception e) {
                PgyCrashManager.reportCaughtException(MyApplication.getContext(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // 设置适配器
            setAdapter();
            // 隐藏ProgressView
            mProgressView.setVisibility(View.GONE);
            // 停止PullToRefreshLayout的刷新
            mPullToRefreshView.setRefreshing(false);
        }
    }
}
