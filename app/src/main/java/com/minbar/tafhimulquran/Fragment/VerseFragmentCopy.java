package com.minbar.tafhimulquran.Fragment;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.minbar.tafhimulquran.Model.VerseModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;
import com.minbar.tafhimulquran.Utils.PronunciationUtils;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.fezilalilDatabaseHelper;
import com.minbar.tafhimulquran.Utils.tafheemEnglishDatabaseHelper;

import java.util.ArrayList;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;


public class VerseFragmentCopy extends Fragment {


    public static final String surahidF = "1";
    public static final String verseidF = "2";
    public static final String contentAr = "1";
    public static final String contentTr = "1";
    public static final String contentBn = "1";



    int idSurah;
    int idVerse;
    String arContent = "1122=@11";
    String trContent = "1122@=@11";
    String bnContent = "1122@=@11";
    SqlLiteDbHelper dbHelper;

    ArrayList<VerseModel> verseModels;

    TextView arabic;
    TextView trans;
    TextView banglaAyat;
    TextView tafheem, bayaan, fezilalil, tafheemEnglish;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verse, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new SqlLiteDbHelper(getActivity());




        ((NestedScrollView) view.findViewById(R.id.fragmentOut)).setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        ((LinearLayout) view.findViewById(R.id.arabicLayout)).setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


        idSurah = Integer.parseInt(getArguments().getString(surahidF));
        idVerse = Integer.parseInt(getArguments().getString(verseidF));
        //arContent = getArguments().getString(contentAr);
        //trContent = getArguments().getString(jcontent);
        //bnContent = getArguments().getString(jcontent);


        idVerse = idVerse+1;
        String[] part = arContent.split("=");

        String ssvv = idSurah+"="+idVerse;
        //String asas = dbHelper.getTestContent(ssvv);

        arabic = view.findViewById(R.id.arabic);
        trans = view.findViewById(R.id.trans);
        banglaAyat = view.findViewById(R.id.banglaAyat);
        tafheem = view.findViewById(R.id.tafheem);
        bayaan = view.findViewById(R.id.bayaan);
        fezilalil = view.findViewById(R.id.fezilalil);
        tafheemEnglish = view.findViewById(R.id.tafheemEnglish);


        Config.BanglaOnubadh(view.findViewById(R.id.VerseTai), getActivity());



        SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (setting.getString("tafheem", "on").equals("off")) {
            trans.setVisibility(View.GONE);
        }

        String s = idSurah+"="+idVerse;


        if (idSurah==9){
            ((LinearLayout) view.findViewById(R.id.bishmillah)).setVisibility(View.GONE);
//            if(idVerse==1){
//                ((LinearLayout) view.findViewById(R.id.bishmillah)).setVisibility(View.GONE);
//            }
        }



//        String getBangla = dbHelper.getTestBangla(s).replace("০","<font color='#E91E63'><sup>০</sup></font>").replace("১","<font color='#E91E63'><sup>১</sup></font>").replace("২","<font color='#E91E63'><sup>২</sup></font>").replace("৩","<font color='#E91E63'><sup>৩</sup></font>").replace("৪","<font color='#E91E63'><sup>৪</sup></font>").replace("৫","<font color='#E91E63'><sup>৫</sup></font>").replace("৬","<font color='#E91E63'><sup>৬</sup></font>").replace("৭","<font color='#E91E63'><sup>৭</sup></font>").replace("৮","<font color='#E91E63'><sup>৮</sup></font>").replace("৯","<font color='#E91E63'><sup>৯</sup></font>");


        arabic.setText(dbHelper.getTestArabic(s));

        // Set Arabic pronunciation visibility
        if (PronunciationUtils.isArabicPronunciationVisible(getActivity())) {
            trans.setText(dbHelper.getTestTras(s));
            trans.setVisibility(View.VISIBLE);
        } else {
            trans.setText("");
            trans.setVisibility(View.GONE);
        }

        banglaAyat.setText(Html.fromHtml(Config.TagColor(dbHelper.getTestBangla(s))));


