package com.duang.easyecard.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * 用于ExpandableListView
 * Created by MrD on 2016/2/23.
 */
public class ExpandableListViewHolder {

    private SparseArray<View> mViews;
    private Context mContext;
    private int mGroupPosition;
    private int mChildPosition;
    private View mConvertView;

    /**
     * 构造方法
     * 用于ExpandalbeListView中的Group
     *
     * @param context
     * @param parent
     * @param groupItemLayoutId
     * @param groupPosition
     */
    public ExpandableListViewHolder(Context context, ViewGroup parent, int groupItemLayoutId,
                                    int groupPosition) {
        this.mContext = context;
        this.mGroupPosition = groupPosition;
        this.mViews = new SparseArray<>();
        mConvertView = LayoutInflater.from(context).inflate(groupItemLayoutId, parent, false);
        mConvertView.setTag(this);
    }

    /**
     * 构造方法
     * 用于ExpandableListView中的Child
     *
     * @param context
     * @param parent
     * @param childItemLayoutId
     * @param groupPosition
     * @param childPosition
     */
    public ExpandableListViewHolder(Context context, ViewGroup parent, int childItemLayoutId,
                                    int groupPosition, int childPosition) {
        this.mContext = context;
        this.mGroupPosition = groupPosition;
        this.mChildPosition = childPosition;
        this.mViews = new SparseArray<>();
        mConvertView = LayoutInflater.from(context).inflate(childItemLayoutId, parent, false);
        mConvertView.setTag(this);
    }

    /**
     * 用于Group
     *
     * @param context
     * @param convertView
     * @param parent
     * @param groupItemLayoutId
     * @param groupPosition
     * @return
     */
    public static ExpandableListViewHolder get(Context context, View convertView, ViewGroup parent,
                                               int groupItemLayoutId, int groupPosition) {
        if (convertView == null) {
            return new ExpandableListViewHolder(context, parent, groupItemLayoutId, groupPosition);
        } else {
            ExpandableListViewHolder holder = (ExpandableListViewHolder) convertView.getTag();
            holder.mGroupPosition = groupPosition;
            return holder;
        }
    }

    /**
     * 用于Child
     *
     * @param context
     * @param convertView
     * @param parent
     * @param childItemLayoutId
     * @param groupPosition
     * @param childPosition
     * @return
     */
    public static ExpandableListViewHolder get(Context context, View convertView, ViewGroup parent,
                                               int childItemLayoutId, int groupPosition,
                                               int childPosition) {
        if (convertView == null) {
            return new ExpandableListViewHolder(context, parent, childItemLayoutId,
                    groupPosition, childPosition);
        } else {
            ExpandableListViewHolder holder = (ExpandableListViewHolder) convertView.getTag();
            holder.mGroupPosition = groupPosition;
            holder.mChildPosition = childPosition;
            return holder;
        }
    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);

        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }

        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param text
     * @return
     */
    public ExpandableListViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public ExpandableListViewHolder setImageResource(int viewId, int resId) {
        useGlideToLoadImages(viewId, resId);
        return this;
    }

    public ExpandableListViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        useGlideToLoadImages(viewId, bitmap);
        return this;
    }

    public ExpandableListViewHolder setImageURI(int viewId, Uri uri) {
        useGlideToLoadImages(viewId, uri);
        return this;
    }

    public void useGlideToLoadImages(int viewId, Object imageAddress) {
        Glide
                .with(mContext)
                .load(imageAddress)
                .into((ImageView) getView(viewId));
    }
}
