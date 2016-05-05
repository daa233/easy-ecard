package com.duang.easyecard.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.duang.changeiconcolorwithtext.ChangeColorTab;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.Model.UserBasicInformation;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pgyersdk.crash.PgyCrashManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

/**
 * MainActivity
 * 应用主界面
 * Created by MrD on 2016/2/4.
 */
public class MainActivity extends BaseActivity implements
        ManagementFragment.StartActivitiesCallback {

    private ViewPager mViewPager;
    private List<Fragment> mTabFragments = new ArrayList<>();
    private ChangeColorTab changeColorTab;

    private AsyncHttpClient httpClient;
    private UserBasicInformation userBasicInformation;
    private List<String> userBasicInformationDataList;
    private List<String> userBasicInformationDataMobileList;
    private String response;
    private int startManageActivityFlag;
    private final String TAG = "MainActivity";
    protected final int CONSTANT_START_NOTHING = 0;
    protected static final int CONSTANT_START_BASIC_INFORMATION = 1;
    protected static final int CONSTANT_START_TRADING_INQUIRY = 2;
    protected static final int CONSTANT_START_REPORT_LOSS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }

    //FindView
    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        changeColorTab = (ChangeColorTab) findViewById(R.id.main_change_color_tab);
    }

    //初始化所有事件
    private void initEvent() {
        changeColorTab.setViewpager(mViewPager);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    //初始化四个Fragment
    private void initData() {
        // 设置ViewPager和TabLayout
        mTabFragments.add(new ManagementFragment());
        mTabFragments.add(new LostAndFoundFragment());
        mTabFragments.add(new MessagesFragment());
        mTabFragments.add(new SettingsFragment());
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTabFragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabFragments.get(position);
            }
        });
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
        // 发送GET请求，获取基本信息
        sendGETRequestToMobile(CONSTANT_START_NOTHING);
    }

    // 向“掌上校园”发送GET请求，有时该服务器会存在问题，失效时向校园卡服务平台发送GET请求
    public void sendGETRequestToMobile(int startActivityFlag) {
        startManageActivityFlag = startActivityFlag;
        // 设置重试次数为1，Timeout时间为1秒。否则移动版出问题时，会等待较长时间
        httpClient.setMaxRetriesAndTimeout(1, 1000);
        httpClient.get(UrlConstant.MOBILE_MANAGE_BASIC_INFO, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 成功响应
                response = new String(responseBody);
                // 解析response
                new JsoupHtmlDataFromMobile().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // 网络错误
                LogUtil.d(TAG, "Failed to get data from MOBILE. Now send POST Request to platform");
                // 向服务平台发送POST请求
                sendPOSTRequestToServicePlatform();
                error.printStackTrace();
            }
        });
    }

    @Override
    public void sendPrePostRequestForTradingInquiry() {
        // 装填POST数据
        RequestParams params = new RequestParams();
        params.add("needHeader", "false");
        // 发送POST请求
        httpClient.post(UrlConstant.TRJN_QUERY, params, new AsyncHttpResponseHandler() {
            // 成功响应，刷新全局httpClient
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyApplication myApp = (MyApplication) getApplication();
                myApp.setHttpClient(httpClient);
                LogUtil.d(TAG, new String(responseBody));
                // 转到ManageTradingInquiryActivity
                startActivity(new Intent(MyApplication.getContext(),
                        ManageTradingInquiryActivity.class));
            }

            // 网络错误
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                LogUtil.e(TAG, "Network error.");
                Toast.makeText(MyApplication.getContext(), getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    // 向校园卡服务平台发送POST请求
    private void sendPOSTRequestToServicePlatform() {
        // 装填POST数据
        RequestParams params = new RequestParams();
        params.add("needHeader", "false");
        httpClient.post(UrlConstant.BASIC_INFO, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 成功响应
                response = new String(responseBody);
                // 解析response
                new JsoupHtmlDataFromServicePlatform().execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // 网络错误
                Toast.makeText(MainActivity.this, R.string.network_error,
                        Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

    }

    // 通过“掌上校园”网站返回的html文本解析数据
    private class JsoupHtmlDataFromMobile extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            // 解析返回的responseHtml
            Document doc;
            try {
                userBasicInformationDataMobileList = new ArrayList<>();
                doc = Jsoup.parse(response);
                Elements contents = doc.getElementsByClass("second");
                for (Element content : contents) {
                    userBasicInformationDataList.add(content.text());
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
            if (userBasicInformationDataMobileList.size() >= 10 &&
                    !userBasicInformationDataMobileList.isEmpty()) {
                // 从“掌上校园”成功获得了数据
                LogUtil.d(TAG, "Success to get UserBasicInformation from Mobile address.");
                // 由于网站管理员删除了“掌上校园”的校园卡账户。。。
                sendPOSTRequestToServicePlatform();
            } else {
                // 从“掌上校园”获取数据失败，转向校园卡服务平台发送数据
                LogUtil.e(TAG, "Fail to get UserBasicInformation from Mobile address.");
                sendPOSTRequestToServicePlatform();
            }
        }
    }

    // 通过网站返回的html文本解析数据
    private class JsoupHtmlDataFromServicePlatform extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            // 解析返回的responseHtml
            Document doc;
            try {
                userBasicInformationDataList = new ArrayList<>();
                doc = Jsoup.parse(response);
                Elements es = doc.getElementsByTag("em");
                for (Element e : es) {
                    userBasicInformationDataList.add(e.text());
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
            if (!userBasicInformationDataList.isEmpty()) {
                // 成功从校园卡服务平台获取数据
                setGlobalUserBasicInformation();
            } else {
                // 意外错误，学校服务器崩了，啥数据都没获得。还是shut down吧。
                LogUtil.e(TAG, "Fail to get data from ServicePlatform too!!!");
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.server_error))
                        .setContentText(getString(R.string.shut_down_caused_by_accident))
                        .setConfirmText(getString(R.string.OK))
                        .show();
            }
        }
    }

    // 设置全局变量UserBasicInformation
    private void setGlobalUserBasicInformation() {
        // 从userInformationDataList中获取数据
        userBasicInformation = new UserBasicInformation();
        // 从掌上校园获得的数据
        if (userBasicInformationDataMobileList.size() >= 10
                && userBasicInformationDataMobileList != null) {
            int i = 0;
            userBasicInformation.setName(userBasicInformationDataMobileList.get(i++));
            userBasicInformation.setStuId(userBasicInformationDataMobileList.get(i++));
            // userBasicInformation.setCardAccount(userBasicInformationDataMobileList.get(i++));
            userBasicInformation.setBalance(userBasicInformationDataMobileList.get(i++));
            userBasicInformation.setBankAccout(userBasicInformationDataMobileList.get(i++));
            userBasicInformation.setCurrentTransition(userBasicInformationDataMobileList.get(i++));
            userBasicInformation.setLastTransition(userBasicInformationDataMobileList.get(i++));
            userBasicInformation.setReportLossState(userBasicInformationDataMobileList.get(i++));
            userBasicInformation.setFreezeState(userBasicInformationDataMobileList.get(i++));
            userBasicInformation.setIdentityType(userBasicInformationDataMobileList.get(i++));
            userBasicInformation.setDepartment(userBasicInformationDataMobileList.get(i));
        } else {
            LogUtil.e(TAG, "Can't get data from mobile data list.");
        }

        // 从校园卡服务平台获得的数据
        if (userBasicInformationDataList.size() >= 7 && userBasicInformationDataList != null) {
            int i = 0;
            userBasicInformation.setName(userBasicInformationDataList.get(i++));
            userBasicInformation.setStuId(userBasicInformationDataList.get(i++));
            userBasicInformation.setCardAccount(userBasicInformationDataList.get(i++));
            LogUtil.d(TAG, "card account = " + userBasicInformationDataList.get(i));
            userBasicInformation.setBalance(userBasicInformationDataList.get(i++));
            userBasicInformation.setCurrentTransition(userBasicInformationDataList.get(i++));
            userBasicInformation.setReportLossState(userBasicInformationDataList.get(i++));
            userBasicInformation.setFreezeState(userBasicInformationDataList.get(i));
        } else {
            LogUtil.e(TAG, "Can't get data from sevice platform data list.");
        }

        // 传递全局变量userBasicInformation
        MyApplication myApp = (MyApplication) getApplication();
        myApp.setUserBasicInformation(userBasicInformation);
        if (myApp.getUserBasicInformation() != null) {
            LogUtil.d(TAG, "Add UserBasicInformation to Application successfully.");
            if (startManageActivityFlag == CONSTANT_START_BASIC_INFORMATION) {
                // 跳转到基本信息
                startActivity(new Intent(this, ManageBasicInformationActivity.class));
            } else if (startManageActivityFlag == CONSTANT_START_REPORT_LOSS) {
                if (userBasicInformation.getReportLossState().contains("正常卡")) {
                    // 正常卡，可以挂失
                    LogUtil.d(TAG, "Report state: Normal.");
                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getString(R.string.hint_start_report_loss_warning_title))
                            .setContentText(getString(
                                    R.string.hint_start_report_loss_warning_content))
                            .setConfirmText(getString(R.string.OK))
                            .setCancelText(getString(R.string.Cancel))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                // 用户确定挂失
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    // 跳转到ManageReportLossActivity
                                    startActivity(new Intent(MainActivity.this,
                                            ManageReportLossActivity.class));
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    // 已经挂失
                    LogUtil.d(TAG, "Report state: Loss.");
                    new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getString(
                                    R.string.report_loss_the_card_has_been_reported_loss))
                            .setConfirmText(getString(R.string.OK))
                            .show();
                }
            }
        } else {
            LogUtil.e(TAG, "Fail to add UserBasicInformation to Application");
            new Throwable().printStackTrace();
        }

    }
}
