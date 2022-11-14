package com.minbar.tafhimulquran.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Model.CharacterModel;
import com.minbar.tafhimulquran.Model.SenModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class SenAdapter extends RecyclerView.Adapter<SenAdapter.ViewHolder>  {

    public Context mContext;
    public List<SenModel> mDataFiltered;

    public SenAdapter(Context context, List<SenModel> list) {
        this.mContext = context;
        this.mDataFiltered = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.chara_design, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder vm, @SuppressLint("RecyclerView") final int i) {
        SenModel cm = this.mDataFiltered.get(i);
        vm.title.setText(cm.getItem());
        vm.title.setTextSize(2,21.0f);

        SenSubAdapter adapter = new SenSubAdapter( mContext,new SqlLiteDbHelper(mContext).getSenSub(cm.getCh_id()+"="+cm.getWd_id()+"="+cm.getSn_id()));
        vm.viewCharaWord.setLayoutManager(new LinearLayoutManager(mContext));
        vm.viewCharaWord.setHasFixedSize(true);
        vm.viewCharaWord.setAdapter(adapter);

        vm.mainV.setOnClickListener(view -> {

            //Toasty.success(mContext,cm.getCh_id()+"="+cm.getWd_id()+"="+cm.getSn_id() , Toasty.LENGTH_SHORT).show();
            if (vm.viewCharaWord.getVisibility() == View.VISIBLE){
                vm.viewCharaWord.setVisibility(View.GONE);
                vm.arrowIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
            }else {
                vm.viewCharaWord.setVisibility(View.VISIBLE);
                vm.arrowIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
            }
        });
    }

    public int getItemCount() {
        return this.mDataFiltered.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView arrowIcon;
        LinearLayout mainV;
        RecyclerView viewCharaWord;

        public ViewHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.vumika_title);
            this.arrowIcon = (ImageView) view.findViewById(R.id.arrowIcon);
            this.viewCharaWord = (RecyclerView) view.findViewById(R.id.viewCharaWord);
            this.mainV = (LinearLayout) view.findViewById(R.id.list_itemId);
        }
    }
}
