package com.duang.easyecard.Activity;

import android.content.Context;
import android.content.Intent;
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
import com.duang.easyecard.Model.LostAndFoundEvent;
import com.duang.easyecard.Model.UserBasicInformation;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.rey.material.widget.Button;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

/**
 * LostAndFoundInformationBrowsingViewDetailActivity
 * Created by MrD on 2016/3/12.
 */
public class LostAndFoundInformationBrowsingViewDetailActivity extends BaseActivity {

    private UITableView mTableView;
    private Button button;
    private SweetAlertDialog sweetAlertDialog;

    private AsyncHttpClient httpClient;
    private UserBasicInformation userBasicInformation;
    private String response;
    private LostAndFoundEvent event;
    private String lostPlace;
    private String description;
    private boolean isViewingOwnEventFlag;

    private final String TAG = "LostAndFoundInformationBrowsingViewDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_and_found_information_browsing_view_detail);
        Toolbar toolbar = (Toolbar) findViewById(
                R.id.lost_and_found_information_browsing_view_detail_toolbar);
        setSupportActionBar(toolbar);
        // 显示Back按钮
        setDisplayHomeButton();
        // 获得传递的LostAndFoundEvent对象
        Intent intent = this.getIntent();
        event = (LostAndFoundEvent) intent.getSerializableExtra("LostAndFoundEvent");
        // 重设标题
        setTitle(getString(R.string.title_activity_lost_and_found_information_browsing_view_detail)
                + getString(R.string.number_point) + event.getId());
        initView();
        initData();
    }

    private void initView() {
        // 实例化控件
        mTableView = (UITableView) findViewById(
                R.id.lost_and_found_information_browsing_view_detail_table_view);
        button = (Button) findViewById(
                R.id.lost_and_found_fragment_information_browsing_view_detail_button);
    }

    private void initData() {
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        userBasicInformation = myApp.getUserBasicInformation();
        // 判断用户是否正在查看自己的丢失信息
        if (userBasicInformation.getStuId().equals(event.getStuId())) {
            // 用户正在查看自己的丢失信息
            LogUtil.d(TAG, "User is viewing his own lost and found event.");
            isViewingOwnEventFlag = true;
            if (event.getState().contains("已招领")) {
                // 用户的该丢失信息已招领
                LogUtil.d(TAG, "User is viewing his own lost and found event. FOUNDED");
                button.setText(getString(R.string.card_has_been_founded));
                button.setBackgroundResource(R.drawable.skyblue_button_selector);
                button.setEnabled(false);
            } else {
                // 用户的该丢失信息未招领
                LogUtil.d(TAG, "User is viewing his own lost and found event. LOST");
                button.setText(getString(R.string.pick_up_card));
                button.setBackgroundResource(R.drawable.green_button_selector);
            }
        } else {
            // 用户正在查看他人的丢失信息
            LogUtil.d(TAG, "User is viewing someone else's lost and found event.");
            isViewingOwnEventFlag = false;
            button.setText(getString(R.string.send_message_to_ta));
            button.setBackgroundResource(R.drawable.green_button_selector);
        }
        sweetAlertDialog = new SweetAlertDialog(
                LostAndFoundInformationBrowsingViewDetailActivity.this,
                SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog
                .setTitleText(getString(R.string.loading))
                .show();
        sweetAlertDialog.setCancelable(false);
        sendGETRequest();  // 发送GET请求，获取丢失地点和说明
    }

    public void onButtonClick(View v) {
        if (isViewingOwnEventFlag) {
            LogUtil.d(TAG, "btn Click: User is viewing his own lost and found event.");
            // 用户正在查看自己的丢失信息，且未招领，点击进行招领
            sweetAlertDialog = new SweetAlertDialog(MyApplication.getContext(),
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
                            sendPickUpCardPOSTRequest();
                        }
                    })
                    .show();
        } else {
            LogUtil.d(TAG, "btn Click: User is viewing someone else's lost and found event.");
            // 用户正在查看他人的丢失信息，点击按钮后通过平台向该丢卡用户发消息
        }
    }

    // 发送GET请求
    private void sendGETRequest() {
        // 组装Url
        UrlConstant.cardLossViewDetailId = event.getId();
        httpClient.get(UrlConstant.getCardLossInfoViewDetail(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 成功响应
                response = new String(responseBody);
                // 解析返回的数据
                new JsoupHtmlData().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                //  网络错误
                sweetAlertDialog.cancel();
                Toast.makeText(LostAndFoundInformationBrowsingViewDetailActivity.this,
                        R.string.network_error, Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    // 用户捡到卡后，将卡片标记为招领状态的请求
    private void sendPickUpCardPOSTRequest() {
        httpClient.post(UrlConstant.CARD_LOSS_PICK_UP_CARD + event.getId(),
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

    // 解析响应数据
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // 解析返回的responseString
            Document doc;
            try {
                doc = Jsoup.parse(response);
                for (Element ps : doc.select("p[class=heightauto]")) {
                    lostPlace = ps.ownText();
                }
                for (Element ps : doc.select("p[class=heightauto clear]")) {
                    description = ps.ownText();
                }
            } catch (Exception e) {
                sweetAlertDialog.cancel();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // 组装UITableView的数据列表
            createTableViewList();
            sweetAlertDialog
                    .setTitleText(getString(R.string.loading_complete))
                    .setConfirmText(getString(R.string.OK))
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            sweetAlertDialog.dismissWithAnimation();
        }
    }

    // 创建UITableView列表
    private void createTableViewList() {
        generateCustomItem(mTableView, getString(R.string.name), event.getName());
        generateCustomItem(mTableView, getString(R.string.stu_id), event.getStuId());
        generateCustomItem(mTableView, getString(R.string.card_account), event.getAccount());
        generateCustomItem(mTableView, getString(R.string.contact), event.getContact());
        generateCustomItem(mTableView, getString(R.string.publish_time), event.getPublishTime());
        generateCustomItem(mTableView, getString(R.string.lost_place), lostPlace);
        generateCustomItem(mTableView, getString(R.string.description), description);
        generateCustomItem(mTableView, getString(R.string.state), event.getState());
        generateCustomItem(mTableView, getString(R.string.found_time), event.getFoundTime());
        // 更新UI
        mTableView.commit();
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
