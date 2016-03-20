package com.duang.easyecard.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.Model.SimpleItem;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.duang.easyecard.Util.ManagementGridViewAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManagementFragment extends Fragment implements
        AdapterView.OnItemClickListener {

    private View viewFragment;
    private StartManageBasicInformationCallback startManageBasicInformationCallback;

    private GridView mGridView;
    private ImageView mCampusImageView;
    private ManagementGridViewAdapter mAdapter;

    // ItemImage图标封装为一个数组
    private int[] iconImage = {
            R.drawable.manage_basic_info,
            R.drawable.manage_trading_inquiry,
            R.drawable.manage_report_loss,
            R.drawable.manage_recharge,
            R.drawable.manage_net_charge,
            R.drawable.manage_change_password,
    };
    private String[] iconText;

    private final String TAG = "ManagementFragment";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof StartManageBasicInformationCallback)) {
            throw new IllegalStateException("fragment所在的Activity必须实现Callbacks接口");
        }
        // 把绑定的activity当成callback对象
        startManageBasicInformationCallback = (StartManageBasicInformationCallback) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewFragment = inflater.inflate(R.layout.fragment_management, null);
        // 实例化控件
        mGridView = (GridView) viewFragment.findViewById(R.id.manage_grid_view);
        mCampusImageView = (ImageView) viewFragment.findViewById(R.id.manage_campus_image_view);
        // 通过Glide设置mCampusImageView资源
        Glide
                .with(this)
                .load(R.drawable.main_campus_scenery)
                .into(mCampusImageView);
        // ItemText封装数组
        iconText = new String[]{
                getResources().getString(R.string.basic_information),
                getResources().getString(R.string.trading_inquiry),
                getResources().getString(R.string.report_loss_card),
                getResources().getString(R.string.recharge),
                getResources().getString(R.string.net_charge),
                getResources().getString(R.string.change_password)
        };
        mAdapter = new ManagementGridViewAdapter(MyApplication.getContext(),
                getDataLists(iconImage, iconText),
                R.layout.item_manage_grid_view);
        // 配置适配器
        mGridView.setAdapter(mAdapter);
        // 设置监听器
        mGridView.setOnItemClickListener(this);
        return viewFragment;
    }

    // 获得数据List并返回
    public List<SimpleItem> getDataLists(int[] imageResources, String[] textArray) {
        List<SimpleItem> itemList = new ArrayList<>();
        if (imageResources.length == textArray.length) {
            SimpleItem simpleItem;
            for (int i = 0; i < imageResources.length; i++) {
                simpleItem = new SimpleItem();
                simpleItem.setResourceId(imageResources[i]);
                simpleItem.setString(textArray[i]);
                itemList.add(simpleItem);
            }
        } else {
            LogUtil.e(TAG, "Error: Arrays' lengths don't match.");
        }
        return itemList;
    }

    // Item的点击事件,根据图片ID来确定点击对象
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (iconImage[position]) {
            case R.drawable.manage_basic_info:
                // 更新基本信息并跳转activity
                startManageBasicInformationCallback.sendGETRequestToMobile(true);
                break;
            case R.drawable.manage_trading_inquiry:
                startActivity(new Intent(this.getContext(), ManageTradingInquiryActivity.class));
                break;
            case R.drawable.manage_report_loss:
                // 先给出用户挂失的提示
                final SweetAlertDialog alertDialog = new SweetAlertDialog(this.getContext(),
                        SweetAlertDialog.WARNING_TYPE);
                alertDialog
                        .setTitleText(getString(R.string.hint_start_report_loss_warning_title))
                        .setContentText(getString(R.string.hint_start_report_loss_warning_content))
                        .setConfirmText(getString(R.string.OK))
                        .setCancelText(getString(R.string.Cancel))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            // 用户确定挂失
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                // 跳转到ManageReportLossActivity
                                startActivity(new Intent(getActivity(), ManageReportLossActivity.class));
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.drawable.manage_recharge:
                break;
            case R.drawable.manage_net_charge:
                break;
            case R.drawable.manage_change_password:
                // 修改查询密码
                startActivity(new Intent(this.getContext(), ManageChangePasswordActivity.class));
                break;
            default:
                break;
        }
    }

    // StartManageBasicInformationCallback接口，为了在打开基本信息界面时及时更新信息
    public interface StartManageBasicInformationCallback {
        void sendGETRequestToMobile(boolean openActivityFlag);
    }
}
