package com.duang.easyecard.Util;

import android.content.Context;

import com.duang.easyecard.R;

import java.util.Calendar;

/**
 * DateUtil
 * Created by MrD on 2016/2/15.
 */
public class DateUtil {

    private Context mContext;

    DateUtil(Context context) {
        mContext = context;
    }

    /**
     * calendarTransformTool
     * Get previous calendar.
     * Input: Calendar presentCalendar, int agoDays
     * Return: Calendar transformedCalendar
     */
    public Calendar calendarTransformTool(Calendar presentCalendar, int agoDays) {
        Calendar transformedCalendar = Calendar.getInstance();
        int year = presentCalendar.get(Calendar.YEAR);
        int month = presentCalendar.get(Calendar.MONTH);
        int dayOfMonth = presentCalendar.get(Calendar.DAY_OF_MONTH);
        transformedCalendar.set(year, month, dayOfMonth - agoDays);
        return transformedCalendar;
    }
    /**
     * passDateValueTool
     * Get date value from calendar
     */
    public void passDateValueTool(Calendar calendar, String date, String dayOfWeekString) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;  // Calendar中Month范围为0-11
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // 组装成字符串类型的日期，格式为"2015-12-20"
        date = year + "-" + month + "-" + dayOfMonth;
        dayOfWeekString = translateDayOfWeekValueTool(dayOfWeek);
    }
    /**
     * translateDayOfWeekValueTool
     * Transform dayOfWeek value form int to String
     */
    public String translateDayOfWeekValueTool(int dayOfWeek) {
        String dayOfWeekString;
        switch (dayOfWeek) {
            case 1:
                dayOfWeekString = mContext.getResources().getString(R.string.Sunday);
                break;
            case 2:
                dayOfWeekString = mContext.getResources().getString(R.string.Monday);
                break;
            case 3:
                dayOfWeekString = mContext.getResources().getString(R.string.Tuesday);
                break;
            case 4:
                dayOfWeekString = mContext.getResources().getString(R.string.Wednesday);
                break;
            case 5:
                dayOfWeekString = mContext.getResources().getString(R.string.Thursday);
                break;
            case 6:
                dayOfWeekString = mContext.getResources().getString(R.string.Friday);
                break;
            case 7:
                dayOfWeekString = mContext.getResources().getString(R.string.Saturday);
                break;
            default:
                dayOfWeekString = mContext.getResources().getString(R.string.error_day_of_week);
                break;
        }
        return dayOfWeekString;
    }
}
