package com.minbar.tafhimulquran.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Activity.DarsActivity;
import com.minbar.tafhimulquran.Model.DarsModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.FontFamily;

import java.util.List;

public class DarsAdapter extends RecyclerView.Adapter<DarsAdapter.ViewHolder> {
    private final List<DarsModel> mDataFiltered;
    private final Context mContext;

    public DarsAdapter(Context context, List<DarsModel> list) {
        this.mContext = context;
        this.mDataFiltered = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dars_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final DarsModel dars = mDataFiltered.get(position);
        
        holder.title.setText(dars.getTitle());
        holder.total.setText(dars.getAuthor());
        holder.number.setText(dars.getBnID());

        // Apply dynamic fonts based on user settings
        holder.title.setTypeface(FontFamily.getBangla(mContext));
        holder.total.setTypeface(FontFamily.getBangla(mContext));
        holder.number.setTypeface(FontFamily.getBangla(mContext));

        holder.linearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, DarsActivity.class);
            intent.putExtra("id", String.valueOf(dars.getId()));
            intent.putExtra("title", dars.getTitle());
            intent.putExtra("author", dars.getAuthor());
            intent.putExtra("ayat", dars.getAyat());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mDataFiltered.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View linearLayout;
        TextView number;
        TextView title;
        TextView total;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.cat_id);
            title = itemView.findViewById(R.id.cat_title);
            total = itemView.findViewById(R.id.cat_numberr);
            linearLayout = itemView.findViewById(R.id.list_itemId);
        }
    }
}
