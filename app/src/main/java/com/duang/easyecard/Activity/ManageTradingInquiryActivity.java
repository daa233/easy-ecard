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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.GlobalData.UrlConstant;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.duang.easyecard.Util.TradingInquiryDateUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ManageTradingInquiryActivity extends BaseActivity implements
        ManageTradingInquiryFragment.GetDataListInitFlagListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private AsyncHttpClient httpClient;
    private boolean historyInitFlag = false;
    private boolean todayInitFlag = false;
    private boolean weekInitFlag = false;
    private final String TAG = "ManageTradingInquiryActivity";

    /**
     * INIT_FLAG
     * 0，未开始加载
     * 1，正在加载
     * 2，加载完成，有数据
     * 3，加载完成，没有数据
     */
    private int HISTORY_TAB_INIT_FLAG = 0;
    private static int TODAY_TAB_INIT_FLAG = 0;
    private static int WEEK_TAB_INIT_FLAG = 0;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.manage_trading_inquiry_toolbar);
        setSupportActionBar(toolbar);
        // 显示home按钮
        setDisplayHomeButton();
        // 操作FloatingActionButton
        FloatingActionButton fab = (FloatingActionButton) findViewById(
                R.id.manage_trading_inquiry_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // 设置ViewPager
        viewPager = (ViewPager) findViewById(R.id.manage_trading_inquiry_viewpager);
        setupViewPager(viewPager);
        // Assigns the ViewPager to TabLayout.
        tabLayout = (TabLayout) findViewById(R.id.manage_trading_inquiry_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    // 初始化数据
    private void initData() {
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
                LogUtil.d(TAG, new String(responseBody));
            }

            // 网络错误
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                LogUtil.e(TAG, "Network error.");
                Toast.makeText(ManageTradingInquiryActivity.this, R.string.network_error,
                        Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    // Defines the number of tabs by setting appropriate fragment and tab name.
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", 0);
        ManageTradingInquiryFragment fragment = new ManageTradingInquiryFragment();
        fragment.setArguments(bundle);
        mViewPagerAdapter.addFragment(fragment, getString(R.string.history_trading_inquiry));
        fragment = new ManageTradingInquiryFragment();
        bundle = new Bundle();
        bundle.putInt("POSITION", 1);
        fragment.setArguments(bundle);
        mViewPagerAdapter.addFragment(fragment, getString(R.string.day_trading_inquiry));
        fragment = new ManageTradingInquiryFragment();
        bundle = new Bundle();
        bundle.putInt("POSITION", 2);
        fragment.setArguments(bundle);
        mViewPagerAdapter.addFragment(fragment, getString(R.string.week_trading_inquiry));
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
                // “历史流水”查询，根据Fragment的状态进行下一步操作
                break;
            case 1:
            case 2:
                // 不是“历史流水”查询，直接退出
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

    // 监听返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            doBack();
            return false;
        }
        return false;
    }

    @Override
    public boolean getDataListInitFlag(int type) {
        switch (type) {
            case 0:
                return historyInitFlag;
            case 1:
                return todayInitFlag;
            case 2:
                return weekInitFlag;
            default:
                LogUtil.e(TAG, "Unexpect type.");
                return false;
        }
    }

    @Override
    public void setDataListInitFlag(int type) {
        switch (type) {
            case 0:
                historyInitFlag = true;
                break;
            case 1:
                todayInitFlag = true;
                break;
            case 2:
                weekInitFlag = true;
                break;
            default:
                LogUtil.e(TAG, "Unexpect type.");
                break;
        }
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
