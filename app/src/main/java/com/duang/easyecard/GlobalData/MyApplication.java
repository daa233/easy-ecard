package com.duang.easyecard.GlobalData;

import android.app.Application;
import android.content.Context;

import com.duang.easyecard.Model.UserBasicInformation;
import com.loopj.android.http.AsyncHttpClient;
import com.pgyersdk.crash.PgyCrashManager;
import com.squareup.leakcanary.LeakCanary;

public class MyApplication extends Application {

    private static Context context;
    private static AsyncHttpClient httpClient;
    private static UserBasicInformation userBasicInformation;

    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();
        LeakCanary.install(this);  // LeakCanary
        PgyCrashManager.register(this);  // 蒲公英Crash分析
    }

    public static Context getContext() {
        return context;
    }

    public AsyncHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(AsyncHttpClient httpClient) {
        MyApplication.httpClient = httpClient;
    }

    public UserBasicInformation getUserBasicInformation() {
        return userBasicInformation;
    }

    public void setUserBasicInformation(UserBasicInformation userBasicInformation) {
        MyApplication.userBasicInformation = userBasicInformation;
    }
}
