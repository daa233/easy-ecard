package com.duang.easyecard.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class MessagesFaqActivity extends BaseActivity implements
        MessagesFaqFragment.GetDataListInitFlagListener {

    private static final String TAG = "MessagesFaqActivity";
    private boolean cardManageListInitFlag = false;
    private boolean applicationCenterInitFlag = false;
    private boolean accountSecureListInitFlag = false;
    private boolean onlinePayListInitFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_faq);
        // 初始化布局
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.messages_faq_toolbar);
        setSupportActionBar(toolbar);
        setDisplayHomeButton();  // 显示Back按钮
        // 设置ViewPager
        ViewPager viewPager = (ViewPager) findViewById(R.id.messages_faq_viewpager);
        setupViewPager(viewPager);
        // Assigns the ViewPager to TabLayout.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.messages_faq_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    // Defines the number of tabs by setting appropriate fragment and tab name.
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", 0);
        MessagesFaqFragment fragment = new MessagesFaqFragment();
        fragment.setArguments(bundle);
        mViewPagerAdapter.addFragment(fragment, getString(R.string.card_manage));
        fragment = new MessagesFaqFragment();
        bundle = new Bundle();
        bundle.putInt("POSITION", 1);
        fragment.setArguments(bundle);
        mViewPagerAdapter.addFragment(fragment, getString(R.string.application_center));
        fragment = new MessagesFaqFragment();
        bundle = new Bundle();
        bundle.putInt("POSITION", 2);
        fragment.setArguments(bundle);
        mViewPagerAdapter.addFragment(fragment, getString(R.string.account_secure));
        fragment = new MessagesFaqFragment();
        bundle = new Bundle();
        bundle.putInt("POSITION", 3);
        fragment.setArguments(bundle);
        mViewPagerAdapter.addFragment(fragment, getString(R.string.online_pay));
        viewPager.setAdapter(mViewPagerAdapter);
    }

    @Override
    public boolean getDataListInitFlag(int type) {
        switch (type) {
            case 0:
                return cardManageListInitFlag;
            case 1:
                return applicationCenterInitFlag;
            case 2:
                return accountSecureListInitFlag;
            case 3:
                return onlinePayListInitFlag;
            default:
                LogUtil.e(TAG, "Unexpect type.");
                return false;
        }
    }

    @Override
    public void setDataListInitFlag(int type) {
        switch (type) {
            case 0:
                cardManageListInitFlag = true;
                break;
            case 1:
                applicationCenterInitFlag = true;
                break;
            case 2:
                accountSecureListInitFlag = true;
                break;
            case 3:
                onlinePayListInitFlag = true;
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
