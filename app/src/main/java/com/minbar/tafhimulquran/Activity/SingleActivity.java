package com.minbar.tafhimulquran.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.minbar.tafhimulquran.Adapter.FragmentAdapter;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.ThemeManager;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class SingleActivity extends AppCompatActivity {


    public static int surahid = 1;
    public static String arabicTxt , transTxt , banglaTxt, jcontent;
    SqlLiteDbHelper dbHelper;

    public  static String[] p;




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
        arabicTxt = getIntent().getStringExtra("arabicTxt");
        transTxt = getIntent().getStringExtra("transTxt");
        banglaTxt = getIntent().getStringExtra("banglaTxt");
        //Toasty.success(getApplicationContext(), String.valueOf(verse_id), Toasty.LENGTH_LONG, false).show();

        if ( surahid==9){
        }else {
            verse_id = verse_id-1;
        }

//        Objects.requireNonNull(getSupportActionBar()).setTitle(dbHelper.getSurahName(surahid));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //jcontent = arabicTxt+"="+transTxt+"@"+banglaTxt;


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialToolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(dbHelper.getSurahName(surahid));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TabLayout tabLayout = findViewById(R.id.toolbarLayout);
        ViewPager2 viewPager2 = findViewById(R.id.viewpager);



        p = dbHelper.getVerseList(surahid);

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(adapter);

        viewPager2.setCurrentItem(verse_id,false);

        tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        TabLayoutMediator tabLayoutMediator=new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(Config.ENtoBN("আয়াত "+(position+1)));
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
