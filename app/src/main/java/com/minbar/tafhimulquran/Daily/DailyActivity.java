package com.minbar.tafhimulquran.Daily;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.minbar.tafhimulquran.Activity.SingleActivity;
import com.minbar.tafhimulquran.Model.HadithModel;
import com.minbar.tafhimulquran.Model.VerseModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.databinding.ActivityDailyBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DailyActivity extends AppCompatActivity {

    ActivityDailyBinding binding;

    SqlLiteDbHelper dbHelper;
    ArrayList<VerseModel> data;
    SharedPreferences setting;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_daily);
        getSupportActionBar().setTitle("প্রতিদিন কুরআন - হাদীস");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setting = PreferenceManager.getDefaultSharedPreferences(this);

        dbHelper = new SqlLiteDbHelper(this);
        getQuran();
        getHadith();

        binding.dailyQuran.setOnClickListener(v -> {
            Intent intent = new Intent(this, SingleActivity.class);
            //Intent intent = new Intent(mcontext, TafheemActivity.class);
            intent.putExtra("surah_id",String.valueOf(data.get(0).getSurahID()));
            intent.putExtra("verse_id",data.get(0).getVerseID());
            intent.putExtra("arabicTxt",data.get(0).getArabic());
            intent.putExtra("transTxt",data.get(0).getTrans());
            intent.putExtra("banglaTxt",data.get(0).getBangla());
            startActivity(intent);
        });




    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {finish();}
        return super.onOptionsItemSelected(item);
    }

    private void getQuran(){

        Random rand = new Random();
        int randomNum = rand.nextInt((6348 - 1) + 1) + 1;
        data = dbHelper.getDailyQuran(randomNum);

        binding.arabic.setText(data.get(0).getArabic());
        if (setting.getString("tafheem", "on").equals("off")) {
            binding.arabicMeana.setVisibility(View.GONE);
        }else {
            binding.arabicMeana.setVisibility(View.VISIBLE);
            binding.trans.setText(data.get(0).getTrans());
        }

        if (setting.getString("banglaOnubadh", "on").equals("off")) {
            binding.VerseTai.setVisibility(View.GONE);
        }else {
            binding.VerseTai.setVisibility(View.VISIBLE);
            binding.banglaAyat.setText(Html.fromHtml(new Config(this).HideNumber(data.get(0).getBangla())));
        }


        if (setting.getString("taisirul", "on").equals("off")) {
            binding.enLayout.setVisibility(View.GONE);
        }else {
            binding.enLayout.setVisibility(View.VISIBLE);
            binding.english.setText(data.get(0).getEnglish());
        }

        binding.dailyQuranTag.setText(Html.fromHtml(dbHelper.getSurahName(data.get(0).getSurahID())+" - "+ Config.ENtoBN(String.valueOf(data.get(0).getVerseID()))+"</small></i> "));
    }
    private void getHadith(){
        ArrayList<HadithModel> data;
        Random rand = new Random();
        int randomNum = rand.nextInt((616 - 1) + 1) + 1;
        data = dbHelper.getDailyHadith(randomNum);

        binding.arabicH.setText(data.get(0).getHadith_arabic());

        if (setting.getString("banglaOnubadh", "on").equals("off")) {
            binding.arabicMeanaH.setVisibility(View.GONE);
        }else {
            binding.arabicMeanaH.setVisibility(View.VISIBLE);
            binding.transH.setText(Html.fromHtml(data.get(0).getHadith_bangla()));
        }


        if (setting.getString("taisirul", "on").equals("off")) {
            binding.VerseTaiH.setVisibility(View.GONE);
        }else {
            binding.VerseTaiH.setVisibility(View.VISIBLE);
            binding.banglaAyatH.setText(data.get(0).getHadith_english());
        }


        binding.dailyHadithTag.setText(Html.fromHtml("রিয়াদুস সালেহীন - "+ Config.ENtoBN(String.valueOf(data.get(0).getHadith_no()))+"</small></i> "));
        if (!data.get(0).getHadith_note().isEmpty()){
            binding.hadithNote.setText("নোটঃ- "+data.get(0).getHadith_note());
        }
        if (data.get(0).getHadith_status() == 1){
            binding.hadithStatus.setText("হাদীসের মান : সহিহ (Sahih)");
        }else if (data.get(0).getHadith_status() == 2){
            binding.hadithStatus.setText("হাদীসের মান : হাসান (Hasan)");
        }else {
            binding.hadithStatus.setText("হাদীসের মান : যঈফ (Dai'f)");
        }

    }

}