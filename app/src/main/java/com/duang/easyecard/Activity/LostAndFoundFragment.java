package com.duang.easyecard.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.duang.easyecard.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LostAndFoundFragment extends Fragment implements View.OnClickListener{

    private LinearLayout lostLinearLayout;
    private LinearLayout foundLinearLayout;

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
        lostLinearLayout = (LinearLayout) getActivity().findViewById(
                R.id.lost_and_found_fragment_lost_linear_layout);
        foundLinearLayout = (LinearLayout) getActivity().findViewById(
                R.id.lost_and_found_fragment_found_linear_layout);
        // 监听点击事件
        lostLinearLayout.setOnClickListener(this);
        foundLinearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.lost_and_found_fragment_lost_linear_layout:
                // 跳转到失卡信息浏览界面
                startActivity(new Intent(getActivity(),
                        LostAndFoundInformationBrowsingActivity.class));
                break;
            case R.id.lost_and_found_fragment_found_linear_layout:
                // 跳转到丢失卡登记界面
                startActivity(new Intent(getActivity(), LostAndFoundRegistrationActivity.class));
                break;
            default:
                break;
        }
    }
}
