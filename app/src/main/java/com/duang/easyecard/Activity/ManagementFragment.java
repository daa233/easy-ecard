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
import android.widget.SimpleAdapter;

import com.duang.easyecard.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManagementFragment extends Fragment  implements
        AdapterView.OnItemClickListener {

    private View viewFragment;

    private GridView gridView;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;

    // ItemImage图标封装为一个数组
    private int [] iconImage = {
            R.drawable.manage_basic_info,
            R.drawable.manage_trading_inquiry,
            R.drawable.manage_report_loss,
            R.drawable.manage_recharge,
            R.drawable.manage_net_charge,
            R.drawable.manage_pay_fees,
    };
    // ItemText封装数组
    private String[] iconText = {"基本信息", "流水查询", "校园卡挂失",
            "转账充值", "网费",    "待缴费"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewFragment = inflater.inflate(R.layout.fragment_management, null);
        // 实例化控件
        gridView = (GridView) viewFragment.findViewById(R.id.manage_grid_view);
        // 新建List
        data_list = new ArrayList<Map<String, Object>>();
        // 获取数据
        getData();
        // 新建适配器
        String [] from = {"image", "text"};
        int [] to = {R.id.grid_view_item_img, R.id.grid_view_item_text};
        sim_adapter = new SimpleAdapter(this.getActivity(), data_list,
                R.layout.manage_grid_view_item, from, to);
        // 配置适配器
        gridView.setAdapter(sim_adapter);
        // 设置监听器
        gridView.setOnItemClickListener(this);
        return viewFragment;
    }

    public List<Map<String, Object>> getData(){
        //icon和iconName的长度是相同的，这里任选其一都可以
        for(int i = 0; i < iconImage.length; i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", iconImage[i]);
            map.put("text", iconText[i]);
            data_list.add(map);
        }
        return data_list;
    }

    // Item的点击事件,根据图片ID来确定点击对象
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (iconImage[position]) {
            /*
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
                final String[] arrayDialogItems = new String[] {"通过校园卡电子服务平台",
                        "通过拨打挂失电话 6678-2221"};
                Dialog alertDialog = new AlertDialog.Builder(getActivity()).
                        setTitle("请选择挂失方式：").
                        setIcon(R.drawable.manage_report_loss)
                        .setItems(arrayDialogItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    // 跳转到ManageReportLossActivity
                                    Intent intent = new Intent(getActivity(),
                                            ManageReportLossActivity.class);
                                    startActivity(intent);
                                } else {
                                    // 拨打挂失电话
                                    AlertDialog.Builder callDialog = new AlertDialog.
                                            Builder(getActivity());
                                    callDialog.setTitle("提示");
                                    callDialog.setMessage("您确定要拨打挂失电话\n"
                                            + "(0532-6678-2221)吗？");
                                    callDialog.setIcon(R.drawable.manage_report_loss);
                                    callDialog.setPositiveButton("确定",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {
                                                    // 通过Intent调用拨打电话程序
                                                    Intent intent = new Intent(
                                                            Intent.ACTION_CALL,
                                                            Uri.parse("tel:" + "053266782221"));
                                                    startActivity(intent);
                                                }
                                            });
                                    callDialog.setNegativeButton("取消",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {}
                                            });
                                    callDialog.show();
                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
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
                */
        }
    }
}
