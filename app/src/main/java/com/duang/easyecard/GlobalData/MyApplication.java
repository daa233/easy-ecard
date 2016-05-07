package com.duang.easyecard.GlobalData;

import android.app.Application;
import android.content.Context;

import com.duang.easyecard.Model.UserBasicInformation;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.pgyersdk.crash.PgyCrashManager;
import com.squareup.leakcanary.LeakCanary;

public class MyApplication extends Application {

    private static Context context;
    private static AsyncHttpClient httpClient;
    private static PersistentCookieStore cookieStore;

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

    public static PersistentCookieStore getCookieStore() {
        if (cookieStore == null) {
            cookieStore = new PersistentCookieStore(context);
        }
        return cookieStore;
    }

    public void setCookieStore(PersistentCookieStore cookie) {
        cookieStore = cookie;
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
