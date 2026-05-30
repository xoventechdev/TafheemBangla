package com.minbar.tafhimulquran.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Activity.BitActivity;
import com.minbar.tafhimulquran.Activity.SingleActivity;
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
import java.util.List;

import es.dmoral.toasty.Toasty;

public class VerseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static final int ITEM_BANNER = 0;
    private static final int ITEM_DATA = 1;

    public static Context mcontext;
    private List<VerseModel> list;
    private List<VerseModel> listFull;

    private int currentPlayingPosition = -1;
    private myviewholder playingHolder;
    private static MediaPlayer mediaPlayer;

    public VerseAdapter(Context context, List<VerseModel> list) {
        this.mcontext = context;
        this.list = list;
        listFull = new ArrayList<>(list);
    }

    @Override
    public int getItemViewType(int position) {
        // Surah 9 (At-Tawbah) does not have Bismillah
        if (VerseActivity.surahid == 9) {
            return ITEM_DATA;
        } else {
            if (position == 0) {
                return ITEM_BANNER;
            } else {
                return ITEM_DATA;
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_BANNER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_sura_list_item_bismillah, parent, false);
            return new banneraddviewholder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ayat, parent, false);
            return new myviewholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final VerseModel model = list.get(position);
        int viewType = getItemViewType(position);

        if (viewType == ITEM_DATA) {
            myviewholder mvh = (myviewholder) holder;

            mvh.ayat_no.setText(Config.ENtoBN(String.valueOf(model.getVerseID())));
            mvh.arabic.setText(new Config(mcontext).Tajweed(model.getArabic()));

            // Set Arabic pronunciation visibility
            if (PronunciationUtils.isArabicPronunciationVisible(mcontext)) {
                mvh.trans.setText(model.getTrans());
                mvh.trans.setVisibility(View.VISIBLE);
            } else {
                mvh.trans.setText("");
                mvh.trans.setVisibility(View.GONE);
            }

            mvh.banglaAyat.setText(Html.fromHtml(new Config(mcontext).HideNumberBySetting(model.getBangla())));
            mvh.english.setText(model.getEnglish());

            mvh.arabic.setTextSize(TypedValue.COMPLEX_UNIT_SP, FontSize.Arabic(mcontext));
            mvh.trans.setTextSize(TypedValue.COMPLEX_UNIT_SP, FontSize.Bangla(mcontext));
            mvh.banglaAyat.setTextSize(TypedValue.COMPLEX_UNIT_SP, FontSize.Bangla(mcontext));
            mvh.english.setTextSize(TypedValue.COMPLEX_UNIT_SP, FontSize.English(mcontext));

            mvh.arabic.setTypeface(FontFamily.Arabic(mcontext));
            mvh.trans.setTypeface(FontFamily.Bangla(mcontext));
            mvh.banglaAyat.setTypeface(FontFamily.Bangla(mcontext));
            mvh.english.setTypeface(FontFamily.English(mcontext));

            mvh.arabic.setOnClickListener(v -> {
                showWordByWordDialog(model.getSurahID(), model.getVerseID());
            });

            XovenHandler xovenHandler = new XovenHandler(mcontext);
            if (xovenHandler.checkFav(model.getId())) {
                mvh.fav.setImageResource(R.drawable.ic_baseline_favorite_24);
            } else {
                mvh.fav.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            }

            mvh.fav.setOnClickListener(v -> {
                if (xovenHandler.checkFav(model.getId())) {
                    xovenHandler.deleteFav(model.getId());
                    mvh.fav.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    Toast.makeText(mcontext, "Removed from favorite", Toast.LENGTH_SHORT).show();
                } else {
                    xovenHandler.addFav(model.getId());
                    mvh.fav.setImageResource(R.drawable.ic_baseline_favorite_24);
                    Toast.makeText(mcontext, "Added to favorite", Toast.LENGTH_SHORT).show();
                }
            });

            mvh.share.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(mcontext);
                String surahName = dbHelper.getSurahName(model.getSurahID());
                intent.putExtra(Intent.EXTRA_TEXT, surahName + " : " + Config.toBangla(String.valueOf(model.getVerseID())) + "\n" + model.getArabic() + "\n\n" + model.getTrans() + "\n\n" + new Config(mcontext).HideNumber(model.getBangla()) + "\n\nতাফহীমুল কুরআন\nhttps://play.google.com/store/apps/details?id=" + mcontext.getPackageName());
                mcontext.startActivity(Intent.createChooser(intent, "Share via"));
            });

            mvh.copy_ayat.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) mcontext.getSystemService(Context.CLIPBOARD_SERVICE);
                SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(mcontext);
                String surahName = dbHelper.getSurahName(model.getSurahID());
                ClipData clip = ClipData.newPlainText("Ayat", surahName + " : " + Config.toBangla(String.valueOf(model.getVerseID())) + "\n" + model.getArabic() + "\n\n" + model.getTrans() + "\n\n" + new Config(mcontext).HideNumber(model.getBangla()) + "\n\nতাফহীমুল কুরআন\nhttps://play.google.com/store/apps/details?id=" + mcontext.getPackageName());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mcontext, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            });

            mvh.bit.setOnClickListener(v -> {
                Intent intent = new Intent(mcontext, BitActivity.class);
                intent.putExtra("surah_id", String.valueOf(model.getSurahID()));
                intent.putExtra("verse_id", Config.toBangla(String.valueOf(model.getVerseID())));
                intent.putExtra("verse_en", String.valueOf(model.getVerseID()));
                intent.putExtra("arabicTxt", model.getArabic());
                intent.putExtra("banglaTxt", model.getBangla());
                mcontext.startActivity(intent);
            });

            mvh.vNote.setOnClickListener(v -> {
                showNoteDialog(model.getSurahID(), model.getVerseID());
            });

            mvh.relativeLayout.setOnClickListener(v -> {
                Intent intent = new Intent(mcontext, SingleActivity.class);
                intent.putExtra("surah_id", String.valueOf(model.getSurahID()));
                intent.putExtra("verse_id", model.getVerseID());
                intent.putExtra("arabicTxt", model.getArabic());
                intent.putExtra("transTxt", model.getTrans());
                intent.putExtra("banglaTxt", model.getBangla());
                mcontext.startActivity(intent);
            });

            if (position == currentPlayingPosition) {
                playingHolder = mvh;
                updatePlayingView();
            } else {
                updateNonPlayingView(mvh);
            }

            mvh.playSingle.setOnClickListener(v -> {
                String vv;
                if (model.getSurahID() == 9) {
                    vv = String.format("%03d", model.getSurahID()) + String.format("%03d", position + 1);
                } else {
                    vv = String.format("%03d", model.getSurahID()) + String.format("%03d", position);
                }
                String singleMp3 = "https://www.everyayah.com/data/Alafasy_64kbps/" + vv + ".mp3";

                if (position == currentPlayingPosition) {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    } else {
                        if (mediaPlayer != null)
                            mediaPlayer.start();
                    }
                } else {
                    currentPlayingPosition = position;
                    if (mediaPlayer != null) {
                        if (null != playingHolder) {
                            updateNonPlayingView(playingHolder);
                        }
                        mediaPlayer.release();
                    }
                    playingHolder = mvh;
                    PlaySound(singleMp3);
                }
                if (mediaPlayer != null)
                    updatePlayingView();
            });

        } else if (viewType == ITEM_BANNER) {
            banneraddviewholder bvh = (banneraddviewholder) holder;
            if (model.getSurahID() == 1) {
                bvh.surah1.setVisibility(View.VISIBLE);
                bvh.surah1Out.setOnClickListener(v -> {
                    ((VerseActivity) v.getContext()).ShowSurah1by1();
                });
            } else {
                bvh.surah1.setVisibility(View.GONE);
            }
        }
    }

    private void updateNonPlayingView(myviewholder mvh) {
        mvh.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_play);
    }

    private void updatePlayingView() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                playingHolder.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_pause);
            } else {
                playingHolder.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_play);
            }
        } else {
            playingHolder.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_play);
        }
    }

    public void releaseMediaPlayer() {
        if (null != playingHolder) {
            updateNonPlayingView(playingHolder);
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        currentPlayingPosition = -1;
    }

    private void PlaySound(String filesound) {
        mediaPlayer = MediaPlayer.create(mcontext, Uri.parse(filesound));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                releaseMediaPlayer();
            }
        });
        mediaPlayer.start();
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

    private void showWordByWordDialog(int surahId, int verseId) {
        SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(mcontext);
        final Dialog dialog = new Dialog(mcontext);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.word_meaning_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);
        }

        TextView titleVerse = dialog.findViewById(R.id.title_verse);
        titleVerse.setText(dbHelper.getSurahName(surahId) + " : " + Config.toBangla(String.valueOf(verseId)));

        ImageView clearLayout = dialog.findViewById(R.id.clearLayout);
        clearLayout.setOnClickListener(v -> dialog.dismiss());

        RecyclerView recycler = dialog.findViewById(R.id.wordListview);
        WordAdapter wordAdapter = new WordAdapter(mcontext, dbHelper.getWord(surahId + "=" + verseId));
        LinearLayoutManager layoutManager = new LinearLayoutManager(mcontext);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(wordAdapter);
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<VerseModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(listFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (VerseModel item : listFull) {
                    if (item.getTrans().toLowerCase().contains(filterPattern) || item.getBangla().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class banneraddviewholder extends RecyclerView.ViewHolder {
        LinearLayout surah1Out;
        TextView surah1;

        public banneraddviewholder(@NonNull View itemView) {
            super(itemView);
            surah1Out = itemView.findViewById(R.id.surah1Out);
            surah1 = itemView.findViewById(R.id.surah1);

            this.surah1.setTypeface(FontFamily.Bangla(mcontext));
            this.surah1.setTextSize(TypedValue.COMPLEX_UNIT_SP, FontSize.Bangla(mcontext));
        }
    }

    public static class myviewholder extends RecyclerView.ViewHolder {
        public TextView ayat_no, arabic, banglaAyat, english, trans;
        public LinearLayout relativeLayout;
        public LinearLayout arabicLayout, enLayout, arabicMeana, banglaOnubadh;
        public ImageView share, copy_ayat, fav, bit, playSingle, vNote;

        @SuppressLint("WrongViewCast")
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            this.ayat_no = (TextView) itemView.findViewById(R.id.ayat_no);
            this.arabic = (TextView) itemView.findViewById(R.id.arabic);
            this.trans = (TextView) itemView.findViewById(R.id.trans);
            this.banglaAyat = (TextView) itemView.findViewById(R.id.banglaAyat);
            this.english = (TextView) itemView.findViewById(R.id.english);
            this.fav = (ImageView) itemView.findViewById(R.id.fav);
            this.share = (ImageView) itemView.findViewById(R.id.share);
            this.copy_ayat = (ImageView) itemView.findViewById(R.id.copy_ayat);
            this.bit = (ImageView) itemView.findViewById(R.id.bit);
            this.vNote = (ImageView) itemView.findViewById(R.id.vNote);
            this.playSingle = (ImageView) itemView.findViewById(R.id.playSingle);
            relativeLayout = (LinearLayout) itemView.findViewById(R.id.ayat_layout);
            arabicLayout = (LinearLayout) itemView.findViewById(R.id.arabicLayout);
            enLayout = (LinearLayout) itemView.findViewById(R.id.enLayout);
            arabicMeana = (LinearLayout) itemView.findViewById(R.id.arabicMeana);
            banglaOnubadh = (LinearLayout) itemView.findViewById(R.id.VerseTai);

            Config.BanglaOnubadh(banglaOnubadh, mcontext);

            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(VerseAdapter.mcontext);
            if (defaultSharedPreferences.getString("taisirul", "on").equals("off")) {
                this.enLayout.setVisibility(View.GONE);
            }
            if (defaultSharedPreferences.getString("tafheem", "on").equals("off")) {
                this.arabicMeana.setVisibility(View.GONE);
            }
        }
    }
}