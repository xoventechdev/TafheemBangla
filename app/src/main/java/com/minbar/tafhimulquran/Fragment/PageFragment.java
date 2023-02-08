package com.minbar.tafhimulquran.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.minbar.tafhimulquran.Adapter.MapsAdapter;
import com.minbar.tafhimulquran.Adapter.PageVerseAdapter;
import com.minbar.tafhimulquran.Model.PageVerseModal;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;

import java.util.ArrayList;


public class PageFragment extends Fragment {

    public static final String idPage = "1";
    SqlLiteDbHelper dbHelper;
    ArrayList<PageVerseModal> pageVerseModals;
    int surahID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new SqlLiteDbHelper(getActivity());
        String pageV = getArguments().getString(idPage);


        pageVerseModals= dbHelper.getPageVerse(Integer.parseInt(pageV));
/*
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView recyclerView2 = (RecyclerView) view.findViewById(R.id.pageView);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PageVerseAdapter(getActivity(), this.pageVerseModals);
        recyclerView2.setAdapter(this.adapter);



 */
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < pageVerseModals.size(); i++) {
            String content = null;
            String verseId = null;
            String forOneNine;

            if (pageVerseModals.get(i).getSurahID() == 9){
                content = pageVerseModals.get(i).getContent();
                verseId = String.valueOf(pageVerseModals.get(i).getVerseId());
                query.append(content).append("("+ Config.ENtoBN(verseId)+")");
//                if (pageVerseModals.get(i).getVerseId()==1){
//                    content = pageVerseModals.get(i).getContent();
//                    verseId = String.valueOf(pageVerseModals.get(i).getVerseId());
//                    forOneNine = "<br>"+dbHelper.getSurahName(pageVerseModals.get(i).getSurahID())+"<br>";
//                    query.append(forOneNine).append(content).append("(").append(Config.ENtoBN(verseId)).append(")");
//                }else {
//                    content = pageVerseModals.get(i).getContent();
//                    verseId = String.valueOf(pageVerseModals.get(i).getVerseId());
//                    query.append(content).append("("+ Config.ENtoBN(verseId)+")");
//                }
            }else {
                if (pageVerseModals.get(i).getVerseId()==0){
                    content = "<br>"+dbHelper.getSurahName(pageVerseModals.get(i).getSurahID());
                    verseId = "<br>";
                    surahID = pageVerseModals.get(i).getSurahID();
                    query.append(content).append(verseId);
                }else {
                    content = pageVerseModals.get(i).getContent();
                    verseId = String.valueOf(pageVerseModals.get(i).getVerseId());
                    query.append(content).append("("+ Config.ENtoBN(verseId)+")");
                }
            }
        }
        String arabicTxt = query.toString();

        TextView arabicView = (TextView) view.findViewById(R.id.txtVerse);
//        arabicView.setText(Html.fromHtml(arabicTxt));
        arabicView.setText(Html.fromHtml(arabicTxt));
        arabicView.setTypeface(FontFamily.getArabic(getActivity()));
        arabicView.setTextSize(2, Float.valueOf(FontSize.getArabic(getActivity())));




    }





}