package com.duang.easyecard.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pgyersdk.crash.PgyCrashManager;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wefika.flowlayout.FlowLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MessagesCreateNoticeActivity extends BaseActivity {

    private final String TAG = "MessagesCreateNoticeActivity";
    private MaterialEditText titleEditText;
    private MaterialEditText addReceiverEditText;
    private FlowLayout receiverFlowLayout;
    private TextView receiverFlowLayoutHintTextView;
    private EditText contentEditText;
    private AsyncHttpClient httpClient;
    private List<String> receiverList;
    private String receiverString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_create_notice);
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.messages_create_notice_toolbar);
        setSupportActionBar(toolbar);
        setDisplayHomeButton();  // 显示Back按钮
        // 实例化控件
        titleEditText = (MaterialEditText) findViewById(
                R.id.messages_create_notice_title_edit_text);
        addReceiverEditText = (MaterialEditText) findViewById(
                R.id.messages_create_notice_add_receiver_edit_text);
        receiverFlowLayout = (FlowLayout) findViewById(
                R.id.messages_create_notice_receiver_flow_layout);
        receiverFlowLayoutHintTextView = (TextView) findViewById(
                R.id.messages_create_notice_receiver_flow_layout_hint_text_view);
        contentEditText = (EditText) findViewById(R.id.messages_create_notice_content_edit_text);
    }

    private void initData() {
        // 获得全局变量httpClient
        httpClient = MyApplication.getHttpClient();
        receiverList = new ArrayList<>();
        // 判断Intent中是否包含数据
        getDataFromIntent();
    }

    // 有可能是从LostAndFoundInformationBrowsingViewDetailActivity跳转过来，这时从Intent中获取数据
    private void getDataFromIntent() {
        Intent intent = this.getIntent();
        String userStuId = intent.getStringExtra("USER_STU_ID");
        if (userStuId == null || userStuId.isEmpty()) {
            // 不是通过LostAndFoundInformationBrowsingViewDetailActivity跳转过来
            LogUtil.d(TAG, "Can't get extra String from Intent");
        } else {
            // 是通过LostAndFoundInformationBrowsingViewDetailActivity跳转过来
            LogUtil.d(TAG, "Success to get extra String from Intent");
            // 通过userStuId搜索用户
            sendPOSTRequestToQueryUser(userStuId);
        }
    }

    // 添加接收人按钮的点击事件
    public void onAddButtonClick(View v) {
        // 先判断addReceiverEditText里有没有内容
        String condition = addReceiverEditText.getText().toString();
        if (condition.isEmpty()) {
            // 没有内容，提示用户需要输入
            addReceiverEditText.setError(getString(R.string.add_receiver_null_input_error));
        } else {
            // 有内容，发送搜索用户请求
            sendPOSTRequestToQueryUser(condition);
        }
    }

    /**
     * 添加接收人
     * sendPOSTRequestToQueryUser
     *
     * @param condition 用户搜索条件
     *                  响应结果样例：{"ret":true,"account":"1302xxxx005","name":"吴彦祖"}
     */
    private void sendPOSTRequestToQueryUser(String condition) {
        // 设置请求参数condition，即搜索条件
        RequestParams params = new RequestParams();
        params.add("condition", condition);
        httpClient.post(UrlConstant.QUERY_USER, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // 响应成功
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getBoolean("ret")) {
                        // 成功搜索到用户
                        LogUtil.d(TAG, "Query user success.");
                        LogUtil.d(TAG, "account: " + response.get("account") + " " + "name: "
                                + response.get("name"));
                        // 先判断是否已经添加过该用户
                        if (receiverList.contains(response.get("account").toString())) {
                            // 已经添加过，提示不要重复添加相同的用户
                            addReceiverEditText.setError(
                                    getString(R.string.add_receiver_duplicate_error));
                        } else {
                            // 没有添加过，添加到列表，并向FlowLayout中添加子View
                            receiverList.add(response.get("account").toString());
                            // 当receiverList中有一个元素时，隐藏receiverFlowLayoutHintTextView
                            if (receiverList.size() == 1) {
                                receiverFlowLayoutHintTextView.setVisibility(View.GONE);
                            }
                            addChildViewToReceiverFlowLayout(receiverFlowLayout,
                                    response.get("name").toString(),
                                    response.get("account").toString());
                            addReceiverEditText.setText(null);
                        }
                    } else {
                        // 没有搜索到用户
                        addReceiverEditText.setError(
                                getString(R.string.add_receiver_can_not_found_user));
                    }
                } catch (Exception e) {
                    // 意外错误
                    addReceiverEditText.setError(
                            getString(R.string.add_receiver_failure));
                    LogUtil.e(TAG, "Unexpected exception in JsonResponseHandler.");
                    PgyCrashManager.reportCaughtException(MyApplication.getContext(), e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                // 网络错误
                super.onFailure(statusCode, headers, responseString, throwable);
                LogUtil.e(TAG, "Network error.");
                Toast.makeText(MyApplication.getContext(), getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 发送消息
     * sendPOSTRequestToCreateNotice
     * {"ret":true,"msg":"发送成功"}
     */
    private void sendPOSTRequestToCreateNotice() {
        RequestParams params = new RequestParams();
        params.add("context", contentEditText.getText().toString());  // 正文，即消息内容
        params.add("revicerDept", "");
        params.add("sendDept", "");
        params.add("title", titleEditText.getText().toString());  // 消息标题
        params.add("typeID", "");
        params.add("userids", receiverString);  // 接收人ID
        params.add("UserSno", "");
        httpClient.post(UrlConstant.CREATE_NOTICE, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // 响应成功
                try {
                    if (response.getBoolean("ret")) {
                        // 发送成功
                        Toast.makeText(MyApplication.getContext(), getString(R.string.send_success),
                                Toast.LENGTH_SHORT).show();
                        // 销毁Activity
                        finish();
                    } else {
                        // 发送失败
                        Toast.makeText(MyApplication.getContext(), getString(R.string.send_failure),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // 意外错误，发送失败
                    LogUtil.e(TAG, "Unexpected exception in JsonResponseHandler.");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                // 网络错误
                LogUtil.e(TAG, "Network error.");
                Toast.makeText(MyApplication.getContext(), getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 向receiverFlowLayout中添加子View
    private void addChildViewToReceiverFlowLayout(final FlowLayout receiverFlowLayout,
                                                  final String name, final String account) {
        FlowLayout.LayoutParams vlp = new FlowLayout.LayoutParams(
                FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        final Button btn = new Button(MyApplication.getContext());
        btn.setLayoutParams(vlp);
        btn.setText(name + "(" + account + ")");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 从FlowLayout中移除对应的子View
                receiverFlowLayout.removeView(btn);
                // 从receiverList中移除对应数据
                receiverList.remove(account);
                // 判断是否显示提示，如果已经添加了子View，就不用显示；反之显示。
                if (receiverList.size() <= 0) {
                    receiverFlowLayoutHintTextView.setVisibility(View.VISIBLE);
                }
            }
        });
        receiverFlowLayout.addView(btn);
    }

    // 创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_messages_create_notice, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // 菜单项选择事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Back
            case android.R.id.home:
                finish();
                break;
            case R.id.action_messages_create_notice_send:
                // 发送消息。先验证所有输入是否合法：合法，发送POST请求；不合法，提示错误
                if (titleEditText.getText().toString().isEmpty()) {
                    // 消息标题为空
                    titleEditText.setError(getString(R.string.notice_title_null_error));
                } else if (receiverList.isEmpty()) {
                    // 接收人为空
                    addReceiverEditText.setError(getString(R.string.add_receiver_list_is_null));
                } else if (contentEditText.getText().toString().isEmpty()) {
                    // 消息内容为空
                    Toast.makeText(MyApplication.getContext(),
                            getString(R.string.notice_content_null_error),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // 符合要求
                    LogUtil.d(TAG, "Ready to send POST.");
                    // 将receiverList转化为对应的receiverString
                    convertReceiverListToReceverString();
                    LogUtil.d(TAG, "ReceiverString: " + receiverString);
                    sendPOSTRequestToCreateNotice();
                }
                break;
            default:
                break;
        }
        return false;
    }

    // 将receiverList转化为对应的receiverString
    private void convertReceiverListToReceverString() {
        receiverString = "|";
        for (int i = 0; i < receiverList.size(); i++) {
            receiverString = receiverString + receiverList.get(i) + "|";
        }
    }

}
