package com.duang.easyecard.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.EncryptorUtil;
import com.duang.easyecard.Util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.security.GeneralSecurityException;

import cz.msebera.android.httpclient.Header;

public class AppStartActivity extends BaseActivity {

    private AsyncHttpClient httpClient;
    private SharedPreferences sharedPreferences;

    private static final String SIGNIN_PREFERENCES = "SIGNIN_PREFERENCES";  // Preferences文件的名称
    private static final String REMEMBER_PASSWORD = "REMEMBER_PASSWORD";
    private static final String AUTO_SIGNIN = "AUTO_SIGNIN";
    private static final String SIGNIN_TYPE = "SIGNIN_TYPE";
    private static final String USER_ACCOUNT = "USER_ACCOUNT";
    private static final String USER_PASSWORD = "USER_PASSWORD";
    private String USER_SEED = "USED_SEED";  // 默认为USER_SEED
    private boolean rememberPasswordFlag = false;
    private boolean autoSigninFlag = false;
    private String signinType;
    private String account;
    private String password;

    private final String TAG = "AppStartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_app_start);
        initView();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                LogUtil.d(TAG, "Just for delay.");
                initData();
            }
        }, 1000);
    }

    private void initView() {
        // 通过Glide载入图片
        Glide
                .with(this)
                .load(R.drawable.ic_launcher_without_background)
                .into((ImageView) findViewById(R.id.app_start_image_view));
    }

    private void initData() {
        // 初始化httpClient
        httpClient = new AsyncHttpClient();
        // 初始化USER_SEED，Use ANDROID_ID as the USER_SEED
        USER_SEED = Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        // 初始化SharedPreferences
        sharedPreferences = this.getSharedPreferences(SIGNIN_PREFERENCES, MODE_PRIVATE);
        // 获取用户设置记住密码及自动登录的选中状态
        rememberPasswordFlag = sharedPreferences.getBoolean(REMEMBER_PASSWORD, false);
        autoSigninFlag = sharedPreferences.getBoolean(AUTO_SIGNIN, false);
        if (rememberPasswordFlag) {
            // 用户之前选择了记住密码。获取已经在本地记住的用户数据，将相关数据填充到布局
            signinType = sharedPreferences.getString(SIGNIN_TYPE, "");
            account = sharedPreferences.getString(USER_ACCOUNT, "");
            password = sharedPreferences.getString(USER_PASSWORD, "");
            // 对账号、密码进行解密
            try {
                account = EncryptorUtil.decrypt(USER_SEED, account);
                password = EncryptorUtil.decrypt(USER_SEED, password);
            } catch (GeneralSecurityException e) {
                Toast.makeText(MyApplication.getContext(),
                        getString(R.string.signin_error_when_decrypting),
                        Toast.LENGTH_SHORT).show();
                LogUtil.e(TAG, "Error: when decrypting.");
                account = "";
                password = "";
                e.printStackTrace();
            }
            if (autoSigninFlag) {
                // 自动登录
                autoSignin(signinType, account, password);
            } else {
                // 记住了密码，但没选择自动登录，跳转到SigninActivity，并传递相关数据
                LogUtil.d(TAG, "User remembered the password. Intent to SigninActivity.");
                intentToSigninActivity();
            }
        } else {
            // 用户没有选择记住密码，跳转到SigninActivity
            LogUtil.d(TAG, "User did't choose to remember password. Intent to SigninActivity.");
            intentToSigninActivity();
        }
    }

    // 跳转到SigninActivity
    private void intentToSigninActivity() {
        Intent intent = new Intent(MyApplication.getContext(), SigninActivity.class);
        intent.putExtra(REMEMBER_PASSWORD, rememberPasswordFlag);
        if (rememberPasswordFlag) {
            // 用户选择了记住密码
            intent.putExtra(AUTO_SIGNIN, autoSigninFlag);
            intent.putExtra(SIGNIN_TYPE, signinType);
            intent.putExtra(USER_ACCOUNT, account);
            intent.putExtra(USER_PASSWORD, password);
        } else {
            // 用户没有选择记住密码
            LogUtil.d(TAG, "User did't choose to remember password. Don't put other data.");
        }
        startActivity(intent);
        finish();  // 销毁活动
    }

    // 自动登录
    private void autoSignin(String signType, String account, String password) {
        LogUtil.d(TAG, "Auto signin is called.");
        // 装填POST数据
        RequestParams params = new RequestParams();
        params.add("signType", signType);
        params.add("UserAccount", account);
        params.add("Password", Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
        httpClient.post(UrlConstant.MOBILE_LOGIN, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // 成功响应
                try {
                    if (response.getString("success").equals("true")) {
                        LogUtil.d(TAG, getString(R.string.signin_automatically_success));
                        // 自动登录成功
                        Toast.makeText(MyApplication.getContext(),
                                getString(R.string.signin_success), Toast.LENGTH_SHORT).show();
                        // 刷新全局httpClient
                        MyApplication myApp = (MyApplication) getApplication();
                        myApp.setHttpClient(httpClient);
                        // 跳转到主界面
                        startActivity(new Intent(MyApplication.getContext(), MainActivity.class));
                        // 销毁活动
                        finish();
                    } else {
                        // 自动登录失败
                        LogUtil.e(TAG,
                                "Auto signin failed. There is something wrong with the data.");
                        Toast.makeText(MyApplication.getContext(),
                                getString(R.string.signin_automatically_failed),
                                Toast.LENGTH_SHORT).show();
                        // 跳转到SigninActivity
                        intentToSigninActivity();
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "Auto signin failed.");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                // 网络错误
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(MyApplication.getContext(), getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
                // 跳转到SigninActivity
                intentToSigninActivity();
            }
        });
    }
}
