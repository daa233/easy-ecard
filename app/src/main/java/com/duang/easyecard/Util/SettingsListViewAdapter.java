package com.duang.easyecard.Util;

import android.content.Context;

import com.duang.easyecard.Model.SettingsListViewItem;
import com.duang.easyecard.R;

import java.util.List;

/**
 * Created by MrD on 2016/4/9.
 */
public class SettingsListViewAdapter  extends CommonAdapter<SettingsListViewItem> {

    public SettingsListViewAdapter(Context context, List<SettingsListViewItem> datas, int itemLayoutId) {
        super(context, datas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder holder, SettingsListViewItem settingsListViewItem) {
        holder
                .setImageResource(R.id.settings_list_view_item_icon,
                        settingsListViewItem.getIconResId())
                .setText(R.id.settings_list_view_item_title, settingsListViewItem.getTitle())
                .setImageResource(R.id.settings_list_view_item_arrow,
                        settingsListViewItem.getArrowResId());
    }
}
