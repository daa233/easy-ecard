package com.duang.easyecard.Activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
import com.loopj.android.http.RequestParams;
import com.pgyersdk.crash.PgyCrashManager;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.protocol.ClientContext;
import cz.msebera.android.httpclient.cookie.ClientCookie;
import cz.msebera.android.httpclient.cookie.Cookie;

public class ManageNetChargeActivity extends BaseActivity {

    private UITableView tableView;
    private MaterialEditText amountEditText;
    private MaterialEditText passwordEditText;

    private AsyncHttpClient httpClient;
    private UserBasicInformation userBasicInformation;
    private String response;
    private String lastTimeNetBalance;
    private boolean netAccountIsExist = false;

    private static final String TAG = "ManageNetChargeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_net_charge);
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.manage_net_charge_toolbar);
        setSupportActionBar(toolbar);
        // 显示Back按钮
        setDisplayHomeButton();
        // 实例化控件
        tableView = (UITableView) findViewById(R.id.manage_net_charge_table_view);
        amountEditText = (MaterialEditText) findViewById(R.id.manage_net_charge_amount_edit_text);
        passwordEditText = (MaterialEditText) findViewById(R.id.manage_net_charge_password_edit_text);
    }

    private void initData() {
        // 获得全局变量httpClient和userBasicInformation
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        userBasicInformation = myApp.getUserBasicInformation();
        // 发送GET请求获得网费余额
        sendGETRequest();
        // 发送POST请求验证是否存在此账户
        sendPOSTRequestToCheckAccount();
    }

    // 发送GET请求获得网费余额
    private void sendGETRequest() {
        httpClient.get(UrlConstant.MOBILE_MANAGE_NET_FEE_INDEX, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 响应成功
                response = new String(responseBody);
                new JsoupHtmlData().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // 网络错误
                Toast.makeText(MyApplication.getContext(), getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
                LogUtil.e(TAG, "Network error when send GET request.");
                error.printStackTrace();
            }
        });
    }

    // 创建UITableView
    private void createUITableViewList() {
        generateCustomItem(tableView, getString(R.string.name),
                userBasicInformation.getName());
        generateCustomItem(tableView, getString(R.string.stu_id),
                userBasicInformation.getStuId());
        generateCustomItem(tableView, getString(R.string.card_account),
                userBasicInformation.getCardAccount());
        if (lastTimeNetBalance != null && !lastTimeNetBalance.isEmpty()) {
            generateCustomItem(tableView, getString(R.string.net_balance),
                    lastTimeNetBalance);
        } else {
            LogUtil.e(TAG, "createUITableViewList: " + "last time net balance is null or empty.");
        }
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

    // 解析响应数据
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // 解析返回的responseString
            Document doc;
            try {
                LogUtil.d(TAG, "JsoupHtmlData: doInBackground called.");
                doc = Jsoup.parse(response);
                LogUtil.d(TAG, "ps = " + doc.getElementsByClass("heightauto").text());
                for (Element p : doc.getElementsByClass("heightauto")) {
                    LogUtil.d(TAG, "p = " + p.toString());
                    // 通过字符串截取获得校园网余额
                    String pString = p.toString();
                    pString = pString.substring(pString.indexOf("</span>") + 7);
                    lastTimeNetBalance = pString.substring(0, pString.indexOf('<'));
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
            LogUtil.d(TAG, "JsoupHtmlData: onPostExecuted called.");
            if (lastTimeNetBalance != null && !lastTimeNetBalance.isEmpty()) {
                // 已得到网费余额，创建tableView
                createUITableViewList();
            } else {
                LogUtil.e(TAG, "Last time net balance is null or empty.");
                Toast.makeText(MyApplication.getContext(), getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Button的点击事件
    public void onConfirmButtonClick(View v) {
        if (!amountEditText.getText().toString().isEmpty()) {
            // 金额输入不为空
            if (Integer.valueOf(amountEditText.getText().toString()) >= 10
                    && Integer.valueOf(amountEditText.getText().toString()) <= 120) {
                // 金额范围符合要求，10到120
                if (Integer.valueOf(amountEditText.getText().toString()) % 10 == 0) {
                    // 金额是10的倍数
                    if (!passwordEditText.getText().toString().isEmpty()) {
                        // 密码输入不为空，输入格式均符合要求。发送POST请求
                        sendPOSTRequestToDoPay(netAccountIsExist);
                    } else {
                        // 密码输入为空
                        passwordEditText.setError(getString(R.string.net_charge_password_is_empty));
                    }
                } else {
                    // 金额不是10的倍数
                    amountEditText.setError(
                            getString(R.string.net_charge_amount_input_is_not_the_multiple_of_ten));
                }
            } else {
                // 金额范围不符合要求
                amountEditText.setError(getString(R.string.net_charge_amount_input_is_invalid));
            }
        } else {
            // 金额输入为空
            amountEditText.setError(getString(R.string.net_charge_amount_input_is_null));
        }
    }

    // 发送POST请求缴网费
    private void sendPOSTRequestToDoPay(boolean netAccountExistsFlag) {
        if (netAccountExistsFlag) {
            // 网络账户存在
            RequestParams params = new RequestParams();
            params.add("iPlanetDirectoryPro", (String) httpClient.getHttpContext()
                    .getAttribute(ClientContext.COOKIE_STORE));
            LogUtil.d(TAG, "iPlanetDirectoryPro =" + httpClient.getHttpContext()
                    .getAttribute(ClientContext.COOKIE_STORE));
            params.add("account", userBasicInformation.getStuId());
            params.add("pwd", passwordEditText.getText().toString());
            params.add("amount", amountEditText.getText().toString());
            params.add("paytype", "Drcom");
            params.add("xiaoqu", "Drcom");
            params.add("xiaoquName", "城市热点");
            params.add("clientType", "webapp");

        }
    }

    // 发送POST请求验证是否存在此账户
    private void sendPOSTRequestToCheckAccount() {
        RequestParams params = new RequestParams();
        params.put("paytype", "Drcom");
        params.put("xiaoqu", "Drcom");
        params.put("account", userBasicInformation.getStuId());
        httpClient.post(UrlConstant.MOBILE_MANAGE_NET_FEE_IS_EXIST, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // 响应成功
                super.onSuccess(statusCode, headers, response);
                try {
                    LogUtil.d(TAG, "reponse = " + response.toString());
                    if (response.getBoolean("success")) {
                        // 存在此用户
                        netAccountIsExist = true;
                    } else {
                        // 不存在此用户
                        Toast.makeText(MyApplication.getContext(),
                                getString(R.string.net_account_is_not_exist),
                                Toast.LENGTH_LONG).show();
                        // 销毁Activity
                        // finish();
                    }
                } catch (Exception e) {
                    PgyCrashManager.reportCaughtException(MyApplication.getContext(), e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                // 网络错误
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}
