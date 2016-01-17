package com.duang.easyecard.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {

	private SparseArray<View> mViews;
	@SuppressWarnings("unused")
	private int mPosition;
	private View mConvertView;
	
	public ViewHolder(Context context, ViewGroup parent, int layoutId, int position)
	{
		
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
		mConvertView.setTag(this);
	}
	
	public static ViewHolder get(Context context, View convertView,
			ViewGroup parent, int layoutId, int position)
	{
		if (convertView == null)
		{
			return new ViewHolder(context, parent, layoutId, position);
		}else
		{
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.mPosition = position;
			return holder;
		}
	}
	
	/**
	 * 通过viewId获取控件
	 * @param viewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewId)
	{
		View view = mViews.get(viewId);
		
		if (view == null)
		{
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
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setText(int viewId, String text)
	{
		TextView tv = getView(viewId);
		tv.setText(text);
		return this;
	}
	
	public ViewHolder setImageResource(int viewId, int resId)
	{
		ImageView view = getView(viewId);
		view.setImageResource(resId);
		return this;
	}
	public ViewHolder setImageBitmap(int viewId, Bitmap bitmap)
	{
		ImageView view = getView(viewId);
		view.setImageBitmap(bitmap);
		return this;
	}
	public ViewHolder setImageURI(int viewId, Uri uri)
	{
		ImageView view = getView(viewId);
		view.setImageURI(uri);
		return this;
	}
}
