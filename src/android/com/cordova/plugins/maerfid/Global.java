package com.maestrale.rfid;

import android.app.Application;
import android.content.Context;


public class Global extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        Global.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return Global.context;
    }
}