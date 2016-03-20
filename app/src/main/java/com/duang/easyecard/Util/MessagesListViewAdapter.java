package com.duang.easyecard.Util;

import android.content.Context;

import com.duang.easyecard.Model.MessagesListViewItem;
import com.duang.easyecard.R;

import java.util.List;

/**
 * Created by MrD on 2016/3/19.
 */
public class MessagesListViewAdapter extends CommonAdapter<MessagesListViewItem> {

    public MessagesListViewAdapter(Context context, List<MessagesListViewItem> datas,
                                   int itemLayoutId) {
        super(context, datas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder holder, MessagesListViewItem messagesListViewItem) {
        holder
                .setImageResource(R.id.messages_list_view_item_icon,
                        messagesListViewItem.getIconResId())
                .setText(R.id.messages_list_view_item_title, messagesListViewItem.getTitle())
                .setText(R.id.messages_list_view_item_new_message_count,
                        messagesListViewItem.getNewMessagesCount())
                .setVisibility(R.id.messages_list_view_item_new_message_count,
                        messagesListViewItem.getNewMessageVisibility())
                .setImageResource(R.id.messages_list_view_item_arrow,
                        messagesListViewItem.getArrowResId());
    }
}
