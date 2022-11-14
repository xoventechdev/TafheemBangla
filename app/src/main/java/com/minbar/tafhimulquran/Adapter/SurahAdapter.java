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

import com.minbar.tafhimulquran.Activity.VerseActivity;
import com.minbar.tafhimulquran.Model.SurahModel;
import com.minbar.tafhimulquran.R;

import java.util.ArrayList;
import java.util.List;

public class SurahAdapter extends RecyclerView.Adapter<SurahAdapter.ViewHolder> implements Filterable {
    CountDownTimer countDownTimer;
    Intent intent = null;
    public Context mContext;
    public List<SurahModel> mDataFiltered;
    public List<SurahModel> surahModelList;

    public SurahAdapter(Context context, List<SurahModel> list) {
        this.mContext = context;
        this.surahModelList = list;
        this.mDataFiltered = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.surah_row_new, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        SurahModel surahModel = this.mDataFiltered.get(i);
        viewHolder.Number.setText(surahModel.getSura_Number());
        viewHolder.BnName.setText(surahModel.getBangla_Name());
        viewHolder.AbName.setText(surahModel.getArabc_Name());
        viewHolder.Ayat.setText(surahModel.getSura_Ayat());
        viewHolder.Mean.setText(surahModel.getSura_BnMean());
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(mContext, VerseActivity.class);
                intent.putExtra("surah_id",String.valueOf( surahModel.getSurah_ID()));
                intent.putExtra("surah_Name", surahModel.getBangla_Name());
                intent.putExtra("ayatCount", surahModel.getSura_Ayat());
                intent.putExtra("location", surahModel.getSura_BnMean());
                mContext.startActivity(intent);
            }
        });
    }

    public int getItemCount() {
        return this.mDataFiltered.size();
    }

    public Filter getFilter() {
        return new Filter() {
            public FilterResults performFiltering(CharSequence charSequence) {
                String charSequence2 = charSequence.toString();
                if (charSequence2.isEmpty()) {
                    SurahAdapter surahAdapter = SurahAdapter.this;
                    surahAdapter.mDataFiltered = surahAdapter.surahModelList;
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (SurahModel next : SurahAdapter.this.surahModelList) {
                        if (next.getBangla_Name().toLowerCase().contains(charSequence2.toLowerCase())) {
                            arrayList.add(next);
                        }
                    }
                    SurahAdapter.this.mDataFiltered = arrayList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = SurahAdapter.this.mDataFiltered;
                return filterResults;
            }

            public void publishResults(CharSequence charSequence, FilterResults filterResults) {
                SurahAdapter.this.mDataFiltered = (List) filterResults.values;
                SurahAdapter.this.notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView AbName;
        TextView Ayat;
        TextView BnName;
        TextView Mean;
        TextView Number;
        LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            this.Number = (TextView) view.findViewById(R.id.suraNo);
            this.BnName = (TextView) view.findViewById(R.id.suraName);
            this.AbName = (TextView) view.findViewById(R.id.name_arabic);
            this.Ayat = (TextView) view.findViewById(R.id.ayah_count);
            this.Mean = (TextView) view.findViewById(R.id.name_meaning);
            this.linearLayout = (LinearLayout) view.findViewById(R.id.layoutId);
        }
    }
}
