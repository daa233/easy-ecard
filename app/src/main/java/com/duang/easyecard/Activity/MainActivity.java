package com.duang.easyecard.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.duang.changeiconcolorwithtext.ChangeColorTab;
import com.duang.easyecard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrD on 2016/2/4.
 */
public class MainActivity extends BaseActivity {

    private ViewPager mViewPager;
    private List<Fragment> mTabFragments = new ArrayList<>();
    private ChangeColorTab changeColorTab;

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
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);        }
    }

    //初始化四个Fragment
    private void initData() {
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
    }
}
