package com.duang.easyecard.Activity;

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
public class MainActivity extends BaseActivity {

    private ViewPager mViewPager;
    private List<Fragment> mTabFragments = new ArrayList<>();
    private ChangeColorTab changeColorTab;

    private AsyncHttpClient httpClient;
    private UserBasicInformation userBasicInformation;
    private List<String> userBasicInformationDataList;
    private String response;
    private final String TAG = "MainActivity";

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
        sendGETRequestToMobile();
    }

    // 向“掌上校园”发送GET请求，有时该服务器会存在问题，失效时向校园卡服务平台发送GET请求
    private void sendGETRequestToMobile() {
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
                Toast.makeText(MainActivity.this, R.string.network_error,
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
                userBasicInformationDataList = new ArrayList<>();
                doc = Jsoup.parse(response);
                Elements contents = doc.getElementsByClass("second");
                for (Element content : contents) {
                    userBasicInformationDataList.add(content.text());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (userBasicInformationDataList.size() >= 10 &&
                    !userBasicInformationDataList.isEmpty()) {
                // 从“掌上校园”成功获得了数据
                LogUtil.d(TAG, "Success to get UserBasicInformation from Mobile address.");
                setGlobalUserBasicInformation(true);
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
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!userBasicInformationDataList.isEmpty()) {
                // 成功从校园卡服务平台获取数据
                setGlobalUserBasicInformation(false);
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
    private void setGlobalUserBasicInformation(boolean flag) {
        // 从userInformationDataList中获取数据
        userBasicInformation = new UserBasicInformation();
        if (flag) {
            // 从掌上校园获得的数据
            userBasicInformation.setName(userBasicInformationDataList.get(0));
            userBasicInformation.setStuId(userBasicInformationDataList.get(1));
            userBasicInformation.setCardAccount(userBasicInformationDataList.get(2));
            userBasicInformation.setBalance(userBasicInformationDataList.get(3));
            userBasicInformation.setBankAccout(userBasicInformationDataList.get(4));
            userBasicInformation.setCurrentTransition(userBasicInformationDataList.get(5));
            userBasicInformation.setLastTransition(userBasicInformationDataList.get(6));
            userBasicInformation.setReportLossState(userBasicInformationDataList.get(7));
            userBasicInformation.setFreezeState(userBasicInformationDataList.get(8));
            userBasicInformation.setIdentityType(userBasicInformationDataList.get(9));
            userBasicInformation.setDepartment(userBasicInformationDataList.get(10));
        } else {
            // 从校园卡服务平台获得的数据
            userBasicInformation.setName(userBasicInformationDataList.get(0));
            userBasicInformation.setStuId(userBasicInformationDataList.get(1));
            userBasicInformation.setCardAccount(userBasicInformationDataList.get(2));
            userBasicInformation.setBalance(userBasicInformationDataList.get(3));
            userBasicInformation.setCurrentTransition(userBasicInformationDataList.get(4));
            userBasicInformation.setReportLossState(userBasicInformationDataList.get(5));
            userBasicInformation.setFreezeState(userBasicInformationDataList.get(6));
        }
        // 传递全局变量userBasicInformation
        MyApplication myApp = (MyApplication) getApplication();
        myApp.setUserBasicInformation(userBasicInformation);
        if (myApp.getUserBasicInformation() != null) {
            LogUtil.d(TAG, "Add UserBasicInformation to Application successfully.");
        } else {
            LogUtil.e(TAG, "Fail to add UserBasicInformation to Application");
            new Throwable().printStackTrace();
        }
    }
}
