package com.minbar.tafhimulquran.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.minbar.tafhimulquran.Hadith.HadithChapterActivity;
import com.minbar.tafhimulquran.R;

public class SplashActivity extends AppCompatActivity {

    Handler handler = new Handler();
    Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Load_setting();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(1024, 1024);

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                startApp();
            }
        }, 1000);
    }

    public void startApp() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void Load_setting() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("NIGHT", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}