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
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;
import com.minbar.tafhimulquran.Utils.PronunciationUtils;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.ThemeManager;
import com.minbar.tafhimulquran.Utils.fezilalilDatabaseHelper;
import com.minbar.tafhimulquran.Utils.tafheemEnglishDatabaseHelper;
import com.minbar.tafhimulquran.databinding.ActivityTafheemBinding;

import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;


public class TafheemActivity extends AppCompatActivity {

    ActivityTafheemBinding binding;
    SqlLiteDbHelper dbHelper;
    Intent is;
    String surahName;
    int surah_id;
    String verse_id_en;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_tafheem);

        is = getIntent();
        surah_id = Integer.parseInt(is.getStringExtra("surah_id"));
        verse_id_en = is.getStringExtra("verse_id");

        dbHelper = SqlLiteDbHelper.getInstance(this);
        surahName = dbHelper.getSurahName(surah_id);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialToolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(surahName+" : "+Config.ENtoBN(verse_id_en));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(this);
        if (setting.getString("tafheem", "on").equals("off")) {
            binding.trans.setVisibility(View.GONE);
        }

        // Set Arabic pronunciation visibility
        if (PronunciationUtils.isArabicPronunciationVisible(this)) {
            binding.trans.setVisibility(View.VISIBLE);
            binding.trans.setText(is.getStringExtra("transTxt"));
        } else {
            binding.trans.setVisibility(View.GONE);
            binding.trans.setText("");
        }

       String getBangla = is.getStringExtra("banglaTxt").replace("০","<font color='#E91E63'><sup>০</sup></font>").replace("১","<font color='#E91E63'><sup>১</sup></font>").replace("২","<font color='#E91E63'><sup>২</sup></font>").replace("৩","<font color='#E91E63'><sup>৩</sup></font>").replace("৪","<font color='#E91E63'><sup>৪</sup></font>").replace("৫","<font color='#E91E63'><sup>৫</sup></font>").replace("৬","<font color='#E91E63'><sup>৬</sup></font>").replace("৭","<font color='#E91E63'><sup>৭</sup></font>").replace("৮","<font color='#E91E63'><sup>৮</sup></font>").replace("৯","<font color='#E91E63'><sup>৯</sup></font>");

        Config.BanglaOnubadh(binding.VerseTai, this);

        binding.arabic.setText(new Config(this).Tajweed(is.getStringExtra("arabicTxt")));
        binding.trans.setText(is.getStringExtra("transTxt"));
        binding.banglaAyat.setText(Html.fromHtml(getBangla));

        binding.arabic.setTypeface(FontFamily.getArabic(this));
        binding.trans.setTypeface(FontFamily.getBangla(this));
        binding.banglaAyat.setTypeface(FontFamily.getBangla(this));
        
        binding.tafheem.setTypeface(FontFamily.getBangla(this));
        binding.bayaan.setTypeface(FontFamily.getBangla(this));
        binding.fezilalil.setTypeface(FontFamily.getBangla(this));
        binding.tafheemEnglish.setTypeface(FontFamily.getBangla(this));

        binding.arabic.setTextSize(2, Float.valueOf(FontSize.getArabic(this)));
        binding.trans.setTextSize(2, Float.valueOf(FontSize.getBangla(this)));
        binding.banglaAyat.setTextSize(2, Float.valueOf(FontSize.getBangla(this)));
        
        binding.tafheem.setTextSize(2, Float.valueOf(FontSize.getBangla(this)));
        binding.bayaan.setTextSize(2, Float.valueOf(FontSize.getBangla(this)));
        binding.fezilalil.setTextSize(2, Float.valueOf(FontSize.getBangla(this)));
        binding.tafheemEnglish.setTextSize(2, Float.valueOf(FontSize.getBangla(this)));

        String s = is.getStringExtra("surah_id")+"="+ Config.ENtoBN(is.getStringExtra("verse_id"));
        
        // Tafheemul Quran
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < dbHelper.getTafheem(s).size(); i++) {
            String sss = dbHelper.getTafheem(s).get(i).toString();
            query.append(sss).append("<br>");
        }
        String main = query.toString().replace("\\n","<br>").replace("[[","").replace("]]","");
        if (main.isEmpty()){
            binding.tafheem.setText("এই আয়াতের তাফসীর নেই।");
        } else {
            binding.tafheem.setText(Html.fromHtml(main));
        }

        // Bayaan (Ibn Kasir)
        if (setting.getString("bayaan", "on").equals("off")) {
            binding.bayaanLayout.setVisibility(View.GONE);
        } else {
            binding.bayaanLayout.setVisibility(View.VISIBLE);
            StringBuilder bayaanquery = new StringBuilder();
            for (int i = 0; i < dbHelper.getBayaan(s).size(); i++) {
                String sss = dbHelper.getBayaan(s).get(i).toString();
                bayaanquery.append(sss).append("<br>");
            }
            String bayaanmain = bayaanquery.toString().replace("[১]","<br><br><b>তাফসীরঃ-</b><br>[১]").replaceFirst(Pattern.quote("<br><br><b>তাফসীরঃ-</b><br>[১]"),"[১]").replace("[২]","<br><br>[২]").replaceFirst(Pattern.quote("<br><br>[২]"),"[২]").replace("[৩]","<br><br>[৩]").replaceFirst(Pattern.quote("<br><br>[৩]"),"[৩]").replace("[৪]","<br><br>[৪]").replaceFirst(Pattern.quote("<br><br>[৪]"),"[৪]");
            if (bayaanmain.isEmpty()){
                binding.bayaan.setText("এই আয়াতের তাফসীর নেই।");
            } else {
                binding.bayaan.setText(Html.fromHtml(bayaanmain));
            }
        }

        // Fezilalil Quran
        if (setting.getString("fezilalil", "off").equals("off")) {
            binding.fezilalilLayout.setVisibility(View.GONE);
        } else {
            binding.fezilalilLayout.setVisibility(View.VISIBLE);
            fezilalilDatabaseHelper fezilalilDatabaseHelper = new fezilalilDatabaseHelper(this);
            StringBuilder fezilalilquery = new StringBuilder();
            for (int i = 0; i < fezilalilDatabaseHelper.getFezilalil(Config.ENtoBN(s)).size(); i++) {
                String sss = fezilalilDatabaseHelper.getFezilalil(Config.ENtoBN(s)).get(i).toString();
                fezilalilquery.append(sss).append("<br>");
            }
            String fezilalilmain = fezilalilquery.toString().replace("[১]","<br><br><b>তাফসীরঃ-</b><br>[১]").replaceFirst(Pattern.quote("<br><br><b>তাফসীরঃ-</b><br>[১]"),"[১]").replace("[২]","<br><br>[২]").replaceFirst(Pattern.quote("<br><br>[২]"),"[২]").replace("[৩]","<br><br>[৩]").replaceFirst(Pattern.quote("<br><br>[৩]"),"[৩]").replace("[৪]","<br><br>[৪]").replaceFirst(Pattern.quote("<br><br>[৪]"),"[৪]");
            if (fezilalilmain.isEmpty()){
                binding.fezilalil.setText("এই আয়াতের তাফসীর নেই।");
            } else {
                binding.fezilalil.setText(Html.fromHtml(fezilalilmain));
            }
        }

        // Tafheemul Quran (English)
        if (setting.getString("tafheem_english", "off").equals("off")) {
            binding.tafheemEnglishLayout.setVisibility(View.GONE);
        } else {
            binding.tafheemEnglishLayout.setVisibility(View.VISIBLE);
            try {
                tafheemEnglishDatabaseHelper tafheemEnglishDatabaseHelper = new tafheemEnglishDatabaseHelper(this);
                StringBuilder tafheemEnglishquery = new StringBuilder();
                for (int i = 0; i < tafheemEnglishDatabaseHelper.getTafheemEnglish(s).size(); i++) {
                    String sss = tafheemEnglishDatabaseHelper.getTafheemEnglish(s).get(i).toString();
                    tafheemEnglishquery.append(sss).append("<br>");
                }
                String tafheemEnglishmain = tafheemEnglishquery.toString().replace("[১]","<br><br><b>তাফসীরঃ-</b><br>[১]").replaceFirst(Pattern.quote("<br><br><b>তাফসীরঃ-</b><br>[১]"),"[১]").replace("[২]","<br><br>[২]").replaceFirst(Pattern.quote("<br><br>[২]"),"[২]").replace("[৩]","<br><br>[৩]").replaceFirst(Pattern.quote("<br><br>[৩]"),"[৩]").replace("[৪]","<br><br>[৪]").replaceFirst(Pattern.quote("<br><br>[৪]"),"[৪]");
                if (tafheemEnglishmain.isEmpty()){
                    binding.tafheemEnglish.setText("এই আয়াতের তাফসীর নেই।");
                } else {
                    binding.tafheemEnglish.setText(Html.fromHtml(tafheemEnglishmain));
                }
            } catch (Exception e) {
                binding.tafheemEnglish.setText("তাফসীর ডাটাবেজটি ডাউনলোড করা নেই। সেটিংস থেকে ডাউনলোড করুন।");
                e.printStackTrace();
            }
        }

        // Click listeners for copy buttons
        binding.copyTafheem.setOnClickListener(v -> ForcopyFatheem("তাফহীমুল কুরআন - আল্লামা সাইয়েদ আবুল আলা মওদূদী রহঃ", binding.tafheem.getText().toString()));
        binding.copyBayaan.setOnClickListener(v -> ForcopyFatheem("তাফসীরে ইবনে কাসীর", binding.bayaan.getText().toString()));
        binding.copyFezilalil.setOnClickListener(v -> ForcopyFatheem("তাফসীর ফী যিলালিল কোরআন", binding.fezilalil.getText().toString()));
        binding.copyTafheemEnglish.setOnClickListener(v -> ForcopyFatheem("তাফহীমুল কুরআন (ইংরেজি)", binding.tafheemEnglish.getText().toString()));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void ForcopyFatheem(String title, String content){
        String copyText = surahName +" : "+Config.ENtoBN(verse_id_en)+"\n" + binding.arabic.getText().toString() + "\n" + binding.banglaAyat.getText().toString() + "\n\n" + title + " :-\n"+ content + "\n\n"+"তাফহীমুল কুরআন"+"\nhttps://play.google.com/store/apps/details?id=" + this.getPackageName();
        ((ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("আয়াত", copyText));
        Toasty.success(getApplicationContext(), "তাফসীর-সহ আয়াতটি কপি হয়েছে", Toast.LENGTH_SHORT, true).show();
    }

}