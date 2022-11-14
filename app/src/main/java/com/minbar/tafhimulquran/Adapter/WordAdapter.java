package com.minbar.tafhimulquran.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Model.WordModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {
    public List<WordModel> categoryListModels;
    public static Context mContext;
    public List<WordModel> mDataFiltered;

    public WordAdapter(Context context, List<WordModel> list) {
        this.mContext = context;
        this.categoryListModels = list;
        this.mDataFiltered = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.word_by_word, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        viewHolder.arabic.setText(this.mDataFiltered.get(i).getArabic());
        viewHolder.bangla.setText(this.mDataFiltered.get(i).getBangla());

    }

    public int getItemCount() {
        return this.mDataFiltered.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView arabic, bangla;

        public ViewHolder(View view) {
            super(view);
            this.arabic = (TextView) view.findViewById(R.id.word_arabic_textView);
            this.bangla = (TextView) view.findViewById(R.id.word_trans_textView);

            this.arabic.setTypeface(FontFamily.getArabic(mContext));
            this.bangla.setTypeface(FontFamily.getBangla(mContext));

            this.arabic.setTextSize(2, Float.valueOf(FontSize.getArabic(mContext)));
            this.bangla.setTextSize(2, Float.valueOf(FontSize.getBangla(mContext)));
        }
    }
}
