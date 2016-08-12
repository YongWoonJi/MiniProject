package com.example.tacademy.miniproject;

import android.app.Application;
import android.content.Context;

/**
 * Created by Tacademy on 2016-08-09.
 */
public class MyApplication extends Application{
    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
