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
import com.pgyersdk.crash.PgyCrashManager;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class ManageChangePasswordActivity extends BaseActivity {

    private static final String TAG = "ManageChangePasswordActivity";
    private static final String PASSWORD = "Password";
    private static final String NEW_PASSWORD = "NewPassword";
    private static final String CONFIRM_PASSWORD = "ConfirmPassword";
    private UITableView userInfoTableView;
    private MaterialEditText passwordEditText;
    private MaterialEditText newPasswordEditText;
    private MaterialEditText confirmNewPasswordEditText;
    private AsyncHttpClient httpClient;
    private UserBasicInformation userBasicInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_change_password);
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.manage_change_password_toolbar);
        setSupportActionBar(toolbar);
        // 显示Back按钮
        setDisplayHomeButton();
        // 实例化控件
        userInfoTableView = (UITableView) findViewById(R.id.manage_change_password_table_view);
        passwordEditText = (MaterialEditText) findViewById(
                R.id.manage_change_password_old_password_edit_text);
        newPasswordEditText = (MaterialEditText) findViewById(
                R.id.manage_change_password_new_password_edit_text);
        confirmNewPasswordEditText = (MaterialEditText) findViewById(
                R.id.manage_change_password_confirm_password_edit_text);
    }

    private void initData() {
        // 获得全局变量httpClient和userBasicInformation
        httpClient = MyApplication.getHttpClient();
        userBasicInformation = MyApplication.getUserBasicInformation();
        // 创建UITableView
        createUITableViewList();
    }

    public void onConfirmButtonClick(View v) {
        LogUtil.d(TAG, "onConfirmButtonClick.");
        if (passwordEditText.getText().toString().isEmpty()) {
            // 原有密码输入为空
            passwordEditText.setError(getString(R.string.change_password_old_password_is_empty));
        } else if (newPasswordEditText.getText().toString().isEmpty()) {
            // 没有输入新的查询密码
            newPasswordEditText.setError(getString(R.string.change_password_new_password_is_empty));
        } else if (confirmNewPasswordEditText.getText().toString().isEmpty()) {
            // 没有再次输入新密码进行确认
            confirmNewPasswordEditText.setError(getString(
                    R.string.change_password_confirm_new_password_is_empty));
        } else {
            // 所有输入均不为空
            if (passwordEditText.getText().toString().length() < 6) {
                // 原有密码输入不足6位
                passwordEditText.setError(getString(R.string.change_password_input_is_short));
            } else if (newPasswordEditText.getText().toString().length() < 6) {
                // 新密码输入不足6位
                newPasswordEditText.setError(getString(R.string.change_password_input_is_short));
            } else if (confirmNewPasswordEditText.getText().toString().length() < 6) {
                // 再次输入的新密码不足6位
                confirmNewPasswordEditText.setError(
                        getString(R.string.change_password_input_is_short));
            } else {
                // 密码格式符合要求
                if (!newPasswordEditText.getText().toString()
                        .equals(confirmNewPasswordEditText.getText().toString())) {
                    // 两次密码输入不一致
                    confirmNewPasswordEditText.setError(getString(
                            R.string.change_password_the_two_inputs_are_not_same));
                } else {
                    // 两次密码输入一致，发送POST请求
                    sendPOSTRequest();
                }
            }
        }

    }

    // 发送POST请求
    private void sendPOSTRequest() {
        // 组装POST数据
        RequestParams params = new RequestParams();
        final String cardNo = "card_" + userBasicInformation.getCardAccount() + "_"
                + userBasicInformation.getCardAccount();
        params.put("CardNo", cardNo);
        params.put("selectCardnos", cardNo);
        params.add(PASSWORD, Base64.encodeToString(passwordEditText.getText().toString().getBytes(),
                Base64.DEFAULT));
        params.add(NEW_PASSWORD, Base64.encodeToString(
                newPasswordEditText.getText().toString().getBytes(), Base64.DEFAULT));
        params.add(CONFIRM_PASSWORD, Base64.encodeToString(
                confirmNewPasswordEditText.getText().toString().getBytes(), Base64.DEFAULT));
        httpClient.post(UrlConstant.MOBILE_MANAGE_CHANGE_QUERY_PWD, params,
                new JsonHttpResponseHandler() {
                    // 响应成功
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            if (response.getString("success").equals("true")) {
                                // 修改成功
                                new SweetAlertDialog(ManageChangePasswordActivity.this,
                                        SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText(getString(R.string.change_password_success))
                                        .setConfirmText(getString(R.string.OK))
                                        .setConfirmClickListener(new SweetAlertDialog
                                                .OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                // 关闭对话框，并清除所有输入
                                                sweetAlertDialog.dismiss();
                                                passwordEditText.setText("");
                                                newPasswordEditText.setText("");
                                                confirmNewPasswordEditText.setText("");
                                            }
                                        })
                                        .show();
                            } else {
                                // 修改失败
                                new SweetAlertDialog(ManageChangePasswordActivity.this,
                                        SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText(getString(R.string.change_password_failed))
                                        .setContentText(response.getString("msg"))
                                        .setConfirmText(getString(R.string.OK))
                                        .setConfirmClickListener(new SweetAlertDialog
                                                .OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                // 关闭对话框
                                                sweetAlertDialog.dismiss();
                                                passwordEditText.setError(getString(R
                                                        .string.change_password_password_is_wrong));
                                            }
                                        })
                                        .show();
                            }
                        } catch (Exception e) {
                            PgyCrashManager.reportCaughtException(MyApplication.getContext(), e);
                            e.printStackTrace();
                        }
                    }

                    // 网络错误
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString,
                                          Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        LogUtil.e(TAG, "Error: POST Response.");
                        Toast.makeText(ManageChangePasswordActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                        throwable.printStackTrace();
                    }
                });
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
