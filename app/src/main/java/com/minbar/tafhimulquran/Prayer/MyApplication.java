package com.minbar.tafhimulquran.Prayer;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.work.Configuration;
import androidx.work.WorkManager;

public class MyApplication extends Application implements Configuration.Provider {
    @Override
    public void onCreate() {
        super.onCreate();
        // Explicitly initialize WorkManager since the default initializer is disabled in the manifest
        WorkManager.initialize(this, getWorkManagerConfiguration());
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build();
    }
}
