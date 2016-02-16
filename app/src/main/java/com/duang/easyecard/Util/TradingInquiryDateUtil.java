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

    public int getHistoryStartYear() {
        return historyStartYear;
    }

    public void setHistoryStartYear(int historyStartYear) {
        this.historyStartYear = historyStartYear;
    }

    public int getHistoryStartMonth() {
        return historyStartMonth;
    }

    public void setHistoryStartMonth(int historyStartMonth) {
        this.historyStartMonth = historyStartMonth;
    }

    public int getHistoryStartDayOfMonth() {
        return historyStartDayOfMonth;
    }

    public void setHistoryStartDayOfMonth(int historyStartDayOfMonth) {
        this.historyStartDayOfMonth = historyStartDayOfMonth;
    }

    public int getHistoryEndYear() {
        return historyEndYear;
    }

    public void setHistoryEndYear(int historyEndYear) {
        this.historyEndYear = historyEndYear;
    }

    public int getHistoryEndMonth() {
        return historyEndMonth;
    }

    public void setHistoryEndMonth(int historyEndMonth) {
        this.historyEndMonth = historyEndMonth;
    }

    public int getHistoryEndDayOfMonth() {
        return historyEndDayOfMonth;
    }

    public void setHistoryEndDayOfMonth(int historyEndDayOfMonth) {
        this.historyEndDayOfMonth = historyEndDayOfMonth;
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

    public int getTodayYear() {
        return todayYear;
    }

    public void setTodayYear(int todayYear) {
        this.todayYear = todayYear;
    }

    public int getTodayMonth() {
        return todayMonth;
    }

    public void setTodayMonth(int todayMonth) {
        this.todayMonth = todayMonth;
    }

    public int getTodayDayOfMonth() {
        return todayDayOfMonth;
    }

    public void setTodayDayOfMonth(int todayDayOfMonth) {
        this.todayDayOfMonth = todayDayOfMonth;
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

    public int getWeekStartYear() {
        return weekStartYear;
    }

    public void setWeekStartYear(int weekStartYear) {
        this.weekStartYear = weekStartYear;
    }

    public int getWeekStartMonth() {
        return weekStartMonth;
    }

    public void setWeekStartMonth(int weekStartMonth) {
        this.weekStartMonth = weekStartMonth;
    }

    public int getWeekStartDayOfMonth() {
        return weekStartDayOfMonth;
    }

    public void setWeekStartDayOfMonth(int weekStartDayOfMonth) {
        this.weekStartDayOfMonth = weekStartDayOfMonth;
    }

    public int getWeekEndYear() {
        return weekEndYear;
    }

    public void setWeekEndYear(int weekEndYear) {
        this.weekEndYear = weekEndYear;
    }

    public int getWeekEndMonth() {
        return weekEndMonth;
    }

    public void setWeekEndMonth(int weekEndMonth) {
        this.weekEndMonth = weekEndMonth;
    }

    public int getWeekEndDayOfMonth() {
        return weekEndDayOfMonth;
    }

    public void setWeekEndDayOfMonth(int weekEndDayOfMonth) {
        this.weekEndDayOfMonth = weekEndDayOfMonth;
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

    public void setHistoryStartDate(Calendar calendar) {
        historyStartYear = calendar.get(Calendar.YEAR);
        historyStartMonth = calendar.get(Calendar.MONTH);
        historyStartDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        historyStartDayOfWeek = translateDayOfWeekValueTool(calendar.get(Calendar.DAY_OF_WEEK));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                mContext.getString(R.string.display_date_format));
        historyStartDate = simpleDateFormat.format(calendar.getTime());
    }

    public void setHistoryEndDate(Calendar calendar) {
        historyEndYear = calendar.get(Calendar.YEAR);
        historyEndMonth = calendar.get(Calendar.MONTH);
        historyEndDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        historyEndDayOfWeek = translateDayOfWeekValueTool(calendar.get(Calendar.DAY_OF_WEEK));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                mContext.getString(R.string.display_date_format));
        historyEndDate = simpleDateFormat.format(calendar.getTime());
    }

    public void setTodayDate(Calendar calendar) {
        todayYear = calendar.get(Calendar.YEAR);
        todayMonth = calendar.get(Calendar.MONTH);
        todayDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        todayDayOfWeek = translateDayOfWeekValueTool(calendar.get(Calendar.DAY_OF_WEEK));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                mContext.getString(R.string.display_date_format));
        todayDate = simpleDateFormat.format(calendar.getTime());
    }

    public void setWeekStartDate(Calendar calendar) {
        weekStartYear = calendar.get(Calendar.YEAR);
        weekStartMonth = calendar.get(Calendar.MONTH);
        weekStartDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        weekStartDayOfWeek = translateDayOfWeekValueTool(calendar.get(Calendar.DAY_OF_WEEK));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                mContext.getString(R.string.display_date_format));
        weekStartDate = simpleDateFormat.format(calendar.getTime());
    }

    public void setWeekEndDate(Calendar calendar) {
        weekEndYear = calendar.get(Calendar.YEAR);
        weekEndMonth = calendar.get(Calendar.MONTH);
        weekEndDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        weekEndDayOfWeek = translateDayOfWeekValueTool(calendar.get(Calendar.DAY_OF_WEEK));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                mContext.getString(R.string.display_date_format));
        weekEndDate = simpleDateFormat.format(calendar.getTime());
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
}
