package com.duang.easyecard.Util;

import android.content.Context;
import android.view.View;

import com.duang.easyecard.Model.Notice;
import com.duang.easyecard.R;

import java.util.List;

/**
 * Created by MrD on 2016/3/23.
 */
public class MessagesNoticeListAdapter extends CommonAdapter<Notice> {

    public MessagesNoticeListAdapter(Context context, List<Notice> datas, int itemLayoutId) {
        super(context, datas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder holder, Notice notice) {
        if (notice.isReceivedType()) { // 位于收件箱需要特殊显示的控件
            if (notice.isUnread()) {
                // 未读消息，设置显示红点，用于标识
                holder.setVisibility(R.id.messages_inbox_and_sent_list_item_new_spot, View.VISIBLE);
            } else {
                // 已读消息设置不显示红点
                holder.setVisibility(R.id.messages_inbox_and_sent_list_item_new_spot, View.GONE);
            }
            // 发件人姓名
            holder.setText(R.id.messages_inbox_and_sent_list_item_user, notice.getSenderName());
        } else { // 位于已发送需要特殊显示的控件
            // 接收人姓名
            holder.setText(R.id.messages_inbox_and_sent_list_item_user, notice.getReceiverName());
        }
        // 消息标题
        holder.setText(R.id.messages_inbox_and_sent_list_item_title, notice.getTitle());
        // 消息发布时间
        holder.setText(R.id.messages_inbox_and_sent_list_item_sent_time, notice.getSentTime());
        // 判断是否处于待删除状态，是则显示CheckBox
        if (notice.isToDelete()) {
            // 显示CheckBox
            holder.setVisibility(R.id.messages_inbox_and_sent_list_item_check_box, View.VISIBLE);
            // 根据Notice中的checked数据确定是否选中CheckBox
            holder.setChecked(R.id.messages_inbox_and_sent_list_item_check_box,
                    notice.isChecked());
        } else {
            // 不显示CheckBox
            holder.setVisibility(R.id.messages_inbox_and_sent_list_item_check_box, View.GONE);
        }
    }
}
