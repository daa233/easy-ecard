package com.duang.easyecard.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ManageTradingInquiryActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    protected static AsyncHttpClient httpClient;
    protected static ArrayList<HashMap<String, String>> historyDataList;
    protected static ArrayList<HashMap<String, String>> todayDataList;
    protected static ArrayList<HashMap<String, String>> weekDataList;

    protected static boolean HISTORY_TAB_INIT_FLAG;
    protected static boolean TODAY_TAB_INIT_FLAG;
    protected static boolean WEEK_TAB_INIT_FLAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_trading_inquiry);
        // 初始化布局
        initView();
        // 初始化数据
        initData();
    }

    // 初始化布局
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // 显示home按钮
        setupActionBar();

        // 设置ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Assigns the ViewPager to TabLayout.
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    // 初始化数据
    private void initData() {
        // 初始化各INIT_FLAG
        HISTORY_TAB_INIT_FLAG = false;
        TODAY_TAB_INIT_FLAG = false;
        WEEK_TAB_INIT_FLAG = false;
        // 获得全局变量httpClient
        MyApplication myApp = (MyApplication) getApplication();
        httpClient = myApp.getHttpClient();
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
                LogUtil.d("ManageTradingInquiryAcitvity", new String(responseBody));
            }

            // 网络错误
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(ManageTradingInquiryActivity.this, R.string.network_error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Defines the number of tabs by setting appropriate fragment and tab name.
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragment(new ManageTradingInquiryHistoryFragment(),
                getResources().getString(R.string.history_trading_inquiry));
        mViewPagerAdapter.addFragment(new ManageTradingInquiryTodayFragment(),
                getResources().getString(R.string.day_trading_inquiry));
        mViewPagerAdapter.addFragment(new ManageTradingInquiryWeekFragment(),
                getResources().getString(R.string.week_trading_inquiry));
        viewPager.setAdapter(mViewPagerAdapter);
    }

    /**
     * doBack方法
     * 根据当前状态判断是否退出
     * 如果不处于“历史流水”查询状态，则直接退出
     * 如果处于“历史流水”查询状态，切换到时间选择界面
     */
    private void doBack() {
        switch (tabLayout.getSelectedTabPosition()) {
            case 0:
                if (!HISTORY_TAB_INIT_FLAG) {
                    // 位于“历史流水”时间选择界面，直接退出
                    finish();
                } else {
                    // 位于“历史流水”查询结果界面，返回到时间选择界面
                    ManageTradingInquiryHistoryFragment.pickDateView.setVisibility(View.VISIBLE);
                    ManageTradingInquiryHistoryFragment.resultView.setVisibility(View.GONE);
                    // 将HISTORY_TAB_INIT_FLAG置为false
                    ManageTradingInquiryActivity.HISTORY_TAB_INIT_FLAG = false;
                }
                break;
            case 1:
            case 2:
                // 不处于“历史流水”查询状态，则直接退出
                finish();
                break;
            default:
                break;
        }
    }

    // 菜单项选择
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                doBack();
                break;
            default:
                break;
        }
        return false;
    }

    // Custom adapter class provides fragments required for the view pager.
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
