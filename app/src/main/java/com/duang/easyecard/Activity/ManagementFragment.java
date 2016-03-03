package com.duang.easyecard.Activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.bumptech.glide.Glide;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.Model.SimpleItem;
import com.duang.easyecard.R;
import com.duang.easyecard.Util.LogUtil;
import com.duang.easyecard.Util.ManagementGridViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManagementFragment extends Fragment implements
        AdapterView.OnItemClickListener {

    private View viewFragment;

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
            R.drawable.manage_pay_fees,
    };

    private String[] iconText;

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
                getResources().getString(R.string.pay_fees)
        };
        mAdapter = new ManagementGridViewAdapter(MyApplication.getContext(),
                getDataLists(iconImage, iconText),
                R.layout.manage_grid_view_item);
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
            LogUtil.e(getTag(), "Error: Arrays' lengths don't match.");
        }
        return itemList;
    }

    // Item的点击事件,根据图片ID来确定点击对象
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (iconImage[position]) {
            case R.drawable.manage_basic_info:
                // 跳转到ManageViewBasicInfoActivity
                Intent intent = new Intent(this.getContext(),
                        ManageBasicInfoActivity.class);
                startActivity(intent);
                break;
            case R.drawable.manage_trading_inquiry:
                intent = new Intent(this.getContext(),
                        ManageTradingInquiryActivity.class);
                startActivity(intent);
                break;
            case R.drawable.manage_report_loss:
                final String[] arrayDialogItems = new String[]{
                        getResources().getString(R.string.by_ecard_service_platform),
                        getResources().getString(R.string.by_call_report_line)};
                Dialog alertDialog = new AlertDialog.Builder(getActivity()).
                        setTitle(getResources().getString(
                                R.string.please_choose_a_way_to_report_loss)).
                        setIcon(R.drawable.manage_report_loss)
                        .setItems(arrayDialogItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    // 跳转到ManageReportLossActivity
                                    /*
                                    startActivity(new Intent(getActivity(),
                                            ManageReportLossActivity.class));
                                            */
                                } else {
                                    // 拨打挂失电话
                                    AlertDialog.Builder callDialog =
                                            new AlertDialog.Builder(getActivity());
                                    callDialog.setMessage(
                                            getResources().getString(R.string.phone_call_check));
                                    callDialog.setPositiveButton(
                                            getResources().getString(R.string.OK),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {
                                                    // 通过Intent调用拨打电话程序
                                                    Intent intent = new Intent(Intent.ACTION_CALL,
                                                            Uri.parse("tel:" + "053266782221"));
                                                    startActivity(intent);
                                                }
                                            });
                                    callDialog.setNegativeButton(
                                            getResources().getString(R.string.Cancel),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {
                                                }
                                            });
                                    callDialog.show();
                                }
                            }
                        }).setNegativeButton(getResources().getString(R.string.Cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).create();
                alertDialog.show();
                break;
            case R.drawable.manage_recharge:
                break;
            case R.drawable.manage_net_charge:
                break;
            case R.drawable.manage_pay_fees:
                break;
            default:
                break;
        }
    }
}
