package com.duang.easyecard.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.Model.UserBasicInformation;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;

public class ManageBasicInformationActivity extends BaseActivity {

    private UITableView tableView;
    private UserBasicInformation userBasicInformation;
    private final String TAG = "ManageBasicInformationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_basic_information);
        initView();
    }

    // 初始化布局和数据
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.manage_basic_information_toolbar);
        setSupportActionBar(toolbar);
        // 显示返回按钮
        setDisplayHomeButton();

        // 绑定控件
        tableView = (UITableView) findViewById(R.id.manage_basic_information_table_view);
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getApplication();
        userBasicInformation = myApp.getUserBasicInformation();
        if (userBasicInformation != null && !userBasicInformation.getStuId().isEmpty()) {
            // 成功获取UserBasicInformation
            LogUtil.d(TAG, "Success to get UserBasicInformation.");
            createUITableViewDataList();
        } else {
            // 获取UserBasicInformation失败
            LogUtil.e(TAG, "Fail to get UserBasicInformation.");
            new Throwable().printStackTrace();
        }
    }

    // 组建列表布局
    private void createUITableViewDataList() {
        generateCustomItem(tableView, getResources().getString(R.string.name),
                userBasicInformation.getName());
        generateCustomItem(tableView, getResources().getString(R.string.stu_id),
                userBasicInformation.getStuId());
        generateCustomItem(tableView, getResources().getString(R.string.card_account),
                userBasicInformation.getCardAccount());
        generateCustomItem(tableView, getResources().getString(R.string.balance),
                userBasicInformation.getBalance());
        generateCustomItem(tableView, getResources().getString(R.string.bank_account),
                userBasicInformation.getBankAccout());
        generateCustomItem(tableView, getResources().getString(R.string.current_transition),
                userBasicInformation.getCurrentTransition());
        generateCustomItem(tableView, getResources().getString(R.string.last_transition),
                userBasicInformation.getLastTransition());
        generateCustomItem(tableView, getResources().getString(R.string.report_loss_state),
                userBasicInformation.getReportLossState());
        generateCustomItem(tableView, getResources().getString(R.string.freeze_state),
                userBasicInformation.getFreezeState());
        generateCustomItem(tableView, getResources().getString(R.string.identity_type),
                userBasicInformation.getIdentityType());
        generateCustomItem(tableView, getResources().getString(R.string.department),
                userBasicInformation.getDepartment());
        tableView.commit();
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
