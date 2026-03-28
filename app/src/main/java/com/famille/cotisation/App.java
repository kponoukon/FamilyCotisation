package com.famille.cotisation;

import android.app.Application;
import androidx.multidex.MultiDex;
import android.content.Context;

public class App extends Application {
    private static App instance;
    public static DatabaseHelper db;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        db = new DatabaseHelper(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static App getInstance() { return instance; }
}
