package com.minbar.tafhimulquran.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Model.MapsModel;
import com.minbar.tafhimulquran.R;

import java.util.List;

public class MapsAdapter extends RecyclerView.Adapter<MapsAdapter.ViewHolder> {
    public List<MapsModel> categoryListModels;
    public Context mContext;
    public List<MapsModel> mDataFiltered;

    public MapsAdapter(Context context, List<MapsModel> list) {
        this.mContext = context;
        this.categoryListModels = list;
        this.mDataFiltered = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.maps_design, viewGroup, false));
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void onBindViewHolder(ViewHolder vw, @SuppressLint("RecyclerView") final int i) {
        MapsModel v = this.mDataFiltered.get(i);
        //vw.vumika_id.setText(v.getBnID());
        vw.vumika_title.setText(v.getTitle());

        vw.webView.getSettings().setJavaScriptEnabled(true);
        vw.webView.getSettings().setUseWideViewPort(true);
        vw.webView.getSettings().setDomStorageEnabled(true);
        vw.webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        vw.webView.getSettings().setLoadWithOverviewMode(true);

        String mStringUrl = "file:///android_asset/"+v.getId()+".jpg";
        vw.webView.loadUrl(mStringUrl);

        //String mStringUrl = "file:///android_asset/"+v.getId()+".jpg";
        //vw.webView.loadDataWithBaseURL(null, "<html><head><style>img {margin-top:auto;margin-bottom:auto}</style></head><body><img src=\"" + mStringUrl + "\"></body></html>", "html/css", "utf-8", null);


        vw.refMaps.setText("সংশ্লিষ্ট আয়াত :\n"+v.getRef());


        vw.webView.setOnClickListener(v1 -> {
            if (vw.webMain.getVisibility() == View.VISIBLE){
                vw.webMain.setVisibility(View.GONE);
                vw.arrowIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
            }else {
                vw.webMain.setVisibility(View.VISIBLE);
                vw.arrowIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
            }
        });

        vw.linearLayout.setOnClickListener(view -> {
            if (vw.webMain.getVisibility() == View.VISIBLE){
                vw.webMain.setVisibility(View.GONE);
                vw.arrowIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
            }else {
                vw.webMain.setVisibility(View.VISIBLE);
                vw.arrowIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
            }
        });
    }

    public int getItemCount() {
        return this.mDataFiltered.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView vumika_id, vumika_title, contentV, refMaps;
        ImageView arrowIcon;
        WebView webView;
        RelativeLayout webMain;

        public ViewHolder(View view) {
            super(view);
            //this.vumika_id = (TextView) view.findViewById(R.id.vumika_id);
            this.vumika_title = (TextView) view.findViewById(R.id.vumika_title);
            this.contentV = (TextView) view.findViewById(R.id.contentV);
            this.refMaps = (TextView) view.findViewById(R.id.refMaps);
            this.arrowIcon = (ImageView) view.findViewById(R.id.arrowIcon);
            this.linearLayout = (LinearLayout) view.findViewById(R.id.list_itemId);
            this.webMain = (RelativeLayout) view.findViewById(R.id.webMain);

            this.webView = (WebView) view.findViewById(R.id.webView);
        }
    }
}
