package com.minbar.tafhimulquran.Hadith;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.minbar.tafhimulquran.Adapter.HadithListAdapter;
import com.minbar.tafhimulquran.Model.HadithListModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.DatabaseHelper;
import com.minbar.tafhimulquran.Utils.ThemeManager;

import java.util.List;
import java.util.Objects;

public class HadithListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HadithListAdapter adapter;
    private DatabaseHelper databaseHelper;
    private List<HadithListModel> hadithList;
    MaterialToolbar toolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hadith_list);

        int chapterId = getIntent().getIntExtra("chapterId", -1);
        String chapterName = getIntent().getStringExtra("chapterName");


        toolbar = findViewById(R.id.toolBar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(chapterName);



        recyclerView = findViewById(R.id.recycler_hadith_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);
        hadithList = databaseHelper.getHadithsByChapterId(chapterId);

        adapter = new HadithListAdapter(this, hadithList);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
