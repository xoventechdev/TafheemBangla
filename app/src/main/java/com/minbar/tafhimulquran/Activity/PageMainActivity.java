package com.minbar.tafhimulquran.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.minbar.tafhimulquran.Adapter.PageParaAdapter;
import com.minbar.tafhimulquran.Adapter.PageSurahAdapter;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.databinding.ActivityPageMainBinding;

import java.util.Objects;

public class PageMainActivity extends AppCompatActivity {

    ActivityPageMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_page_main);

        //binding.pageItemList.
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.chapa_quran);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(this);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}