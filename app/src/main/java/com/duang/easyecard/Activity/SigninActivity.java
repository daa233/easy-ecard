package com.duang.easyecard.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.duang.easyecard.R;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Util.ImageUtil;
import com.duang.easyecard.Util.LogUtil;
import com.duang.easyecard.Util.ImageUtil.OnLoadImageListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rey.material.widget.Button;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.Spinner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;

public class SigninActivity extends BaseActivity implements OnClickListener,
OnFocusChangeListener {
	
	private AsyncHttpClient httpClient = new AsyncHttpClient();

	private Spinner signinTypeSpinner;
	private AutoCompleteTextView accountInput;
	private EditText passwordInput;
	private EditText checkcodeInput;
	private TextView accountText;
	private TextView hintText;
	private Button signinButton;
	private ImageView checkcodeImage;
	private CheckBox rememberPasswordCheckBox;
	
	private String signtype = "SynSno";  // {"SynSno", "SynCard"}
	private String username;
	private String password;
	private String checkcode;
	
	private List<String> spinnerList = new ArrayList<>();
	private ArrayAdapter<String> spinnerAdapter;
	private ArrayAdapter<String> autoCompleteAdapter;
	private static String[] autoCompleteStringArray = {"Recent Accounts", ""};
	private static Map<String, String> rememberedPassword =
			new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.SigninActivity_label);
		setContentView(R.layout.activity_signin);
		
		initView();
	}

	public void initView() {
		// 实例化控件
		signinTypeSpinner = (Spinner) findViewById(R.id.signin_type_spinner);
		accountInput = (AutoCompleteTextView) 
				findViewById(R.id.signin_account_input);
		passwordInput = (EditText) findViewById(R.id.signin_password_input);
		checkcodeInput = (EditText) findViewById(R.id.signin_checkcode_input);
		accountText = (TextView) findViewById(R.id.signin_account_text);
		hintText = (TextView) findViewById(R.id.signin_hint_text);
		signinButton = (Button) findViewById(R.id.signin_signin_button);
		checkcodeImage = (ImageView) findViewById(R.id.signin_checkcode_image);
		rememberPasswordCheckBox = (CheckBox) findViewById(
				R.id.signin_remember_password_check_box);

		// 初始提示，输入学工号
		hintText.setText(R.string.hint_input_stu_id);
		/*
		 设置Spinner
		  */
		// 添加列表项
        spinnerList.add(getResources().getString(R.string.stu_id));
		spinnerList.add(getResources().getString(R.string.card_account));
		// 新建适配器，利用系统内置的layout
		spinnerAdapter = new ArrayAdapter<>(this,
				android.R.layout.simple_spinner_item, spinnerList);
		// 设置下拉菜单样式，利用系统内置的layout
		spinnerAdapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		// 绑定适配器到控件
		signinTypeSpinner.setAdapter(spinnerAdapter);
		// 设置选择响应事件
		signinTypeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(Spinner parent, View view, int position, long id) {
				// 选中响应事件
				if (position == 0) {
					accountText.setText(R.string.stu_id);
					accountInput.setHint(R.string.hint_input_stu_id);
					signtype = "SynSno";
					hintText.setText(R.string.hint_input_stu_id);
				} else if (position == 1) {
					accountText.setText(R.string.card_account);
					accountInput.setHint(R.string.card_account_input_hint);
					signtype = "SynCard";
					hintText.setText(R.string.hint_input_card_account);
				}
			}
		});
		
		// 设置账号自动填充的适配器
		autoCompleteAdapter = new ArrayAdapter<>(this,
				android.R.layout.simple_list_item_1, autoCompleteStringArray);
		accountInput.setAdapter(autoCompleteAdapter);
		accountInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// 选中了自动填充文本，将记住的密码输入
				passwordInput.setText(rememberedPassword
						.get(autoCompleteStringArray[1]));
			}
		});
		
		getCheckcodeImage();  // 获取验证码
		
		// 监听EditText的焦点改变事件
		accountInput.setOnFocusChangeListener(this);
		passwordInput.setOnFocusChangeListener(this);
		checkcodeInput.setOnFocusChangeListener(this);
		
		// 监听控件的点击事件
		signinButton.setOnClickListener(this);
		checkcodeImage.setOnClickListener(this);
	}

	private void getCheckcodeImage() {
		// 获取验证码图片
		ImageUtil.onLoadImage(UrlConstant.GET_CHECKCODE_IMG, httpClient,
				new OnLoadImageListener() {
			@Override
			public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
				if (bitmap != null) {
					checkcodeImage.setImageBitmap(bitmap);
					checkcodeInput.setText(null);
				}
			}
		});
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// 控件焦点的改变事件
		switch (v.getId()) {
		case R.id.signin_account_input:
			if (!hasFocus) {
				if (!accountInput.getText().toString().isEmpty()) {
					if (passwordInput.getText().toString().isEmpty()) {
						hintText.setText(R.string.hint_input_password);
					}
				}
			}
			break;
		case R.id.signin_password_input:
			if (hasFocus) {
				if (accountInput.getText().toString().isEmpty()) {
					if (signtype.equals("SynSno")) {
						hintText.setText(R.string.hint_input_stu_id);
					} else {
						hintText.setText(R.string.hint_input_card_account);
					}
				}
			} else {
				// 失去焦点
				if (!accountInput.getText().toString().isEmpty()) {
					if (passwordInput.getText().toString().isEmpty()) {
						hintText.setText(R.string.hint_input_password);
					}
				}
			}
			break;
		case R.id.signin_checkcode_input:
			if (hasFocus) {
				if (accountInput.getText().toString().isEmpty()) {
					if (signtype.equals("SynSno")) {
						hintText.setText(R.string.hint_input_stu_id);
					} else {
						hintText.setText(R.string.hint_input_card_account);
					}
				} else if (passwordInput.getText().toString().isEmpty()) {
					hintText.setText(R.string.hint_input_password);
				} else {
					hintText.setText(R.string.hint_click_image_if_not_clear);
				}
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		// 控件的点击事件
		switch (v.getId()) {
		// 验证码图片
		case R.id.signin_checkcode_image:
			getCheckcodeImage();
			break;
		// 点击登录按钮
		case R.id.signin_signin_button:
			if (!accountInput.getText().toString().isEmpty()) {
				if (!passwordInput.getText().toString().isEmpty()) {
					if (!checkcodeInput.getText().toString().isEmpty()) {
						// 屏蔽登录按钮的点击功能
						signinButton.setClickable(false);
						// 显示“正在登录”
						signinButton.setText(R.string.signin_processing);
                        hintText.setText(R.string.hint_signin_processing);
						// 发送POST请求
						sendPOSTRequest();
					} else {
                        // 提示输入验证码
						hintText.setText(R.string.hint_input_checkcode);
					}
				} else {
                    // 提示输入密码
					hintText.setText(R.string.hint_input_password);
				}
			} else {
                // 提示输入账号
                if (signtype.equals("SynSno")) {
                    hintText.setText(R.string.hint_input_stu_id);
                } else {
                    hintText.setText(R.string.hint_input_card_account);
                }
			}
			break;
		default:
			break;
		}
	}

	// 发送POST请求
	private void sendPOSTRequest() {
		// 装填POST数据
        RequestParams params = new RequestParams();
        checkcode = checkcodeInput.getText().toString();
        username = accountInput.getText().toString();
        password = passwordInput.getText().toString();
        params.add("checkcode", checkcode);
        params.add("IsUsedKeyPad", "False");
        params.add("signtype", signtype);
        params.add("username", username);
        params.add("password", password);
		// 发送POST请求
        httpClient.post(UrlConstant.MINI_CHECK_IN, params, new AsyncHttpResponseHandler() {
            // 成功响应
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                if (response.contains("success")) {
                    // 登录成功
                    signinSuccess();
                } else {
                    // 登录发生错误
                    signinFailed(response);
                }
            }
            // 网络错误
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(SigninActivity.this, R.string.network_error,
                        Toast.LENGTH_SHORT).show();
            }
        });
	}

	// 登录成功
	private void signinSuccess() {
		hintText.setText(R.string.hint_signin_success);
		signinButton.setText(R.string.signin_success);
		// 传递全局变量http
		MyApplication myApp = (MyApplication) getApplication();
		myApp.setHttpClient(httpClient);
		if (myApp.getHttpClient() != null) {
			LogUtil.d("httpClient", "success to spread");
		}
		// 记录登录成功的账号
		autoCompleteStringArray[1] = username;
		// 判断记住密码复选框是否被选中
		if (rememberPasswordCheckBox.isChecked()) {
			// 记住密码
			rememberedPassword.put(username, password);
		} else {
			// 不记住密码，若已经记住，需要清除记忆
			if(rememberedPassword.containsKey(username)) {
				rememberedPassword.remove(username);
			}
		}
		// 跳转到主界面
		Intent intent = new Intent(MyApplication.getContext(),
				MainActivity.class);
		startActivity(intent);
		finish();  // 销毁活动
	}
	// 登录失败
	private void signinFailed(String str) {
		// 登录出错
		signinButton.setText(R.string.signin);  // 登录按钮恢复“登录”字样
        if (str.contains("帐户不存在")) {
            hintText.setText(R.string.hint_account_not_exist);
        } else if (str.contains("帐号查询条件不足")) {
            hintText.setText(R.string.hint_account_query_condition_less);
        } else if (str.contains("查询密码")) {
            hintText.setText(R.string.hint_password_error);
			passwordInput.setText("");
		} else if (str.contains("验证码")) {
            hintText.setText(R.string.hint_checkcode_error);
			getCheckcodeImage();  // 刷新验证码
		} else {
            LogUtil.e("SigninActivity", str);
            hintText.setText(R.string.hint_unknown_error);
        }
		// 恢复登录按钮的点击功能
		signinButton.setClickable(true);
	}
}
