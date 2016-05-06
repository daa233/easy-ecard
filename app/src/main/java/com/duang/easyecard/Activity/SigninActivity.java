package com.duang.easyecard.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.EncryptorUtil;
import com.duang.easyecard.Util.ImageUtil;
import com.duang.easyecard.Util.ImageUtil.OnLoadImageListener;
import com.duang.easyecard.Util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.Spinner;

import java.security.GeneralSecurityException;
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
    private CheckBox rememberPasswordCheckBox;
    private CheckBox autoSigninCheckBox;
    private SweetAlertDialog sweetAlertDialog;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private AsyncHttpClient httpClient;
    private List<String> spinnerList = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private boolean rememberPasswordFlag = false;
    private boolean autoSigninFlag = false;
    private boolean signinSuccessFlag = false;
    private String signinType;
    private String account;
    private String password;

    private String signtype = "SynSno";  // 登录类型，{"SynSno", "SynCard"}，默认为"SynSno"
    private final String TAG = "SigninActivity.";
    private static final String SIGNIN_PREFERENCES = "SIGNIN_PREFERENCES";  // Preferences文件的名称
    private static final String REMEMBER_PASSWORD = "REMEMBER_PASSWORD";
    private static final String AUTO_SIGNIN = "AUTO_SIGNIN";
    private static final String SIGNIN_TYPE = "SIGNIN_TYPE";
    private static final String USER_ACCOUNT = "USER_ACCOUNT";
    private static final String USER_PASSWORD = "USER_PASSWORD";
    private String USER_SEED = "USED_SEED";  // 默认为USER_SEED

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        rememberPasswordCheckBox = (CheckBox) findViewById(R.id.signin_remember_password_check_box);
        autoSigninCheckBox = (CheckBox) findViewById(R.id.signin_auto_signin_check_box);
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
        // rememberPasswordCheckBox的选择事件
        rememberPasswordCheckBox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // 将rememberPasswordFlag置为isChecked
                        rememberPasswordFlag = isChecked;
                        // 将autoSigninCheckBox置为isChecked
                        autoSigninCheckBox.setEnabled(isChecked);
                        if (!isChecked) {
                            // 如果选择不记住密码，需要将自动登录置为未选中状态
                            autoSigninCheckBox.setChecked(false);
                        }
                    }
                });
        // autoSigninCheckBox的选择事件
        autoSigninCheckBox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // 将autoSigninFlag置为isChecked
                        autoSigninFlag = isChecked;
                    }
                });
    }

    // 初始化数据
    private void initData() {
        // 初始化httpClient
        httpClient = new AsyncHttpClient();
        // 初始化USER_SEED，Use ANDROID_ID as the USER_SEED
        USER_SEED = Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        // 获取验证码
        getCheckcodeImage();
        // 获取Intent，以及Intent传递的数据
        Intent intent = getIntent();
        rememberPasswordFlag = intent.getBooleanExtra(REMEMBER_PASSWORD, false);
        if (rememberPasswordFlag) {
            // 用户选择了记住密码
            autoSigninFlag = intent.getBooleanExtra(AUTO_SIGNIN, false);
            signinType = intent.getStringExtra(SIGNIN_TYPE);
            account = intent.getStringExtra(USER_ACCOUNT);
            password = intent.getStringExtra(USER_PASSWORD);
            // 根据登录类型选取SigninTypeSpinner应该选取的位置
            signinTypeSpinner.setSelection(signinType.equals("SynSno") ? 0 : 1);
            accountEditText.setText(account);
            passwordEditText.setText(password);
            rememberPasswordCheckBox.setChecked(rememberPasswordFlag);
            autoSigninCheckBox.setChecked(autoSigninFlag);
        } else {
            // 用户没有选择记住密码
            LogUtil.d(TAG, "User did't choose to remember password.");
        }
        // 检查更新
        checkForUpdate(false);
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
            // 所有EditText都有输入，显示SweetAlertDialog，准备发送POST请求
            sweetAlertDialog = new SweetAlertDialog(SigninActivity.this,
                    SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog
                    .setTitleText(getString(R.string.signin_processing))
                    .show();
            // 不允许退出SweetAlertDialog
            sweetAlertDialog.setCancelable(false);
            // 发送POST请求
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
                sweetAlertDialog.cancel();
                Toast.makeText(SigninActivity.this, R.string.network_error,
                        Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    // 登录成功
    private void onSigninSuccess() {
        LogUtil.d(TAG, getString(R.string.signin_success));
        // 将signinSuccessFlag置为true
        signinSuccessFlag = true;
        // 刷新全局httpClient
        MyApplication myApp = (MyApplication) getApplication();
        myApp.setHttpClient(httpClient);
        // 显示登录成功对话框
        sweetAlertDialog
                .setTitleText(getString(R.string.signin_success))
                .setConfirmText(getString(R.string.OK))
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        // 延时一小段时间后关闭对话框
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sweetAlertDialog.dismiss();
                // 跳转到主界面
                startActivity(new Intent(SigninActivity.this, MainActivity.class));
                finish();  // 销毁活动
            }
        }, 1000);
    }

    // 登录失败
    private void onSigninFailed(String str) {
        LogUtil.d(TAG, getString(R.string.signin_failed));
        // 将signinSuccessFlag置为false
        signinSuccessFlag = false;
        if (str.contains("帐号查询条件不足")) {
            // 账号输入有误
            accountEditText.setError(getString(R.string.signin_account_not_exists));
            // 关闭SweetAlertDialog
            sweetAlertDialog.dismiss();
        } else if (str.contains("查询密码错误")) {
            // 查询密码错误
            passwordEditText.setError(getString(R.string.signin_password_is_wrong));
            // 关闭SweetAlertDialog
            sweetAlertDialog.dismiss();
        } else if (str.contains("验证码不正确")) {
            // 验证码不正确
            checkcodeEditText.setError(getString(R.string.signin_checkcode_is_wrong));
            // 关闭SweetAlertDialog
            sweetAlertDialog.dismiss();
        } else if (str.contains("挂失")) {
            // 该卡已挂失
            sweetAlertDialog
                    .setTitleText(getString(R.string.signin_failed))
                    .setContentText(getString(R.string.signin_the_card_has_been_reported_loss))
                    .setConfirmText(getString(R.string.OK))
                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
        } else {
            // 未知错误
            LogUtil.e(TAG, "Unknown error: " + str);
            // 显示错误对话框
            sweetAlertDialog
                    .setTitleText(getString(R.string.signin_failed))
                    .setContentText(getString(R.string.unknown_error) + str)
                    .setConfirmText(getString(R.string.OK))
                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
        }
    }

    // 根据用户选择，判断是否记住密码
    private void toRememberPassword(boolean flag) {
        // 获取名字为“SIGNIN_PREFERENCES”的参数文件对象
        sharedPreferences = this.getSharedPreferences(SIGNIN_PREFERENCES, MODE_PRIVATE);
        //使用Editor接口修改SharedPreferences中的值并提交
        editor = sharedPreferences.edit();
        if (flag) {
            // 用户选择记住密码
            String account = accountEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            // 对用户的账号、密码进行加密
            try {
                account = EncryptorUtil.encrypt(USER_SEED, account);
                password = EncryptorUtil.encrypt(USER_SEED, password);
            } catch (GeneralSecurityException e) {
                Toast.makeText(SigninActivity.this,
                        getString(R.string.signin_error_when_encrypting),
                        Toast.LENGTH_SHORT).show();
                account = "";
                password = "";
                PgyCrashManager.reportCaughtException(MyApplication.getContext(), e);
                e.printStackTrace();
            }
            editor.putBoolean(REMEMBER_PASSWORD, rememberPasswordFlag);
            editor.putBoolean(AUTO_SIGNIN, autoSigninFlag);
            editor.putString(SIGNIN_TYPE, signtype);
            editor.putString(USER_ACCOUNT, account);
            editor.putString(USER_PASSWORD, password);
            editor.commit();
        } else {
            // 用户选择不记住密码，清除已经记住的密码
            editor.clear().commit();
        }
    }

    // 检查更新
    private void checkForUpdate(final boolean flag) {
        PgyUpdateManager.register(SigninActivity.this, new UpdateManagerListener() {
            @Override
            public void onNoUpdateAvailable() {
                // 没有可用更新
                if (flag) {
                    Toast.makeText(MyApplication.getContext(),
                            getString(R.string.settings_no_update_available),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onUpdateAvailable(String result) {
                // 有可用更新
                // 将新版本信息封装到AppBean中
                final AppBean appBean = getAppBeanFromString(result);
                new AlertDialog.Builder(MyApplication.getContext())
                        .setTitle(getString(R.string.settings_update))
                        .setMessage(appBean.getVersionCode() + "\n"
                                + appBean.getVersionName() + "\n" + appBean.getReleaseNote())
                        .setNegativeButton(
                                getString(R.string.settings_update_later),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 以后再说
                                        dialog.dismiss();
                                    }
                                })
                        .setPositiveButton(getString(R.string.settings_update_now),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 立即下载更新
                                        startDownloadTask(SigninActivity.this,
                                                appBean.getDownloadURL());
                                        dialog.dismiss();
                                    }
                                }).show();
            }
        });
    }

    // 活动即将销毁时调用
    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, getClass().getSimpleName() + "onStop().");
        if (signinSuccessFlag) {
            // 根据用户选择，判断是否记住密码
            toRememberPassword(rememberPasswordFlag);
        } else {
            // 用户登录失败并退出应用
            LogUtil.d(TAG, "User signed in failed and exit, or the app was covered.");
        }
    }
}
