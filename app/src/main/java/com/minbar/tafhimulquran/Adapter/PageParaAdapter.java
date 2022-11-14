package com.minbar.tafhimulquran.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Activity.PageActivity;
import com.minbar.tafhimulquran.Model.SurahModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;

import java.util.ArrayList;
import java.util.List;

public class PageParaAdapter extends RecyclerView.Adapter<PageParaAdapter.ViewHolder> {
    CountDownTimer countDownTimer;
    Intent intent = null;
    public Context mContext;




    public PageParaAdapter(Context context) {
        this.mContext = context;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.para_design, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {




        viewHolder.subNumber.setText(Config.ENtoBN(String.valueOf(i+1)));


        SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(mContext);
        String ds = dbHelper.goParaNumber(i+1);

        viewHolder.senSubView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, PageActivity.class);
            intent.putExtra("pageId", 0);
            intent.putExtra("paraId", Integer.parseInt(ds));
            mContext.startActivity(intent);
        });
    }

    public int getItemCount() {
        return 30;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subNumber;
        LinearLayout senSubView, lastLine;


        public ViewHolder(View view) {
            super(view);
            this.subNumber = (TextView) view.findViewById(R.id.subNumber);
            this.senSubView = (LinearLayout) view.findViewById(R.id.senSubView);
            this.lastLine = (LinearLayout) view.findViewById(R.id.lastLine);
        }
    }
}
