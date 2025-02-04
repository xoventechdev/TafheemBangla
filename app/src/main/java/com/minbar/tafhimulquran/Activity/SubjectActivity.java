package com.minbar.tafhimulquran.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.minbar.tafhimulquran.Adapter.SubVerseAdapter;
import com.minbar.tafhimulquran.Adapter.WordAdapter;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
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

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialToolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(is.getStringExtra("sub_title"));
        toolbar.setSubtitle(is.getStringExtra("sub_sub"));
        setSupportActionBar(toolbar);
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
        clearLayout.setOnClickListener(v -> dialog.dismiss());
        ((ImageView) dialog.findViewById(R.id.copyLayout)).setOnClickListener(v -> {
            ((ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("শব্দে শন্দে তাফহীমুল কুরআন",
                    "শব্দে শন্দে তাফহীমুল কুরআন \n"+strParts[0]+" : "+ Config.ENtoBN(id_Parts[1])+"\n"+
                            copyWord(id) ));
            Toasty.success(getApplicationContext(), "আয়াতটি শব্দে শন্দে কপি হয়েছে", Toast.LENGTH_SHORT, true).show();
        });

        RecyclerView recycler = (RecyclerView) dialog.findViewById(R.id.wordListview);
        WordAdapter wordAdapter = new WordAdapter(this,dbHelper.getWord(id));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(wordAdapter);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    private String copyWord(String any){
        StringBuilder query = new StringBuilder();
        for (int i = 0; i <  dbHelper.getWord(any).size(); i++) {
            String arabic = dbHelper.getWord(any).get(i).getArabic();
            String bangla = dbHelper.getWord(any).get(i).getBangla();
            query.append("আরাবিক - বাংলা : "+arabic+" - "+bangla).append("\n");
        }
        return query.toString();
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