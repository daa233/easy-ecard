package com.duang.easyecard.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.duang.easyecard.GlobalData.MyApplication;
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
    private boolean historyLoadingFlag = false;
    private boolean todayLoadingFlag = false;
    private boolean weekLoadingFlag = false;
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

    // 设置加载的状态，如果正在加载，就屏蔽Back按键
    @Override
    public void setLoadingFlag(int type, boolean flag) {
        switch (type) {
            case 0:
                historyLoadingFlag = flag;
                break;
            case 1:
                todayLoadingFlag = flag;
                break;
            case 2:
                weekLoadingFlag = flag;
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
        if (historyLoadingFlag || todayLoadingFlag || weekLoadingFlag) {
            // 正在加载，请稍候
            Toast.makeText(MyApplication.getContext(), getString(R.string.is_loading),
                    Toast.LENGTH_SHORT).show();
        } else {
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
    }

    // 创建菜单项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_manage_trading_inquiry, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // 菜单项选择
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Back
            case android.R.id.home:
                doBack();
                break;
            // Info，本段时间消费总额
            case R.id.action_manage_trading_inquiry_info:
                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:
                        if (historyInitFlag) {
                            ManageTradingInquiryFragment fragment = (ManageTradingInquiryFragment)
                                    getSupportFragmentManager().findFragmentByTag(
                                            fragmentTagHashMap.get(String.valueOf(0)));
                            fragment.showSumTransaction();
                        } else {
                            LogUtil.e(TAG, "History not init.");
                        }
                        break;
                    case 1:
                        if (todayInitFlag) {
                            ManageTradingInquiryFragment fragment = (ManageTradingInquiryFragment)
                                    getSupportFragmentManager().findFragmentByTag(
                                            fragmentTagHashMap.get(String.valueOf(1)));
                            fragment.showSumTransaction();
                        } else {
                            LogUtil.e(TAG, "Today not init.");
                        }
                        break;
                    case 2:
                        if (weekInitFlag) {
                            ManageTradingInquiryFragment fragment = (ManageTradingInquiryFragment)
                                    getSupportFragmentManager().findFragmentByTag(
                                            fragmentTagHashMap.get(String.valueOf(2)));
                            fragment.showSumTransaction();
                        } else {
                            LogUtil.e(TAG, "Week not init.");
                        }
                        break;
                    default:
                        break;
                }
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
