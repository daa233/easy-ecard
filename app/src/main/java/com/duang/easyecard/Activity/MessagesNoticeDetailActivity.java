package com.duang.easyecard.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Model.Notice;
import com.duang.easyecard.Model.UserBasicInformation;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class MessagesNoticeDetailActivity extends BaseActivity {

    private SweetAlertDialog sweetAlertDialog;
    private FloatingActionButton showMoreFab;
    private TextView titleTextView;
    private TextView longSentTimeTextView;
    private TextView senderNameTextView;
    private TextView receiverNameTextView;
    private TextView shortSentTimeTextView;
    private TextView operationSystemTextView;
    private TextView categoryTextView;
    private TextView contentTextView;

    private Notice notice;
    private AsyncHttpClient httpClient;
    private UserBasicInformation userBasicInformation;
    private String response;
    private String content;
    private String longReceiverName;  // 发送给多人时，详细的接收人信息
    private boolean type;  // 标识消息类型 ｛Notice.SENT_TYPE, Notice.RECEIVED_TYPE}
    private boolean foledFlag = true;  // 折叠标志，默认为true，为折叠状态
    private final String TAG = "MessagesNoticeDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_notice_detail);
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.messages_notice_detail_toolbar);
        setSupportActionBar(toolbar);
        // 显示Back按钮
        setDisplayHomeButton();
        // 实例化控件
        showMoreFab = (FloatingActionButton) findViewById(R.id.messages_notice_detail_fab);
        Glide
                .with(MyApplication.getContext())
                .load(R.drawable.ic_keyboard_arrow_down_white_24dp)
                .into(showMoreFab);
        titleTextView = (TextView) findViewById(R.id.messages_notice_detail_title);
        longSentTimeTextView = (TextView) findViewById(R.id.messages_notice_detail_long_sent_time);
        senderNameTextView = (TextView) findViewById(R.id.messages_notice_detail_sender_name);
        receiverNameTextView = (TextView) findViewById(R.id.messages_notice_detail_receiver_name);
        shortSentTimeTextView = (TextView) findViewById(
                R.id.messages_notice_detail_short_sent_time);
        operationSystemTextView = (TextView) findViewById(
                R.id.messages_notice_detail_operation_system);
        categoryTextView = (TextView) findViewById(R.id.messages_notice_detail_category);
        contentTextView = (TextView) findViewById(R.id.messages_notice_detail_content);
        // 显示正在加载对话框
        sweetAlertDialog = new SweetAlertDialog(MessagesNoticeDetailActivity.this,
                SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText(getString(R.string.loading)).show();
    }

    private void initData() {
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        userBasicInformation = myApp.getUserBasicInformation();
        // 获得Intent传递的Notice对象
        Intent intent = this.getIntent();
        notice = (Notice) intent.getSerializableExtra("Notice");
        type = intent.getBooleanExtra("TYPE", true);
        // 发送预请求，获取消息的详细内容
        sendPreGETRequest(type);
        titleTextView.setText(notice.getTitle());  // 设置消息标题
        if (type) {
            // 收件箱消息详情
            senderNameTextView.setText(notice.getSenderName());
            receiverNameTextView.setText(getString(R.string.receiver) +
                    userBasicInformation.getName());
            receiverNameTextView.setVisibility(View.GONE);
        } else {
            // 已发送消息详情
            receiverNameTextView.setText(notice.getReceiverName());  // 简略的接收人
            senderNameTextView.setText(getString(R.string.sender) +
                    userBasicInformation.getName());
            senderNameTextView.setVisibility(View.GONE);
        }
        // 设置详细时间
        longSentTimeTextView.setText(getString(R.string.sent_time) + notice.getSentTime());
        shortSentTimeTextView.setText(notice.getSentTime());  // 简略发送时间
        operationSystemTextView.setText(getString(R.string.operation_system) +
                notice.getOperationSystem());  // 业务系统
        categoryTextView.setText(getString(R.string.category) +
                notice.getCategory());  // 分类
    }

    // 发送预请求
    private void sendPreGETRequest(boolean type) {
        String address;
        if (type) {
            address = UrlConstant.getReceivedNotice();
        } else {
            address = UrlConstant.getSentNotice();
        }
        httpClient.get(address, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 响应成功，发送POST请求
                sendPOSTRequest();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // 网络错误
                LogUtil.e(TAG, "Network error.");
                error.printStackTrace();
            }
        });
    }

    // 发送POST请求，获取消息的详细内容
    private void sendPOSTRequest() {
        // 装填POST数据
        RequestParams params = new RequestParams();
        params.add("id", notice.getId());
        params.add("isSend", type ? "0" : "1");
        httpClient.post(UrlConstant.NOTICE_DETAIL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 成功响应
                response = new String(responseBody);
                // 解析响应数据
                new JsoupHtmlData().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // 网络错误
                LogUtil.e(TAG, "Network error.");
                sweetAlertDialog
                        .setTitleText(getString(R.string.network_error))
                        .setConfirmText(getString(R.string.OK))
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                error.printStackTrace();
            }
        });
    }

    // FloatingActionButton的点击事件
    public void onShowMoreFabClick(View v) {
        if (foledFlag) {
            // 在折叠状态，点击展开
            titleTextView.setMaxLines(4);
            longSentTimeTextView.setVisibility(View.VISIBLE);  // 显示完整时间
            shortSentTimeTextView.setVisibility(View.GONE);  // 隐藏简略时间
            // 设置发送人和收件人
            if (type) {
                // 收件箱消息
                senderNameTextView.setText(getString(R.string.sender) + notice.getSenderName());
                receiverNameTextView.setText(getString(R.string.receiver) +
                        userBasicInformation.getName());
                receiverNameTextView.setVisibility(View.VISIBLE);
            } else {
                // 已发送消息
                receiverNameTextView.setText(getString(R.string.receiver) + longReceiverName);
                senderNameTextView.setText(getString(R.string.sender) +
                        userBasicInformation.getName());
                senderNameTextView.setVisibility(View.VISIBLE);
            }
            // 显示业务系统
            operationSystemTextView.setVisibility(View.VISIBLE);
            // 显示分类
            categoryTextView.setVisibility(View.VISIBLE);
            // 变为向上的箭头
            Glide
                    .with(MyApplication.getContext())
                    .load(R.drawable.ic_keyboard_arrow_up_white_24dp)
                    .into(showMoreFab);
            foledFlag = false;
        } else {
            // 在展开状态，点击折叠
            titleTextView.setMaxLines(1);  // 将标题显示行数限制为1行
            longSentTimeTextView.setVisibility(View.GONE);  // 隐藏完整时间
            shortSentTimeTextView.setVisibility(View.VISIBLE);  // 显示简略时间
            // 设置发送人和收件人
            if (type) {
                // 收件箱消息
                senderNameTextView.setText(notice.getSenderName());
                receiverNameTextView.setVisibility(View.GONE);
            } else {
                // 已发送消息
                receiverNameTextView.setText(notice.getReceiverName());
                senderNameTextView.setVisibility(View.GONE);
            }
            // 显示业务系统
            operationSystemTextView.setVisibility(View.GONE);
            // 显示分类
            categoryTextView.setVisibility(View.GONE);
            // 变为向下的箭头
            Glide
                    .with(MyApplication.getContext())
                    .load(R.drawable.ic_keyboard_arrow_down_white_24dp)
                    .into(showMoreFab);
            foledFlag = true;
        }
    }

    // 解析响应数据
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // 解析返回的responseString
            Document doc;
            try {
                LogUtil.d(TAG, "JsoupHtmlData: doInBackground called.");
                doc = Jsoup.parse(response);
                Elements div = doc.getElementsByClass("heightauto").select("div");
                for (Element divContent : div) {
                    if (divContent.ownText() != null || !divContent.ownText().isEmpty()) {
                        content = divContent.ownText();
                    }
                }
                if (!type) {
                    // 已发送消息，解析出详细的接收人及接收人是否已读消息
                    Elements ps = doc.select("p");
                    int pCount = 0;
                    for (Element p : ps) {
                        if (pCount == 2) {
                            Elements spans = p.select("span");
                            boolean spanFlag = true;
                            for (Element span : spans) {
                                if (spanFlag) {
                                    if (longReceiverName == null || longReceiverName.isEmpty()) {
                                        longReceiverName = span.text();
                                    } else {
                                        longReceiverName = longReceiverName + "，" + span.text();
                                    }
                                }
                                spanFlag = !spanFlag;
                            }
                        }
                        pCount++;
                    }
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "Jsoup error.");
                Toast.makeText(MessagesNoticeDetailActivity.this, getString(R.string.network_error),
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LogUtil.d(TAG, "JsoupHtmlData: onPostExecuted called.");
            if (content != null && !content.isEmpty()) {
                contentTextView.setText(content);
            } else {
                LogUtil.e(TAG, "JsoupHtmlData Error. Can't get content.");
            }
            sweetAlertDialog
                    .setTitleText(getString(R.string.loading_complete))
                    .setConfirmText(getString(R.string.OK))
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            sweetAlertDialog.dismissWithAnimation();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_message_notice_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_messages_notice_detail_reply:
                // 回复消息
                Snackbar.make(findViewById(R.id.activity_messages_notice_detail_coordinator_layout),
                        getString(R.string.reply_hint), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.OK), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        LogUtil.d(TAG, "Reply the message.");
                                    }
                                }
                        ).show();
                return true;
            case R.id.action_messages_notice_detail_delete:
                // 删除消息
                Snackbar.make(findViewById(R.id.activity_messages_notice_detail_coordinator_layout),
                        getString(R.string.confirm_to_delete_one), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.OK), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        LogUtil.d(TAG, "Delete this message.");
                                        sendPOSTRequestToDelete(notice.getId());
                                    }
                                }
                        ).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // 发送删除消息的POST请求
    private void sendPOSTRequestToDelete(String ids) {
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
                        // 删除成功
                        LogUtil.d(TAG, "Success to delete. msg: " + response.getString("msg"));
                        Toast.makeText(MyApplication.getContext(),
                                getString(R.string.success_to_delete), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // 删除失败
                        LogUtil.e(TAG, "Failed to delete. msg: " + response.getString("msg"));
                        Toast.makeText(MyApplication.getContext(),
                                getString(R.string.fail_to_delete), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // 删除失败
                    LogUtil.e(TAG, "Failed to delete. Caught an exception.");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                // 网络错误
                super.onFailure(statusCode, headers, responseString, throwable);
                LogUtil.e(TAG, "Network error.");
                throwable.printStackTrace();
            }
        });
    }
}
