package com.minbar.tafhimulquran.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Activity.VerseActivity;
import com.minbar.tafhimulquran.Hadith.HadithListActivity;
import com.minbar.tafhimulquran.Model.HadithListModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class HadithListAdapter extends RecyclerView.Adapter<HadithListAdapter.ViewHolder> {
    private List<HadithListModel> hadithList;
    static Context context;

    public HadithListAdapter(Context context, List<HadithListModel> hadithList) {
        this.context = context;
        this.hadithList = hadithList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hadith, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HadithListModel hadith = hadithList.get(position);
        holder.tvArabicHadith.setText(hadith.getArabicText());
        holder.tvBanglaHadith.setText(hadith.getBanglaText());
        holder.tvGrade.setText(hadith.getGrade());
        holder.hadithNumber.setText(Config.getStringInBangla(String.valueOf(hadith.getId())));


        holder.hadithFav.setOnClickListener(v -> {
//            boolean favStatuss = xovenHandler.checkFav(ID);
//            if (favStatuss){
//                xovenHandler.deleteFav(ID);
//                mvh.fav.setImageResource(R.drawable.ic_baseline_favorite_border_24);
//            }else {
//                xovenHandler.addFav(ID);
//                mvh.fav.setImageResource(R.drawable.ic_baseline_favorite_24);
//            }
        });

        holder.hadithShare.setOnClickListener(v -> {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.TEXT", "রিয়াদুস সালেহীন : "+holder.hadithNumber.getText().toString()+"\n" + holder.tvArabicHadith.getText().toString() + "\n" + holder.tvBanglaHadith.getText().toString()+ "\n\n"+"\nhttps://play.google.com/store/apps/details?id=" + HadithListAdapter.context.getPackageName());
            HadithListAdapter.context.startActivity(Intent.createChooser(intent, "হাদিসটি শেয়ার করুন"));
        });

        holder.hadithCopy.setOnClickListener(v -> {
            Context context = HadithListAdapter.context;
            ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("হাদিস", "রিয়াদুস সালেহীন : "+holder.hadithNumber.getText().toString()+"\n" + holder.tvArabicHadith.getText().toString() + "\n" + holder.tvBanglaHadith.getText().toString()+ "\n\n"+"\nhttps://play.google.com/store/apps/details?id=" + HadithListAdapter.context.getPackageName()));
            //Toast.makeText(VerseAdapter.mcontext, "This verse has been copied", Toast.LENGTH_SHORT).show();
            Toasty.success(HadithListAdapter.context, "হাদিসটি কপি হয়েছে", Toast.LENGTH_SHORT, true).show();

        });

    }

    @Override
    public int getItemCount() {
        return hadithList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvArabicHadith, tvBanglaHadith, tvNarrator, tvGrade, hadithNumber;
        ImageView hadithFav, hadithShare, hadithCopy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvArabicHadith = itemView.findViewById(R.id.tv_arabic_hadith);
            tvBanglaHadith = itemView.findViewById(R.id.tv_bangla_hadith);
            tvNarrator = itemView.findViewById(R.id.tv_narrator);
            tvGrade = itemView.findViewById(R.id.tv_grade);
            hadithNumber = itemView.findViewById(R.id.hadithNumber);


            hadithFav = itemView.findViewById(R.id.hadithFav);
            hadithShare = itemView.findViewById(R.id.hadithShare);
            hadithCopy = itemView.findViewById(R.id.hadithCopy);






            this.tvArabicHadith.setTypeface(FontFamily.getArabic(context));
            this.tvBanglaHadith.setTypeface(FontFamily.getBangla(context));

            this.tvArabicHadith.setTextSize(2, Float.valueOf(FontSize.getArabic(context)));
            this.tvBanglaHadith.setTextSize(2, Float.valueOf(FontSize.getBangla(context)));

        }
    }
}
