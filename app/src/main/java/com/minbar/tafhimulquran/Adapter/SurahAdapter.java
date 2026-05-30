package com.minbar.tafhimulquran.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Activity.VerseActivity;
import com.minbar.tafhimulquran.Model.SurahModel;
import com.minbar.tafhimulquran.R;

import java.util.ArrayList;
import java.util.List;

public class SurahAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public Context mContext;
    public List<SurahModel> mDataFiltered;
    public List<SurahModel> surahModelList;
    private OnHeaderBoundListener headerBoundListener;
    private boolean hasHeader = true;

    public interface OnHeaderBoundListener {
        void onHeaderBound(View headerView);
    }

    public SurahAdapter(Context context, List<SurahModel> list, OnHeaderBoundListener listener) {
        this.mContext = context;
        this.surahModelList = list;
        this.mDataFiltered = list;
        this.headerBoundListener = listener;
        this.hasHeader = true;
    }

    public SurahAdapter(Context context, List<SurahModel> list) {
        this.mContext = context;
        this.surahModelList = list;
        this.mDataFiltered = list;
        this.hasHeader = false;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasHeader && position == 0) return TYPE_HEADER;
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_home_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.surah_row_new, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            if (headerBoundListener != null) {
                headerBoundListener.onHeaderBound(holder.itemView);
            }
        } else if (holder instanceof ItemViewHolder) {
            int dataPosition = hasHeader ? position - 1 : position;
            final SurahModel surahModel = mDataFiltered.get(dataPosition);
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.Number.setText(surahModel.getSura_Number());
            itemHolder.BnName.setText(surahModel.getBangla_Name());
            itemHolder.AbName.setText(surahModel.getArabc_Name());
            itemHolder.Ayat.setText(surahModel.getSura_Ayat());
            itemHolder.Mean.setText(surahModel.getSura_BnMean());
            itemHolder.linearLayout.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, VerseActivity.class);
                intent.putExtra("surah_id", String.valueOf(surahModel.getSurah_ID()));
                intent.putExtra("surah_Name", surahModel.getBangla_Name());
                intent.putExtra("ayatCount", surahModel.getSura_Ayat());
                intent.putExtra("location", surahModel.getSura_BnMean());
                mContext.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataFiltered.size() + (hasHeader ? 1 : 0);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mDataFiltered = surahModelList;
                } else {
                    List<SurahModel> filteredList = new ArrayList<>();
                    for (SurahModel row : surahModelList) {
                        if (row.getBangla_Name().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    mDataFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mDataFiltered;
                return filterResults;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataFiltered = (ArrayList<SurahModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView AbName, Ayat, BnName, Mean, Number;
        LinearLayout linearLayout;

        public ItemViewHolder(View view) {
            super(view);
            Number = view.findViewById(R.id.suraNo);
            BnName = view.findViewById(R.id.suraName);
            AbName = view.findViewById(R.id.name_arabic);
            Ayat = view.findViewById(R.id.ayah_count);
            Mean = view.findViewById(R.id.name_meaning);
            linearLayout = view.findViewById(R.id.layoutId);
        }
    }
}
