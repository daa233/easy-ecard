package com.duang.easyecard.Activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuView;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Model.Notice;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.duang.easyecard.Util.MessagesNoticeListAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yalantis.phoenix.PullToRefreshView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class MessagesInboxActivity extends BaseActivity implements
        SwipeMenuView.OnSwipeItemClickListener, AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private SwipeMenuListView mListView;
    private PullToRefreshView mPullToRefreshView;
    private SweetAlertDialog sweetAlertDialog;
    private List<Notice> dataList;
    private MessagesNoticeListAdapter mAdapter;
    private AsyncHttpClient httpClient;
    private String response;
    private int pageIndex;  // 访问的网页信息的页码
    private int maxPageIndex;  // 最大页码
    private boolean FIRST_TIME_TO_PARSE_FLAG = true;  // 首次解析标志，默认为true
    private boolean TO_DELETE_FLAG = false;
    private int checkedToDeleteCount = 0;

    private final String TAG = "MessagesInboxActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_inbox);
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setDisplayHomeButton();
        // 实例化控件
        mListView = (SwipeMenuListView) findViewById(R.id.messages_inbox_list_view);
        mPullToRefreshView = (PullToRefreshView) findViewById(
                R.id.messages_inbox_pull_to_refresh_view);
        // create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteMenuItem = new SwipeMenuItem(MyApplication.getContext());
                // 设置item参数
                deleteMenuItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteMenuItem.setWidth(dp2px(90));
                deleteMenuItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteMenuItem);
            }
        };
        // 设置creator
        mListView.setMenuCreator(creator);
        // 监听MenuItem的点击事件
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // delete the item
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        // 设置Swipe的方向
        mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        // 监听PullToRefreshView的下拉刷新事件
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 开始刷新
                initData();
            }
        });
        // 监听Item的点击事件
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
    }

    private void initData() {
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        // 初始化数据列表
        dataList = new ArrayList<>();
        // 初始化页码，默认均为1
        pageIndex = 1;
        maxPageIndex = 1;
        // 显示对话框
        sweetAlertDialog = new SweetAlertDialog(MessagesInboxActivity.this,
                SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog
                .setTitleText(getString(R.string.loading))
                .show();
        sendPreGETRequest();
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
                sweetAlertDialog
                        .setTitleText(getString(R.string.network_error))
                        .setConfirmText(getString(R.string.OK))
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                error.printStackTrace();
            }
        });
    }

    // 发送GET请求
    private void sendGETRequest() {
        UrlConstant.receivedNoticeIndex = pageIndex;
        httpClient.get(UrlConstant.getReceivedNotice(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 成功响应
                LogUtil.d(TAG, "GET reponse success.");
                response = new String(responseBody);
                // 解析网页响应
                new JsoupHtmlData().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                LogUtil.d(TAG, "GET reponse failed.");
                // 网络错误
                sweetAlertDialog
                        .setTitleText(getString(R.string.network_error))
                        .setConfirmText(getString(R.string.OK))
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                error.printStackTrace();
            }
        });
    }

    // SwipeItem的点击事件
    @Override
    public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {

    }

    // ListItem的点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.d(TAG, "onItemClick.");
        if (TO_DELETE_FLAG) {
            // 处于正在删除的选择状态
            if (dataList.get(position).isChecked()) {
                // 已经选中，则置为未选中
                dataList.get(position).setChecked(false);
                checkedToDeleteCount--;
            } else {
                // 原本未选中，则置为选中
                dataList.get(position).setChecked(true);
                checkedToDeleteCount++;
            }
            if (checkedToDeleteCount == 0) {
                // 如果一个都没有选中，退出删除编辑状态
                for (int i = 0; i < dataList.size(); i++) {
                    dataList.get(i).setToDelete(false);
                    dataList.get(i).setChecked(false);
                }
                TO_DELETE_FLAG = false;
            }
            mAdapter.notifyDataSetChanged();
        } else {
            // 未处在删除状态，跳转查看详细内容Activity
            LogUtil.d(TAG, "Intent to start Detail Activity.");
        }
    }

    // ListItem的长时间点击事件，在每个Item中显示CheckBox
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.d(TAG, "onItemLongClick.");
        TO_DELETE_FLAG = true;
        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).setToDelete(true);
        }
        dataList.get(position).setChecked(true);
        checkedToDeleteCount++;
        mAdapter.notifyDataSetChanged();
        return false;
    }

    // 解析响应数据
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            LogUtil.d(TAG, "Start Jsoup");
            Document doc;
            try {
                doc = Jsoup.parse(response);
                // 首次解析时得到最大页码，避免maxPageIndex在解析到最后一页时减小
                if (FIRST_TIME_TO_PARSE_FLAG) {
                    String remainString = "";
                    for (Element page : doc.select("a[data-ajax=true]")) {
                        remainString = page.attr("href");
                    }
                    LogUtil.d(TAG, "remainString(undealed): " + remainString);
                    // 当记录页数少于1时，remainString为空
                    if (!remainString.isEmpty()) {
                        // remainString不为空，根据网页内容确定最大页码
                        remainString = remainString.substring(
                                remainString.indexOf("pageIndex=") + 10);  // 有些页面为"pageindex"
                        maxPageIndex = Integer.valueOf(remainString);
                        LogUtil.d(TAG, "maxPageIndex = " + maxPageIndex);
                    } else {
                        // remainString为空, maxIndex值保持不变
                        LogUtil.d(TAG, "maxPageIndex = " + maxPageIndex);
                    }
                    // 将首次解析标志置为false
                    FIRST_TIME_TO_PARSE_FLAG = false;
                }
                Notice notice;
                // 找到表格，并筛选元素
                Elements rows = doc.select("tr");
                for (Element single_row : rows) {
                    if (single_row.hasAttr("anchor")) {  // 有效的一行记录
                        // 新建Notice对象，并获取相关信息
                        notice = new Notice(Notice.RECEIVED_TYPE);
                        notice.setId(single_row.attr("anchor"));
                        // 判断消息是否已读
                        if (single_row.attr("style").contains("bold")) {
                            // 未读
                            notice.setUnread(true);
                        } else {
                            // 已读
                            notice.setUnread(false);
                        }
                        notice.setSentTime(single_row.child(1).text());
                        notice.setTitle(single_row.child(2).child(0).text());
                        notice.setSenderName(single_row.child(3).text());
                        notice.setOperationSystem(single_row.child(4).text());
                        notice.setCategory(single_row.child(5).text());
                        dataList.add(notice);  // 将notice对象添加到数据列表
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
            // 判断是否已经全部加载完成
            if (pageIndex < maxPageIndex) {
                // 还有数据需要加载，页码加1，然后继续发送GET请求
                pageIndex++;
                sendGETRequest();
            } else {
                // 已经全部加载完成
                LogUtil.d(TAG, "Loading Finished.");
                // 设置Adapter
                mAdapter = new MessagesNoticeListAdapter(MyApplication.getContext(), dataList,
                        R.layout.item_messages_inbox_and_sent_list);
                mListView.setAdapter(mAdapter);
                // 显示加载成功的对话框
                sweetAlertDialog
                        .setTitleText(getString(R.string.loading_complete))
                        .setConfirmText(getString(R.string.OK))
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                // 关停PullToRefreshView的刷新
                mPullToRefreshView.setRefreshing(false);
                // 延迟一段时间后，关闭sweetAlertDialog
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sweetAlertDialog.dismiss();
                    }
                }, 1200);
            }
        }
    }

    // 单位转换 dp to px
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
