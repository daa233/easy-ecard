package com.duang.easyecard.Util;

import android.content.Context;

import com.duang.easyecard.Model.Group;
import com.duang.easyecard.Model.TradingInquiry;
import com.duang.easyecard.R;
import com.duang.mypinnedheaderlistview.CommonExpandableListAdapter;
import com.duang.mypinnedheaderlistview.ExpandableListViewHolder;

import java.util.List;

/**
 * Created by MrD on 2016/2/27.
 */
public class TradingInquiryExpandableListAdapter extends
        CommonExpandableListAdapter<Group, TradingInquiry> {

    public TradingInquiryExpandableListAdapter(Context context, List<Group> groupList,
                                        int groupItemLayoutId, List<List<TradingInquiry>> childList,
                                        int childItemLayoutId) {
        super(context, groupList, groupItemLayoutId, childList, childItemLayoutId);
    }

    @Override
    public void convertGroupView(ExpandableListViewHolder holder, Group group) {
        holder.setText(R.id.manage_traing_inquiry_group_item_text, group.getTitle());
    }

    @Override
    public void convertChildView(ExpandableListViewHolder holder, TradingInquiry tradingInquiry) {
        holder.setText(R.id.manage_trading_inquiry_child_trading_time_text,
                tradingInquiry.getTradingTime()).
                setText(R.id.manage_trading_inquiry_child_merchant_name_text,
                        tradingInquiry.getMerchantName()).
                setText(R.id.manage_trading_inquiry_child_trading_name_text,
                        tradingInquiry.getTradingName()).
                setText(R.id.manage_trading_inquiry_child_transaction_amount_text,
                        tradingInquiry.getTransactionAmount());
    }
}
