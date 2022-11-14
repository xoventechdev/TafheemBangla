package com.minbar.tafhimulquran.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.minbar.tafhimulquran.Adapter.FavVerseAdapter;
import com.minbar.tafhimulquran.Adapter.WordAdapter;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.XovenHandler;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class FavActivity extends AppCompatActivity {

    FavVerseAdapter adapter;
    SqlLiteDbHelper dbHelper;
    RecyclerView recyclerView;
    NestedScrollView hideLayout ;
    ArrayList list;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);


        getSupportActionBar().setTitle(getString(R.string.main_bottom_nav_fav)+" আয়াত");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        hideLayout = findViewById(R.id.hideLayout);

        list = new XovenHandler(this).getAllFav();

        StringBuilder query = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            String sss = list.get(i).toString();
            query.append(sss).append(",");
        }
        StringBuffer sb= new StringBuffer(query);
        if (!list.isEmpty()){
            sb.deleteCharAt(sb.length()-1);
        }
        String vb = sb.toString();
        //Toasty.success(getApplicationContext(), vb, Toasty.LENGTH_SHORT).show();

        this.dbHelper = new SqlLiteDbHelper(this);
        RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.recycler_fav);
        this.recyclerView = recyclerView2;
        recyclerView2.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.adapter = new FavVerseAdapter(this, this.dbHelper.getFav(vb));
        this.recyclerView.setAdapter(this.adapter);

        checkList();

    }
    public void checkList(){
        if(new XovenHandler(this).getAllFav().isEmpty()){
            this.hideLayout.setVisibility(View.GONE);
            findViewById(R.id.noFav).setVisibility(View.VISIBLE);
        } else {
            this.hideLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.noFav).setVisibility(View.GONE);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("SetTextI18n")
    public void onClickCalled(String anyValue) {
        //Toasty.success(getApplicationContext(), anyValue , Toasty.LENGTH_LONG).show();
        String[] strParts = anyValue.split("@");
        String id = strParts[1];
        String[] id_Parts = id.split("=");


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
        titleVerse.setText(strParts[0]+" : "+ Config.ENtoBN(id_Parts[1]));

        ImageView clearLayout = (ImageView) dialog.findViewById(R.id.clearLayout);
        clearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        RecyclerView recycler = (RecyclerView) dialog.findViewById(R.id.wordListview);
        //recycler = (RecyclerView) findViewById(R.id.wordListview);
        WordAdapter wordAdapter = new WordAdapter(this,dbHelper.getWord(id));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(layoutManager);
        //recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //recycler.setReverseLayout(true);
        //recycler.setLayoutManager(new GridLayoutManager(this, 3));
        //recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(wordAdapter);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}