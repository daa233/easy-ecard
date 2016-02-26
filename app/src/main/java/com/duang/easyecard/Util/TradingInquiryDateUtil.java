package com.duang.easyecard.Util;

import android.content.Context;
import android.text.format.Formatter;

import com.duang.easyecard.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TradingInquiryDateUtil
 * Created by MrD on 2016/2/15.
 */
public class TradingInquiryDateUtil {

    private Context mContext;

    private int historyStartYear, historyStartMonth, historyStartDayOfMonth;
    private String historyStartDate, historyStartDayOfWeek;
    private int historyEndYear, historyEndMonth, historyEndDayOfMonth;
    private String historyEndDate, historyEndDayOfWeek;

    private int todayYear, todayMonth, todayDayOfMonth;
    private String todayDate, todayDayOfWeek;

    private int weekStartYear, weekStartMonth, weekStartDayOfMonth;
    private String weekStartDate, weekStartDayOfWeek;
    private int weekEndYear, weekEndMonth, weekEndDayOfMonth;
    private String weekEndDate, weekEndDayOfWeek;

    public TradingInquiryDateUtil(Context context) {
        mContext = context;
        Calendar calendar = Calendar.getInstance();
        setTodayDate(calendar);
        // 历史流水和一周流水默认结束日期为当前日期前1天
        calendar = calendarTransformTool(calendar, 1);
        setHistoryEndDate(calendar);
        setWeekEndDate(calendar);
        // 一周流水起始日期为当前日期前7天
        calendar = calendarTransformTool(calendar, 7);
        setWeekStartDate(calendar);
        // 历史流水默认起始日期为当前日期前15天
        calendar = calendarTransformTool(calendar, 8);
        setHistoryStartDate(calendar);
    }


    public void setHistoryStartDate(Calendar calendar) {
        historyStartYear = calendar.get(Calendar.YEAR);
        historyStartMonth = calendar.get(Calendar.MONTH) + 1;
        historyStartDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        historyStartDayOfWeek = translateDayOfWeekValueTool(calendar.get(Calendar.DAY_OF_WEEK));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                mContext.getString(R.string.display_date_format));
        historyStartDate = simpleDateFormat.format(calendar.getTime());
    }

    public void setHistoryStartDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, dayOfMonth);
        setHistoryStartDate(calendar);
    }

    public void setHistoryEndDate(Calendar calendar) {
        historyEndYear = calendar.get(Calendar.YEAR);
        historyEndMonth = calendar.get(Calendar.MONTH) + 1;
        historyEndDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        historyEndDayOfWeek = translateDayOfWeekValueTool(calendar.get(Calendar.DAY_OF_WEEK));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                mContext.getString(R.string.display_date_format));
        historyEndDate = simpleDateFormat.format(calendar.getTime());
    }

    public void setHistoryEndDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, dayOfMonth);
        setHistoryEndDate(calendar);
    }

    public void setTodayDate(Calendar calendar) {
        todayYear = calendar.get(Calendar.YEAR);
        todayMonth = calendar.get(Calendar.MONTH) + 1;
        todayDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        todayDayOfWeek = translateDayOfWeekValueTool(calendar.get(Calendar.DAY_OF_WEEK));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                mContext.getString(R.string.display_date_format));
        todayDate = simpleDateFormat.format(calendar.getTime());
    }

    public void setWeekStartDate(Calendar calendar) {
        weekStartYear = calendar.get(Calendar.YEAR);
        weekStartMonth = calendar.get(Calendar.MONTH) + 1;
        weekStartDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        weekStartDayOfWeek = translateDayOfWeekValueTool(calendar.get(Calendar.DAY_OF_WEEK));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                mContext.getString(R.string.display_date_format));
        weekStartDate = simpleDateFormat.format(calendar.getTime());
    }

    public void setWeekStartDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, dayOfMonth);
        setWeekStartDate(calendar);
    }

    public void setWeekEndDate(Calendar calendar) {
        weekEndYear = calendar.get(Calendar.YEAR);
        weekEndMonth = calendar.get(Calendar.MONTH) + 1;
        weekEndDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        weekEndDayOfWeek = translateDayOfWeekValueTool(calendar.get(Calendar.DAY_OF_WEEK));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                mContext.getString(R.string.display_date_format));
        weekEndDate = simpleDateFormat.format(calendar.getTime());
    }

    public void setWeekEndDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, dayOfMonth);
        setWeekEndDate(calendar);
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
                dayOfWeekString = mContext.getString(R.string.Monday);
                break;
            case 3:
                dayOfWeekString = mContext.getString(R.string.Tuesday);
                break;
            case 4:
                dayOfWeekString = mContext.getString(R.string.Wednesday);
                break;
            case 5:
                dayOfWeekString = mContext.getString(R.string.Thursday);
                break;
            case 6:
                dayOfWeekString = mContext.getString(R.string.Friday);
                break;
            case 7:
                dayOfWeekString = mContext.getString(R.string.Saturday);
                break;
            default:
                dayOfWeekString = mContext.getString(R.string.error_day_of_week);
                break;
        }
        return dayOfWeekString;
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

    public int getHistoryStartYear() {
        return historyStartYear;
    }

    public int getHistoryStartMonth() {
        return historyStartMonth;
    }

    public int getHistoryStartDayOfMonth() {
        return historyStartDayOfMonth;
    }

    public String getHistoryStartDate() {
        return historyStartDate;
    }

    public String getHistoryStartDayOfWeek() {
        return historyStartDayOfWeek;
    }

    public int getHistoryEndYear() {
        return historyEndYear;
    }

    public int getHistoryEndMonth() {
        return historyEndMonth;
    }

    public int getHistoryEndDayOfMonth() {
        return historyEndDayOfMonth;
    }

    public String getHistoryEndDate() {
        return historyEndDate;
    }

    public String getHistoryEndDayOfWeek() {
        return historyEndDayOfWeek;
    }

    public int getTodayYear() {
        return todayYear;
    }

    public int getTodayMonth() {
        return todayMonth;
    }

    public int getTodayDayOfMonth() {
        return todayDayOfMonth;
    }

    public String getTodayDate() {
        return todayDate;
    }

    public String getTodayDayOfWeek() {
        return todayDayOfWeek;
    }

    public int getWeekStartYear() {
        return weekStartYear;
    }

    public int getWeekStartMonth() {
        return weekStartMonth;
    }

    public int getWeekStartDayOfMonth() {
        return weekStartDayOfMonth;
    }

    public String getWeekStartDate() {
        return weekStartDate;
    }

    public String getWeekStartDayOfWeek() {
        return weekStartDayOfWeek;
    }

    public int getWeekEndYear() {
        return weekEndYear;
    }

    public int getWeekEndMonth() {
        return weekEndMonth;
    }

    public int getWeekEndDayOfMonth() {
        return weekEndDayOfMonth;
    }

    public String getWeekEndDate() {
        return weekEndDate;
    }

    public String getWeekEndDayOfWeek() {
        return weekEndDayOfWeek;
    }
}
