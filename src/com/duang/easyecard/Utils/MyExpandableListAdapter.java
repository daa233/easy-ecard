package com.duang.easyecard.Utils;

import java.util.ArrayList;
import java.util.List;

import com.duang.easyecard.R;
import com.duang.easyecard.Models.Group;
import com.duang.easyecard.Models.TradingInquiry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

	@SuppressWarnings("unused")
	private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Group> mGroupList;
	private ArrayList<List<TradingInquiry>> mChildList;

    public MyExpandableListAdapter(Context context, ArrayList<Group> groupList,
    		ArrayList<List<TradingInquiry>> childList) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        mGroupList = groupList;
        mChildList = childList;
    }

    // 返回父列表个数
    @Override
    public int getGroupCount() {
        return mGroupList.size();
    }

    // 返回子列表个数
    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {

        return mGroupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {

        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView == null) {
            groupHolder = new GroupHolder();
            convertView = mInflater.inflate(R.layout.trading_inquiry_group, null);
            groupHolder.textView = (TextView) convertView
                    .findViewById(R.id.group);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }

        groupHolder.textView.setText(((Group) getGroup(groupPosition))
                .getTitle());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder = null;
        if (convertView == null) {
            childHolder = new ChildHolder();
            convertView = mInflater.inflate(R.layout.trading_inquiry_child, null);
            // 实例化childHolder的TextView
            childHolder.textTradingTime = (TextView) convertView
             .findViewById(R.id.trading_inquiry_child_trading_time_text);
            childHolder.textMerchantName = (TextView) convertView
             .findViewById(R.id.trading_inquiry_child_merchant_name_text);
            childHolder.textTradingName = (TextView) convertView
             .findViewById(R.id.trading_inquiry_child_trading_name_text);
            childHolder.textTransactionAmount = (TextView) convertView
             .findViewById(R.id.trading_inquiry_child_transaction_amount_text);

            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        // 设置childHolder里TextView的显示内容
        childHolder.textTradingTime.setText(
        		((TradingInquiry) getChild(groupPosition,
                childPosition)).getTradingTime());
        childHolder.textMerchantName.setText(
        		((TradingInquiry) getChild(groupPosition,
                childPosition)).getMerchantName());
        childHolder.textTradingName.setText(
        		((TradingInquiry) getChild(groupPosition,
                childPosition)).getTradingName());
        childHolder.textTransactionAmount.setText(
        		((TradingInquiry) getChild(groupPosition,
                childPosition)).getTransactionAmount());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupHolder {
        TextView textView;
        ImageView imageView;
    }

    class ChildHolder {
    	TextView textTradingTime;
        TextView textMerchantName;
        TextView textTradingName;
        TextView textTransactionAmount;
    }
}
