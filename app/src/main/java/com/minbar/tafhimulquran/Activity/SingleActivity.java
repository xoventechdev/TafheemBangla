package com.minbar.tafhimulquran.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.minbar.tafhimulquran.Adapter.FragmentAdapter;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.ThemeManager;

public class SingleActivity extends AppCompatActivity {

    public static int surahid = 1;
    public static String arabicTxt , transTxt , banglaTxt, jcontent;
    SqlLiteDbHelper dbHelper;
    public static String[] p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);

        dbHelper = SqlLiteDbHelper.getInstance(this);

        String surahIdStr = getIntent().getStringExtra("surah_id");
        if (surahIdStr != null) {
            try {
                surahid = Integer.parseInt(surahIdStr);
            } catch (NumberFormatException e) {
                surahid = getIntent().getIntExtra("surah_id", 1);
            }
        } else {
            surahid = getIntent().getIntExtra("surah_id", 1);
        }

        int verse_id = getIntent().getIntExtra("verse_id", 0);

        int currentItem;
        // Surah 1 has verses 0-7 in DB. Others start from 1.
        if (surahid == 1) {
            currentItem = verse_id;
        } else {
            currentItem = verse_id - 1;
        }

        TabLayout tabLayout = findViewById(R.id.toolbarLayout);
        ViewPager2 viewPager2 = findViewById(R.id.viewpager);

        p = dbHelper.getVerseList(surahid);

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(adapter);
        viewPager2.setCurrentItem(currentItem, false);

        // Force RTL for the TabLayout and ViewPager2 for traditional Quran reading order (Right-to-Left)
        tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        viewPager2.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (surahid == 1) {
                    tab.setText(Config.ENtoBN("আয়াত " + position));
                } else {
                    tab.setText(Config.ENtoBN("আয়াত " + (position + 1)));
                }
            }
        });
        tabLayoutMediator.attach();
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
