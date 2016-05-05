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
import com.loopj.android.http.RequestParams;
import com.pgyersdk.crash.PgyCrashManager;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Button;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class LostAndFoundRegistrationActivity extends BaseActivity {

    private UITableView tableView;
    private MaterialEditText contactEditText;
    private MaterialEditText lostPlaceEditText;
    private MaterialEditText descriptionEditText;
    private Button button;
    private SweetAlertDialog sweetAlertDialog;

    private UserBasicInformation userBasicInformation;
    private AsyncHttpClient httpClient;
    private String response;
    private String spanString;  // 解析为特定值时，说明该卡已经登记丢失
    private List<String> pList;  // 丢失登记信息
    private boolean registratedFlag = false;
    private String lostAndFoundEventId;

    private final String TAG = "LostAndFoundRegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_and_found_registration);
        initView();
        initData();
    }

    // 初始化布局
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.lost_and_found_registration_toolbar);
        setSupportActionBar(toolbar);
        // 显示Back按钮
        setDisplayHomeButton();
        // 实例化控件
        tableView = (UITableView) findViewById(
                R.id.lost_and_found_registration_user_information_table_view);
        contactEditText = (MaterialEditText) findViewById(
                R.id.lost_and_found_registration_contact_edit_text);
        lostPlaceEditText = (MaterialEditText) findViewById(
                R.id.lost_and_found_registration_lost_place_edit_text);
        descriptionEditText = (MaterialEditText) findViewById(
                R.id.lost_and_found_registration_description_edit_text);
        button = (Button) findViewById(R.id.lost_and_found_registration_button);
    }

    // 初始化数据
    private void initData() {
        // 获得全局变量httpClient和userBasicInformation
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        userBasicInformation = myApp.getUserBasicInformation();
        // 发送GET请求
        sendGETRequest();
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
    public void onButtonClick(View v) {
        if (registratedFlag) {
            // 已登记为丢失卡，点击招领
            sweetAlertDialog = new SweetAlertDialog(LostAndFoundRegistrationActivity.this,
                    SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog
                    .setTitleText(getString(
                            R.string.lost_and_found_registration_pick_up_card_title))
                    .setContentText(getString(
                            R.string.lost_and_found_registration_pick_up_card_content))
                    .setConfirmText(getString(R.string.OK))
                    .setCancelText(getString(R.string.Cancel))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            // 确定将卡片标记为招领状态，发送POST请求
                            sendSearchPOSTRequest();
                        }
                    })
                    .show();
        } else {
            // 未登记为丢失卡，点击判断内容填充是否符合条件
            if (contactEditText.getText().toString().isEmpty()) {
                // 联系方式为空
                contactEditText.setError(
                        getString(R.string.lost_and_found_registration_contact_is_empty));
            } else if (contactEditText.getText().toString().length() < 7) {
                // 联系方式小于7位
                contactEditText.setError(
                        getString(R.string.lost_and_found_registration_contact_is_too_short));
            } else if (lostPlaceEditText.getText().toString().isEmpty()) {
                // 丢失地点为空
                lostPlaceEditText.setError(
                        getString(R.string.lost_and_found_registration_lost_place_is_empty));
            } else {
                // 用户输入符合条件，让用户确认
                sweetAlertDialog = new SweetAlertDialog(LostAndFoundRegistrationActivity.this,
                        SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(
                                getString(R.string.lost_and_found_registration_save_check_title))
                        .setContentText(
                                getString(R.string.lost_and_found_registration_save_check_content))
                        .setConfirmText(getString(R.string.OK))
                        .setCancelText(getString(R.string.Cancel))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                // 发送丢失登记的POST请求
                                LogUtil.d(TAG, "Ready to send registration POST request.");
                                sendRegistrationPOSTRequest();
                            }
                        });
                sweetAlertDialog.show();
            }
        }
    }

    // 发送GET请求
    public void sendGETRequest() {
        httpClient.get(UrlConstant.CARD_LOSS_LOST_MY_CARD, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 成功响应
                response = new String(responseBody);
                new JsoupGETHtmlData().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // 响应失败
                Toast.makeText(LostAndFoundRegistrationActivity.this,
                        getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    // 发送POST请求
    private void sendRegistrationPOSTRequest() {
        // 装填POST数据
        RequestParams params = new RequestParams();
        params.add("Name", userBasicInformation.getName());
        params.add("Sno", userBasicInformation.getStuId());
        params.add("CardNo", userBasicInformation.getCardAccount());
        params.add("Phone", contactEditText.getText().toString());
        params.add("Address", lostPlaceEditText.getText().toString());
        params.add("Note", descriptionEditText.getText().toString());
        params.add("Status", "1");
        httpClient.post(UrlConstant.CARD_LOSS_LOSE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                response = new String(responseBody);
                if (response.contains("保存成功")) {
                    // 保存成功
                    sweetAlertDialog
                            .setTitleText(getString(R.string.lost_registration))
                            .setContentText(response)
                            .setConfirmText(getString(R.string.OK))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    // 点击确定，重新载入当前界面
                                    initData();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                } else {
                    // 保存失败，未知错误
                    sweetAlertDialog
                            .setTitleText(getString(R.string.lost_registration))
                            .setContentText(response)
                            .setConfirmText(getString(R.string.OK))
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // 网络错误
                sweetAlertDialog
                        .setTitleText(getString(R.string.lost_registration))
                        .setContentText(getString(R.string.network_error))
                        .setConfirmText(getString(R.string.OK))
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                error.printStackTrace();
            }
        });
    }

    // 发送POST请求，在失卡信息中搜索用户已登记的丢失信息
    private void sendSearchPOSTRequest() {
        // 组装POST数据
        RequestParams params = new RequestParams();
        params.add("cardno", userBasicInformation.getCardAccount());
        params.add("name", userBasicInformation.getName());
        params.add("sno", userBasicInformation.getStuId());
        httpClient.post(UrlConstant.CARD_LOSS_INFO_MANAGELIST, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // 响应成功
                        response = new String(responseBody);
                        new JsoupSearchPOSTHtmlData().execute();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                          Throwable error) {
                        // 网络错误
                        new SweetAlertDialog(LostAndFoundRegistrationActivity.this,
                                SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getString(R.string.network_error))
                                .setConfirmText(getString(R.string.OK))
                                .show();
                        Toast.makeText(LostAndFoundRegistrationActivity.this,
                                getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
    }

    // 解析响应数据
    private class JsoupGETHtmlData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // 解析返回的responseString
            Document doc;
            try {
                doc = Jsoup.parse(response);
                pList = new ArrayList<>();
                for (Element p : doc.select("p")) {
                    if (p.select("span") != null && !p.select("span").text().isEmpty()) {
                        spanString = p.select("span").text();
                        LogUtil.d(TAG, "spanString: " + spanString);
                    } else {
                        pList.add(p.ownText());
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
            if (spanString != null && spanString.contains("您的校园卡已经登录挂失")) {
                // 用户已经登记丢失，获取用户保存的数据，标志位registratedFlag置为true
                registratedFlag = true;
                contactEditText.setText(pList.get(3));
                contactEditText.setFocusable(false);
                lostPlaceEditText.setText(pList.get(4));
                lostPlaceEditText.setFocusable(false);
                descriptionEditText.setText(pList.get(5));
                descriptionEditText.setFocusable(false);
                button.setText(getString(R.string.pick_up_card));
                button.setBackgroundResource(R.drawable.green_button_selector);
            } else {
                // 用户没有登记丢失
                LogUtil.d(TAG, "The card has not been registrated.");
                registratedFlag = false;
                button.setText(getString(R.string.save));
                button.setBackgroundResource(R.drawable.skyblue_button_selector);
            }
        }
    }

    // 解析失卡招领信息网页返回的响应数据，确定用户丢失信息ID
    private class JsoupSearchPOSTHtmlData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            // 解析返回的responseString
            Document doc;
            try {
                doc = Jsoup.parse(response);
                // 判断字段中是否有含有待招领的ID
                for (Element a : doc.select("a")) {
                    if (a.text().contains("招领")) {
                        // 获取到了含有“招领”字样的a标签
                        lostAndFoundEventId = a.toString().substring(a.toString().indexOf("(") + 1);
                        lostAndFoundEventId = lostAndFoundEventId.substring(0,
                                lostAndFoundEventId.indexOf(")"));
                        LogUtil.d(TAG, "JsoupSearchPostHtmlData: a=" + a.toString());
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!lostAndFoundEventId.isEmpty()) {
                // 获得了用户登记丢失信息的ID，发送标记为招领的POST请求
                LogUtil.d(TAG, "lostAndFoundEventId = " + lostAndFoundEventId);
                sendPickUpCardPOSTRequest();
            } else {
                // 发生意外错误，没有解析出用户丢失信息ID
                LogUtil.e(TAG, "Fail to get the ID of user's LostAndFoundEvent.");
                sweetAlertDialog
                        .setTitleText(getString(
                                R.string.lost_and_found_registration_pick_up_card_title))
                        .setContentText(getString(R.string.operation_failed))
                        .setConfirmText(getString(R.string.OK))
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        }
    }

    // 用户捡到卡后，将卡片标记为招领状态的请求
    private void sendPickUpCardPOSTRequest() {
        httpClient.post(UrlConstant.CARD_LOSS_PICK_UP_CARD + lostAndFoundEventId,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // 响应成功
                        response = new String(responseBody);
                        if (response.contains("True")) {
                            // 招领成功
                            sweetAlertDialog
                                    .setTitleText(getString(R.string
                                            .lost_and_found_registration_pick_up_card_title))
                                    .setContentText(getString(R.string.operaton_successed))
                                    .setConfirmText(getString(R.string.OK))
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            // 重新载入此界面
                            initData();
                        } else {
                            // 招领失败
                            sweetAlertDialog
                                    .setTitleText(getString(R.string
                                            .lost_and_found_registration_pick_up_card_title))
                                    .setContentText(getString(R.string.operation_failed))
                                    .setConfirmText(getString(R.string.OK))
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                          Throwable error) {
                        // 网络错误
                        sweetAlertDialog
                                .setTitleText(getString(
                                        R.string.lost_and_found_registration_pick_up_card_title))
                                .setContentText(getString(R.string.network_error))
                                .setConfirmText(getString(R.string.OK))
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        error.printStackTrace();
                    }
                });
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
