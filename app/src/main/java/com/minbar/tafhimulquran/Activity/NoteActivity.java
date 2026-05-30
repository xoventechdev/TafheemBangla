package com.minbar.tafhimulquran.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.minbar.tafhimulquran.Adapter.NoteAdapter;
import com.minbar.tafhimulquran.Model.NoteModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.NoteDatabaseHelper;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.ThemeManager;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView tvEmptyNotes;
    NoteAdapter adapter;
    NoteDatabaseHelper noteDbHelper;
    SqlLiteDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.notes_title);

        recyclerView = findViewById(R.id.recycler_notes);
        tvEmptyNotes = findViewById(R.id.tv_empty_notes);

        noteDbHelper = new NoteDatabaseHelper(this);
        dbHelper = SqlLiteDbHelper.getInstance(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadNotes();
    }

    private void loadNotes() {
        List<NoteModel> notes = noteDbHelper.getAllNotes(dbHelper);
        if (notes.isEmpty()) {
            tvEmptyNotes.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            tvEmptyNotes.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
            adapter = new NoteAdapter(this, notes);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }
}