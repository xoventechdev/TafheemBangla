package com.minbar.tafhimulquran.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Activity.SentenceActivity;
import com.minbar.tafhimulquran.Model.CharacterSubModel;
import com.minbar.tafhimulquran.R;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class CharacterSubAdapter extends RecyclerView.Adapter<CharacterSubAdapter.ViewHolder>  {

    public Context mContext;
    public List<CharacterSubModel> mDataFiltered;

    public CharacterSubAdapter(Context context, List<CharacterSubModel> list) {
        this.mContext = context;
        this.mDataFiltered = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.chara1_design, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder vm, @SuppressLint("RecyclerView") final int i) {
        CharacterSubModel cm = this.mDataFiltered.get(i);
        vm.word_title.setText(cm.getItem());

        if (i == this.mDataFiltered.size() - 1) {
            vm.lastLine.setVisibility(View.GONE);
        } else {
            vm.lastLine.setVisibility(View.VISIBLE);
        }

        vm.mainV.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, SentenceActivity.class);
            intent.putExtra("ch_wd",String.valueOf(cm.getCh_id()+"="+cm.getWd_id()));
            intent.putExtra("title",cm.getItem());
            mContext.startActivity(intent);
        });
    }

    public int getItemCount() {
        return this.mDataFiltered.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView word_title;
        LinearLayout mainV;
        View lastLine;

        public ViewHolder(View view) {
            super(view);
            this.word_title = (TextView) view.findViewById(R.id.word_title);
            this.mainV = (LinearLayout) view.findViewById(R.id.list_itemId);
            this.lastLine = (View) view.findViewById(R.id.lastLine);
        }
    }
}
