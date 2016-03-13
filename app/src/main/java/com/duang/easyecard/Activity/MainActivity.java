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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

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
        mTabFragments.add(new InformationFragment());
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
        sendGETRequest();
    }

    // 发送GET请求
    private void sendGETRequest() {
        httpClient.get(UrlConstant.MOBILE_MANAGE_BASIC_INFO, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 成功响应
                response = new String(responseBody);
                // 解析response
                new JsoupHtmlData().execute();
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

    // 通过网站返回的html文本解析数据
    private class JsoupHtmlData extends AsyncTask<Void, Void, Void> {

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
            setGlobalUserBasicInformation();
        }
    }

    private void setGlobalUserBasicInformation() {
        // 从userInformationDataList中获取数据
        userBasicInformation = new UserBasicInformation();
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
