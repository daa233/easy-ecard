package com.duang.easyecard.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManageTradingInquiryActivity extends BaseActivity implements
        ManageTradingInquiryFragment.CommunicateListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private boolean historyInitFlag = false;
    private boolean todayInitFlag = false;
    private boolean weekInitFlag = false;
    private HashMap<String, String> fragmentTagHashMap;
    private final String TAG = "ManageTradingInquiryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_trading_inquiry);
        // 初始化布局
        initView();
        // 初始化fragmentTagHashMap
        fragmentTagHashMap = new HashMap<>();
    }

    // 初始化布局
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.manage_trading_inquiry_toolbar);
        setSupportActionBar(toolbar);
        // 显示home按钮
        setDisplayHomeButton();
        // 设置ViewPager
        viewPager = (ViewPager) findViewById(R.id.manage_trading_inquiry_viewpager);
        setupViewPager(viewPager);
        // Assigns the ViewPager to TabLayout.
        tabLayout = (TabLayout) findViewById(R.id.manage_trading_inquiry_tabs);
        tabLayout.setupWithViewPager(viewPager);
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

    // 让Fragment获得初始化状态
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

    // 设置Fragment的初始化状态
    @Override
    public void setDataListInitFlag(int type, boolean flag) {
        switch (type) {
            case 0:
                historyInitFlag = flag;
                break;
            case 1:
                todayInitFlag = flag;
                break;
            case 2:
                weekInitFlag = flag;
                break;
            default:
                LogUtil.e(TAG, "Unexpect type.");
                break;
        }
    }

    // 获得Fragment传递过来的Tag，用于定位Fragment
    @Override
    public void getFragmentTag(int type, String tag) {
        fragmentTagHashMap.put(String.valueOf(type), tag);
    }

    // 该类型Fragment已销毁
    @Override
    public void isOnStop(int type) {
        // 获得Fragment的实例，当“历史流水”销毁时，在“一周流水”中显示交易额汇总；反之亦然
        String tag = fragmentTagHashMap.get(String.valueOf(2 - type));
        ManageTradingInquiryFragment fragment = (ManageTradingInquiryFragment)
                getSupportFragmentManager().findFragmentByTag(tag);
        fragment.showSumTransaction();
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

    /**
     * doBack方法
     * 根据当前状态判断是否退出
     * 如果不处于“历史流水”查询状态，则直接退出
     * 如果处于“历史流水”查询状态，切换到时间选择界面
     */
    private void doBack() {
        switch (tabLayout.getSelectedTabPosition()) {
            case 0:
                // 位于“历史流水”查询
                if (historyInitFlag) {
                    // 正在显示ListView(查询结果），返回到选择时间界面
                    ManageTradingInquiryFragment fragment = (ManageTradingInquiryFragment)
                            getSupportFragmentManager().findFragmentByTag(
                                    fragmentTagHashMap.get(String.valueOf(0)));
                    fragment.backToPickDateView();
                    // 设置初始化标志为false
                    historyInitFlag = false;
                } else {
                    // 正在选择时间，点击直接退出
                    finish();
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

    // 监听返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            doBack();
            return false;
        }
        return false;
    }
}
