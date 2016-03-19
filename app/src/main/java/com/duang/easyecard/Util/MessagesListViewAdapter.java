package com.duang.easyecard.Util;

import android.content.Context;

import com.duang.easyecard.Model.MessagesListViewItem;

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
    }
}
