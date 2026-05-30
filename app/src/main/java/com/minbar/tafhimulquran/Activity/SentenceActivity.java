package com.minbar.tafhimulquran.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.minbar.tafhimulquran.Adapter.CharacterAdapter;
import com.minbar.tafhimulquran.Adapter.SenAdapter;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.ThemeManager;

import java.util.Objects;

public class SentenceActivity extends AppCompatActivity {

    SqlLiteDbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialToolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(getIntent().getStringExtra("title"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = SqlLiteDbHelper.getInstance(this);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView recyclerView = findViewById(R.id.viewSentence);
        SenAdapter adapter = new SenAdapter(this,dbHelper.getSen(getIntent().getStringExtra("ch_wd")));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {finish();}
        return super.onOptionsItemSelected(item);
    }


    public void aboutShow(int s) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.about_surah_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = -1;
        lp.height = -1;

        TextView title_about = (TextView) dialog.findViewById(R.id.title_about);
        title_about.setText(dbHelper.getSurahName(s)+" এর ভূমিকা");

        ImageView clear_about = (ImageView) dialog.findViewById(R.id.clear_about);
        clear_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView aboutContent = (TextView) dialog.findViewById(R.id.aboutContent);
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < dbHelper.getAboutContent(s).size(); i++) {
            String sss = dbHelper.getAboutContent(s).get(i).toString();
            String[] strParts = sss.split("@");
            query.append(strParts[1]).append("<br>");
        }
        String main = query.toString().replace("\\n","<br>");
        aboutContent.setText(Html.fromHtml(main));
        aboutContent.setTypeface(FontFamily.getBangla(this));
        aboutContent.setTextSize(2, Float.valueOf(FontSize.getArabic(this)));

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }




}
