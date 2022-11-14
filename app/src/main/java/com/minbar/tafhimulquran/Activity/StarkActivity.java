package com.minbar.tafhimulquran.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.minbar.tafhimulquran.Adapter.StarkAdapter;
import com.minbar.tafhimulquran.Adapter.VerseAdapter;
import com.minbar.tafhimulquran.Adapter.WordAdapter;
import com.minbar.tafhimulquran.Model.VerseModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;

import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class StarkActivity extends AppCompatActivity {

    public List<VerseModel> verseModels;
    SqlLiteDbHelper mDatabase;
    String surah_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stark);

        mDatabase = new SqlLiteDbHelper(this);

        String v = getIntent().getStringExtra("idSurah");
        String vv = getIntent().getStringExtra("idAyat");
        surah_Name = mDatabase.getSurahName(Integer.parseInt(v));
        Objects.requireNonNull(getSupportActionBar()).setTitle(surah_Name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        verseModels = mDatabase.getAyatOvidan(v+"="+vv);
        //Toasty.success(getApplicationContext(), String.valueOf(verseModels.size()), Toasty.LENGTH_SHORT).show();


        if (!verseModels.isEmpty()){
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView recyclerView = (RecyclerView) findViewById(R.id.starkViwer);
            StarkAdapter adapter = new StarkAdapter(this,mDatabase.getAyatOvidan(v+"="+vv));
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);
        }else {
            Toasty.warning(getApplicationContext(), "Sorry, there are no verse", Toasty.LENGTH_SHORT).show();
        }







    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {  finish();  }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    public void onClickCalled(String anyValue) {
        //Toasty.success(getApplicationContext(), anyValue , Toasty.LENGTH_LONG).show();
        String[] strParts = anyValue.split("=");
        int surah = Integer.parseInt(strParts[0]);
        String verseBN = strParts[1];

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.word_meaning_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = -2;
        lp.height = -2;

        TextView titleVerse = (TextView) dialog.findViewById(R.id.title_verse);
        titleVerse.setText(surah_Name+" : "+Config.ENtoBN(verseBN));

        ImageView clearLayout = (ImageView) dialog.findViewById(R.id.clearLayout);
        clearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        RecyclerView recycler = (RecyclerView) dialog.findViewById(R.id.wordListview);
        //recycler = (RecyclerView) findViewById(R.id.wordListview);
        WordAdapter wordAdapter = new WordAdapter(this,mDatabase.getWord(anyValue));

        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(wordAdapter);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}