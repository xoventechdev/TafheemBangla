package com.minbar.tafhimulquran.Prayer;

import android.app.Application;
import androidx.work.Configuration;
import androidx.work.WorkManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        WorkManager.initialize(this, new Configuration.Builder().build());
    }
}