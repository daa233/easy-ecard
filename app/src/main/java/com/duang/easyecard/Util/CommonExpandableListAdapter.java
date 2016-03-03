package com.duang.easyecard.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.List;

/**
 * CommonExpandableListAdapter，用于ExpandableListView
 * Created by MrD on 2016/2/23.
 */
public abstract class CommonExpandableListAdapter<T, E> extends BaseExpandableListAdapter {

    protected Context mContext;
    protected List<T> mGroupList;
    protected List<List<E>> mChildList;
    protected LayoutInflater mInflater;
    protected final int mGroupItemLayoutId;
    protected final int mChildItemLayoutId;

    public CommonExpandableListAdapter(Context context, List<T> groupList, int groupItemLayoutId,
                                       List<List<E>> childList, int childItemLayoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mGroupList = groupList;
        this.mGroupItemLayoutId = groupItemLayoutId;
        this.mChildList = childList;
        this.mChildItemLayoutId = childItemLayoutId;
    }

    @Override
    public int getGroupCount() {
        return mGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildList.get(groupPosition).size();
    }

    @Override
    public T getGroup(int groupPosition) {
        return mGroupList.get(groupPosition);
    }

    @Override
    public E getChild(int groupPosition, int childPosition) {
        return mChildList.get(groupPosition).get(childPosition);
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
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        final ExpandableListViewHolder groupViewHolder = getGroupViewHolder(groupPosition, convertView, parent);
        convertGroupView(groupViewHolder, getGroup(groupPosition));
        return groupViewHolder.getConvertView();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        final ExpandableListViewHolder childViewHolder = getChildViewHolder(groupPosition,
                childPosition, convertView, parent);
        convertChildView(childViewHolder, getChild(groupPosition, childPosition));
        return childViewHolder.getConvertView();
    }

    protected ExpandableListViewHolder getGroupViewHolder(int position, View convertView,
                                                                                           ViewGroup parent)
    {
        return ExpandableListViewHolder.get(mContext, convertView, parent, mGroupItemLayoutId,
                position);
    }

    public abstract void convertGroupView(ExpandableListViewHolder holder, T t);


    protected ExpandableListViewHolder getChildViewHolder(int groupPosition, int childPosition,
                                                                                           View convertView, ViewGroup parent)
    {
        return ExpandableListViewHolder.get(mContext, convertView, parent, mChildItemLayoutId,
                groupPosition, childPosition);
    }

    public abstract void convertChildView(ExpandableListViewHolder holder, E e);
}
