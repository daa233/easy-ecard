package com.duang.easyecard.Util;

import android.content.Context;

import com.duang.easyecard.Model.LostAndFoundEvent;
import com.duang.easyecard.R;

import java.util.List;

/**
 * LostAndFoundEventAdapter
 * Used for LostAndFoundInformationBrowsingListView
 * Created by MrD on 2016/3/11.
 */
public class LostAndFoundEventAdapter extends CommonAdapter<LostAndFoundEvent> {

    // Constructor
    public LostAndFoundEventAdapter(Context context, List<LostAndFoundEvent> datas,
                                    int itemLayoutId) {
        super(context, datas, itemLayoutId);
    }

    // 设置资源
    @Override
    public void convert(ViewHolder holder, LostAndFoundEvent lostAndFoundEvent) {
        holder
                .setText(R.id.lost_and_found_information_list_item_name,
                        lostAndFoundEvent.getName())
                .setText(R.id.lost_and_found_information_list_item_stu_id,
                        lostAndFoundEvent.getStuId())
                .setText(R.id.lost_and_found_information_list_item_state,
                        lostAndFoundEvent.getState())
                .setText(R.id.lost_and_found_information_list_item_id,
                        "No." + lostAndFoundEvent.getId())
                .setText(R.id.lost_and_found_information_list_item_publish_time,
                        lostAndFoundEvent.getPublishTime());
    }
}
