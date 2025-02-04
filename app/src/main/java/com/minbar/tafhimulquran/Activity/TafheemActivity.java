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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.minbar.tafhimulquran.Adapter.VerseAdapter;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;
import com.minbar.tafhimulquran.Utils.LoadAds;
import com.minbar.tafhimulquran.Utils.QuranArabicUtils;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.databinding.ActivityTafheemBinding;

import java.util.Objects;

import es.dmoral.toasty.Toasty;


public class TafheemActivity extends AppCompatActivity {

    ActivityTafheemBinding binding;
    SqlLiteDbHelper dbHelper;
    Intent is;
    String surahName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_tafheem);

        is = getIntent();
        int surah_id = Integer.parseInt(is.getStringExtra("surah_id"));

        dbHelper = new SqlLiteDbHelper(this);
        surahName = dbHelper.getSurahName(surah_id);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialToolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(surahName+" : "+Config.ENtoBN(is.getStringExtra("verse_id")));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (defaultSharedPreferences.getString("tafheem", "on").equals("off")) {
            binding.trans.setVisibility(View.GONE);
        }

       String getBangla = is.getStringExtra("banglaTxt").replace("০","<font color='#E91E63'><sup>০</sup></font>").replace("১","<font color='#E91E63'><sup>১</sup></font>").replace("২","<font color='#E91E63'><sup>২</sup></font>").replace("৩","<font color='#E91E63'><sup>৩</sup></font>").replace("৪","<font color='#E91E63'><sup>৪</sup></font>").replace("৫","<font color='#E91E63'><sup>৫</sup></font>").replace("৬","<font color='#E91E63'><sup>৬</sup></font>").replace("৭","<font color='#E91E63'><sup>৭</sup></font>").replace("৮","<font color='#E91E63'><sup>৮</sup></font>").replace("৯","<font color='#E91E63'><sup>৯</sup></font>");

        //binding.ayatNo.setText(is.getStringExtra("verse_id"));

        Config.BanglaOnubadh(binding.VerseTai, this);

        binding.arabic.setText(new Config(this).Tajweed(is.getStringExtra("arabicTxt")));
        binding.trans.setText(is.getStringExtra("transTxt"));
        binding.banglaAyat.setText(Html.fromHtml(getBangla));

        binding.arabic.setTypeface(FontFamily.getArabic(this));
        binding.trans.setTypeface(FontFamily.getBangla(this));
        binding.banglaAyat.setTypeface(FontFamily.getBangla(this));
        binding.tafheem.setTypeface(FontFamily.getBangla(this));

        binding.arabic.setTextSize(2, Float.valueOf(FontSize.getArabic(this)));
        binding.trans.setTextSize(2, Float.valueOf(FontSize.getBangla(this)));
        binding.banglaAyat.setTextSize(2, Float.valueOf(FontSize.getBangla(this)));
        binding.tafheem.setTextSize(2, Float.valueOf(FontSize.getBangla(this)));

        String s = is.getStringExtra("surah_id")+"="+ Config.ENtoBN(is.getStringExtra("verse_id"));
        //Toasty.success(getApplicationContext(), s, Toasty.LENGTH_LONG).show();
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < dbHelper.getTafheem(s).size(); i++) {
            //Toasty.success(getApplicationContext(), dbHelper.getTafheem(s).get(i).toString(), Toasty.LENGTH_LONG).show();
            String sss = dbHelper.getTafheem(s).get(i).toString();
            //String[] strParts = sss.split("@");
            query.append(sss).append("<br>");
        }
        String main = query.toString().replace("\\n","<br>").replace("[[","").replace("]]","");
        if (main.isEmpty()){
            binding.tafheem.setText("এই আয়াতের তাফসীর নেই।");
        } else {
            binding.tafheem.setText(Html.fromHtml(main));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tafheemmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.copyF:
                copyFatheem();
                //Toasty.success(getApplicationContext(), "Okey", Toasty.LENGTH_LONG).show();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void copyFatheem(){
        ((ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Ayah", surahName +" : "+is.getStringExtra("verse_id")+"\n" + binding.arabic.getText().toString() + "\n" + binding.banglaAyat.getText().toString() + "\n\n" + "তাফহীমুল কুরআন - আল্লামা সাইয়েদ আবুল আলা মওদূদী রহঃ :-\n"+binding.tafheem.getText().toString() + "\n\n"+"তাফহীমুল কুরআন"+"\nhttps://play.google.com/store/apps/details?id=" + this.getPackageName()));
        //Toast.makeText(VerseAdapter.mcontext, "This verse has been copied", Toast.LENGTH_SHORT).show();
        Toasty.success(getApplicationContext(), "The verse is copied.", Toast.LENGTH_SHORT, true).show();


    }

}