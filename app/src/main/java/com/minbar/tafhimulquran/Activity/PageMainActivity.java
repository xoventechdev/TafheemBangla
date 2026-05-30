package com.minbar.tafhimulquran.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.minbar.tafhimulquran.Adapter.PageParaAdapter;
import com.minbar.tafhimulquran.Adapter.PageSurahAdapter;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.ThemeManager;
import com.minbar.tafhimulquran.databinding.ActivityPageMainBinding;

import java.util.Objects;

public class PageMainActivity extends AppCompatActivity {

    ActivityPageMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_page_main);

        //binding.pageItemList.
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialToolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.chapa_quran);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SqlLiteDbHelper dbHelper = SqlLiteDbHelper.getInstance(this);

        binding.pageItemList.setHasFixedSize(true);
        binding.pageItemList.setLayoutManager(new LinearLayoutManager(this));
        //this.surahModelList = new ArrayList();
        PageSurahAdapter adapter = new PageSurahAdapter(this, dbHelper.getSurah());
        binding.pageItemList.setAdapter(adapter);



        binding.pageItemPara.setHasFixedSize(true);
        binding.pageItemPara.setLayoutManager(new LinearLayoutManager(this));
        //this.surahModelList = new ArrayList();
        PageParaAdapter adaptear = new PageParaAdapter(this);
        binding.pageItemPara.setAdapter(adaptear);
    }
}
