package com.duang.easyecard.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.ImageUtil;
import com.duang.easyecard.Util.ImageUtil.OnLoadImageListener;
import com.duang.easyecard.Util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class SigninActivity extends BaseActivity {

    private Spinner signinTypeSpinner;
    private MaterialEditText accountEditText;
    private MaterialEditText passwordEditText;
    private MaterialEditText checkcodeEditText;
    private ImageView checkcodeImage;
    private CheckBox autoSigninCheckBox;
    private CheckBox rememberPasswordCheckBox;
    private SweetAlertDialog sweetAlertDialog;

    private AsyncHttpClient httpClient;
    private List<String> spinnerList = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    private String signtype = "SynSno";  // 登录类型，{"SynSno", "SynCard"}，默认为"SynSno"
    private final String TAG = "SigninActivity.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.SigninActivity_label);
        setContentView(R.layout.activity_signin);
        initView();
        initData();
    }

    // 初始化布局
    public void initView() {
        // 实例化控件
        signinTypeSpinner = (Spinner) findViewById(R.id.signin_type_spinner);
        accountEditText = (MaterialEditText) findViewById(R.id.signin_account_edit_text);
        passwordEditText = (MaterialEditText) findViewById(R.id.signin_password_edit_text);
        checkcodeEditText = (MaterialEditText) findViewById(R.id.signin_checkcode_edit_text);
        checkcodeImage = (ImageView) findViewById(R.id.signin_checkcode_image);
        autoSigninCheckBox = (CheckBox) findViewById(R.id.signin_auto_signin_check_box);
        rememberPasswordCheckBox = (CheckBox) findViewById(R.id.signin_remember_password_check_box);
        // 利用Glide载入默认图片资源
        Glide
                .with(this)
                .load(R.drawable.default_checkcode_img)
                .into(checkcodeImage);
        Glide
                .with(this)
                .load(R.drawable.signin_pic_below)
                .into((ImageView) findViewById(R.id.signin_pic_below));
        /*
         设置Spinner
		  */
        // 添加列表项
        spinnerList.add(getResources().getString(R.string.stu_id));
        spinnerList.add(getResources().getString(R.string.card_account));
        // 新建适配器，利用系统内置的layout
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
        // 设置下拉菜单样式，利用系统内置的layout
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 绑定适配器到控件
        signinTypeSpinner.setAdapter(spinnerAdapter);
        // 设置选择响应事件
        signinTypeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                // 选中响应事件
                if (position == 0) {
                    // 学（工）号
                    accountEditText.setHint(R.string.stu_id_input_hint);
                    accountEditText.setFloatingLabelText(getString(R.string.stu_id));
                    signtype = "SynSno";
                } else if (position == 1) {
                    // 校园卡账号
                    accountEditText.setHint(R.string.card_account_input_hint);
                    accountEditText.setFloatingLabelText(getString(R.string.card_account));
                    signtype = "SynCard";
                } else {
                    // 意外错误
                    LogUtil.e(TAG, "Error in SpinnerItem selected.");
                }
            }
        });
    }

    // 初始化数据
    private void initData() {
        // 初始化httpClient
        httpClient = new AsyncHttpClient();
        // 获取验证码
        getCheckcodeImage();
    }

    // 获取验证码图片
    private void getCheckcodeImage() {
        ImageUtil.onLoadImage(UrlConstant.GET_CHECKCODE_IMG, httpClient, new OnLoadImageListener() {
            @Override
            public void OnLoadImage(byte[] imageBytes) {
                Glide
                        .with(SigninActivity.this)
                        .load(imageBytes)
                        .into(checkcodeImage);
            }
        });
    }

    // 验证码图片的点击事件
    public void onCheckcodeImageClick(View v) {
        getCheckcodeImage();
    }

    // 登录按钮点击事件
    public void onSigninButtonClick(View v) {
        if (accountEditText.getText().toString().isEmpty()) {
            // 账号输入为空
            accountEditText.setError(signtype.equals("SynSno") ?
                    getString(R.string.signin_stu_id_is_empty) :
                    getString(R.string.signin_card_account_is_empty));
        } else if (passwordEditText.getText().toString().isEmpty()) {
            // 查询密码输入为空
            passwordEditText.setError(getString(R.string.signin_password_is_empty));
        } else if (checkcodeEditText.getText().toString().isEmpty()) {
            // 验证码为空
            checkcodeEditText.setError(getString(R.string.signin_checkcode_is_empty));
        } else {
            // 所有EditText都有输入，发送POST请求
            sendPOSTRequest();
        }
    }

    // 发送POST请求
    private void sendPOSTRequest() {
        // 装填POST数据
        RequestParams params = new RequestParams();
        params.add("checkcode", checkcodeEditText.getText().toString());
        params.add("IsUsedKeyPad", "False");
        params.add("signtype", signtype);
        params.add("username", accountEditText.getText().toString());
        params.add("password", passwordEditText.getText().toString());
        // 发送POST请求
        httpClient.post(UrlConstant.MINI_CHECK_IN, params, new AsyncHttpResponseHandler() {
            // 成功响应
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                if (response.contains("success")) {
                    // 登录成功
                    onSigninSuccess();
                } else {
                    // 登录发生错误
                    onSigninFailed(response);
                }
            }

            // 网络错误
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                LogUtil.e(TAG, "Error: In POST Response.");
                Toast.makeText(SigninActivity.this, R.string.network_error,
                        Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    // 登录成功
    private void onSigninSuccess() {
        LogUtil.d(TAG, getString(R.string.signin_success));
        // 显示登录成功对话框
        sweetAlertDialog = new SweetAlertDialog(SigninActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog
                .setTitleText(getString(R.string.signin_success))
                .setConfirmText(getString(R.string.OK))
                .show();
        // 刷新全局httpClient
        MyApplication myApp = (MyApplication) getApplication();
        myApp.setHttpClient(httpClient);
        // 延时一小段时间后关闭对话框
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sweetAlertDialog.dismiss();
                // 跳转到主界面
                startActivity(new Intent(SigninActivity.this, MainActivity.class));
                finish();  // 销毁活动
            }
        }, 1200);
    }

    // 登录失败
    private void onSigninFailed(String str) {
        LogUtil.d(TAG, getString(R.string.signin_failed));
        if (str.contains("帐号查询条件不足")) {
            // 账号输入有误
            accountEditText.setError(getString(R.string.signin_account_not_exists));
        } else if (str.contains("查询密码错误")) {
            // 查询密码错误
            passwordEditText.setError(getString(R.string.signin_password_is_wrong));
        } else if (str.contains("验证码不正确")) {
            // 验证码不正确
            checkcodeEditText.setError(getString(R.string.signin_checkcode_is_wrong));
        } else if (str.contains("挂失")) {
            // 该卡已挂失
            sweetAlertDialog = new SweetAlertDialog(SigninActivity.this,
                    SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialog
                    .setTitleText(getString(R.string.signin_failed))
                    .setContentText(getString(R.string.signin_the_card_has_been_reported_loss))
                    .setConfirmText(getString(R.string.OK))
                    .show();
        } else {
            // 未知错误
            LogUtil.e(TAG, "Unknown eroor: " + str);
            // 显示错误对话框
            sweetAlertDialog = new SweetAlertDialog(SigninActivity.this,
                    SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialog
                    .setTitleText(getString(R.string.signin_failed))
                    .setContentText(getString(R.string.unknown_error) + str)
                    .setConfirmText(getString(R.string.OK))
                    .show();
        }
    }
}
