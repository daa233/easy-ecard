package com.duang.easyecard.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.duang.easyecard.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LostAndFoundFragment extends Fragment implements View.OnClickListener {

    public LostAndFoundFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lost_and_found, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 实例化控件
        LinearLayout infoBrowsingLinearLayout = (LinearLayout) getActivity().findViewById(
                R.id.lost_and_found_fragment_info_browsing_linear_layout);
        LinearLayout reportLossLinearLayout = (LinearLayout) getActivity().findViewById(
                R.id.lost_and_found_fragment_report_loss_linear_layout);
        ImageView infoBrowsingImageView = (ImageView) getActivity().findViewById(
                R.id.lost_and_found_fragment_info_browsing_image_view);
        ImageView reportLossImageView = (ImageView) getActivity().findViewById(
                R.id.lost_and_found_fragment_report_loss_image_view);
        // 通过Glide加载图片资源
        Glide
                .with(this)
                .load(R.drawable.lost_and_found_info_browsing)
                .into(infoBrowsingImageView);
        Glide
                .with(this)
                .load(R.drawable.lost_and_found_registration)
                .into(reportLossImageView);
        // 监听点击事件
        infoBrowsingLinearLayout.setOnClickListener(this);
        reportLossLinearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lost_and_found_fragment_info_browsing_linear_layout:
                // 跳转到失卡信息浏览界面
                startActivity(new Intent(getActivity(),
                        LostAndFoundInformationBrowsingActivity.class));
                break;
            case R.id.lost_and_found_fragment_report_loss_linear_layout:
                // 跳转到丢失卡登记界面
                startActivity(new Intent(getActivity(), LostAndFoundRegistrationActivity.class));
                break;
            default:
                break;
        }
    }
}
