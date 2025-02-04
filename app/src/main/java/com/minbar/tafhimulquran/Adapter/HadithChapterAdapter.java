package com.minbar.tafhimulquran.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Hadith.HadithListActivity;
import com.minbar.tafhimulquran.Model.HadithChapter;
import com.minbar.tafhimulquran.R;

import java.util.ArrayList;
import java.util.List;

public class HadithChapterAdapter extends RecyclerView.Adapter<HadithChapterAdapter.ViewHolder> {
    private List<HadithChapter> chapterList;
    private List<HadithChapter> filteredChapterList;
    private Context context;

    public HadithChapterAdapter(Context context, List<HadithChapter> chapterList) {
        this.context = context;
        this.chapterList = chapterList;
        this.filteredChapterList = new ArrayList<>(chapterList); // Initialize filtered list
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hadith_chapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HadithChapter chapter = filteredChapterList.get(position);
        holder.chapterTitle.setText(chapter.getTitle());
        holder.chapterNumber.setText("হাদিসের রেঞ্জ: "+ chapter.getHadisRange());
        holder.number.setText(chapter.getNumber());
        holder.hadithLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, HadithListActivity.class);
            intent.putExtra("chapterId", Integer.valueOf(chapter.getChapterId()));
            intent.putExtra("chapterName", chapter.getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredChapterList.size();
    }

    public void filter(String query) {
        filteredChapterList.clear();
        if (query.isEmpty()) {
            filteredChapterList.addAll(chapterList);
        } else {
            for (HadithChapter chapter : chapterList) {
                if (chapter.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        String.valueOf(chapter.getNumber()).contains(query)) {
                    filteredChapterList.add(chapter);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView chapterTitle;
        TextView chapterNumber;
        TextView number;
        ConstraintLayout hadithLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chapterTitle = itemView.findViewById(R.id.tv_title);
            chapterNumber = itemView.findViewById(R.id.tv_subtitle);
            number = itemView.findViewById(R.id.number);
            hadithLayout = itemView.findViewById(R.id.hadithChapterLayout);
        }
    }

}