package com.minbar.tafhimulquran.Fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.minbar.tafhimulquran.Model.VerseModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;
import com.minbar.tafhimulquran.Utils.NoteDatabaseHelper;
import com.minbar.tafhimulquran.Utils.PronunciationUtils;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.XovenHandler;
import com.minbar.tafhimulquran.Utils.fezilalilDatabaseHelper;
import com.minbar.tafhimulquran.Utils.tafheemEnglishDatabaseHelper;

import java.util.ArrayList;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class VerseFragment extends Fragment {

    public static final String surahidF = "1";
    public static final String verseidF = "2";

    // Static to persist selection across fragments in ViewPager2
    private static int selectedTafseerIndex = 0;

    private int idSurah;
    private int idVerse;
    private SqlLiteDbHelper dbHelper;
    private XovenHandler xovenHandler;
    private NoteDatabaseHelper noteDbHelper;
    private VerseModel currentVerseModel;

    private TextView arabic, trans, banglaAyat, verseReference, toolbarTitle;
    private TextView tafheem, bayaan, fezilalil, tafheemEnglish;
    private View tafheemLayout, bayaanLayout, fezilalilLayout, tafheemEnglishLayout;
    private MaterialButton tabTafheem, tabBayaan, tabFezilalil, tabTafheemEng;
    private ImageButton btnBack, btnPrev, btnNext, btnShare;
    ImageView btnFav, btnNote, copyTafheem, copyBayaan, copyFezilalil, copyTafheemEnglish;
    private View tafseerScroll;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verse, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        dbHelper = SqlLiteDbHelper.getInstance(getActivity());
        xovenHandler = new XovenHandler(getActivity());
        noteDbHelper = new NoteDatabaseHelper(getActivity());

        if (getArguments() != null) {
            String surahIdStr = getArguments().getString(surahidF);
            String verseIdStr = getArguments().getString(verseidF);
            if (surahIdStr != null) idSurah = Integer.parseInt(surahIdStr);
            if (verseIdStr != null) idVerse = Integer.parseInt(verseIdStr);
        }
        
        currentVerseModel = dbHelper.getVerseById(idSurah, idVerse);

        initViews(view);
        setupContent(view);
        setupListeners(view);
        
        // Initial visibility sync
        updateTafseerVisibility();
        updateFavStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Sync visibility when swiping back into view
        updateTafseerVisibility();
        updateFavStatus();
    }

    private void initViews(View view) {
        toolbarTitle = view.findViewById(R.id.toolbar_title);
        verseReference = view.findViewById(R.id.verse_reference);
        btnBack = view.findViewById(R.id.btn_back);
        btnPrev = view.findViewById(R.id.btn_prev);
        btnNext = view.findViewById(R.id.btn_next);
        btnShare = view.findViewById(R.id.btn_share);
        btnFav = view.findViewById(R.id.btn_fav);
        btnNote = view.findViewById(R.id.btn_note);

        arabic = view.findViewById(R.id.arabic);
        trans = view.findViewById(R.id.trans);
        banglaAyat = view.findViewById(R.id.banglaAyat);

        tafheem = view.findViewById(R.id.tafheem);
        bayaan = view.findViewById(R.id.bayaan);
        fezilalil = view.findViewById(R.id.fezilalil);
        tafheemEnglish = view.findViewById(R.id.tafheemEnglish);

        tafheemLayout = view.findViewById(R.id.tafheemLayout);
        bayaanLayout = view.findViewById(R.id.bayaanLayout);
        fezilalilLayout = view.findViewById(R.id.fezilalilLayout);
        tafheemEnglishLayout = view.findViewById(R.id.tafheemEnglishLayout);

        tabTafheem = view.findViewById(R.id.tab_tafheem);
        tabBayaan = view.findViewById(R.id.tab_bayaan);
        tabFezilalil = view.findViewById(R.id.tab_fezilalil);
        tabTafheemEng = view.findViewById(R.id.tab_tafheem_eng);
        
        tafseerScroll = view.findViewById(R.id.tafseerScroll);

        copyTafheem = view.findViewById(R.id.copyTafheem);
        copyBayaan = view.findViewById(R.id.copyBayaan);
        copyFezilalil = view.findViewById(R.id.copyFezilalil);
        copyTafheemEnglish = view.findViewById(R.id.copyTafheemEnglish);
    }

    private void setupContent(View view) {
        String surahName = dbHelper.getSurahName(idSurah);
        toolbarTitle.setText(surahName);
        verseReference.setText(surahName + " " + Config.ENtoBN(idSurah + ":" + idVerse));

        String s = idSurah + "=" + idVerse;

        // Bismillah logic
        // Surah 1 has verse 0 (Bismillah) in DB, so banner is not needed.
        // Surah 9 doesn't show Bismillah banner.
        if (idSurah != 9 && idSurah != 1 && idVerse == 1) {
            view.findViewById(R.id.bishmillah).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.bishmillah).setVisibility(View.GONE);
        }

        // Arabic
        arabic.setText(dbHelper.getTestArabic(s));
        arabic.setTypeface(FontFamily.getArabic(getActivity()));
        arabic.setTextSize(2, Float.parseFloat(FontSize.getArabic(getActivity())));

        // Pronunciation
        if (PronunciationUtils.isArabicPronunciationVisible(getActivity())) {
            trans.setText(dbHelper.getTestTras(s));
            trans.setVisibility(View.VISIBLE);
        } else {
            trans.setVisibility(View.GONE);
        }
        trans.setTypeface(FontFamily.getBangla(getActivity()));
        trans.setTextSize(2, Float.parseFloat(FontSize.getBangla(getActivity())));

        // Bangla Ayat
        banglaAyat.setText(Html.fromHtml(Config.TagColor(dbHelper.getTestBangla(s))));
        banglaAyat.setTypeface(FontFamily.getBangla(getActivity()));
        banglaAyat.setTextSize(2, Float.parseFloat(FontSize.getBangla(getActivity())));

        Config.BanglaOnubadh(view.findViewById(R.id.VerseTai), getActivity());

        loadTafseers(s);
    }

    private void setupListeners(View view) {
        tabTafheem.setOnClickListener(v -> selectTab(0));
        tabBayaan.setOnClickListener(v -> selectTab(1));
        tabFezilalil.setOnClickListener(v -> selectTab(2));
        tabTafheemEng.setOnClickListener(v -> selectTab(3));

        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        if (getActivity() != null) {
            ViewPager2 viewPager = getActivity().findViewById(R.id.viewpager);
            if (viewPager != null) {
                btnPrev.setOnClickListener(v -> {
                    if (viewPager.getCurrentItem() > 0) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                    }
                });
                btnNext.setOnClickListener(v -> {
                    if (viewPager.getAdapter() != null && viewPager.getCurrentItem() < viewPager.getAdapter().getItemCount() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    }
                });
                
                // Navigation buttons visibility based on position
                int position = (idSurah == 1) ? idVerse : idVerse - 1;
                btnPrev.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
                if (viewPager.getAdapter() != null) {
                    btnNext.setVisibility(position == viewPager.getAdapter().getItemCount() - 1 ? View.INVISIBLE : View.VISIBLE);
                }
            }
        }

        btnShare.setOnClickListener(v -> shareAyat());
        
        btnFav.setOnClickListener(v -> {
            if (currentVerseModel == null) return;
            int verseTableId = currentVerseModel.getId();
            if (xovenHandler.checkFav(verseTableId)) {
                xovenHandler.deleteFav(verseTableId);
                Toasty.info(getActivity(), "পছন্দ তালিকা থেকে মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show();
            } else {
                xovenHandler.addFav(verseTableId);
                Toasty.success(getActivity(), "পছন্দ তালিকায় যোগ করা হয়েছে", Toast.LENGTH_SHORT).show();
            }
            updateFavStatus();
        });

        btnNote.setOnClickListener(v -> showNoteDialog());

        if (copyTafheem != null) {
            copyTafheem.setOnClickListener(v -> ForcopyFatheem("তাফহীমুল কুরআন", stripHtmlTags(tafheem.getText().toString())));
        }

        if (copyBayaan != null) {
            copyBayaan.setOnClickListener(v -> ForcopyFatheem("তাফসীরে ইবনে কাসীর", stripHtmlTags(bayaan.getText().toString())));
        }

        if (copyFezilalil != null) {
            copyFezilalil.setOnClickListener(v -> ForcopyFatheem("তাফসীর ফী যিলালিল কোরআন", stripHtmlTags(fezilalil.getText().toString())));
        }

        if (copyTafheemEnglish != null) {
            copyTafheemEnglish.setOnClickListener(v -> ForcopyFatheem("Tafhim-ul-Quran (English)", stripHtmlTags(tafheemEnglish.getText().toString())));
        }
    }

    private void updateFavStatus() {
        if (currentVerseModel == null || btnFav == null || getContext() == null) return;
        if (xovenHandler.checkFav(currentVerseModel.getId())) {
            btnFav.setImageResource(R.drawable.ic_baseline_favorite_24);
            btnFav.setColorFilter(getThemeColor(R.attr.colorPrimary));
        } else {
            btnFav.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            btnFav.setColorFilter(getThemeColor(R.attr.textColorSecondary));
        }
    }

    private int getThemeColor(int attr) {
        if (getContext() == null) return 0;
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }

    private void showNoteDialog() {
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_note, null);
        EditText etNote = dialogView.findViewById(R.id.etNote);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSaveNote);
        MaterialButton btnClose = dialogView.findViewById(R.id.btnCloseDialog);
        MaterialButton btnTafseer = dialogView.findViewById(R.id.btnTafseer);

        btnTafseer.setVisibility(View.GONE);

        String existingNote = noteDbHelper.getNote(String.valueOf(idSurah), String.valueOf(idVerse));
        etNote.setText(existingNote);

        AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme)
                .setView(dialogView)
                .create();

        btnSave.setOnClickListener(v -> {
            String note = etNote.getText().toString().trim();
            noteDbHelper.saveOrUpdateNote(String.valueOf(idSurah), String.valueOf(idVerse), note);
            Toasty.success(getActivity(), "নোট সেভ করা হয়েছে", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void selectTab(int index) {
        selectedTafseerIndex = index;
        updateTafseerVisibility();
    }

    private void updateTafseerVisibility() {
        if (tafheemLayout == null) return;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        
        boolean bayaanOn = sp.getString("bayaan", "on").equals("on");
        boolean fezilalilOn = sp.getString("fezilalil", "off").equals("on");
        boolean englishOn = sp.getString("tafheem_english", "off").equals("on");

        tabBayaan.setVisibility(bayaanOn ? View.VISIBLE : View.GONE);
        tabFezilalil.setVisibility(fezilalilOn ? View.VISIBLE : View.GONE);
        tabTafheemEng.setVisibility(englishOn ? View.VISIBLE : View.GONE);

        if (selectedTafseerIndex == 1 && !bayaanOn) selectedTafseerIndex = 0;
        if (selectedTafseerIndex == 2 && !fezilalilOn) selectedTafseerIndex = 0;
        if (selectedTafseerIndex == 3 && !englishOn) selectedTafseerIndex = 0;

        tafheemLayout.setVisibility(selectedTafseerIndex == 0 ? View.VISIBLE : View.GONE);
        bayaanLayout.setVisibility(selectedTafseerIndex == 1 ? View.VISIBLE : View.GONE);
        fezilalilLayout.setVisibility(selectedTafseerIndex == 2 ? View.VISIBLE : View.GONE);
        tafheemEnglishLayout.setVisibility(selectedTafseerIndex == 3 ? View.VISIBLE : View.GONE);

        updateTabStyles();
    }

    private void updateTabStyles() {
        if (getContext() == null) return;
        
        setTabButtonStyle(tabTafheem, selectedTafseerIndex == 0);
        setTabButtonStyle(tabBayaan, selectedTafseerIndex == 1);
        setTabButtonStyle(tabFezilalil, selectedTafseerIndex == 2);
        setTabButtonStyle(tabTafheemEng, selectedTafseerIndex == 3);
    }

    private void setTabButtonStyle(MaterialButton button, boolean isSelected) {
        if (getContext() == null) return;
        if (isSelected) {
            button.setBackgroundTintList(ColorStateList.valueOf(getThemeColor(R.attr.colorPrimary)));
            button.setTextColor(getThemeColor(android.R.attr.colorBackground));
            button.setStrokeWidth(0);
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(getThemeColor(R.attr.colorSurface)));
            button.setTextColor(getThemeColor(R.attr.textColorPrimary));
            button.setStrokeWidth(1);
            button.setStrokeColor(ColorStateList.valueOf(getThemeColor(R.attr.dividerColor)));
        }
    }

    private void loadTafseers(String s) {
        float banglaSize = Float.parseFloat(FontSize.getBangla(getActivity()));

        // Tafheem
        StringBuilder query = new StringBuilder();
        ArrayList<String> tafheemData = dbHelper.getTafheem(s);
        for (String item : tafheemData) query.append(item).append("<br>");
        String main = query.toString().replace("\\n", "<br>").replace("[[", "").replace("]]", "");
        if (main.isEmpty()) tafheem.setText("এই আয়াতের তাফসীর নেই।");
        else Config.setHtmlWithLinks(tafheem, main, getActivity());
        tafheem.setTextSize(2, banglaSize);

        // Bayaan
        StringBuilder bayaanquery = new StringBuilder();
        ArrayList<String> bayaanData = dbHelper.getBayaan(s);
        for (String item : bayaanData) bayaanquery.append(item).append("<br>");
        String bayaanmain = bayaanquery.toString().replace("[১]", "<br><br><b>তাফসীরঃ-</b><br>[১]").replaceFirst(Pattern.quote("<br><br><b>তাফসীরঃ-</b><br>[১]"), "[১]").replace("[২]", "<br><br>[২]").replaceFirst(Pattern.quote("<br><br>[২]"), "[২]");
        if (bayaanmain.isEmpty()) bayaan.setText("এই আয়াতের তাফসীর নেই।");
        else Config.setHtmlWithLinks(bayaan, bayaanmain, getActivity());
        bayaan.setTextSize(2, banglaSize);

        // Fezilalil
        try {
            fezilalilDatabaseHelper fezilalilHelper = new fezilalilDatabaseHelper(getActivity());
            StringBuilder fezilalilquery = new StringBuilder();
            ArrayList<String> fezilalilData = fezilalilHelper.getFezilalil(Config.ENtoBN(s));
            for (String item : fezilalilData) fezilalilquery.append(item).append("<br>");
            String fezilalilmain = fezilalilquery.toString().replace("[১]", "<br><br><b>তাফসীরঃ-</b><br>[১]").replaceFirst(Pattern.quote("<br><br><b>তাফসীরঃ-</b><br>[১]"), "[১]");
            if (fezilalilmain.isEmpty()) fezilalil.setText("এই আয়াতের তাফসীর নেই।");
            else Config.setHtmlWithLinks(fezilalil, fezilalilmain, getActivity());
        } catch (Exception e) {
            fezilalil.setText("তাফসীর ফী যিলালিল কোরআন ডাটাবেজটি পাওয়া যায়নি।");
        }
        fezilalil.setTextSize(2, banglaSize);

        // English
        try {
            tafheemEnglishDatabaseHelper engHelper = new tafheemEnglishDatabaseHelper(getActivity());
            StringBuilder engQuery = new StringBuilder();
            ArrayList<String> engData = engHelper.getTafheemEnglish(s);
            for (String item : engData) engQuery.append(item).append("<br>");
            String engMain = engQuery.toString().replace("[১]", "<br><br><b>Tafseer:-</b><br>[১]").replaceFirst(Pattern.quote("<br><br><b>Tafseer:-</b><br>[১]"), "[১]");
            if (engMain.isEmpty()) tafheemEnglish.setText("Tafseer not available.");
            else Config.setHtmlWithLinks(tafheemEnglish, engMain, getActivity());
        } catch (Exception e) {
            tafheemEnglish.setText("তাফসীর ডাটাবেজটি ডাউনলোড করা নেই। সেটিংস থেকে ডাউনলোড করুন।");
        }
        tafheemEnglish.setTextSize(2, banglaSize);
    }

    public void ForcopyFatheem(String title, String content) {
        Context context = getContext();
        if (context == null) return;

        String surahName = dbHelper.getSurahName(idSurah);
        String packageName = context.getPackageName();
        String arabicText = arabic != null ? arabic.getText().toString() : "";
        String banglaText = banglaAyat != null ? banglaAyat.getText().toString() : "";

        String copyText = surahName + " : " + Config.ENtoBN(String.valueOf(idVerse)) + "\n"
                + arabicText + "\n"
                + banglaText + "\n\n"
                + title + " :-\n" + content + "\n\n"
                + "তাফহীমুল কুরআন" + "\nhttps://play.google.com/store/apps/details?id=" + packageName;

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Ayat", copyText);
            clipboard.setPrimaryClip(clip);
            Toasty.success(context, "তাফসীর-সহ আয়াতটি কপি হয়েছে", Toast.LENGTH_SHORT, true).show();
        }
    }

    private void shareAyat() {
        String surahName = dbHelper.getSurahName(idSurah);
        String shareText = surahName + " : " + Config.ENtoBN(String.valueOf(idVerse)) + "\n" + arabic.getText().toString() + "\n" + banglaAyat.getText().toString() + "\n\n" + "Shared via Tafheem Bangla App";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private String stripHtmlTags(String htmlText) {
        if (htmlText == null) return "";
        return htmlText.replaceAll("<br>", "\n")
                .replaceAll("<br/>", "\n")
                .replaceAll("<br />", "\n")
                .replaceAll("<b>", "")
                .replaceAll("</b>", "")
                .replaceAll("<i>", "")
                .replaceAll("</i>", "")
                .replaceAll("<sup>", "")
                .replaceAll("</sup>", "")
                .replaceAll("\\[.*?\\]", "")
                .replaceAll("\n{3,}", "\n\n");
    }
}
