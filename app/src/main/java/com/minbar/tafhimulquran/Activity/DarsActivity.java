package com.minbar.tafhimulquran.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;

import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.databinding.ActivityDarsBinding;

public class DarsActivity extends AppCompatActivity {

    ActivityDarsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_dars);

        getSupportActionBar().setTitle("দারসুল কুরআন");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent is = getIntent();
        binding.title.setText(Html.fromHtml(is.getStringExtra("title")+"<br><i><small>(আলোচ্য সুরাহ :- "+is.getStringExtra("ayat")+")</small></i>"));
        binding.author.setText(is.getStringExtra("author"));
        SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(this);
        binding.content.setText(Html.fromHtml(dbHelper.getDarsContent(Integer.parseInt(is.getStringExtra("id")))));
        binding.content.setTypeface(FontFamily.getBangla(this));
        binding.content.setTextSize(2,Float.valueOf(FontSize.getBangla(this)));

        /*
        SharedPreferences ads = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor adsEdit;
        adsEdit = ads.edit();
        int i = ads.getInt("test4", 1);
        if (i == 1) {
            adsEdit.putInt("test4", 0);
            adsEdit.apply();
        } else {
            adsEdit.putInt("test4", 1);
            adsEdit.apply();
            LoadAds.admobInter(this);
        }
        public void onBackPressed() {
            LoadAds.admobLoad(this);
            super.onBackPressed();
        }

         */
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //LoadAds.admobLoad(this);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}