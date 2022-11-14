package com.minbar.tafhimulquran.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.minbar.tafhimulquran.Adapter.SearchVerseAdapter;
import com.minbar.tafhimulquran.Adapter.SurahAdapter;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.databinding.ActivitySearchBinding;

import es.dmoral.toasty.Toasty;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    SqlLiteDbHelper dbHelper;

    SurahAdapter surahAdapter;
    public  static String getTXT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_search);

        //Objects.requireNonNull(getSupportActionBar()).setTitle("Serach Page");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        dbHelper = new SqlLiteDbHelper(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.searchView.setHasFixedSize(true);
        binding.searchView.setLayoutManager(layoutManager);


        binding.etSearch.setOnClickListener(v -> {
            getTXT = binding.etSearch.getText().toString();
            if (binding.sSurah.isChecked()){
                if (binding.etSearch.length() >0){
                    surahAdapter = new SurahAdapter(this, this.dbHelper.getSurahSerach(binding.etSearch.getText().toString()));
                    if (this.dbHelper.getSurahSerach(binding.etSearch.getText().toString()).size() <1){
                        Toasty.warning(getApplicationContext(),"কোন ফলাফল পাওয়া যায়নি", Toasty.LENGTH_LONG, false).show();
                        binding.noData.setVisibility(View.VISIBLE);
                        binding.searchView.setVisibility(View.GONE);
                    }else {
                        Toasty.success(getApplicationContext(),String.valueOf(this.dbHelper.getSurahSerach(binding.etSearch.getText().toString()).size())+" টি ফলাফল পাওয়া গেছে", Toasty.LENGTH_LONG, false).show();
                        binding.noData.setVisibility(View.GONE);
                        binding.searchView.setVisibility(View.VISIBLE);
                        binding.searchView.setAdapter(surahAdapter);
                    }
                }
            }else if (binding.sBn.isChecked()){
                if (binding.etSearch.length() >0){
                    SearchVerseAdapter adapter = new SearchVerseAdapter(this, this.dbHelper.getAyatSearchBn(binding.etSearch.getText().toString()));
                    if (this.dbHelper.getAyatSearchBn(binding.etSearch.getText().toString()).size() <1){
                        Toasty.warning(getApplicationContext(),"কোন ফলাফল পাওয়া যায়নি", Toasty.LENGTH_LONG, false).show();
                        binding.noData.setVisibility(View.VISIBLE);
                        binding.searchView.setVisibility(View.GONE);
                    }else {
                        Toasty.success(getApplicationContext(),String.valueOf(this.dbHelper.getAyatSearchBn(binding.etSearch.getText().toString()).size())+" টি ফলাফল পাওয়া গেছে", Toasty.LENGTH_LONG, false).show();
                        binding.noData.setVisibility(View.GONE);
                        binding.searchView.setVisibility(View.VISIBLE);
                        binding.searchView.setAdapter(adapter);
                    }
                }
            }else if (binding.sEn.isChecked()){
                if (binding.etSearch.length() >0){
                    SearchVerseAdapter adapter = new SearchVerseAdapter(this, this.dbHelper.getAyatSearchEn(binding.etSearch.getText().toString()));
                    if (this.dbHelper.getAyatSearchEn(binding.etSearch.getText().toString()).size() <1){
                        Toasty.warning(getApplicationContext(),"কোন ফলাফল পাওয়া যায়নি", Toasty.LENGTH_LONG, false).show();
                        binding.noData.setVisibility(View.VISIBLE);
                        binding.searchView.setVisibility(View.GONE);
                    }else {
                        Toasty.success(getApplicationContext(),String.valueOf(this.dbHelper.getAyatSearchEn(binding.etSearch.getText().toString()).size())+" টি ফলাফল পাওয়া গেছে", Toasty.LENGTH_LONG, false).show();
                        binding.noData.setVisibility(View.GONE);
                        binding.searchView.setVisibility(View.VISIBLE);
                        binding.searchView.setAdapter(adapter);
                    }
                }
            }




        });


        binding.sSurah.setOnClickListener(v -> {
            binding.etSearch.getText().clear();
            binding.sSurah.setChecked(true);
            binding.sBn.setChecked(false);
            binding.sEn.setChecked(false);
        });
        binding.sBn.setOnClickListener(v -> {
            binding.etSearch.getText().clear();
            binding.sBn.setChecked(true);
            binding.sSurah.setChecked(false);
            binding.sEn.setChecked(false);
        });
        binding.sEn.setOnClickListener(v -> {
            binding.etSearch.getText().clear();
            binding.sEn.setChecked(true);
            binding.sBn.setChecked(false);
            binding.sSurah.setChecked(false);
        });
  /*

        binding.sSurah.setOnClickListener(v -> {
            binding.sSurah.setChecked(true);
            binding.sBn.setChecked(false);
            binding.sEn.setChecked(false);

            surahAdapter = new SurahAdapter(this, this.dbHelper.getSurahV(as));
            binding.searchView.setAdapter(surahAdapter);


        });


        binding.sBn.setOnClickListener(v -> {
            binding.sBn.setChecked(true);
            binding.sSurah.setChecked(false);
            binding.sEn.setChecked(false);
            //binding.etSearch.getText().toString();

            VerseAdapter adapter = new VerseAdapter(this,dbHelper.getAyatSearch("আমরা"));
            Toasty.success(getApplicationContext(), String.valueOf(dbHelper.getAyatSearch("আমরা").size()), Toasty.LENGTH_SHORT).show();
            binding.searchView.setAdapter(adapter);

            binding.searchView.setVisibility(View.VISIBLE);

        });


        binding.sEn.setOnClickListener(v -> {
            binding.sEn.setChecked(true);
            binding.sBn.setChecked(false);
            binding.sSurah.setChecked(false);
            if (binding.etSearch.length()<0){
                binding.searchView.setAdapter(surahAdapter);
                binding.searchView.setVisibility(View.VISIBLE);
            }
        });





        if (binding.sSurah.isChecked()){
            binding.sBn.setChecked(false);
            binding.sEn.setChecked(false);
            binding.searchView.setAdapter(surahAdapter);
        }



         */


/*
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (count == 0 ){
                    binding.searchView.setVisibility(View.GONE);
                }else {
                    binding.searchView.setVisibility(View.VISIBLE);
                }


            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count == 0 ){
                    binding.searchView.setVisibility(View.GONE);
                }else {
                    binding.searchView.setVisibility(View.VISIBLE);
                }


            }
            @Override
            public void afterTextChanged(Editable s){
                as = String.valueOf(s);
                surahAdapter.getFilter().filter(s);
            }
        });





 */




    }



/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

 */
}