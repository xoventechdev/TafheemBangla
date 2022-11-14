package com.minbar.tafhimulquran.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Activity.AudioActivity;
import com.minbar.tafhimulquran.Model.SurahModel;
import com.minbar.tafhimulquran.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import nl.changer.audiowife.AudioWife;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> implements Filterable {
    CountDownTimer countDownTimer;
    Intent intent = null;
    public Context mContext;
    public List<SurahModel> mDataFiltered;
    public List<SurahModel> surahModelList;
    AudioWife audioWife;

    public AudioAdapter(Context context, List<SurahModel> list) {
        this.mContext = context;
        this.surahModelList = list;
        this.mDataFiltered = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.audio_row, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder vm, @SuppressLint("RecyclerView") final int i) {
        SurahModel surahModel = this.mDataFiltered.get(i);
        vm.suraNo.setText(surahModel.getSura_Number());
        vm.suraName.setText(surahModel.getBangla_Name());
        vm.name_meaning.setText(surahModel.getSura_BnMean());
        vm.bt_d.setColorFilter(ContextCompat.getColor(mContext,
                R.color.common), android.graphics.PorterDuff.Mode.SRC_IN);
        vm.bt_play.setColorFilter(ContextCompat.getColor(mContext,
                R.color.common), android.graphics.PorterDuff.Mode.SRC_IN);


        //String playID = String.format("%03d",surahModel.getSurah_ID());
        //String filrName = playID+".mp3";
        File filePath = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + this.mDataFiltered.get(i).getSurah_ID()+".mp3");

        vm.bt_d.setOnClickListener(v -> {
            filePath.delete();
            vm.bt_d.setVisibility(View.INVISIBLE);
            vm.bt_play.setVisibility(View.GONE);
            vm.bt_down.setVisibility(View.VISIBLE);
        });

        if (new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + this.mDataFiltered.get(i).getSurah_ID()+".mp3").exists()){
            vm.bt_d.setVisibility(View.VISIBLE);
            vm.bt_play.setVisibility(View.VISIBLE);
            vm.bt_down.setVisibility(View.GONE);
            //vm.bt_dp.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
        }

        vm.bt_play.setOnClickListener(v -> {
            Toasty.success(mContext, String.valueOf(filePath), Toasty.LENGTH_LONG).show();
            ((AudioActivity) v.getContext()).playAudio(this.mDataFiltered.get(i).getSurah_ID()+"@"+surahModel.getBangla_Name());
        });
        vm.bt_down.setOnClickListener(v -> {
            //Toasty.success(mContext, String.valueOf(filePath), Toasty.LENGTH_LONG).show();
            ((AudioActivity) v.getContext()).showdOWNLOAD(this.mDataFiltered.get(i).getSurah_ID()+"@"+surahModel.getBangla_Name());

        });


        vm.linearLayout.setOnClickListener(view -> {
            Toasty.success(mContext,String.valueOf(filePath),Toasty.LENGTH_SHORT).show();
            /*
            Intent intent = new Intent(mContext, VerseActivity.class);
            intent.putExtra("surah_id",String.valueOf( surahModel.getSurah_ID()));
            intent.putExtra("surah_Name", surahModel.getBangla_Name());
            intent.putExtra("ayatCount", surahModel.getSura_Ayat());
            intent.putExtra("location", surahModel.getSura_BnMean());
            mContext.startActivity(intent);

             */
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
                    AudioAdapter surahAdapter = AudioAdapter.this;
                    surahAdapter.mDataFiltered = surahAdapter.surahModelList;
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (SurahModel next : AudioAdapter.this.surahModelList) {
                        if (next.getBangla_Name().toLowerCase().contains(charSequence2.toLowerCase())) {
                            arrayList.add(next);
                        }
                    }
                    AudioAdapter.this.mDataFiltered = arrayList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = AudioAdapter.this.mDataFiltered;
                return filterResults;
            }

            public void publishResults(CharSequence charSequence, FilterResults filterResults) {
                AudioAdapter.this.mDataFiltered = (List) filterResults.values;
                AudioAdapter.this.notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView suraNo, suraName, name_meaning;
        ImageView bt_down, bt_play,bt_d;
        LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            this.suraNo = (TextView) view.findViewById(R.id.suraNo);
            this.suraName = (TextView) view.findViewById(R.id.suraName);
            this.name_meaning = (TextView) view.findViewById(R.id.name_meaning);
            this.bt_down = (ImageView) view.findViewById(R.id.bt_down);
            this.bt_play = (ImageView) view.findViewById(R.id.bt_play);
            this.bt_d = (ImageView) view.findViewById(R.id.bt_d);
            this.linearLayout = (LinearLayout) view.findViewById(R.id.layoutId);
        }
    }
}
