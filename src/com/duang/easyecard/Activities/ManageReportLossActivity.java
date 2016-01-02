package com.duang.easyecard.Activities;

import com.duang.easyecard.R;
import com.duang.easyecard.Utils.LogUtil;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ManageReportLossActivity extends BaseActivity implements
OnClickListener{
	
	private TextView nameText;
	private TextView stuIdText;
	private TextView cardIdText;
	private EditText passwordEditText;
	private ImageView passwordImageView;
	private EditText checkcodeEditText;
	private ImageView checkcodeImageView;
	private Button reportLossButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_loss);
		// 显示返回按钮
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		initView();
		
		initData();
	}

	private void initView() {
		// 实例化控件
		nameText = (TextView) findViewById(R.id.report_loss_name);
		stuIdText = (TextView) findViewById(R.id.report_loss_stu_id);
		cardIdText = (TextView) findViewById(R.id.report_loss_card_id);
		passwordEditText = (EditText) findViewById(R.id.report_loss_password);
		passwordImageView = (ImageView) findViewById(
				R.id.report_loss_password_img);
		checkcodeEditText = (EditText) findViewById(
				R.id.report_loss_checkcode_input);
		checkcodeImageView = (ImageView) findViewById(
				R.id.report_loss_checkcode_image);
		reportLossButton = (Button) findViewById(R.id.report_loss_button);
		
		passwordEditText.setOnClickListener(this);
		checkcodeImageView.setOnClickListener(this);
		reportLossButton.setOnClickListener(this);
	}

	private void initData() {
		
	}
	// 触屏事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int eventAction = event.getAction();
		// 触屏按下时触发
		if (eventAction == 0) {
			int[] passwordImgLocation = new int[2];
			passwordImageView.getLocationOnScreen(passwordImgLocation);
			LogUtil.d("ManageReportLossActivity", "Sreen "
					+ passwordImgLocation[0]
					+ "  " + passwordImgLocation[1]);
			LogUtil.d("ManageReportLossActivity",
					"getRawX()" + event.getRawX());
			LogUtil.d("ManageReportLossActivity",
					"getRawY()" + event.getRawY());
			
			LogUtil.d("ManageReportLossActivity", "width = " 
					+ passwordImageView.getWidth());
			LogUtil.d("ManageReportLossActivity", "height = "
					+ passwordImageView.getHeight());
			// 点击相对于图片左上角的坐标
			float touchX = event.getRawX() - passwordImgLocation[0];
			float touchY = event.getRawY() - passwordImgLocation[1];
			LogUtil.d("ManageReportLossActivity", "touchX = " + touchX);
			LogUtil.d("ManageReportLossActivity", "touchY = " + touchY);
			// 如果点击了图片
			if (touchX > 0 && touchY > 0 &&
					touchX - passwordImageView.getWidth() < 0 &&
					touchY - passwordImageView.getHeight() < 0) {
				LogUtil.d("ManageReportLossActivity", "TouchResult = "
						+ getTouchResult(touchX, touchY,
								passwordImageView.getWidth(),
								passwordImageView.getHeight()));
				inputPassword(getTouchResult(touchX, touchY,
						passwordImageView.getWidth(),
						passwordImageView.getHeight()));
				LogUtil.d("ManageReportLossActivity", "password = " + 
						passwordEditText.getText().toString());
			}
		}
		return super.onTouchEvent(event);
	}
	
	@Override
	public void onClick(View v) {
		// 控件点击事件
		switch (v.getId()) {
		case R.id.report_loss_password:
			passwordEditText.setHint("请通过点击图片输入");
			passwordImageView.setVisibility(View.VISIBLE);
			break;
		case R.id.report_loss_checkcode_image:
			break;
		case R.id.report_loss_button:
			break;
		default:
			break;
		}
	}
	// 根据点击图片获得的操作数指令进行输入
	private void inputPassword(int cmd) {
		String password = passwordEditText.getText().toString();
		if (cmd != -200) {
			switch (cmd) {
			case -1:
				// 退格
				if (password.length() > 1) {
					password = password.substring(1);
				} else if (password.length() == 1) {
					password = "";
				}
				passwordEditText.setText(password);
				break;
			case -10:
				// 清空
				passwordEditText.setText("");
				break;
			case 200:
				// 确定
				passwordEditText.setHint("请输入校园卡查询密码");
				passwordImageView.setVisibility(View.INVISIBLE);
				break;
			default:
				// 数字，最大长度为6
				if (password.length() < 6) {
					password = cmd + password;
					passwordEditText.setText(password);
					break;
				}
			}
		}
	}
	// 获得点击图片对应的操作数
	private int getTouchResult(double x, double y, int width, int height) {
		// 为了适应所有屏幕，按比例确定返回值
		final double w_space = width * 18.00  / 1080.00;  // 按钮横向间隙
		final double h_space = height * 13.00 / 183.00;  // 按钮纵向间隙
		final double side = width * 90.00 / 1080.00;  // 数字按键边长
		if (y < side) {
			// 点击了第一行
			if (x < side + w_space * 0.5) {
				return 0;
			} else if (x > side * 1 + w_space * 1.5 &&
					x < side * 2 + w_space * 1.5) {
				return 1;
			} else if (x > side * 2 + w_space * 2.5 &&
					x < side * 3 + w_space * 2.5) {
				return 2;
			} else if (x > side * 3 + w_space * 3.5 &&
					x < side * 4 + w_space * 3.5) {
				return 3;
			} else if (x > side * 4 + w_space * 4.5 &&
					x < side * 5 + w_space * 4.5) {
				return 4;
			} else if (x > side * 5 + w_space * 5.5 &&
					x < side * 6 + w_space * 5.5) {
				return 5;
			} else if (x > side * 6 + w_space * 6.5 &&
					x < side * 7 + w_space * 6.5) {
				return 6;
			} else if (x > side * 7 + w_space * 7.5 &&
					x < side * 8 + w_space * 7.5) {
				return 7;
			} else if (x > side * 8 + w_space * 8.5 &&
					x < side * 9 + w_space * 8.5) {
				return 8;
			} else if (x > side * 9 + w_space * 9.5 &&
					x < width) {
				return 9;
			} else {
				return -200;  // 错误值
			}
		} else if (y > side + h_space && y < height) {
			// 点了第二行
			if (x < 250.00 / 1080.00 * width) {
				return -1;  // 退格
			} else if (x > 284.00 / 1080.00 * width &&
					x < 800.00 / 1080.00 * width) {
				return -10;  // 清空
			} else if (x > 835.00 / 1080.00 * width &&
					x < width) {
				return 200;  // 确定
			} else {
				return -200;  // 错误值
			}
		} else {
			return -200;  // 错误值
		}
	}
}