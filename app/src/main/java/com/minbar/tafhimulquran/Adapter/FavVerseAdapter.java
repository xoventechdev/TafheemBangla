package com.minbar.tafhimulquran.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Activity.BitActivity;
import com.minbar.tafhimulquran.Activity.FavActivity;
import com.minbar.tafhimulquran.Activity.TafheemActivity;
import com.minbar.tafhimulquran.Activity.VerseActivity;
import com.minbar.tafhimulquran.Model.VerseModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;
import com.minbar.tafhimulquran.Utils.NoteDatabaseHelper;
import com.minbar.tafhimulquran.Utils.PronunciationUtils;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.XovenHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class FavVerseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final int TYPE_SURAH_TITLE = 0;
    private static final int TYPE_VERSE = 1;

    private final List<Object> displayItems = new ArrayList<>();
    private final List<Object> allItems = new ArrayList<>();
    private static Context mcontext;

    private SqlLiteDbHelper dbHelper;

    public FavVerseAdapter(Context context, List<VerseModel> verseList) {
        mcontext = context;
        this.dbHelper = new SqlLiteDbHelper(context);

        // Group verses by surah
        groupVersesBySurah(verseList);
    }

    private void groupVersesBySurah(List<VerseModel> verseList) {
        // Sort verses by surah ID and verse ID - using Collections.sort for API 21 compatibility
        Collections.sort(verseList, new Comparator<VerseModel>() {
            @Override
            public int compare(VerseModel v1, VerseModel v2) {
                int surahCompare = Integer.compare(v1.getSurahID(), v2.getSurahID());
                if (surahCompare != 0) return surahCompare;
                return Integer.compare(v1.getVerseID(), v2.getVerseID());
            }
        });

        // Group by surah - avoiding computeIfAbsent for API 21 compatibility
        Map<Integer, List<VerseModel>> surahMap = new HashMap<>();
        List<Integer> surahIds = new ArrayList<>();
        for (VerseModel verse : verseList) {
            int surahId = verse.getSurahID();
            if (!surahMap.containsKey(surahId)) {
                surahMap.put(surahId, new ArrayList<VerseModel>());
                surahIds.add(surahId);
            }
            surahMap.get(surahId).add(verse);
        }

        // Add titles and verses to the list
        displayItems.clear();
        allItems.clear();

        for (int surahId : surahIds) {
            String surahName = dbHelper.getSurahName(surahId);
            List<VerseModel> verses = surahMap.get(surahId);
            
            displayItems.add(surahName); // Add title
            displayItems.addAll(verses); // Add verses
            
            allItems.add(surahName); // Add title
            allItems.addAll(verses); // Add verses
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_SURAH_TITLE:
                View titleView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_surah_title, parent, false);
                return new SurahTitleViewHolder(titleView);

            case TYPE_VERSE:
            default:
                View verseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ayat, parent, false);
                return new VerseViewHolder(verseView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = displayItems.get(position);

        if (holder instanceof SurahTitleViewHolder) {
            SurahTitleViewHolder titleHolder = (SurahTitleViewHolder) holder;
            titleHolder.tvSurahTitle.setText((String) item);
        } else if (holder instanceof VerseViewHolder) {
            VerseViewHolder verseHolder = (VerseViewHolder) holder;
            VerseModel model = (VerseModel) item;
            bindVerseViewHolder(verseHolder, model);
        }
    }

    private void bindVerseViewHolder(VerseViewHolder holder, VerseModel model) {
        dbHelper = new SqlLiteDbHelper(mcontext);
        String verse_id = String.valueOf(model.getVerseID());

        holder.ayat_no.setText(Config.ENtoBN(verse_id));
        holder.surah_name_ayat.setText(dbHelper.getSurahName(model.getSurahID()));
        holder.arabic.setText(Config.Tajweed(mcontext, model.getArabic()));

        // Set Arabic pronunciation visibility
        if (PronunciationUtils.isArabicPronunciationVisible(mcontext)) {
            holder.trans.setText(Html.fromHtml(model.getTrans()));
            holder.trans.setVisibility(View.VISIBLE);
        } else {
            holder.trans.setText("");
            holder.trans.setVisibility(View.GONE);
        }

        holder.banglaAyat.setText(Html.fromHtml(new Config(mcontext).HideNumberBySetting(model.getBangla())+"<i><small> - তাফহীমুল কুরআন</small></i>"));
        holder.english.setText(Html.fromHtml(model.getEnglish()+"<i><small> - Sahih International</small></i>"));

        holder.arabicLayout.setOnClickListener(v -> {
            String qqq = dbHelper.getSurahName(model.getSurahID())+"@"+model.getSurahID()+"="+model.getVerseID();
            ((FavActivity) v.getContext()).onClickCalled(qqq);
        });

        int ID = model.getId();
        XovenHandler xovenHandler = new XovenHandler(mcontext);
        boolean favStatus = xovenHandler.checkFav(ID);

        if (favStatus){
            holder.fav.setImageResource(R.drawable.ic_baseline_favorite_24);
        }

        holder.fav.setOnClickListener(v -> {
            boolean favStatuss = xovenHandler.checkFav(ID);
            if (favStatuss){
                xovenHandler.deleteFav(ID);
                holder.fav.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                notifytoview(getItemPosition(model));
                ((FavActivity) v.getContext()).checkList();
            } else {
                xovenHandler.addFav(ID);
                holder.fav.setImageResource(R.drawable.ic_baseline_favorite_24);
            }
        });

        holder.share.setOnClickListener(v -> {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(mcontext);
            String surahName = dbHelper.getSurahName(model.getSurahID());
            intent.putExtra("android.intent.extra.TEXT", surahName + " : " + Config.toBangla(String.valueOf(model.getVerseID())) + "\n" + model.getArabic() + "\n\n" + new Config(mcontext).HideNumber(model.getBangla()) + "\n\nতাফহীমুল কুরআন\nhttps://play.google.com/store/apps/details?id=" + mcontext.getPackageName());
            mcontext.startActivity(Intent.createChooser(intent, "Share the verse"));
        });

        holder.copy_ayat.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) mcontext.getSystemService(Context.CLIPBOARD_SERVICE);
            SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(mcontext);
            String surahName = dbHelper.getSurahName(model.getSurahID());
            ClipData clip = ClipData.newPlainText("Ayah", surahName + " : " + Config.toBangla(String.valueOf(model.getVerseID())) + "\n" + model.getArabic() + "\n\n" + new Config(mcontext).HideNumber(model.getBangla()) + "\n\nতাফহীমুল কুরআন\nhttps://play.google.com/store/apps/details?id=" + mcontext.getPackageName());
            clipboard.setPrimaryClip(clip);
            Toasty.success(mcontext, "The verse is copied.", Toast.LENGTH_SHORT, true).show();
        });

        holder.bit.setOnClickListener(v -> {
            Intent intent = new Intent(mcontext, BitActivity.class);
            intent.putExtra("surah_id", String.valueOf(model.getSurahID()));
            intent.putExtra("verse_id", Config.toBangla(String.valueOf(model.getVerseID())));
            intent.putExtra("verse_en", String.valueOf(model.getVerseID()));
            intent.putExtra("arabicTxt", model.getArabic());
            intent.putExtra("banglaTxt", model.getBangla());
            mcontext.startActivity(intent);
        });

        holder.vNote.setOnClickListener(v -> {
            showNoteDialog(model.getSurahID(), model.getVerseID());
        });

        holder.relativeLayout.setOnClickListener(v -> {
            Intent intent = new Intent(mcontext, TafheemActivity.class);
            intent.putExtra("surah_id",String.valueOf(model.getSurahID()));
            intent.putExtra("verse_id",String.valueOf(model.getVerseID()));
            intent.putExtra("arabicTxt",model.getArabic());
            intent.putExtra("transTxt",model.getTrans());
            intent.putExtra("banglaTxt",model.getBangla());
            mcontext.startActivity(intent);
        });
    }

    private void showNoteDialog(int surahId, int verseId) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mcontext);
        @SuppressLint("InflateParams") View dialogView = LayoutInflater.from(mcontext).inflate(R.layout.dialog_note, null);

        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etNote = dialogView.findViewById(R.id.etNote);
        View btnSaveNote = dialogView.findViewById(R.id.btnSaveNote);
        View btnCloseDialog = dialogView.findViewById(R.id.btnCloseDialog);
        View btnTafseer = dialogView.findViewById(R.id.btnTafseer);
        btnTafseer.setVisibility(View.GONE);

        SqlLiteDbHelper db = new SqlLiteDbHelper(mcontext);
        String surahName = db.getSurahName(surahId);
        String title = surahName + " - আয়াতঃ " + Config.ENtoBN(String.valueOf(verseId));
        tvDialogTitle.setText(title);

        NoteDatabaseHelper noteDb = new NoteDatabaseHelper(mcontext);
        etNote.setText(noteDb.getNote(String.valueOf(surahId), String.valueOf(verseId)));

        androidx.appcompat.app.AlertDialog dialog = builder.setView(dialogView).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();

        btnSaveNote.setOnClickListener(v -> {
            String note = etNote.getText().toString().trim();
            noteDb.saveOrUpdateNote(String.valueOf(surahId), String.valueOf(verseId), note);
            Toasty.success(mcontext, "নোট সেভ করা হয়েছে", Toast.LENGTH_SHORT, true).show();
            dialog.dismiss();
        });

        btnCloseDialog.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public int getItemCount() {
        return displayItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = displayItems.get(position);
        return item instanceof String ? TYPE_SURAH_TITLE : TYPE_VERSE;
    }

    private int getItemPosition(VerseModel model) {
        for (int i = 0; i < displayItems.size(); i++) {
            if (displayItems.get(i) instanceof VerseModel) {
                VerseModel verse = (VerseModel) displayItems.get(i);
                if (verse.getId() == model.getId()) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void notifytoview(int position) {
        if (position >= 0 && position < displayItems.size()) {
            displayItems.remove(position);
            notifyItemRemoved(position);

            // Also update allItems
            Object removedItem = allItems.get(position);
            allItems.remove(removedItem);
        }
    }

    public void updateData(List<VerseModel> verseList) {
        groupVersesBySurah(verseList);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Object> filteredList = new ArrayList<>();

            if (constraint.toString().isEmpty()) {
                filteredList.addAll(allItems);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                // Create a map to track which surahs have matching verses
                Map<Integer, Boolean> surahsWithMatches = new HashMap<>();

                for (Object item : allItems) {
                    if (item instanceof String) {
                        // Always include surah titles
                        filteredList.add(item);
                    } else if (item instanceof VerseModel) {
                        VerseModel verse = (VerseModel) item;
                        if (verse.getArabic().toLowerCase().contains(filterPattern) ||
                            verse.getBangla().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                            surahsWithMatches.put(verse.getSurahID(), true);
                        }
                    }
                }

                // Add surah titles for surahs that have matching verses
                for (Object item : allItems) {
                    if (item instanceof String) {
                        String surahName = (String) item;
                        for (int surahId : surahsWithMatches.keySet()) {
                            if (dbHelper.getSurahName(surahId).equals(surahName)) {
                                if (!filteredList.contains(surahName)) {
                                    int pos = 0;
                                    for (Object filteredItem : filteredList) {
                                        if (filteredItem instanceof VerseModel) {
                                            VerseModel v = (VerseModel) filteredItem;
                                            if (v.getSurahID() == surahId) {
                                                filteredList.add(pos, surahName);
                                                break;
                                            }
                                        }
                                        pos++;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            displayItems.clear();
            displayItems.addAll((List<Object>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    // View Holder for Surah Title
    public static class SurahTitleViewHolder extends RecyclerView.ViewHolder {
        TextView tvSurahTitle;

        public SurahTitleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSurahTitle = itemView.findViewById(R.id.tvSurahTitle);
        }
    }

    // View Holder for Verse
    public static class VerseViewHolder extends RecyclerView.ViewHolder {
        public TextView ayat_no, surah_name_ayat, arabic, banglaAyat, english, trans;
        public LinearLayout relativeLayout, arabicLayout, enLayout, arabicMeana, banglaOnubadh;
        public ImageView share, copy_ayat, fav, vNote, bit;

        public VerseViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ayat_no = itemView.findViewById(R.id.ayat_no);
            this.surah_name_ayat = itemView.findViewById(R.id.surah_name_ayat);
            this.arabic = itemView.findViewById(R.id.arabic);
            this.trans = itemView.findViewById(R.id.trans);
            this.banglaAyat = itemView.findViewById(R.id.banglaAyat);
            this.english = itemView.findViewById(R.id.english);
            this.fav = itemView.findViewById(R.id.fav);
            this.share = itemView.findViewById(R.id.share);
            this.copy_ayat = itemView.findViewById(R.id.copy_ayat);
            this.vNote = itemView.findViewById(R.id.vNote);
            this.bit = itemView.findViewById(R.id.bit);
            relativeLayout = itemView.findViewById(R.id.ayat_layout);
            arabicLayout = itemView.findViewById(R.id.arabicLayout);
            enLayout = itemView.findViewById(R.id.enLayout);
            arabicMeana = itemView.findViewById(R.id.arabicMeana);
            banglaOnubadh = itemView.findViewById(R.id.VerseTai);

            Config.BanglaOnubadh(banglaOnubadh, mcontext);

            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mcontext);
            if (defaultSharedPreferences.getString("taisirul", "on").equals("off")) {
                this.enLayout.setVisibility(View.GONE);
            }
            if (defaultSharedPreferences.getString("tafheem", "on").equals("off")) {
                this.arabicMeana.setVisibility(View.GONE);
            }

            this.arabic.setTypeface(FontFamily.getArabic(mcontext));
            this.trans.setTypeface(FontFamily.getBangla(mcontext));
            this.banglaAyat.setTypeface(FontFamily.getBangla(mcontext));

            this.arabic.setTextSize(2, Float.parseFloat(FontSize.getArabic(mcontext)));
            this.trans.setTextSize(2, Float.parseFloat(FontSize.getBangla(mcontext)));
            this.banglaAyat.setTextSize(2, Float.parseFloat(FontSize.getBangla(mcontext)));
        }
    }
}
