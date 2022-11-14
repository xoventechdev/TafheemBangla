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

import com.minbar.tafhimulquran.Activity.SubjectActivity;
import com.minbar.tafhimulquran.Model.SubModel;
import com.minbar.tafhimulquran.R;

import java.util.List;

public class SubAdapter extends RecyclerView.Adapter<SubAdapter.ViewHolder> {
    public Context mContext;
    public List<SubModel> mDataFiltered;

    public SubAdapter(Context context, List<SubModel> list) {
        this.mContext = context;
        this.mDataFiltered = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.category_design, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder vh, @SuppressLint("RecyclerView") final int i) {
        SubModel sm = this.mDataFiltered.get(i);
        vh.title.setText(sm.getTitle());
        vh.total.setText("মোট আয়াত সংখ্যা " + sm.getTotal());
        vh.number.setText(sm.getNumber());
        vh.linearLayout.setOnClickListener(view -> {
            Intent intent = new Intent(SubAdapter.this.mContext, SubjectActivity.class);
            intent.putExtra("sub_title", sm.getTitle());
            intent.putExtra("subId", String.valueOf(sm.getId()));
            intent.putExtra("sub_sub", vh.total.getText().toString());
            SubAdapter.this.mContext.startActivity(intent);
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
