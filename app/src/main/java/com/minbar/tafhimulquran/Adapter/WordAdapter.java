package com.minbar.tafhimulquran.Adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Activity.VerseActivity;
import com.minbar.tafhimulquran.Model.WordModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;

import java.util.List;

import es.dmoral.toasty.Toasty;

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

    public void onBindViewHolder(ViewHolder vh, @SuppressLint("RecyclerView") final int i) {
        vh.arabic.setText(this.mDataFiltered.get(i).getArabic());
        vh.bangla.setText(this.mDataFiltered.get(i).getBangla());
        vh.wordView.setOnClickListener(v -> {
            Context context = WordAdapter.mContext;
            ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("শব্দে শন্দে তাফহীমুল কুরআন",  "আরাবিক : "+vh.arabic.getText().toString() + "\n"+"বাংলা : " + vh.bangla.getText().toString() + "\n\n"+"তাফহীমুল কুরআন"+"\nhttps://play.google.com/store/apps/details?id=" + WordAdapter.mContext.getPackageName()));
            //Toast.makeText(WordAdapter.mcontext, "This verse has been copied", Toast.LENGTH_SHORT).show();
            Toasty.success(WordAdapter.mContext, "শব্দটি কপি হয়েছে", Toast.LENGTH_SHORT, true).show();
        });

    }

    public int getItemCount() {
        return this.mDataFiltered.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView arabic, bangla;
        LinearLayout wordView;

        public ViewHolder(View view) {
            super(view);
            this.arabic = (TextView) view.findViewById(R.id.word_arabic_textView);
            this.bangla = (TextView) view.findViewById(R.id.word_trans_textView);
            this.wordView = (LinearLayout) view.findViewById(R.id.wordView);

            this.arabic.setTypeface(FontFamily.getArabic(mContext));
            this.bangla.setTypeface(FontFamily.getBangla(mContext));

            this.arabic.setTextSize(2, Float.valueOf(FontSize.getArabic(mContext)));
            this.bangla.setTextSize(2, Float.valueOf(FontSize.getBangla(mContext)));
        }
    }
}
