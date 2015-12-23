package com.duang.easyecard.Activities;

import java.util.ArrayList;
import java.util.List;

import com.duang.easyecard.R;
import com.duang.easyecard.GlobalData.MyApplication;
import com.duang.easyecard.UI.PinnedHeaderExpandableListView;
import com.duang.easyecard.UI.PinnedHeaderExpandableListView.OnHeaderUpdateListener;
import com.duang.easyecard.model.Group;
import com.duang.easyecard.model.TradingInquiry;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.LayoutParams;

public class ManageTradingInquiryHistoryResultFragment extends Fragment implements 
ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener,
OnHeaderUpdateListener {
	
	private PinnedHeaderExpandableListView mExpandableListView;
	private ArrayList<Group> groupList;
	private ArrayList<List<TradingInquiry>> childList;
	private HorizontalScrollView mHorizontalScrollView;

	private MyexpandableListAdapter adapter;

	private View viewFragment;  // 缓存Fragment的View
	
	private int width;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (viewFragment == null) {
			viewFragment =  inflater.inflate(
					R.layout.fragment_trading_inquiry_history_result,
					container, false);
		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误
		ViewGroup parent = (ViewGroup) viewFragment.getParent();
		if (parent != null) {
			parent.removeView(viewFragment);
		}
		return viewFragment;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 获取屏幕的像素宽度
		WindowManager wm = getActivity().getWindowManager();
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		width = outMetrics.widthPixels;
		// 动态设置footLayout的宽度
		LinearLayout footLayout = (LinearLayout) getActivity().findViewById(R.id.history_trading_inquiry_foot_linear);
		footLayout.setMinimumWidth(width * 2);
		// 实例化底部HorizontalScrollListView
		mHorizontalScrollView = (HorizontalScrollView) getActivity().findViewById(R.id.history_trading_inquiry_foot_scroll);
		
		mExpandableListView = (PinnedHeaderExpandableListView) getActivity().findViewById(R.id.histroy_trading_inquiry_expandablelist);
		initData();
		adapter = new MyexpandableListAdapter(getActivity());
        mExpandableListView.setAdapter(adapter);

        // 展开所有group
        for (int i = 0, count = mExpandableListView.getCount(); i < count; i++) {
            mExpandableListView.expandGroup(i);
        }

        mExpandableListView.setOnHeaderUpdateListener(this);
        mExpandableListView.setOnChildClickListener(this);
        mExpandableListView.setOnGroupClickListener(this);
	}
	
	private Runnable scrollRunable = new Runnable() {
		
		@Override
		public void run() {
			// 将HorizontalScrollView移动到最右边
			mHorizontalScrollView.smoothScrollTo(width * 2, 0);
		}
	};
	
	private void initData() {
		Handler handler = new Handler();
		handler.postDelayed(scrollRunable, 2000);
		// TODO Auto-generated method stub
		groupList = new ArrayList<Group>();
        Group group = null;
        for (int i = 0; i < 3; i++) {
            group = new Group();
            group.setTitle("group-" + i);
            groupList.add(group);
        }

        childList = new ArrayList<List<TradingInquiry>>();
        for (int i = 0; i < groupList.size(); i++) {
            ArrayList<TradingInquiry> childTemp;
            if (i == 0) {
                childTemp = new ArrayList<TradingInquiry>();
                for (int j = 0; j < 13; j++) {
                	TradingInquiry tradingInquiry = new TradingInquiry();
                    tradingInquiry.setmTradingTime("yy-" + j);
                    childTemp.add(tradingInquiry);
                }
            } else if (i == 1) {
                childTemp = new ArrayList<TradingInquiry>();
                for (int j = 0; j < 8; j++) {
                	TradingInquiry tradingInquiry = new TradingInquiry();
                    tradingInquiry.setmTradingName("ff-" + j);
                    childTemp.add(tradingInquiry);
                }
            } else {
                childTemp = new ArrayList<TradingInquiry>();
                for (int j = 0; j < 23; j++) {
                	TradingInquiry tradingInquiry = new TradingInquiry();
                    tradingInquiry.setmTradingTime("hh-" + j);
                    childTemp.add(tradingInquiry);
                }
            }
            childList.add(childTemp);
        }
	}

	/**
     * 数据源
     */
    class MyexpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private LayoutInflater inflater;

        public MyexpandableListAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        // 返回父列表个数
        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        // 返回子列表个数
        @Override
        public int getChildrenCount(int groupPosition) {
            return childList.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {

            return groupList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {

            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent) {
            GroupHolder groupHolder = null;
            if (convertView == null) {
                groupHolder = new GroupHolder();
                convertView = inflater.inflate(R.layout.trading_inquiry_group, null);
                groupHolder.textView = (TextView) convertView
                        .findViewById(R.id.group);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }

            groupHolder.textView.setText(((Group) getGroup(groupPosition))
                    .getTitle());
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder childHolder = null;
            if (convertView == null) {
                childHolder = new ChildHolder();
                convertView = inflater.inflate(R.layout.trading_inquiry_child, null);

                childHolder.textMerchantName = (TextView) convertView
                        .findViewById(R.id.trading_inquiry_child_merchant_name_text);
                childHolder.textTradingName = (TextView) convertView
                        .findViewById(R.id.trading_inquiry_child_trading_name_text);
                childHolder.textTransactionAmount = (TextView) convertView
                        .findViewById(R.id.trading_inquiry_child_transaction_amount_text);

                convertView.setTag(childHolder);
            } else {
                childHolder = (ChildHolder) convertView.getTag();
            }

            childHolder.textMerchantName.setText(((TradingInquiry) getChild(groupPosition,
                    childPosition)).getmTradingName());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    @Override
    public boolean onGroupClick(final ExpandableListView parent, final View v,
            int groupPosition, final long id) {

        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
            int groupPosition, int childPosition, long id) {
        Toast.makeText(MyApplication.getContext(),
                childList.get(groupPosition).get(childPosition).getmTradingTime(), 1)
                .show();

        return false;
    }

    class GroupHolder {
        TextView textView;
        ImageView imageView;
    }

    class ChildHolder {
        TextView textMerchantName;
        TextView textTradingName;
        TextView textTransactionAmount;
    }

    @Override
    public View getPinnedHeader() {
        View headerView = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.trading_inquiry_group, null);
        headerView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        return headerView;
    }

    @Override
    public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
        Group firstVisibleGroup = (Group) adapter.getGroup(firstVisibleGroupPos);
        TextView textView = (TextView) headerView.findViewById(R.id.group);
        textView.setText(firstVisibleGroup.getTitle());
    }

    public boolean giveUpTouchEvent(MotionEvent event) {
        if (mExpandableListView.getFirstVisiblePosition() == 0) {
            View view = mExpandableListView.getChildAt(0);
            if (view != null && view.getTop() >= 0) {
                return true;
            }
        }
        return false;
    }

}
