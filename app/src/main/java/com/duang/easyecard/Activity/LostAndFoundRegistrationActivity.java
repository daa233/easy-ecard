package com.duang.easyecard.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.Model.UserBasicInformation;
import com.duang.easyecard.R;
import com.loopj.android.http.AsyncHttpClient;
import com.rengwuxian.materialedittext.MaterialEditText;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;

public class LostAndFoundRegistrationActivity extends BaseActivity {

    private UITableView tableView;
    private MaterialEditText contactEditText;
    private MaterialEditText lostPlaceEditText;
    private MaterialEditText descriptionEditText;

    private UserBasicInformation userBasicInformation;
    private AsyncHttpClient httpClient;

    private final String TAG = "LostAndFoundRegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_and_found_registration);
        // 显示Back按钮
        setupActionBar();
        initView();
        initData();
    }

    // 初始化布局
    private void initView() {
        // 实例化控件
        tableView = (UITableView) findViewById(
                R.id.lost_and_found_registration_user_information_table_view);
        contactEditText = (MaterialEditText) findViewById(
                R.id.lost_and_found_registration_contact_edit_text);
        lostPlaceEditText = (MaterialEditText) findViewById(
                R.id.lost_and_found_registration_lost_place_edit_text);
        descriptionEditText = (MaterialEditText) findViewById(
                R.id.lost_and_found_registration_description_edit_text);
    }

    // 初始化数据
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
        generateCustomItem(tableView, getString(R.string.name),
                userBasicInformation.getName());
        generateCustomItem(tableView, getString(R.string.stu_id),
                userBasicInformation.getStuId());
        generateCustomItem(tableView, getString(R.string.card_account),
                userBasicInformation.getCardAccount());
        tableView.commit();
    }

    // 提交按钮的点击事件
    public void onSubmitButtonClick(View v) {

    }

    // 构造UItableView的列表项，传入title和content
    private void generateCustomItem(UITableView tableView, String title, String content) {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout relativeLayout = (RelativeLayout) mLayoutInflater.inflate(
                R.layout.table_view_custom_item, null);
        TextView titleText = (TextView) relativeLayout.getChildAt(0);
        titleText.setText(title);
        TextView contentText = (TextView) relativeLayout.getChildAt(1);
        contentText.setText(content);
        ViewItem v = new ViewItem(relativeLayout);
        v.setClickable(false);
        tableView.addViewItem(v);
    }
}
