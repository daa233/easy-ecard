package com.duang.easyecard.Util;

import android.content.Context;

import com.duang.easyecard.Model.SimpleItem;
import com.duang.easyecard.R;

import java.util.List;

/**
 * Created by MrD on 2016/3/3.
 */
public class ManagementGridViewAdapter extends CommonAdapter<SimpleItem>{

    // 构造函数
    public ManagementGridViewAdapter(Context context, List<SimpleItem> itemList, int itemLayoutId) {
        super(context, itemList, itemLayoutId);
    }
    @Override
    public void convert(ViewHolder holder, SimpleItem simpleItem) {
        holder
                .setText(R.id.grid_view_item_text, simpleItem.getString())
                .setImageResource(R.id.grid_view_item_img, simpleItem.getResourceId());
    }
}
