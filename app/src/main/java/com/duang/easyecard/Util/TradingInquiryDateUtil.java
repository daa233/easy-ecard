package com.duang.easyecard.Util;

import android.content.Context;

import com.duang.easyecard.R;

import java.util.Calendar;

/**
 * TradingInquiryDateUtil
 * Created by MrD on 2016/2/15.
 */
public class TradingInquiryDateUtil {

    private Context mContext;

    private int historyStartYear, historyStartMonth, historyStartDayOfMonth;
    private int historyEndYear, historyEndMonth, historyEndDayOfMonth;

    private String historyStartDate, historyStartDayOfWeek;
    private String historyEndDate, historyEndDayOfWeek;
    private String todayDate, todayDayOfWeek;
    private String weekStartDate, weekStartDayOfWeek;
    private String weekEndDate, weekEndDayOfWeek;

    public TradingInquiryDateUtil(Context context) {
        mContext = context;
        Calendar calendar = Calendar.getInstance();
        todayDate = getStringDateFromCalendar(calendar);
        todayDayOfWeek = getStringDayOfWeekValuefromCalendar(calendar);
        // 历史流水和一周流水默认结束日期为当前日期前1天
        calendar = calendarTransformTool(calendar, 1);
        historyEndDate = getStringDateFromCalendar(calendar);
        historyEndDayOfWeek = getStringDayOfWeekValuefromCalendar(calendar);
        weekEndDate = getStringDateFromCalendar(calendar);
        weekEndDayOfWeek = getStringDayOfWeekValuefromCalendar(calendar);
        // 一周流水起始日期为当前日期前7天
        calendar = calendarTransformTool(calendar, 6);
        weekStartDate = getStringDateFromCalendar(calendar);
        weekStartDayOfWeek = getStringDayOfWeekValuefromCalendar(calendar);
        // 历史流水默认起始日期为当前日期前15天
        calendar = calendarTransformTool(calendar, 8);
        historyStartDate = getStringDateFromCalendar(calendar);
        historyStartDayOfWeek = getStringDayOfWeekValuefromCalendar(calendar);
    }

    public String getHistoryStartDate() {
        return historyStartDate;
    }

    public void setHistoryStartDate(String historyStartDate) {
        this.historyStartDate = historyStartDate;
    }

    public String getHistoryStartDayOfWeek() {
        return historyStartDayOfWeek;
    }

    public void setHistoryStartDayOfWeek(String historyStartDayOfWeek) {
        this.historyStartDayOfWeek = historyStartDayOfWeek;
    }

    public String getHistoryEndDate() {
        return historyEndDate;
    }

    public void setHistoryEndDate(String historyEndDate) {
        this.historyEndDate = historyEndDate;
    }

    public String getHistoryEndDayOfWeek() {
        return historyEndDayOfWeek;
    }

    public void setHistoryEndDayOfWeek(String historyEndDayOfWeek) {
        this.historyEndDayOfWeek = historyEndDayOfWeek;
    }

    public String getTodayDate() {
        return todayDate;
    }

    public void setTodayDate(String todayDate) {
        this.todayDate = todayDate;
    }

    public String getTodayDayOfWeek() {
        return todayDayOfWeek;
    }

    public void setTodayDayOfWeek(String todayDayOfWeek) {
        this.todayDayOfWeek = todayDayOfWeek;
    }

    public String getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(String weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    public String getWeekStartDayOfWeek() {
        return weekStartDayOfWeek;
    }

    public void setWeekStartDayOfWeek(String weekStartDayOfWeek) {
        this.weekStartDayOfWeek = weekStartDayOfWeek;
    }

    public String getWeekEndDate() {
        return weekEndDate;
    }

    public void setWeekEndDate(String weekEndDate) {
        this.weekEndDate = weekEndDate;
    }

    public String getWeekEndDayOfWeek() {
        return weekEndDayOfWeek;
    }

    public void setWeekEndDayOfWeek(String weekEndDayOfWeek) {
        this.weekEndDayOfWeek = weekEndDayOfWeek;
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
     * 返回字符串类型的日期，格式为"2015-12-20"
     */
    public String getStringDateFromCalendar(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;  // Calendar中Month范围为0-11
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        // 组装成字符串类型的日期，格式为"2015-12-20"
        String date = year + "-" + month + "-" + dayOfMonth;
        return date;
    }

    /**
     * 返回String类型的DayOfWeek
     * @param calendar
     * @return
     */
    public String getStringDayOfWeekValuefromCalendar(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return translateDayOfWeekValueTool(dayOfWeek);
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
