package com.duang.easyecard.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Model.Notice;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.duang.easyecard.Util.MessagesNoticeListAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yalantis.phoenix.PullToRefreshView;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class MessagesInboxAndSentActivity extends BaseActivity implements
        SwipeMenuListView.OnMenuItemClickListener, AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private SwipeMenuListView mListView;
    private PullToRefreshView mPullToRefreshView;
    private SweetAlertDialog sweetAlertDialog;
    private PopupWindow mPopupWindowAtTop;
    private TextView mCheckedCountTextView;
    private Button mCancelButton;
    private Button mSelectButton;
    private FloatingActionButton mDeleteFab;

    private List<Notice> dataList;
    private HashSet<String> checkedToDeleteHashSet;  // 用于存储已经选中的NoticeId，不会重复
    private MessagesNoticeListAdapter mAdapter;
    private AsyncHttpClient httpClient;
    private String response;
    private int pageIndex;  // 访问的网页信息的页码
    private int maxPageIndex;  // 最大页码
    private boolean FIRST_TIME_TO_PARSE_FLAG;  // 首次解析标志，默认为true
    private boolean TO_DELETE_FLAG = false;
    private boolean type;
    private final String TAG = "MessagesInboxAndSentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_inbox_and_sent);
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.messages_inbox_toolbar);
        setSupportActionBar(toolbar);
        setDisplayHomeButton();
        // 实例化控件
        mListView = (SwipeMenuListView) findViewById(R.id.messages_inbox_list_view);
        mDeleteFab = (FloatingActionButton) findViewById(R.id.messages_inbox_fab);
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
        mListView.setOnMenuItemClickListener(this);
        // 设置Swipe的方向
        mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        // 监听Item的点击事件
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        // 监听PullToRefreshView的下拉刷新事件
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 开始刷新
                initData();
            }
        });
    }

    private void initData() {
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        // 获得Intent传递的类型
        Intent intent = this.getIntent();
        type = intent.getBooleanExtra("TYPE", true);
        setTitle(type ? getString(R.string.title_activity_messages_inbox)
                : getString(R.string.title_activity_messages_sent));
        // 初始化数据列表
        dataList = new ArrayList<>();
        checkedToDeleteHashSet = new HashSet<>();
        // 初始化初次解析标志
        FIRST_TIME_TO_PARSE_FLAG = true;
        // 初始化页码，默认均为1
        pageIndex = 1;
        maxPageIndex = 1;
        // 显示对话框
        if (sweetAlertDialog == null || !sweetAlertDialog.isShowing()) {
            sweetAlertDialog = new SweetAlertDialog(MessagesInboxAndSentActivity.this,
                    SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog
                    .setTitleText(getString(R.string.loading))
                    .show();
        }
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
        String address;
        // 根据类型确定GET请求的地址
        if (type) {
            UrlConstant.receivedNoticeIndex = pageIndex;
            address = UrlConstant.getReceivedNotice();
        } else {
            UrlConstant.sentNoticeIndex = pageIndex;
            address = UrlConstant.getSentNotice();
        }
        httpClient.get(address, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 成功响应
                LogUtil.d(TAG, "GET response success.");
                response = new String(responseBody);
                // 解析网页响应
                new JsoupHtmlData().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                LogUtil.d(TAG, "GET response failed.");
                // 网络错误
                sweetAlertDialog
                        .setTitleText(getString(R.string.network_error))
                        .setConfirmText(getString(R.string.OK))
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                error.printStackTrace();
            }
        });
    }

    // ListItem的点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.d(TAG, "onItemClick at " + position);
        if (TO_DELETE_FLAG) {
            // 处于正在删除的选择状态
            LogUtil.d(TAG, "In state to Delete.");
            if (dataList.get(position).isChecked()) {
                // 已经选中，则置为未选中
                dataList.get(position).setChecked(false);
                // 删除checkedToDeleteHashSet中存储的NoticeId
                checkedToDeleteHashSet.remove(((Notice) parent.getItemAtPosition(position)).getId());
            } else {
                // 原本未选中，则置为选中
                dataList.get(position).setChecked(true);
                // 将NoticeId存储到checkedToDeleteHashSet
                checkedToDeleteHashSet.add(((Notice) parent.getItemAtPosition(position)).getId());
            }
            if (checkedToDeleteHashSet.size() == 0) {
                // 如果一个都没有选中，退出删除编辑状态
                cancelDeleting();
            } else if (checkedToDeleteHashSet.size() < dataList.size()) {
                // 没有全选，此时全选按钮显示“全选”
                mSelectButton.setText(getString(R.string.select_all));
            } else {
                // 已经全选，将按钮功能变为“全不选”
                mSelectButton.setText(getString(R.string.select_nothing));
            }
            mCheckedCountTextView.setText(getString(R.string.has_selected) +
                    checkedToDeleteHashSet.size() + getString(R.string.item));
            mAdapter.notifyDataSetChanged();
        } else {
            // 未处在删除状态，跳转查看详细内容Activity
            LogUtil.d(TAG, "Intent to start Detail Activity.");
            Intent intent = new Intent(MessagesInboxAndSentActivity.this,
                    MessagesNoticeDetailActivity.class);
            intent.putExtra("TYPE", type);
            Bundle bundle = new Bundle();
            Notice notice = (Notice) parent.getItemAtPosition(position);
            bundle.putSerializable("Notice", notice);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    // ListItem的长时间点击事件，在每个Item中显示CheckBox
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.d(TAG, "onItemLongClick.");
        // 显示PopupWindow
        showPopupWindow();
        // 显示mDeleteFab
        mDeleteFab.setVisibility(View.VISIBLE);
        // 此时禁止下拉刷新
        mPullToRefreshView.setEnabled(false);
        TO_DELETE_FLAG = true;
        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).setToDelete(true);
            dataList.get(i).setChecked(false);  // 防止长按另一个Item时checkedToDeleteCount无法清零
        }
        dataList.get(position).setChecked(true);
        // 清空checkedToDeleteHashSet
        checkedToDeleteHashSet.clear();
        checkedToDeleteHashSet.add(((Notice) parent.getItemAtPosition(position)).getId());
        mCheckedCountTextView.setText(getString(R.string.has_selected) +
                checkedToDeleteHashSet.size() + getString(R.string.item));
        mAdapter.notifyDataSetChanged();
        return true;
    }

    // 监听MenuItem的点击事件
    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        LogUtil.d(TAG, "onMenuItemClick at " + position);
        final int itemPosition = position;
        switch (index) {
            case 0:
                Snackbar.make(findViewById(R.id.activity_messages_inbox_and_sent_coordinator_layout),
                        getString(R.string.confirm_to_delete_one), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.OK), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        sendPOSTRequest(dataList.get(itemPosition).getId());
                                    }
                                }
                        ).show();
                break;
            default:
                break;
        }
        return false;
    }

    // DeleteFab的点击事件
    public void onDeleteFabClick(View v) {
        LogUtil.d(TAG, "onDeleteFabClick.");
        // 显示Snackbar
        Snackbar.make(findViewById(R.id.activity_messages_inbox_and_sent_coordinator_layout),
                getString(R.string.confirm_to_delete) + checkedToDeleteHashSet.size()
                        + getString(R.string.question_mark), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.OK), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogUtil.d(TAG, "On snackbar confirm click.");
                        // 删除相关Notice
                        String[] checkedToDeleteStringArray =
                                checkedToDeleteHashSet.toArray(new String[0]);
                        String checkedToDeleteIds = checkedToDeleteStringArray[0];
                        for (int i = 1; i < checkedToDeleteStringArray.length; i++) {
                            checkedToDeleteIds = checkedToDeleteIds + ","
                                    + checkedToDeleteStringArray[i];
                        }
                        // 发送POST请求，删除相关消息
                        sendPOSTRequest(checkedToDeleteIds);
                    }
                }).setCallback(new Snackbar.Callback() {
            @Override
            public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);
                // 需要暂时屏蔽掉“取消”按钮和“全选”按钮的点击功能，以防止事件冲突
                mCancelButton.setClickable(false);
                mSelectButton.setClickable(false);
            }

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                // 恢复“取消”按钮和“全选”按钮的点击功能
                mCancelButton.setClickable(true);
                mSelectButton.setClickable(true);
            }
        }).show();
    }

    // 发送删除消息的POST请求
    private void sendPOSTRequest(String ids) {
        RequestParams params = new RequestParams();
        params.add("ids", ids);
        params.add("isSend", type ? "0" : "1");
        httpClient.post(UrlConstant.DELETE_NOTICE, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // 响应成功
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getString("ret").equals("true")) {
                        // 删除成功，刷新ListView
                        LogUtil.d(TAG, "Success to delete. msg: " + response.getString("msg"));
                        initData();
                    } else {
                        // 删除失败
                        sweetAlertDialog
                                .setTitleText(getString(R.string.operation_failed))
                                .setContentText(response.getString("msg"))
                                .setConfirmText(getString(R.string.OK))
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // 删除失败
                    sweetAlertDialog
                            .setTitleText(getString(R.string.operation_failed))
                            .setConfirmText(getString(R.string.OK))
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                // 网络错误
                super.onFailure(statusCode, headers, responseString, throwable);
                // 网络错误
                sweetAlertDialog
                        .setTitleText(getString(R.string.network_error))
                        .setContentText(responseString)
                        .setConfirmText(getString(R.string.OK))
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                throwable.printStackTrace();
            }
        });
    }

    // 显示PopupWindow
    private void showPopupWindow() {
        // 根据PopupWindow状态，确定是否新建
        if (mPopupWindowAtTop == null || !mPopupWindowAtTop.isShowing()) {
            // PopupWindow自定义布局
            View contentView = LayoutInflater.from(MyApplication.getContext()).inflate(
                    R.layout.popup_window_in_inbox_and_sent_at_top, null);
            // 实例化PopupWindow中的控件
            mCancelButton = (Button) contentView.findViewById(
                    R.id.popup_window_in_inbox_and_sent_cancel_button);
            mSelectButton = (Button) contentView.findViewById(
                    R.id.popup_window_in_inbox_and_sent_select_all_button);
            mPopupWindowAtTop = new PopupWindow(contentView, Toolbar.LayoutParams.MATCH_PARENT,
                    dp2px(56), true);
            // 监听各按钮的点击事件
            mCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 取消
                    cancelDeleting();
                }
            });
            mSelectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkedToDeleteHashSet.size() < dataList.size()) {
                        // 全选
                        for (int i = 0; i < dataList.size(); i++) {
                            dataList.get(i).setChecked(true);
                            checkedToDeleteHashSet.add(dataList.get(i).getId());
                            mCheckedCountTextView.setText(getString(R.string.has_selected) +
                                    checkedToDeleteHashSet.size() + getString(R.string.item));
                        }
                        mSelectButton.setText(getString(R.string.select_nothing));
                        mAdapter.notifyDataSetChanged();
                    } else {
                        // 全不选，并退出编辑状态
                        cancelDeleting();
                    }
                }
            });
            mCheckedCountTextView = (TextView) contentView.findViewById(
                    R.id.popup_window_in_inbox_and_sent_selected_text_view);
        }
        mPopupWindowAtTop.setFocusable(false);
        mPopupWindowAtTop.setOutsideTouchable(false);
        mPopupWindowAtTop.showAtLocation(findViewById(
                R.id.activity_messages_inbox_and_sent_coordinator_layout), Gravity.TOP, 0, 0);
    }

    // 取消所有选择，使选择的项数减为0，退出删除状态
    private void cancelDeleting() {
        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).setToDelete(false);
            dataList.get(i).setChecked(false);
        }
        mPopupWindowAtTop.dismiss();  // 让PopupWindow消失
        mDeleteFab.setVisibility(View.GONE);  // 隐藏DeleteFab
        mPullToRefreshView.setEnabled(true);  // 激活下拉刷新
        TO_DELETE_FLAG = false;  // 删除状态标志位置为false
        mAdapter.notifyDataSetChanged();  // 刷新列表
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
                if (type) {
                    // 收件箱消息
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
                } else {
                    // 已发送消息
                    Elements rows = doc.select("tr");
                    for (Element single_row : rows) {
                        if (single_row.hasAttr("anchor")) {  // 有效的一行记录
                            // 新建Notice对象，并获取相关信息
                            notice = new Notice(Notice.SENT_TYPE);
                            notice.setId(single_row.attr("anchor"));
                            // 判断消息是否已读
                            if (single_row.attr("style").contains("bold")) {
                                // 未读
                                notice.setUnread(true);
                            } else {
                                // 已读
                                notice.setUnread(false);
                            }
                            notice.setTitle(single_row.child(1).child(0).text());
                            notice.setSentTime(single_row.child(2).text());
                            notice.setReceiverName(single_row.child(3).text());
                            notice.setOperationSystem(single_row.child(4).text());
                            notice.setCategory(single_row.child(5).text());
                            dataList.add(notice);  // 将notice对象添加到数据列表
                        }
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
                if (TO_DELETE_FLAG) {
                    // 显示删除成功对话框
                    sweetAlertDialog
                            .setTitleText(getString(R.string.success_to_delete))
                            .setConfirmText(getString(R.string.OK))
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    // 退出删除状态
                    cancelDeleting();
                } else {
                    // 显示加载成功的对话框
                    sweetAlertDialog
                            .setTitleText(getString(R.string.loading_complete))
                            .setConfirmText(getString(R.string.OK))
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                }
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
