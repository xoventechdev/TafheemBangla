package com.minbar.tafhimulquran.Daily;

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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.minbar.tafhimulquran.Activity.SingleActivity;
import com.minbar.tafhimulquran.Model.HadithModel;
import com.minbar.tafhimulquran.Model.VerseModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.databinding.ActivityDailyBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class DailyActivity extends AppCompatActivity {

    ActivityDailyBinding binding;

    SqlLiteDbHelper dbHelper;
    ArrayList<VerseModel> data;
    SharedPreferences setting;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_daily);

        MaterialToolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("প্রতিদিন কুরআন - হাদীস");

        binding.copyDaily.setOnClickListener(v -> {
            ((ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("আয়াত ও হাদিস", "আল কুরআন : " + binding.dailyQuranTag.getText().toString() + "\n" + binding.arabic.getText().toString() + "\n" + binding.banglaAyat.getText().toString() + "\n\n" + "আল হাদিস : " + binding.dailyHadithTag.getText().toString() + "\n" + binding.arabicH.getText().toString() + "\n" + binding.transH.getText().toString() + "\n\n" + "তাফহীমুল কুরআন" + "\nhttps://play.google.com/store/apps/details?id=" + this.getPackageName()));
            Toasty.success(this, "আয়াত ও হাদিস কপি হয়েছে", Toast.LENGTH_SHORT, true).show();
        });

        setting = PreferenceManager.getDefaultSharedPreferences(this);

        dbHelper = new SqlLiteDbHelper(this);
        getQuran();
        getHadith();

        binding.dailyQuran.setOnClickListener(v -> {
            if (data != null && !data.isEmpty()) {
                Intent intent = new Intent(this, SingleActivity.class);
                intent.putExtra("surah_id", String.valueOf(data.get(0).getSurahID()));
                intent.putExtra("verse_id", data.get(0).getVerseID());
                intent.putExtra("arabicTxt", data.get(0).getArabic());
                intent.putExtra("transTxt", data.get(0).getTrans());
                intent.putExtra("banglaTxt", data.get(0).getBangla());
                startActivity(intent);
            } else {
                Toasty.warning(this, "এই মুহূর্তে আয়াত দেখানো সম্ভব নয়।", Toast.LENGTH_SHORT, true).show();
            }
        });


        binding.arabic.setTypeface(FontFamily.getArabic(this));
        binding.arabicH.setTypeface(FontFamily.getArabic(this));
        binding.banglaAyat.setTypeface(FontFamily.getBangla(this));
        binding.trans.setTypeface(FontFamily.getBangla(this));
        binding.transH.setTypeface(FontFamily.getBangla(this));

        binding.arabic.setTextSize(2, Float.valueOf(FontSize.getArabic(this)));
        binding.arabicH.setTextSize(2, Float.valueOf(FontSize.getArabic(this)));
        binding.banglaAyat.setTextSize(2, Float.valueOf(FontSize.getBangla(this)));
        binding.trans.setTextSize(2, Float.valueOf(FontSize.getBangla(this)));
        binding.transH.setTextSize(2, Float.valueOf(FontSize.getBangla(this)));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void getQuran() {
        Random rand = new Random();
        int randomNum = rand.nextInt((6348 - 1) + 1) + 1;
        data = dbHelper.getDailyQuran(randomNum);

        if (data != null && !data.isEmpty()) {
            binding.arabic.setText(data.get(0).getArabic());

            if (setting.getString("tafheem", "on").equals("off")) {
                binding.arabicMeana.setVisibility(View.GONE);
            } else {
                binding.arabicMeana.setVisibility(View.VISIBLE);
                binding.trans.setText(data.get(0).getTrans());
            }

            if (setting.getString("banglaOnubadh", "on").equals("off")) {
                binding.VerseTai.setVisibility(View.GONE);
            } else {
                binding.VerseTai.setVisibility(View.VISIBLE);
                binding.banglaAyat.setText(Html.fromHtml(new Config(this).HideNumber(data.get(0).getBangla())));
            }

            if (setting.getString("taisirul", "on").equals("off")) {
                binding.enLayout.setVisibility(View.GONE);
            } else {
                binding.enLayout.setVisibility(View.VISIBLE);
                binding.english.setText(data.get(0).getEnglish());
            }

            binding.dailyQuranTag.setText(Html.fromHtml(
                    dbHelper.getSurahName(data.get(0).getSurahID()) + " - " +
                            Config.ENtoBN(String.valueOf(data.get(0).getVerseID())) + "</small></i> "
            ));
        } else {
            Toasty.error(this, "দুঃখিত, কুরআনের আয়াত পাওয়া যায়নি।", Toast.LENGTH_LONG, true).show();
        }
    }


    private void getHadith() {
        ArrayList<HadithModel> data;
        Random rand = new Random();
        int randomNum = rand.nextInt((616 - 1) + 1) + 1;
        data = dbHelper.getDailyHadith(randomNum);

        if (data == null || data.isEmpty()) {
            // Handle gracefully if no hadith found
            binding.arabicH.setText("আজকের জন্য কোন হাদীস পাওয়া যায়নি।");
            binding.arabicMeanaH.setVisibility(View.GONE);
            binding.VerseTaiH.setVisibility(View.GONE);
            binding.dailyHadithTag.setText("হাদীস পাওয়া যায়নি");
            binding.hadithNote.setText("");
            binding.hadithStatus.setText("");
            return;
        }

        // Proceed safely
        HadithModel hadith = data.get(0);

        binding.arabicH.setText(hadith.getHadith_arabic());

        if (setting.getString("banglaOnubadh", "on").equals("off")) {
            binding.arabicMeanaH.setVisibility(View.GONE);
        } else {
            binding.arabicMeanaH.setVisibility(View.VISIBLE);
            binding.transH.setText(Html.fromHtml(hadith.getHadith_bangla()));
        }

        if (setting.getString("taisirul", "on").equals("off")) {
            binding.VerseTaiH.setVisibility(View.GONE);
        } else {
            binding.VerseTaiH.setVisibility(View.VISIBLE);
            binding.banglaAyatH.setText(hadith.getHadith_english());
        }

        binding.dailyHadithTag.setText(Html.fromHtml("রিয়াদুস সালেহীন - " + Config.ENtoBN(String.valueOf(hadith.getHadith_no())) + "</small></i> "));

        if (!hadith.getHadith_note().isEmpty()) {
            binding.hadithNote.setText("নোটঃ- " + hadith.getHadith_note());
        }

        switch (hadith.getHadith_status()) {
            case 1:
                binding.hadithStatus.setText("হাদীসের মান : সহিহ (Sahih)");
                break;
            case 2:
                binding.hadithStatus.setText("হাদীসের মান : হাসান (Hasan)");
                break;
            default:
                binding.hadithStatus.setText("হাদীসের মান : যঈফ (Dai'f)");
        }
    }
}