package com.duang.easyecard.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.duang.easyecard.R;

import java.util.ArrayList;
import java.util.List;

public class MessagesCommonProblemsActivity extends BaseActivity implements
        MessagesCommonProblemsFragment.GetSelectedTabListener{

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_common_problems);
        // 初始化布局
        initView();
        // 初始化数据
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.messages_common_problems_toolbar);
        setSupportActionBar(toolbar);
        setDisplayHomeButton();  // 显示Back按钮
        // 设置ViewPager
        viewPager = (ViewPager) findViewById(R.id.messages_common_problems_viewpager);
        setupViewPager(viewPager);
        // Assigns the ViewPager to TabLayout.
        tabLayout = (TabLayout) findViewById(R.id.messages_common_problems_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initData() {
    }

    // Defines the number of tabs by setting appropriate fragment and tab name.
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", 0);
        MessagesCommonProblemsFragment fragment = new MessagesCommonProblemsFragment();
        fragment.setArguments(bundle);
        mViewPagerAdapter.addFragment(fragment, "校园卡管理");
        bundle.putInt("POSITION", 1);
        fragment.setArguments(bundle);
        mViewPagerAdapter.addFragment(fragment, "应用中心");
        bundle.putInt("POSITION", 2);
        fragment.setArguments(bundle);
        mViewPagerAdapter.addFragment(fragment, "帐户安全");
        bundle.putInt("POSITION", 3);
        fragment.setArguments(bundle);
        mViewPagerAdapter.addFragment(fragment, "在线缴费");
        viewPager.setAdapter(mViewPagerAdapter);
    }

    // 实现在Fragment中的接口，以便在Fragment中获取当前Tab的位置
    @Override
    public int getSelectedTabPosition() {
        return tabLayout.getSelectedTabPosition();
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
