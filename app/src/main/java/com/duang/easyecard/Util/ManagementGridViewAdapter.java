package com.duang.easyecard.Util;

import android.content.Context;

import com.duang.easyecard.Model.GridViewItem;
import com.duang.easyecard.R;

import java.util.List;

/**
 * Created by MrD on 2016/3/3.
 */
public class ManagementGridViewAdapter extends CommonAdapter<GridViewItem>{

    // 构造函数
    public ManagementGridViewAdapter(Context context, List<GridViewItem> itemList, int itemLayoutId) {
        super(context, itemList, itemLayoutId);
    }
    @Override
    public void convert(ViewHolder holder, GridViewItem gridViewItem) {
        holder
                .setText(R.id.grid_view_item_text, gridViewItem.getString())
                .setImageResource(R.id.grid_view_item_img, gridViewItem.getResourceId());
    }
}
