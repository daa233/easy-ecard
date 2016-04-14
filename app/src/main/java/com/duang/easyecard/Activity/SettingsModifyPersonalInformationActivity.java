package com.duang.easyecard.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SettingsModifyPersonalInformationActivity extends BaseActivity {

    private MaterialEditText editText;
    private Button button;

    private AsyncHttpClient httpClient;
    private String response;
    private int type;
    private String content;

    private final String TAG = "SettingsModifyPersonalInformationActivity";
    private final int CONSTANT_NICKNAME = 1;
    private final int CONSTANT_EMAIL = 5;
    private final int CONSTANT_PHONE = 6;
    private final int CONSTANT_MSN = 7;
    private final int CONSTANT_QQ = 8;

    // EditText的监视器
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().equals(content)) {
                editText.setHelperText(getString(R.string.did_not_modify_anything));
                button.setEnabled(false);
            } else {
                button.setEnabled(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_modify_personal_information);
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_modify_personal_information_toolbar);
        setSupportActionBar(toolbar);
        // 显示Back按钮
        setDisplayHomeButton();
        // 实例化控件
        editText = (MaterialEditText) findViewById(
                R.id.settings_modify_personal_information_edit_text);
        button = (Button) findViewById(R.id.settings_modify_personal_information_submit_button);
    }

    private void initData() {
        // 获得由SettingsPersonalInformationActivity传递来的数据
        Intent intent = getIntent();
        type = intent.getIntExtra("TYPE", 0);
        content = intent.getStringExtra("CONTENT");
        LogUtil.d(TAG, "type = " + type);
        LogUtil.d(TAG, "content = " + content);
        // 初始化EditText
        initEditText();
        editText.addTextChangedListener(textWatcher);
    }

    // 根据传递来的类型和内容初始化EditText
    private void initEditText() {
        // 设置EditText默认内容
        editText.setText(content);
        // 根据类型设置标题和EditText的其他相关内容：label, hint, error...
        switch (type) {
            case CONSTANT_NICKNAME:
                // 修改昵称
                setTitle(getString(
                        R.string.title_activity_settings_modify_personal_information_nickname));
                editText.setFloatingLabelText(getString(R.string.nickname));
                editText.setHint(getString(
                        R.string.settings_modify_personal_information_nickname_hint));
                editText.setMaxCharacters(20);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case CONSTANT_EMAIL:
                // 修改Email
                setTitle(getString(
                        R.string.title_activity_settings_modify_personal_information_email));
                editText.setFloatingLabelText(getString(R.string.email));
                editText.setHint(getString(
                        R.string.settings_modify_personal_information_email_hint));
                editText.setMaxCharacters(20);
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case CONSTANT_PHONE:
                // 修改Phone
                setTitle(getString(
                        R.string.title_activity_settings_modify_personal_information_phone));
                editText.setFloatingLabelText(getString(R.string.phone));
                editText.setHint(getString(
                        R.string.settings_modify_personal_information_phone_hint));
                editText.setMaxCharacters(20);
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case CONSTANT_MSN:
                // 修改MSN
                setTitle(getString(
                        R.string.title_activity_settings_modify_personal_information_msn));
                editText.setFloatingLabelText(getString(R.string.msn));
                editText.setHint(getString(
                        R.string.settings_modify_personal_information_msn_hint));
                editText.setMaxCharacters(30);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case CONSTANT_QQ:
                // 修改QQ
                setTitle(getString(
                        R.string.title_activity_settings_modify_personal_information_qq));
                editText.setFloatingLabelText(getString(R.string.qq));
                editText.setHint(getString(
                        R.string.settings_modify_personal_information_qq_hint));
                editText.setMaxCharacters(12);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            default:
                break;
        }
    }

    // “提交”按钮的点击事件
    public void onSubmitButtonClick(View v) {
        LogUtil.d(TAG, "onSubmitButtonClick.");
    }
}
