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

import com.minbar.tafhimulquran.Activity.SentenceActivity;
import com.minbar.tafhimulquran.Activity.StarkActivity;
import com.minbar.tafhimulquran.Model.SenSubModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;

import java.util.List;
import java.util.Objects;

public class SenSubAdapter extends RecyclerView.Adapter<SenSubAdapter.ViewHolder>  {

    public Context mContext;
    public List<SenSubModel> mDataFiltered;

    public SenSubAdapter(Context context, List<SenSubModel> list) {
        this.mContext = context;
        this.mDataFiltered = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.sen_sub_design, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder vm, @SuppressLint("RecyclerView") final int i) {
        SenSubModel cm = this.mDataFiltered.get(i);
        vm.subNumber.setText(Config.ENtoBN(String.valueOf(i+1)));
        vm.subName.setText(Config.ENtoBN(cm.getIdSurah())+" নং সুরাহ "+new SqlLiteDbHelper(mContext).getSurahName(Integer.parseInt(cm.getIdSurah())));
        vm.subAyat.setText("আয়াত নং: "+Config.ENtoBN(cm.getIdAyat()));
        if(cm.getIdTika() == null){
            vm.subTika.setText("টিকা নং: তথ্য নেই");
        }else {
            vm.subTika.setText("টিকা নং: "+Config.ENtoBN(cm.getIdTika()));
        }


        if (i== this.mDataFiltered.size()){
            vm.lastLine.setVisibility(View.GONE);
        }
        vm.senSubView.setOnClickListener(view -> {
            if(Objects.equals(cm.getIdAyat(), "0")){
                ((SentenceActivity) view.getContext()).aboutShow(Integer.parseInt(cm.getIdSurah()));
                //Toasty.success(mContext,"Coming Soon" , Toasty.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(mContext, StarkActivity.class);
                intent.putExtra("idSurah", cm.getIdSurah());
                intent.putExtra("idAyat", cm.getIdAyat());
                mContext.startActivity(intent);
            }

        });
    }

    public int getItemCount() {
        return this.mDataFiltered.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subNumber,subName, subAyat, subTika;
        LinearLayout senSubView, lastLine;

        public ViewHolder(View view) {
            super(view);
            this.subNumber = (TextView) view.findViewById(R.id.subNumber);
            this.subName = (TextView) view.findViewById(R.id.subName);
            this.subAyat = (TextView) view.findViewById(R.id.subAyat);
            this.subTika = (TextView) view.findViewById(R.id.subTika);
            this.senSubView = (LinearLayout) view.findViewById(R.id.senSubView);
            this.lastLine = (LinearLayout) view.findViewById(R.id.lastLine);
        }
    }
}
