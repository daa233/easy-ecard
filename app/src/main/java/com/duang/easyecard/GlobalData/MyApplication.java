package com.duang.easyecard.GlobalData;

import android.app.Application;
import android.content.Context;

import com.duang.easyecard.Model.UserBasicInformation;
import com.loopj.android.http.AsyncHttpClient;
import com.pgyersdk.crash.PgyCrashManager;

public class MyApplication extends Application {

    private static Context context;
    private static AsyncHttpClient httpClient;

    private static UserBasicInformation userBasicInformation;

    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();
        PgyCrashManager.register(this);  // 蒲公英Crash分析
    }

    public static Context getContext() {
        return context;
    }

    public static AsyncHttpClient getHttpClient() {
        if (httpClient != null) {
            return httpClient;
        } else {
            httpClient = new AsyncHttpClient();
            return httpClient;
        }
    }

    public void setHttpClient(AsyncHttpClient client) {
        httpClient = client;
    }

    public static UserBasicInformation getUserBasicInformation() {
        if (userBasicInformation != null) {
            return userBasicInformation;
        } else {
            userBasicInformation = new UserBasicInformation();
            return userBasicInformation;
        }
    }

    public void setUserBasicInformation(UserBasicInformation information) {
        userBasicInformation = information;
    }
}
