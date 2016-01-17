package com.duang.easyecard.Activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import com.duang.easyecard.R;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Utils.HttpUtil;
import com.duang.easyecard.Utils.HttpUtil.HttpCallbackListener;
import com.duang.easyecard.Utils.ImageUtil;
import com.duang.easyecard.Utils.LogUtil;
import com.duang.easyecard.Utils.ImageUtil.OnLoadImageListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SigninActivity extends BaseActivity implements OnClickListener,
OnFocusChangeListener {
	
	private HttpClient httpClient = new DefaultHttpClient();
	
	private Spinner signinTypeSpinner;
	private AutoCompleteTextView accountInput;
	private EditText passwordInput;
	private EditText checkcodeInput;
	private TextView accountText;
	private TextView hintText;
	private Button signinButton;
	private ImageView checkcodeImage;
	
	private String signtype;  // {"SynSno", "SynCard"}
	private String username;
	private String password;
	private String checkcode;
	
	private List<String> spinnerList = new ArrayList<String>();
	private ArrayAdapter<String> spinnerAdapter;
	private ArrayAdapter<String> autoCompleteAdapter;
	private static String[] autoCompleteStringArray = {"最近登录成功的账号", ""};
	private static Map<String, String> rememberedPassword =
			new HashMap<String, String>();
	
	private static final int SIGNIN_SUCCESS = 1;
	private static final int SIGNIN_FAILED = 0;
	private static final int NETWORK_ERROR = 0x404;
	
	private static int DONT_DISPLAY_AGAIN_FLAG = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("登录");
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
		
		// 设置Spinner
		// 添加列表项
		spinnerList.add("学工号");
		spinnerList.add("校园卡账号");
		// 新建适配器，利用系统内置的layout
		spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spinnerList);
		// 设置下拉菜单样式，利用系统内置的layout
		spinnerAdapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		// 绑定适配器到控件
		signinTypeSpinner.setAdapter(spinnerAdapter);
		// 设置选择响应事件
		signinTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// 选中响应事件
				if (position == 0) {
					accountText.setText("学 工 号");
					accountInput.setHint("请输入学（工）号");
					signtype = "SynSno";
					hintText.setText("提示：请输入学（工）号");
				} else if (position == 1) {
					accountText.setText("校园卡号");
					accountInput.setHint("请输入校园卡账号");
					signtype = "SynCard";
					hintText.setText("提示：请输入校园卡账号");
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// 什么都没选中
			}
		});
		
		// 设置账号自动填充的适配器
		autoCompleteAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, autoCompleteStringArray);
		accountInput.setAdapter(autoCompleteAdapter);
		accountInput.setOnItemClickListener(new OnItemClickListener() {
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
						hintText.setText("提示：请输入密码");
					}
				}
			}
			break;
		case R.id.signin_password_input:
			if (hasFocus) {
				if (accountInput.getText().toString().isEmpty()) {
					if (signtype.equals("SynSno")) {
						hintText.setText("提示：请输入学（工）号");
					} else {
						hintText.setText("提示：请输入校园卡账号");
					}
				}
			} else {
				// 失去焦点
				if (!accountInput.getText().toString().isEmpty()) {
					if (passwordInput.getText().toString().isEmpty()) {
						hintText.setText("提示：请输入密码");
					}
				}
			}
			break;
		case R.id.signin_checkcode_input:
			if (hasFocus) {
				if (accountInput.getText().toString().isEmpty()) {
					if (signtype.equals("SynSno")) {
						hintText.setText("提示：请输入学（工）号");
					} else {
						hintText.setText("提示：请输入校园卡账号");
					}
				} else if (passwordInput.getText().toString().isEmpty()) {
					hintText.setText("提示：请输入密码");
				} else {
					hintText.setText("提示：如果看不清，试试点击图片换一张");
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
						// 登录按钮显示“正在登录”
						signinButton.setText("正在登录...");
						// 发送POST请求
						sendPostRequest();
					} else {
						hintText.setText("提示：请输入验证码");
					}
				} else {
					hintText.setText("提示：请输入密码");
				}
			} else {
				hintText.setText("提示：请输入学（工）号");
			}
			break;
		default:
			break;
		}
	}

	// 处理从线程中传递出来的消息
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SIGNIN_SUCCESS:
				// 登录成功
				hintText.setText("提示：登录成功！");
				signinButton.setText("登录成功");
	        	// 传递全局变量http
	        	MyApplication myApp = (MyApplication) getApplication();
	        	myApp.setHttpClient(httpClient);
	        	if (myApp.getHttpClient() != null) {
	        	 	LogUtil.d("httpClient", "success to spread");
	        	}
	        	// 记录登录成功的账号
				autoCompleteStringArray[1] = username;
				// 弹出是否记住密码对话框
				if (rememberedPassword.isEmpty()) {
					// 初次使用此客户端登录
					popRememberPasswordDialog();
				} else if (!rememberedPassword.containsKey(username)) {
					// 切换用户登录
					popRememberPasswordDialog();
				} else if (DONT_DISPLAY_AGAIN_FLAG == 0) {
					popRememberPasswordDialog();
				} else {
					// 直接跳转到主界面
					Intent intent = new Intent(MyApplication.getContext(),
							MainActivity.class);
					startActivity(intent);
					finish();  // 销毁活动
				}
				break;
			case SIGNIN_FAILED:
				// 登录出错
				signinButton.setText("登      录");  // 登录按钮恢复“登录”字样
				String responseString = msg.obj + "";
				hintText.setText("提示：" + responseString);
	        	if (responseString.contains("查询密码")) {
	        		passwordInput.setText("");
	        	} else if (responseString.contains("验证码")) {
	        		getCheckcodeImage();  // 刷新验证码
	        	}
	        	// 恢复登录按钮的点击功能
	        	signinButton.setClickable(true);
				break;
			case NETWORK_ERROR:
				// 网络错误
				Toast.makeText(SigninActivity.this, "网络错误",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};
	
	private void sendPostRequest() {
		// 装填POST数据
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		checkcode = checkcodeInput.getText().toString();
		username = accountInput.getText().toString();
		password = passwordInput.getText().toString();
		params.add(new BasicNameValuePair("checkcode", checkcode));
		params.add(new BasicNameValuePair("IsUsedKeyPad", "False"));
		params.add(new BasicNameValuePair("signtype", signtype));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		// 发送POST请求
		HttpUtil.sendPostRequest(httpClient, UrlConstant.MINI_CHECK_IN, params,
				new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				// 成功响应
				if (response.contains("success")) {
					// 发送登录成功的消息
		        	LogUtil.d("response", "success");
		        	Message message = new Message();
		        	message.what = SIGNIN_SUCCESS;
		        	handler.sendMessage(message);
		        } else {
		        	// 登录发生错误
		        	Message message = new Message();
		        	message.what = SIGNIN_FAILED;
		        	message.obj = response;
		        	handler.sendMessage(message);
		        }
			}
			@Override
			public void onError(Exception e) {
				// 网络错误
				Message message = new Message();
	        	message.what = NETWORK_ERROR;
	        	handler.sendMessage(message);
			}
		});
	}
	
	private void popRememberPasswordDialog() {
		AlertDialog.Builder dialog = new AlertDialog
    			.Builder(SigninActivity.this);
    	String[] dialogItems = {"记住密码", "不再提示"};
    	dialog.setMultiChoiceItems(dialogItems, null,
    			new OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int which, boolean isChecked) {
				// Item被选中的响应事件
				switch (which) {
				case 0:
					// 选中了“记住密码”
					rememberedPassword.put(username, password);
					break;
				case 1:
					// 选中了“不再提示”
					DONT_DISPLAY_AGAIN_FLAG = 1;
					break;
				default:
					break;
				}
			}
		});
    	dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 跳转到主界面
				Intent intent = new Intent(MyApplication.getContext(),
						MainActivity.class);
				startActivity(intent);
				finish();  // 销毁活动
			}
    	});
    	dialog.show();
	}
}
