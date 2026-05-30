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

import java.util.ArrayList;
import java.util.List;

public class PageSurahAdapter extends RecyclerView.Adapter<PageSurahAdapter.ViewHolder> implements Filterable {
    CountDownTimer countDownTimer;
    Intent intent = null;
    public Context mContext;
    public List<SurahModel> mDataFiltered;
    public List<SurahModel> surahModelList;

    public PageSurahAdapter(Context context, List<SurahModel> list) {
        this.mContext = context;
        this.surahModelList = list;
        this.mDataFiltered = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.sen_sub_design, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        SurahModel surahModel = this.mDataFiltered.get(i);


        viewHolder.subNumber.setText(Config.ENtoBN(surahModel.getSura_Number()));
        viewHolder.subName.setText(surahModel.getBangla_Name());
        viewHolder.subAyat.setVisibility(View.GONE);
        viewHolder.subTika.setVisibility(View.GONE);

        viewHolder.senSubView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PageActivity.class);
                intent.putExtra("pageId", surahModel.getSurah_ID());
                //intent.putExtra("surah_Name", surahModel.getBangla_Name());
                //intent.putExtra("ayatCount", surahModel.getSura_Ayat());
                //intent.putExtra("location", surahModel.getSura_BnMean());
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
                    PageSurahAdapter surahAdapter = PageSurahAdapter.this;
                    surahAdapter.mDataFiltered = surahAdapter.surahModelList;
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (SurahModel next : PageSurahAdapter.this.surahModelList) {
                        if (next.getBangla_Name().toLowerCase().contains(charSequence2.toLowerCase())) {
                            arrayList.add(next);
                        }
                    }
                    PageSurahAdapter.this.mDataFiltered = arrayList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = PageSurahAdapter.this.mDataFiltered;
                return filterResults;
            }

            public void publishResults(CharSequence charSequence, FilterResults filterResults) {
                PageSurahAdapter.this.mDataFiltered = (List) filterResults.values;
                PageSurahAdapter.this.notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subNumber,subName, subAyat, subTika;
        LinearLayout senSubView;
        View lastLine;


        public ViewHolder(View view) {
            super(view);
            this.subNumber = (TextView) view.findViewById(R.id.subNumber);
            this.subName = (TextView) view.findViewById(R.id.subName);
            this.subAyat = (TextView) view.findViewById(R.id.subAyat);
            this.subTika = (TextView) view.findViewById(R.id.subTika);
            this.senSubView = (LinearLayout) view.findViewById(R.id.senSubView);
            this.lastLine = (View) view.findViewById(R.id.lastLine);
        }
    }
}
