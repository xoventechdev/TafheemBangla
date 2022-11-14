package com.minbar.tafhimulquran.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.minbar.tafhimulquran.Adapter.SubVerseAdapter;
import com.minbar.tafhimulquran.Adapter.WordAdapter;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.databinding.ActivitySubjectBinding;

import es.dmoral.toasty.Toasty;

public class SubjectActivity extends AppCompatActivity {

    ActivitySubjectBinding binding;
    SqlLiteDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_subject);

        Intent is = getIntent();
        getSupportActionBar().setTitle(is.getStringExtra("sub_title"));
        getSupportActionBar().setSubtitle(is.getStringExtra("sub_sub"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        dbHelper = new SqlLiteDbHelper(this);

        SubVerseAdapter adapter = new SubVerseAdapter(this,dbHelper.getSubVerse(Integer.parseInt(is.getStringExtra("subId"))));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.subVerse.setHasFixedSize(true);
        binding.subVerse.setLayoutManager(mLayoutManager);
        binding.subVerse.setAdapter(adapter);





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
        titleVerse.setText(strParts[0]+" : "+id_Parts[1]);

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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}