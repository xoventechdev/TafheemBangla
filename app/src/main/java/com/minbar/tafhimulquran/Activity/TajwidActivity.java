package com.minbar.tafhimulquran.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.LoadAds;
import com.minbar.tafhimulquran.Utils.ThemeManager;

import java.util.Objects;

public class TajwidActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tajwid);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialToolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("তাজবিদ");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
