package com.minbar.tafhimulquran.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.ThemeManager;
import com.minbar.tafhimulquran.databinding.ActivityDarsBinding;

import es.dmoral.toasty.Toasty;

public class DarsActivity extends AppCompatActivity {

    ActivityDarsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_dars);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialToolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("দারসুল কুরআন");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent is = getIntent();
        binding.title.setText(Html.fromHtml(is.getStringExtra("title")+"<br><i><small>(আলোচ্য সুরাহ :- "+is.getStringExtra("ayat")+")</small></i>"));
        binding.author.setText(is.getStringExtra("author"));
        SqlLiteDbHelper dbHelper = SqlLiteDbHelper.getInstance(this);
        binding.content.setText(Html.fromHtml(dbHelper.getDarsContent(Integer.parseInt(is.getStringExtra("id")))));
        binding.content.setTypeface(FontFamily.getBangla(this));
        binding.content.setTextSize(2,Float.valueOf(FontSize.getBangla(this)));
        binding.copyDars.setOnClickListener(v -> ForcopyDars());

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
    public void ForcopyDars(){
        ((ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("দারসুল কুরআন", "দারসুল কুরআন : "+ "\n" + binding.title.getText().toString() + "\n" + binding.author.getText().toString() + "\n" + binding.content.getText().toString() +"\n\n"+"তাফহীমুল কুরআন"+"\nhttps://play.google.com/store/apps/details?id=" + this.getPackageName()));
        //Toast.makeText(VerseAdapter.mcontext, "This verse has been copied", Toast.LENGTH_SHORT).show();
        Toasty.success(this, "দারসুল কুরআন কপি হয়েছে", Toast.LENGTH_SHORT, true).show();


    }
}
