package com.duang.easyecard.Util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

public class ActivityCollector {

public static List<Activity> activities = new ArrayList<Activity>();
	
	public static List<FragmentActivity> fragmentActivities = new ArrayList<FragmentActivity>();
	
	public static void addActivity(Activity activity)	{
		activities.add(activity);
	}
	
	public static void removeActivity(Activity activity)	{
		activities.remove(activity);
	}
	
	
	// 推广到FragmentActivity
	public static void addActivity(FragmentActivity fragmentActivity)	{
		activities.add(fragmentActivity);
	}
	
	public static void removeActivity(FragmentActivity fragmentActivity)	{
		activities.remove(fragmentActivity);
	}
	
	public static void finishAll()	{
		for (Activity activity : activities)	{
			if (!activity.isFinishing())	{
				activity.finish();
			}
		}
		
		// 推广到FragmentActivity
		for (FragmentActivity fragmentActivity : fragmentActivities)	{
			if (!fragmentActivity.isFinishing())	{
				fragmentActivity.finish();
			}
		}
	}
}
