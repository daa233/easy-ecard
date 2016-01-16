package com.duang.easyecard.Utils;

import java.util.List;

import com.duang.easyecard.R;
import com.duang.easyecard.Models.LostInfo;
import com.duang.easyecard.Utils.CommonAdapter;

import android.content.Context;

public class LostInfoAdapter extends CommonAdapter<LostInfo> {
	// 构造函数
	public LostInfoAdapter(Context context, List<LostInfo> datas,
			int itemLayoutId) {
		super(context, datas, itemLayoutId);
	}
	
	// 设置资源
	@Override
	public void convert(ViewHolder holder, LostInfo lostInfo) {
		holder.setText(R.id.lost_and_found_info_list_item_name,
				lostInfo.getName())
			  .setText(R.id.lost_and_found_info_list_item_stu_id,
					  lostInfo.getStuId())
			  .setText(R.id.lost_and_found_info_list_item_state,
					  lostInfo.getState())
			  .setText(R.id.lost_and_found_info_list_item_publish_time,
					  lostInfo.getPublishTime());
	}
}