        arabic.setTypeface(FontFamily.getArabic(getActivity()));
        trans.setTypeface(FontFamily.getBangla(getActivity()));
        banglaAyat.setTypeface(FontFamily.getBangla(getActivity()));
        tafheem.setTypeface(FontFamily.getBangla(getActivity()));
        bayaan.setTypeface(FontFamily.getBangla(getActivity()));
        fezilalil.setTypeface(FontFamily.getBangla(getActivity()));
        tafheemEnglish.setTypeface(FontFamily.getBangla(getActivity()));

        arabic.setTextSize(2, Float.parseFloat(FontSize.getArabic(getActivity())));
        trans.setTextSize(2, Float.parseFloat(FontSize.getBangla(getActivity())));
        banglaAyat.setTextSize(2, Float.parseFloat(FontSize.getBangla(getActivity())));
        tafheem.setTextSize(2, Float.parseFloat(FontSize.getBangla(getActivity())));
        bayaan.setTextSize(2, Float.parseFloat(FontSize.getBangla(getActivity())));
        fezilalil.setTextSize(2, Float.parseFloat(FontSize.getBangla(getActivity())));
        tafheemEnglish.setTextSize(2, Float.parseFloat(FontSize.getBangla(getActivity())));



        //Toasty.success(getApplicationContext(), s, Toasty.LENGTH_LONG).show();
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < dbHelper.getTafheem(s).size(); i++) {
            //Toasty.success(getApplicationContext(), dbHelper.getTafheem(s).get(i).toString(), Toasty.LENGTH_LONG).show();
            String sss = dbHelper.getTafheem(s).get(i).toString();
            //String[] strParts = sss.split("@");
            query.append(sss).append("<br>");
        }
        String main = query.toString().replace("\\n","<br>").replace("[[","").replace("]]","");
        if (main.isEmpty()){
            tafheem.setText("এই আয়াতের তাফসীর নেই।");
        } else {
            Config.setHtmlWithLinks(tafheem, main, getActivity());
        }



        if (setting.getString("bayaan", "on").equals("off")) {
            view.findViewById(R.id.bayaanLayout).setVisibility(View.GONE);
        }else {
            view.findViewById(R.id.bayaanLayout).setVisibility(View.VISIBLE);
            StringBuilder bayaanquery = new StringBuilder();
            for (int i = 0; i < dbHelper.getBayaan(s).size(); i++) {
                String sss = dbHelper.getBayaan(s).get(i).toString();
                bayaanquery.append(sss).append("<br>");
            }

            String bayaanmain = bayaanquery.toString().replace("[১]","<br><br><b>তাফসীরঃ-</b><br>[১]").replaceFirst(Pattern.quote("<br><br><b>তাফসীরঃ-</b><br>[১]"),"[১]").replace("[২]","<br><br>[২]").replaceFirst(Pattern.quote("<br><br>[২]"),"[২]").replace("[৩]","<br><br>[৩]").replaceFirst(Pattern.quote("<br><br>[৩]"),"[৩]").replace("[৪]","<br><br>[৪]").replaceFirst(Pattern.quote("<br><br>[৪]"),"[৪]");

            if (bayaanmain.isEmpty()){
                bayaan.setText("এই আয়াতের তাফসীর নেই।");
            } else {
                Config.setHtmlWithLinks(bayaan, bayaanmain, getActivity());
            }
        }


        if (setting.getString("fezilalil", "off").equals("off")) {
            view.findViewById(R.id.fezilalilLayout).setVisibility(View.GONE);
        }else {
            view.findViewById(R.id.fezilalilLayout).setVisibility(View.VISIBLE);
            fezilalilDatabaseHelper fezilalilDatabaseHelper = new fezilalilDatabaseHelper(getActivity());
            StringBuilder fezilalilquery = new StringBuilder();
            for (int i = 0; i < fezilalilDatabaseHelper.getFezilalil(Config.ENtoBN(s)).size(); i++) {
                String sss = fezilalilDatabaseHelper.getFezilalil(Config.ENtoBN(s)).get(i).toString();
                fezilalilquery.append(sss).append("<br>");
            }
            
            String fezilalilmain = fezilalilquery.toString().replace("[১]","<br><br><b>তাফসীরঃ-</b><br>[১]").replaceFirst(Pattern.quote("<br><br><b>তাফসীরঃ-</b><br>[১]"),"[১]").replace("[২]","<br><br>[২]").replaceFirst(Pattern.quote("<br><br>[২]"),"[২]").replace("[৩]","<br><br>[৩]").replaceFirst(Pattern.quote("<br><br>[৩]"),"[৩]").replace("[৪]","<br><br>[৪]").replaceFirst(Pattern.quote("<br><br>[৪]"),"[৪]");

            if (fezilalilmain.isEmpty()){
                fezilalil.setText("এই আয়াতের তাফসীর নেই।");
            } else {
                Config.setHtmlWithLinks(fezilalil, fezilalilmain, getActivity());
            }
        }

        if (setting.getString("tafheem_english", "off").equals("off")) {
            view.findViewById(R.id.tafheemEnglishLayout).setVisibility(View.GONE);
        }else {
            view.findViewById(R.id.tafheemEnglishLayout).setVisibility(View.VISIBLE);
            try {
                tafheemEnglishDatabaseHelper tafheemEnglishDatabaseHelper = new tafheemEnglishDatabaseHelper(getActivity());
                StringBuilder tafheemEnglishquery = new StringBuilder();
                for (int i = 0; i < tafheemEnglishDatabaseHelper.getTafheemEnglish(s).size(); i++) {
                    String sss = tafheemEnglishDatabaseHelper.getTafheemEnglish(s).get(i).toString();
                    tafheemEnglishquery.append(sss).append("<br>");
                }

                String tafheemEnglishmain = tafheemEnglishquery.toString().replace("[১]","<br><br><b>তাফসীরঃ-</b><br>[১]").replaceFirst(Pattern.quote("<br><br><b>তাফসীরঃ-</b><br>[১]"),"[১]").replace("[২]","<br><br>[২]").replaceFirst(Pattern.quote("<br><br>[২]"),"[২]").replace("[৩]","<br><br>[৩]").replaceFirst(Pattern.quote("<br><br>[৩]"),"[৩]").replace("[৪]","<br><br>[৪]").replaceFirst(Pattern.quote("<br><br>[৪]"),"[৪]");

                if (tafheemEnglishmain.isEmpty()){
                    tafheemEnglish.setText("এই আয়াতের তাফসীর নেই।");
                } else {
                    Config.setHtmlWithLinks(tafheemEnglish, tafheemEnglishmain, getActivity());
                }
            } catch (Exception e) {
                tafheemEnglish.setText("তাফসীর ডাটাবেজটি ডাউনলোড করা নেই। সেটিংস থেকে ডাউনলোড করুন।");
                e.printStackTrace();
            }
        }

        view.findViewById(R.id.copyTafheem).setOnClickListener(v -> {
            if (getActivity() != null) {
                ForcopyFatheem("তাফহীমুল কুরআন - আল্লামা সাইয়েদ আবুল আলা মওদূদী রহঃ", tafheem.getText().toString());
            }
        });
        view.findViewById(R.id.copyBayaan).setOnClickListener(v -> {
            if (getActivity() != null) {
                ForcopyFatheem("তাফসীরে ইবনে কাসীর", bayaan.getText().toString());
            }
        });
        view.findViewById(R.id.copyFezilalil).setOnClickListener(v -> {
            if (getActivity() != null) {
                ForcopyFatheem("তাফসীর ফী যিলালিল কোরআন", fezilalil.getText().toString());
            }
        });
        view.findViewById(R.id.copyTafheemEnglish).setOnClickListener(v -> {
            if (getActivity() != null) {
                ForcopyFatheem("তাফহীমুল কুরআন (ইংরেজি)", tafheemEnglish.getText().toString());
            }
        });
    }

    public void ForcopyFatheem(String title, String content){
        Activity activity = getActivity();
        if (activity == null) return;

        String copyText = dbHelper.getSurahName(idSurah) +" : "+Config.ENtoBN(String.valueOf(idVerse))+"\n" + arabic.getText().toString() + "\n" + banglaAyat.getText().toString() + "\n\n" + title + " :-\n"+ content + "\n\n"+"তাফহীমুল কুরআন"+"\nhttps://play.google.com/store/apps/details?id=" + activity.getPackageName();
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("আয়াত", copyText));
        }
        Toasty.success(activity, "তাফসীর-সহ আয়াতটি কপি হয়েছে", Toast.LENGTH_SHORT, true).show();
    }


}