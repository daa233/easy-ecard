package com.duang.easyecard.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.Model.SettingsListViewItem;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.duang.easyecard.Util.SettingsListViewAdapter;
import com.pgyersdk.feedback.PgyFeedback;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

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
    private AppBean appBean;

    private int[] iconImageArray = {
            R.drawable.ic_assignment_ind_cyan_a700_36dp,
            R.drawable.ic_share_blue_900_36dp,
            R.drawable.ic_update_teal_a700_36dp,
            R.drawable.ic_feedback_amber_a400_36dp,
            R.drawable.ic_code_pink_500_36dp,
            R.drawable.ic_exit_to_app_blue_grey_500_36dp
    };
    private String[] titleArray;
    private final int arrowResId = R.drawable.ic_keyboard_arrow_right_black_24dp;
    private final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

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
                getString(R.string.settings_share_app),
                getString(R.string.settings_update),
                getString(R.string.settings_feedback),
                getString(R.string.settings_about),
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
        // 检查更新
        checkForUpdate(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.d(TAG, "onItemClick: " + position);
        switch (position) {
            case 0:
                // 个人信息
                startActivity(new Intent(MyApplication.getContext(),
                        SettingsPersonalInformationActivity.class));
                break;
            case 1:
                // 分享应用
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,
                        getString(R.string.settings_share_app_download_link));
                startActivity(Intent.createChooser(intent, getString(R.string.settings_share_app)));
                break;
            case 2:
                // 检查更新
                checkForUpdate(true);
                break;
            case 3:
                // 意见反馈
                // 处理用户权限
                if (ContextCompat.checkSelfPermission(MyApplication.getContext(),
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MyApplication.getContext(),
                            getString(R.string.settings_feedback_permission_note),
                            Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                } else {
                    // 显示用户反馈对话框
                    PgyFeedback.getInstance().showDialog(getActivity());
                }
                break;
            case 4:
                // 关于软件
                break;
            case 5:
                // 退出登录
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

    // 检查更新
    private void checkForUpdate(final boolean flag) {
        PgyUpdateManager.register(getActivity(), new UpdateManagerListener() {
            @Override
            public void onNoUpdateAvailable() {
                // 没有可用更新
                if (flag) {
                    Toast.makeText(MyApplication.getContext(),
                            getString(R.string.settings_no_update_available),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onUpdateAvailable(String result) {
                // 有可用更新
                // 将新版本信息封装到AppBean中
                appBean = getAppBeanFromString(result);
                new AlertDialog.Builder(MyApplication.getContext())
                        .setTitle(getString(R.string.settings_update))
                        .setMessage(appBean.getVersionCode() + "\n"
                                + appBean.getVersionName() + "\n" + appBean.getReleaseNote())
                        .setNegativeButton(
                                getString(R.string.settings_update_later),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 以后再说
                                        dialog.dismiss();
                                    }
                                })
                        .setPositiveButton(getString(R.string.settings_update_now),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 立即下载更新
                                        startDownloadTask(getActivity(), appBean.getDownloadURL());
                                        dialog.dismiss();
                                    }
                                }).show();
            }
        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 显示用户反馈对话框
                PgyFeedback.getInstance().showDialog(getActivity());
            } else {
                // Permission Denied
                Toast.makeText(MyApplication.getContext(),
                        "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}