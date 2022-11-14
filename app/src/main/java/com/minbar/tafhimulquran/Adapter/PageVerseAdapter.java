package com.minbar.tafhimulquran.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Model.PageVerseModal;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;

import java.util.List;

public class PageVerseAdapter extends RecyclerView.Adapter<PageVerseAdapter.ViewHolder> {

    public static Context mContext;
    public List<PageVerseModal> mDataFiltered;

    public PageVerseAdapter(Context context, List<PageVerseModal> list) {
        this.mContext = context;
        this.mDataFiltered = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.page_ltem, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        viewHolder.txtVerse.setText(this.mDataFiltered.get(i).getContent());
        viewHolder.txtVerseId.setText(String.valueOf(this.mDataFiltered.get(i).getVerseId()));

    }

    public int getItemCount() {
        return this.mDataFiltered.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtVerse, txtVerseId;

        public ViewHolder(View view) {
            super(view);
            this.txtVerse = (TextView) view.findViewById(R.id.txtVerse);
            this.txtVerseId = (TextView) view.findViewById(R.id.txtVerseId);

            //this.arabic.setTypeface(FontFamily.getArabic(mContext));
            //this.bangla.setTypeface(FontFamily.getBangla(mContext));

            //this.arabic.setTextSize(2, Float.valueOf(FontSize.getArabic(mContext)));
            //this.bangla.setTextSize(2, Float.valueOf(FontSize.getBangla(mContext)));
        }
    }
}
