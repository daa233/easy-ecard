package com.duang.easyecard.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pgyersdk.crash.PgyCrashManager;
import com.rey.material.widget.ProgressView;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class SettingsPersonalInformationActivity extends BaseActivity
        implements UITableView.ClickListener {

    ProgressView mProgressView;
    UITableView mTableView;

    private AsyncHttpClient httpClient;
    private UserBasicInformation userBasicInformation;
    private String response;
    private List<String> dataList;
    private String id;
    private String account;
    private String nickname;
    private String email;
    private String phone;
    private String msn;
    private String qq;

    private final String TAG = "SettingsPersonalInformationActivity";
    private final int CONSTANT_NICKNAME = 1;
    private final int CONSTANT_EMAIL = 5;
    private final int CONSTANT_PHONE = 6;
    private final int CONSTANT_MSN = 7;
    private final int CONSTANT_QQ = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_personal_information);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_personal_information_toolbar);
        setSupportActionBar(toolbar);
        // 显示Back按钮
        setDisplayHomeButton();
        // 实例化控件
        mProgressView = (ProgressView) findViewById(
                R.id.settings_personal_information_progress_view);
        mTableView = (UITableView) findViewById(R.id.settings_personal_information_table_view);
        // 监听点击事件
        mTableView.setClickListener(this);
    }

    private void initData() {
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        userBasicInformation = myApp.getUserBasicInformation();
        account = userBasicInformation.getStuId();
        // 初始化数据列表
        dataList = new ArrayList<>();
        // 发送GET请求
        sendGETRequest();
    }

    // 发送GET请求
    private void sendGETRequest() {
        httpClient.get(UrlConstant.USER_INFO, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 响应成功
                response = new String(responseBody);
                // 解析响应数据
                new JsoupHtmlData().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // 网络错误
                Toast.makeText(MyApplication.getContext(), getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
                mProgressView.setVisibility(View.INVISIBLE);
            }
        });
    }

    // UITableView的点击事件
    @Override
    public void onClick(int i) {
        LogUtil.d(TAG, "UITableView onClick at " + i);
        // 跳转到修改个人信息Activity，请求码为点击位置
        Intent intent = new Intent(MyApplication.getContext(),
                SettingsModifyPersonalInformationActivity.class);
        intent.putExtra("ID", id);
        intent.putExtra("ACCOUNT", account);
        intent.putExtra("TYPE", i);
        intent.putExtra("NICKNAME", nickname);
        intent.putExtra("EMAIL", email);
        intent.putExtra("PHONE", phone);
        intent.putExtra("MSN", msn);
        intent.putExtra("QQ", qq);
        intent.putExtra("CONTENT", getDataAt(i));
        startActivity(intent);
    }

    // 通过UITableView的点击位置获得dataList中对应的数据
    private String getDataAt(int position) {
        switch (position) {
            case CONSTANT_NICKNAME:
                return nickname;
            case CONSTANT_EMAIL:
                return email;
            case CONSTANT_PHONE:
                return  phone;
            case CONSTANT_MSN:
                return msn;
            case CONSTANT_QQ:
                return qq;
            default:
                LogUtil.e(TAG, "Unexpected position in dataList.");
                return null;
        }
    }

    // 解析响应数据
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            LogUtil.d(TAG, "Start Jsoup: In doInBackground.");
            // 解析返回的responseString
            Document doc;
            try {
                doc = Jsoup.parse(response);
                Elements inputs = doc.select("input");
                for (Element input : inputs) {
                    // 获取之前的内容
                    if (input.attr("type").equals("text")) {
                        dataList.add(input.attr("value"));
                    }
                    // 获取ID
                    if (input.attr("id").equals("ID")) {
                        id = input.attr("value");
                    }
                }
            } catch (Exception e) {
                PgyCrashManager.reportCaughtException(MyApplication.getContext(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LogUtil.d(TAG, "onPostExecute.");
            nickname = dataList.get(0);
            email = dataList.get(1);
            phone = dataList.get(2);
            msn = dataList.get(3);
            qq = dataList.get(4);
            // 先清空TableView
            mTableView.clear();
            // 创建TableView
            createTableViewDataList();
            // 隐藏ProgressView，显示TableView
            mProgressView.setVisibility(View.INVISIBLE);
            mTableView.setVisibility(View.VISIBLE);
        }
    }

    // 创建TableView
    private void createTableViewDataList() {
        generateCustomItem(mTableView, getString(R.string.platform_account),
                userBasicInformation.getStuId(), false);
        generateCustomItem(mTableView, getString(R.string.nickname), dataList.get(0), true);
        generateCustomItem(mTableView, getString(R.string.name),
                userBasicInformation.getName(), false);
        generateCustomItem(mTableView, getString(R.string.stu_id),
                userBasicInformation.getStuId(), false);
        generateCustomItem(mTableView, getString(R.string.card_account),
                userBasicInformation.getCardAccount(), false);
        generateCustomItem(mTableView, getString(R.string.email), dataList.get(1), true);
        generateCustomItem(mTableView, getString(R.string.phone), dataList.get(2), true);
        generateCustomItem(mTableView, getString(R.string.msn), dataList.get(3), true);
        generateCustomItem(mTableView, getString(R.string.qq), dataList.get(4), true);
        mTableView.commit();
    }

    // 构造UItableView的列表项，传入title和content和clickable
    private void generateCustomItem(UITableView tableView, String title, String content,
                                    boolean clickable) {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout relativeLayout = (RelativeLayout) mLayoutInflater.inflate(
                R.layout.item_table_view_custom, null);
        TextView titleText = (TextView) relativeLayout.getChildAt(0);
        titleText.setText(title);
        TextView contentText = (TextView) relativeLayout.getChildAt(1);
        contentText.setText(content);
        ViewItem v = new ViewItem(relativeLayout);
        v.setClickable(clickable);  // Set clickable
        tableView.addViewItem(v);
    }

    // 创建菜单选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings_personal_information, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // 菜单的选择事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings_personal_information_sync:
                // 从一卡通同步信息
                LogUtil.d(TAG, "Sync information from one-card.");
                // 显示ProgressView，隐藏TableView
                mProgressView.setVisibility(View.VISIBLE);
                mTableView.setVisibility(View.INVISIBLE);
                // 发送POST请求
                sendPOSTRequest();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 发送POST请求，从一卡通同步用户信息
    private void sendPOSTRequest() {
        httpClient.post(UrlConstant.ONE_KEY_SYNC, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // 响应成功
                try {
                    if (response.getString("ret").equals("true")) {
                        // 同步响应成功，重新发送GET请求获取数据
                        sendGETRequest();
                        Toast.makeText(MyApplication.getContext(), getString(
                                R.string.sync_from_one_card_success), Toast.LENGTH_SHORT).show();
                    } else {
                        // 同步失败
                        Toast.makeText(MyApplication.getContext(), getString(
                                R.string.sync_from_one_card_error), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                // 网络错误
                Toast.makeText(MyApplication.getContext(), getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
                throwable.printStackTrace();
            }
        });
    }
}
