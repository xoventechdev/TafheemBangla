package com.minbar.tafhimulquran.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Model.VumikaModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.FontFamily;

import java.util.List;

public class VumikaAdapter extends RecyclerView.Adapter<VumikaAdapter.ViewHolder> {
    public List<VumikaModel> categoryListModels;
    public static Context mContext;
    public List<VumikaModel> mDataFiltered;

    public VumikaAdapter(Context context, List<VumikaModel> list) {
        this.mContext = context;
        this.categoryListModels = list;
        this.mDataFiltered = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.vumika_design, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder vw, @SuppressLint("RecyclerView") final int i) {
        VumikaModel v = this.mDataFiltered.get(i);
        //vw.vumika_id.setText(v.getBnID());
        vw.vumika_title.setText(v.getTitle());
        vw.contentV.setText(Html.fromHtml(v.getContent()));
        vw.linearLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (vw.contentV.getVisibility() == View.VISIBLE){
                    vw.contentV.setVisibility(View.GONE);
                    vw.arrowIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }else {
                    vw.contentV.setVisibility(View.VISIBLE);
                    vw.arrowIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                }
            }
        });
    }

    public int getItemCount() {
        return this.mDataFiltered.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView vumika_id, vumika_title, contentV;
        ImageView arrowIcon;

        public ViewHolder(View view) {
            super(view);
            //this.vumika_id = (TextView) view.findViewById(R.id.vumika_id);
            this.vumika_title = (TextView) view.findViewById(R.id.vumika_title);
            this.contentV = (TextView) view.findViewById(R.id.contentV);
            this.arrowIcon = (ImageView) view.findViewById(R.id.arrowIcon);
            this.linearLayout = (LinearLayout) view.findViewById(R.id.list_itemId);

            this.vumika_title.setTypeface(FontFamily.getBangla(mContext));
            this.contentV.setTypeface(FontFamily.getBangla(mContext));



        }
    }
}
