package com.duang.easyecard.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.Model.SettingsListViewItem;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.duang.easyecard.Util.SettingsListViewAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * SettingsFragment 设置
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private View viewFragment;
    private ListView mListView;

    private List<SettingsListViewItem> dataList;
    private SettingsListViewAdapter mAdapter;

    private int[] iconImageArray = {
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher
    };
    private String[] titleArray;
    private final int arrowResId = R.drawable.ic_keyboard_arrow_right_black_24dp;

    private final String TAG = "SettingsFragment";

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewFragment = inflater.inflate(R.layout.fragment_settings, container, false);
        initView();
        initData();
        return viewFragment;
    }

    private void initView() {
        // 实例化控件
        mListView = (ListView) viewFragment.findViewById(R.id.settings_list_view);
    }

    private void initData() {
        // ItemTitle
        titleArray = new String[]{
                getString(R.string.settings_personal_information),
                getString(R.string.settings_feedback),
                getString(R.string.about),
                getString(R.string.sign_off)
        };
        dataList = new ArrayList<>();
        SettingsListViewItem item;
        if (iconImageArray.length == titleArray.length) {
            for (int i = 0; i < titleArray.length; i++) {
                item = new SettingsListViewItem(titleArray[i]);
                item.setIconResId(iconImageArray[i]);
                item.setArrowResId(arrowResId);
                dataList.add(item);
            }
        } else {
            // 数据数目不匹配
            LogUtil.e(TAG, "Error: Arrays' lengths don't match.");
        }
        mAdapter = new SettingsListViewAdapter(MyApplication.getContext(), dataList,
                R.layout.item_settings_fragment_list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.d(TAG, "onItemClick: " + position);
        switch (position) {
            case 0:
                // 打开“个人信息”
                startActivity(new Intent(MyApplication.getContext(),
                        SettingsPersonalInformationActivity.class));
                break;
            case 1:
                // 打开“意见反馈”
                startActivity(new Intent(MyApplication.getContext(),
                        SettingsFeedbackActivity.class));
                break;
            case 2:
                // 打开“关于软件”
                break;
            case 3:
                // “退出登录”
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getString(R.string.sign_off))
                        .setContentText(getString(R.string.sign_off_config))
                        .setConfirmText(getString(R.string.OK))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                // 点击“确定”按钮，销毁MainActivity，跳转到SigninActivity
                                signOff();
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .setCancelText(getString(R.string.Cancel))
                        .show();
                break;
            default:
                break;
        }
    }

    // 注销
    private void signOff() {
        // 将全局变量httpClient和userBasicInformation设置为null
        MyApplication myApp = (MyApplication) getActivity().getApplication();
        myApp.setHttpClient(null);
        myApp.setUserBasicInformation(null);
        // 跳转到SigninActivity
        startActivity(new Intent(MyApplication.getContext(), SigninActivity.class));
        // 销毁MainActivity
        getActivity().finish();
    }
}