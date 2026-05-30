package com.minbar.tafhimulquran.Hadith;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.minbar.tafhimulquran.Adapter.HadithChapterAdapter;
import com.minbar.tafhimulquran.Model.HadithChapter;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.DatabaseHelper;
import com.minbar.tafhimulquran.Utils.ThemeManager;

import java.util.List;
import java.util.Objects;

public class HadithChapterActivity extends AppCompatActivity {

    MaterialToolbar toolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hadith_chapter);

        toolbar = findViewById(R.id.toolBar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("রিয়াদুস সালেহীন");
            getSupportActionBar().setSubtitle("১৯০৫টি হাদিস");


        EditText searchBar = findViewById(R.id.search_bar);
        RecyclerView recyclerView = findViewById(R.id.hadith_list);

// Initialize Database and Adapter
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<HadithChapter> chapters = databaseHelper.getAllChapters();
        HadithChapterAdapter adapter = new HadithChapterAdapter(this, chapters);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

// Set up TextWatcher for search
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString()); // Filter the list based on user input
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });




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