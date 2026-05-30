package com.minbar.tafhimulquran.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.minbar.tafhimulquran.Adapter.DarsAdapter;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.ThemeManager;

public class DarsListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DarsAdapter adapter;
    private SqlLiteDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dars_list);

        MaterialToolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new SqlLiteDbHelper(this);
        recyclerView = findViewById(R.id.recyclerDars);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new DarsAdapter(this, dbHelper.getDars());
        recyclerView.setAdapter(adapter);
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
