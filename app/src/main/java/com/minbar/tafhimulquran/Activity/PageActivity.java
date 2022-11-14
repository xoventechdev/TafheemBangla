package com.minbar.tafhimulquran.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.minbar.tafhimulquran.Adapter.FragmentAdapter;
import com.minbar.tafhimulquran.Adapter.PageAdapter;
import com.minbar.tafhimulquran.Fragment.PageFragment;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.databinding.ActivityPageBinding;

import java.util.Objects;

public class PageActivity extends AppCompatActivity {

    public static int pageId = 1;
    public static String arabicTxt , transTxt , banglaTxt, jcontent;
    SqlLiteDbHelper dbHelper;

    public  static String[] pPage;

    ActivityPageBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_page);



        dbHelper = new SqlLiteDbHelper(this);
        pageId = getIntent().getIntExtra("pageId", 1);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.chapa_quran);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pPage = dbHelper.getPageList();

        PageAdapter adapter = new PageAdapter(getSupportFragmentManager(), getLifecycle());
        binding.viewpager.setAdapter(adapter);

        if (pageId == 0){
            binding.viewpager.setCurrentItem(getIntent().getIntExtra("paraId", 1)-1,false);
        }else {
            binding.viewpager.setCurrentItem(dbHelper.checkPageNumber(pageId)-1,false);
        }

        binding.toolbarLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        TabLayoutMediator tabLayoutMediator=new TabLayoutMediator(binding.toolbarLayout, binding.viewpager, (tab, position) -> tab.setText(Config.ENtoBN("পৃষ্ঠা -  "+(position+1))));
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