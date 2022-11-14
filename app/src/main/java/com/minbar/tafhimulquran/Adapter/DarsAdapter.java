package com.minbar.tafhimulquran.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Activity.DarsActivity;
import com.minbar.tafhimulquran.Model.DarsModel;
import com.minbar.tafhimulquran.R;

import java.util.List;

public class DarsAdapter extends RecyclerView.Adapter<DarsAdapter.ViewHolder> {
    public List<DarsModel> categoryListModels;
    public Context mContext;
    public List<DarsModel> mDataFiltered;

    public DarsAdapter(Context context, List<DarsModel> list) {
        this.mContext = context;
        this.categoryListModels = list;
        this.mDataFiltered = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.dars_design, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        viewHolder.title.setText(this.mDataFiltered.get(i).getTitle());
        viewHolder.total.setText(this.mDataFiltered.get(i).getAuthor());
        viewHolder.number.setText(this.mDataFiltered.get(i).getBnID());
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(DarsAdapter.this.mContext, DarsActivity.class);
                intent.putExtra("id", String.valueOf(DarsAdapter.this.mDataFiltered.get(i).getId()));
                intent.putExtra("title", DarsAdapter.this.mDataFiltered.get(i).getTitle());
                intent.putExtra("author", DarsAdapter.this.mDataFiltered.get(i).getAuthor());
                intent.putExtra("ayat", DarsAdapter.this.mDataFiltered.get(i).getAyat());
                DarsAdapter.this.mContext.startActivity(intent);
            }
        });
    }

    public int getItemCount() {
        return this.mDataFiltered.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView number;
        TextView title;
        TextView total;

        public ViewHolder(View view) {
            super(view);
            this.number = (TextView) view.findViewById(R.id.cat_id);
            this.title = (TextView) view.findViewById(R.id.cat_title);
            this.total = (TextView) view.findViewById(R.id.cat_numberr);
            this.linearLayout = (LinearLayout) view.findViewById(R.id.list_itemId);
        }
    }
}
