package com.duang.easyecard.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class ManageReportLossActivity extends BaseActivity {

    private UITableView userInfoTableView;
    private EditText passwordEditText;
    private ProgressDialog mProgressDialog;

    private AsyncHttpClient httpClient;
    private String response;

    private String name;
    private String stuId;
    private String cardAccount;
    private String password;

    private boolean USER_INFO_BEEN_INITIALIZED = false;

    private final String TAG = "ManageReportLossActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_report_loss);
        // 显示home按钮
        setupActionBar();
        initView();
        initData();
    }

    private void initView() {
        // 实例化控件
        userInfoTableView = (UITableView) findViewById(
                R.id.manage_report_loss_information_table_view);
        passwordEditText = (EditText) findViewById(R.id.manage_report_loss_password);

        // Create a progressDialog
        mProgressDialog = new ProgressDialog(ManageReportLossActivity.this);
        // Set progressDialog message
        mProgressDialog.setMessage(getResources().getString(R.string.loading) + "  o(>﹏<)o");
        mProgressDialog.setIndeterminate(false);
        // Show progressDialog
        mProgressDialog.show();
    }

    private void initData() {
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        // 初始化cardAccount
        cardAccount = "0";
        sendGETRequest();
    }

    // 发送GET请求
    private void sendGETRequest() {
        httpClient.get(this, UrlConstant.MOBILE_MANAGE_CARD_LOST, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 成功响应
                response = new String(responseBody);
                new JsoupGetHtmlData().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 网络错误
                Toast.makeText(ManageReportLossActivity.this, R.string.network_error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // “挂失”按钮的点击事件
    public void onReportLossButtonClick(View v) {
        LogUtil.d(TAG, "onReportLossButtonClick.");
        if (USER_INFO_BEEN_INITIALIZED) {
            password = passwordEditText.getText().toString();
            if (password.isEmpty()) {
                // 没有输入密码
                Toast.makeText(this,
                        getString(R.string.hint_input_password), Toast.LENGTH_SHORT).show();
            } else {
                // 输入了密码，对密码进行加密，并发送POST请求
                sendPOSTRequest(Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
            }
        }
    }

    // 发送POST请求
    private void sendPOSTRequest(String encodedPassword) {
        RequestParams requestParams = new RequestParams();
        // 添加参数
        requestParams.put("CardNo", cardAccount);
        // 采用Base64对查询密码进行加密
        requestParams.put("Password", encodedPassword);
        LogUtil.d(TAG, requestParams.toString());
        httpClient.post(UrlConstant.MOBILE_MANAGE_CARD_LOST,
                requestParams, new JsonHttpResponseHandler() {
                    // 成功响应，处理返回的JSON数据
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            if (response.getString("success").equals("true")) {
                                // 挂失成功
                                new SweetAlertDialog(ManageReportLossActivity.this,
                                        SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText(getString(R.string.report_loss_success))
                                        .show();
                            } else {
                                // 挂失失败
                                new SweetAlertDialog(ManageReportLossActivity.this,
                                        SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText(getString(R.string.wrong_query_password))
                                        .show();
                                passwordEditText.setText("");
                            }
                            LogUtil.d(TAG, response.getString("success"));
                            LogUtil.d(TAG, response.getString("msg"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // 网络错误
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString,
                                          Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(ManageReportLossActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT);
                    }
                });
    }

    // 通过网站返回的html文本解析数据
    private class JsoupGetHtmlData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            // 解析返回的response
            Document doc;
            try {
                doc = Jsoup.parse(response);
                Elements es = doc.select("em");
                name = es.get(0).text();
                stuId = es.get(1).text();
                cardAccount = es.get(2).text();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            generateCustomItem(userInfoTableView, getString(R.string.name), name);
            generateCustomItem(userInfoTableView, getString(R.string.stu_id), stuId);
            generateCustomItem(userInfoTableView, getString(R.string.card_account), cardAccount);
            userInfoTableView.commit();
            // Close ProgressDialog
            mProgressDialog.dismiss();
            // 如果解析获得的信息不为空，将USER_INFO_BEEN_INITIALIZED置为true;
            if (!(name == null || name.isEmpty())) {
                if (!(stuId == null || stuId.isEmpty())) {
                    if (!(cardAccount == null || cardAccount.isEmpty())) {
                        USER_INFO_BEEN_INITIALIZED = true;
                    }
                }
            }
        }
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
