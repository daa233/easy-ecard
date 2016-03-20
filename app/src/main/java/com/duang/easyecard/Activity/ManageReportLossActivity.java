package com.duang.easyecard.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Model.UserBasicInformation;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class ManageReportLossActivity extends BaseActivity {

    private UITableView userInfoTableView;
    private MaterialEditText passwordEditText;
    private UserBasicInformation userBasicInformation;
    private AsyncHttpClient httpClient;
    private String password;
    private final String TAG = "ManageReportLossActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_report_loss);
        Toolbar toolbar = (Toolbar) findViewById(R.id.manage_report_loss_toolbar);
        setSupportActionBar(toolbar);
        // 显示home按钮
        setDisplayHomeButton();
        initView();
        initData();
    }

    private void initView() {
        // 实例化控件
        userInfoTableView = (UITableView) findViewById(
                R.id.manage_report_loss_information_table_view);
        passwordEditText = (MaterialEditText) findViewById(R.id.manage_report_loss_password);
    }

    private void initData() {
        // 获得全局变量httpClient和userBasicInformation
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        userBasicInformation = myApp.getUserBasicInformation();
        // 创建UITableView
        createUITableViewList();
    }

    // 创建UITableView
    private void createUITableViewList() {
        generateCustomItem(userInfoTableView, getString(R.string.name),
                userBasicInformation.getName());
        generateCustomItem(userInfoTableView, getString(R.string.stu_id),
                userBasicInformation.getStuId());
        generateCustomItem(userInfoTableView, getString(R.string.card_account),
                userBasicInformation.getCardAccount());
        userInfoTableView.commit();
    }

    // “挂失”按钮的点击事件
    public void onReportLossButtonClick(View v) {
        LogUtil.d(TAG, "onReportLossButtonClick.");
        password = passwordEditText.getText().toString();
        if (password.isEmpty()) {
            // 没有输入密码
            passwordEditText.setError(getString(R.string.report_loss_password_is_empty));
        } else {
            // 对密码进行加密，并发送POST请求
            sendPOSTRequest(Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
        }
    }

    // 发送POST请求
    private void sendPOSTRequest(String encodedPassword) {
        RequestParams requestParams = new RequestParams();
        // 添加参数
        requestParams.put("CardNo", userBasicInformation.getCardAccount());
        // 采用Base64对查询密码进行加密
        requestParams.put("Password", encodedPassword);
        httpClient.post(UrlConstant.MOBILE_MANAGE_CARD_LOST,
                requestParams, new JsonHttpResponseHandler() {
                    // 成功响应，处理返回的JSON数据
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            if (response.getString("success").equals("true")) {
                                // 挂失成功
                                new SweetAlertDialog(ManageReportLossActivity.this,
                                        SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText(getString(R.string.report_loss_success))
                                        .setConfirmText(getString(R.string.OK))
                                        .setConfirmClickListener(new SweetAlertDialog
                                                .OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                // 销毁Activity
                                                sweetAlertDialog.dismiss();
                                                finish();
                                            }
                                        })
                                        .show();
                            } else {
                                // 挂失失败
                                passwordEditText.setError(
                                        getString(R.string.report_loss_wrong_password));
                            }
                            LogUtil.d(TAG, response.getString("success"));
                            LogUtil.d(TAG, response.getString("msg"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // 网络错误
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString,
                                          Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        LogUtil.e(TAG, "Error: POST Response.");
                        Toast.makeText(ManageReportLossActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                        throwable.printStackTrace();
                    }
                });
    }

    // 构造UItableView的列表项，传入title和content
    private void generateCustomItem(UITableView tableView, String title, String content) {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout relativeLayout = (RelativeLayout) mLayoutInflater.inflate(
                R.layout.item_table_view_custom, null);
        TextView titleText = (TextView) relativeLayout.getChildAt(0);
        titleText.setText(title);
        TextView contentText = (TextView) relativeLayout.getChildAt(1);
        contentText.setText(content);
        ViewItem v = new ViewItem(relativeLayout);
        v.setClickable(false);
        tableView.addViewItem(v);
    }
}
