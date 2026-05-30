package com.minbar.tafhimulquran.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.minbar.tafhimulquran.Adapter.SearchVerseAdapter;
import com.minbar.tafhimulquran.Adapter.SurahAdapter;
import com.minbar.tafhimulquran.Model.SurahModel;
import com.minbar.tafhimulquran.Model.VerseModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.ThemeManager;
import com.minbar.tafhimulquran.databinding.ActivitySearchBinding;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.dmoral.toasty.Toasty;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private SqlLiteDbHelper dbHelper;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    public static String getTXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        dbHelper = SqlLiteDbHelper.getInstance(this);
        binding.searchView.setLayoutManager(new LinearLayoutManager(this));
        binding.searchView.setHasFixedSize(true);

        initListeners();
    }

    private void initListeners() {
        // Clear search text
        binding.btnClear.setOnClickListener(v -> binding.etSearch.setText(""));

        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Handle filter changes - re-trigger search if there's text
        binding.searchFilterGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String query = binding.etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                performSearch(query);
            }
        });

        // Search as you type (Debouncing)
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
                if (s.length() > 0) {
                    binding.btnClear.setVisibility(View.VISIBLE);
                    searchRunnable = () -> performSearch(s.toString().trim());
                    // Delay search for 500ms after user stops typing
                    searchHandler.postDelayed(searchRunnable, 500);
                } else {
                    binding.btnClear.setVisibility(View.GONE);
                    binding.searchView.setVisibility(View.GONE);
                    binding.emptyStateContainer.setVisibility(View.GONE);
                    binding.searchLoader.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Handle 'Search' button on soft keyboard
        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchHandler.removeCallbacks(searchRunnable);
                performSearch(binding.etSearch.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    @SuppressWarnings("unchecked")
    private void performSearch(String query) {
        if (query.isEmpty()) return;

        getTXT = query;
        
        // UI updates before starting query
        runOnUiThread(() -> {
            binding.searchLoader.setVisibility(View.VISIBLE);
            binding.emptyStateContainer.setVisibility(View.GONE);
        });

        // Run query in background thread
        executorService.execute(() -> {
            final List<?> results;
            final boolean isSurah = binding.sSurah.isChecked();
            final boolean isBn = binding.sBn.isChecked();

            if (isSurah) {
                results = dbHelper.getSurahSerach(query);
            } else if (isBn) {
                results = dbHelper.getAyatSearchBn(query);
            } else {
                results = dbHelper.getAyatSearchEn(query);
            }

            // Return results to UI thread
            runOnUiThread(() -> {
                binding.searchLoader.setVisibility(View.GONE);

                if (results == null || results.isEmpty()) {
                    binding.searchView.setVisibility(View.GONE);
                    binding.emptyStateContainer.setVisibility(View.VISIBLE);
                } else {
                    binding.emptyStateContainer.setVisibility(View.GONE);
                    binding.searchView.setVisibility(View.VISIBLE);
                    
                    if (isSurah) {
                        SurahAdapter adapter = new SurahAdapter(this, (List<SurahModel>) results);
                        binding.searchView.setAdapter(adapter);
                    } else {
                        SearchVerseAdapter adapter = new SearchVerseAdapter(this, (List<VerseModel>) results);
                        binding.searchView.setAdapter(adapter);
                    }
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
        searchHandler.removeCallbacks(searchRunnable);
    }
}
