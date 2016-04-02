package com.duang.easyecard.Util;

import android.content.Context;

import com.duang.easyecard.Model.MessageBoardItem;
import com.duang.easyecard.R;

import java.util.List;

/**
 * Created by MrD on 2016/3/30.
 */
public class MessagesBoardListAdapter extends CommonAdapter<MessageBoardItem> {

    public MessagesBoardListAdapter(Context context, List<MessageBoardItem> datas,
                                    int itemLayoutId) {
        super(context, datas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder holder, MessageBoardItem messageBoardItem) {
        holder
                .setText(R.id.messages_board_list_item_type, messageBoardItem.getType())
                .setText(R.id.messages_board_list_item_time, messageBoardItem.getTime())
                .setText(R.id.messages_board_list_item_user, messageBoardItem.getUser())
                .setText(R.id.messages_board_list_item_title, messageBoardItem.getTitle())
                .setText(R.id.messages_board_list_item_reply, messageBoardItem.getReply())
                .setImageResource(R.id.messages_board_list_item_user_img, R.mipmap.ic_launcher)
                .setImageResource(R.id.messages_board_list_item_platform_img, R.mipmap.ic_launcher);
    }
}
