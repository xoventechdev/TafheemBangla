package com.minbar.tafhimulquran.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import com.minbar.tafhimulquran.Utils.QuranArabicUtils;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.XovenHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class SubVerseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
       //private  List<VerseModel> VerseModel= new ArrayList<>();
          //public List<VerseModel> patientListAll;
    private  List<VerseModel> recycleritems = new ArrayList<>();      //U
    private List<VerseModel> patientListAll = new ArrayList<>();
          static  Context mcontext;



       private static final int item_data=1;
       private static final int item_banner=0;
    String getBangla;

    SqlLiteDbHelper dbHelper;
    TextToSpeech  quranSpeech;

     public SubVerseAdapter(Context context, List<VerseModel> recycleritems) {
         mcontext = context;
         this.recycleritems = recycleritems;
         //this.recycleritems = recycleritems;   //U
         this.patientListAll = new ArrayList<>(recycleritems);

     }


     @NonNull
     @Override
     public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
     {
         switch (viewType) {
             case item_data:
                 View dataview = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ayat, parent, false);
                 return new myviewholder(dataview);

             case item_banner:
             default:
                 View bannerview = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_sura_list_item_bismillah, parent, false);
                 return new banneraddviewholder(bannerview);

         }
     }

     @Override
     public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
     {
         int viwtype= getItemViewType(position);

         switch (viwtype)
         {
             case item_data:
                 myviewholder mvh=(myviewholder) holder;
                 VerseModel model= (VerseModel) recycleritems.get(position);

                 dbHelper = new SqlLiteDbHelper(mcontext);
                 String verse_id = String.valueOf(model.getVerseID());






                 mvh.ayat_no.setText(Config.ENtoBN(verse_id));
                 mvh.surah_name_ayat.setText(dbHelper.getSurahName(model.getSurahID()));
                 mvh.arabic.setText(new Config(mcontext).Tajweed(model.getArabic()));

                 // Set Arabic pronunciation visibility
                 if (PronunciationUtils.isArabicPronunciationVisible(mcontext)) {
                     mvh.trans.setText(Html.fromHtml(model.getTrans()));
                     mvh.arabicMeana.setVisibility(View.VISIBLE);
                 } else {
                     mvh.trans.setText("");
                     mvh.arabicMeana.setVisibility(View.GONE);
                 }

                 //getBangla = model.getBangla().replace("০","").replace("১","").replace("২","").replace("৩","").replace("৪","").replace("৫","").replace("৬","").replace("৭","").replace("৮","").replace("৯","");


                 mvh.banglaAyat.setText(Html.fromHtml(new Config(mcontext).HideNumberBySetting(model.getBangla())+"<i><small> - তাফহীমুল কুরআন</small></i>"));
                 mvh.english.setText(Html.fromHtml(model.getEnglish()+"<i><small> - Sahih International</small></i>"));

/*
                 mvh.txtSound.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                             quranSpeech.speak(model.getArabic(), TextToSpeech.QUEUE_FLUSH, null, null);
                         }
                         else {
                             quranSpeech.speak(model.getArabic(), TextToSpeech.QUEUE_FLUSH, null);
                         }

                     }
                 });


 */
                 quranSpeech = new TextToSpeech(mcontext, status -> {
                     if(status != TextToSpeech.ERROR) {
                         quranSpeech.setLanguage(new Locale("ar_QA"));
                     }
                 });

                 mvh.arabicLayout.setOnClickListener(v -> {
                     //Toasty.success(mcontext,String.valueOf(model.getSurahID()+" = "+model.getVerseID()),Toasty.LENGTH_LONG).show();
                     String qqq = String.valueOf(dbHelper.getSurahName(model.getSurahID())+"@"+model.getSurahID()+"="+model.getVerseID());
                     //((SubjectActivity) v.getContext()).onClickCalled(qqq);
                     onClickCalled(qqq);

                 });


                 int ID = model.getId();
                 XovenHandler xovenHandler = new XovenHandler(mcontext);
                 boolean favStatus = xovenHandler.checkFav(ID);

                 if (favStatus){
                     mvh.fav.setImageResource(R.drawable.ic_baseline_favorite_24);
                 }

                 mvh.fav.setOnClickListener(v -> {
                     boolean favStatuss = xovenHandler.checkFav(ID);
                     if (favStatuss){
                         xovenHandler.deleteFav(ID);
                         mvh.fav.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                         if (v.getContext() instanceof FavActivity) {
                             ((FavActivity) v.getContext()).checkList();
                         }
                     }else {
                         xovenHandler.addFav(ID);
                         mvh.fav.setImageResource(R.drawable.ic_baseline_favorite_24);

                     }
                 });



                 mvh.vNote.setOnClickListener(v -> {
                     showNoteDialog(model.getSurahID(), model.getVerseID());
                 });

                 mvh.share.setOnClickListener(v -> {
                     Intent intent = new Intent("android.intent.action.SEND");
                     intent.setType("text/plain");
                     SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(mcontext);
                     String surahName = dbHelper.getSurahName(model.getSurahID());
                     intent.putExtra("android.intent.extra.TEXT", surahName + " : " + Config.toBangla(String.valueOf(model.getVerseID())) + "\n" + model.getArabic() + "\n\n" + new Config(mcontext).HideNumber(model.getBangla()) + "\n\nতাফহীমুল কুরআন\nhttps://play.google.com/store/apps/details?id=" + SubVerseAdapter.mcontext.getPackageName());
                     SubVerseAdapter.mcontext.startActivity(Intent.createChooser(intent, "Share the verse"));
                 });
                 mvh.copy_ayat.setOnClickListener(v -> {
                     Context context = SubVerseAdapter.mcontext;
                     SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(mcontext);
                     String surahName = dbHelper.getSurahName(model.getSurahID());
                     ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Ayah", surahName + " : " + Config.toBangla(String.valueOf(model.getVerseID())) + "\n" + model.getArabic() + "\n\n" + new Config(mcontext).HideNumber(model.getBangla()) + "\n\nতাফহীমুল কুরআন\nhttps://play.google.com/store/apps/details?id=" + SubVerseAdapter.mcontext.getPackageName()));
                     //Toast.makeText(VerseAdapter.mcontext, "This verse has been copied", Toast.LENGTH_SHORT).show();
                     Toasty.success(SubVerseAdapter.mcontext, "The verse is copied.", Toast.LENGTH_SHORT, true).show();

                 });

                 mvh.relativeLayout.setOnClickListener(v -> {

                     Intent intent = new Intent(mcontext, TafheemActivity.class);
                     intent.putExtra("surah_id",String.valueOf(model.getSurahID()));
                     intent.putExtra("verse_id",String.valueOf(model.getVerseID()));
                     intent.putExtra("arabicTxt",model.getArabic());
                     intent.putExtra("transTxt",model.getTrans());
                     intent.putExtra("banglaTxt",model.getBangla());
                     mcontext.startActivity(intent);
                 });

                 mvh.bit.setOnClickListener(v -> {

                     Intent intent = new Intent(mcontext, BitActivity.class);
                     intent.putExtra("surah_id",String.valueOf(model.getSurahID()));
                     intent.putExtra("verse_id",mvh.ayat_no.getText().toString());
                     intent.putExtra("verse_en",verse_id);
                     intent.putExtra("arabicTxt",model.getArabic());
                     intent.putExtra("banglaTxt",model.getBangla());
                     mcontext.startActivity(intent);
                 });


                 break;

             case item_banner:
             default:
                 banneraddviewholder bvh=(banneraddviewholder) holder;
         }


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


    @SuppressLint("SetTextI18n")
    public void onClickCalled(String anyValue) {
        //Toasty.success(getApplicationContext(), anyValue , Toasty.LENGTH_LONG).show();
        String[] strParts = anyValue.split("@");
        String id = strParts[1];
        String[] id_Parts = id.split("=");


        final Dialog dialog = new Dialog(mcontext);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.word_meaning_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = -2;
        lp.height = -2;

        TextView titleVerse = (TextView) dialog.findViewById(R.id.title_verse);
        titleVerse.setText(strParts[0]+" : "+Config.ENtoBN(id_Parts[1]));

        ImageView clearLayout = (ImageView) dialog.findViewById(R.id.clearLayout);
        clearLayout.setOnClickListener(v -> dialog.dismiss());

        ((ImageView) dialog.findViewById(R.id.copyLayout)).setOnClickListener(v -> {

            Context context = SubVerseAdapter.mcontext;
            ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("শব্দে শন্দে তাফহীমুল কুরআন",
                    "শব্দে শন্দে তাফহীমুল কুরআন \n"+strParts[0]+" : "+ Config.ENtoBN(id_Parts[1])+"\n"+
                            copyWord(id) ));
            Toasty.success(context, "আয়াতটি শব্দে শন্দে কপি হয়েছে", Toast.LENGTH_SHORT, true).show();
        });

        RecyclerView recycler = (RecyclerView) dialog.findViewById(R.id.wordListview);
        //recycler = (RecyclerView) findViewById(R.id.wordListview);
        WordAdapter wordAdapter = new WordAdapter(mcontext,dbHelper.getWord(id));
        LinearLayoutManager layoutManager = new LinearLayoutManager(mcontext);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(layoutManager);
        //recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //recycler.setReverseLayout(true);
        //recycler.setLayoutManager(new GridLayoutManager(this, 3));
        //recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(wordAdapter);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    private String copyWord(String any){
        StringBuilder query = new StringBuilder();
        for (int i = 0; i <  dbHelper.getWord(any).size(); i++) {
            String arabic = dbHelper.getWord(any).get(i).getArabic();
            String bangla = dbHelper.getWord(any).get(i).getBangla();
            query.append("আরাবিক - বাংলা : "+arabic+" - "+bangla).append("\n");
        }
        return query.toString();
    }





    @Override
     public int getItemCount() {
         return recycleritems.size();
     }


     public int getItemViewType(int position) {

             if(position == 0)
                 return item_banner;
             else
                 return item_data;


     }



    //U
    @Override
    public Filter getFilter() {
        return filter;
    }
    //U
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constaint) {

            List<VerseModel> filteredList = new ArrayList<>();

            if (constaint.toString().isEmpty()) {
                filteredList.addAll(patientListAll );
            } else {
                for (VerseModel patient : patientListAll ) {
                    if (patient.getArabic().toLowerCase().contains(constaint.toString().toLowerCase())
                            || patient.getBangla().toLowerCase().contains(constaint.toString().toLowerCase()) ) {
                        filteredList.add(patient);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            recycleritems.clear();
            recycleritems.addAll((Collection<? extends VerseModel>) filterResults.values);
            notifyDataSetChanged();
        }
    };





    public static class myviewholder extends RecyclerView.ViewHolder
     {
         public TextView ayat_no,surah_name_ayat,  arabic,banglaAyat,english, trans;
         //, banglaAyat, english;
         public LinearLayout relativeLayout, arabicLayout, enLayout, arabicMeana;
         public ImageView share, copy_ayat, fav, bit, vNote;


         public myviewholder(@NonNull View itemView)
         {
             super(itemView);
             this.ayat_no = (TextView) itemView.findViewById(R.id.ayat_no);
             this.surah_name_ayat = (TextView) itemView.findViewById(R.id.surah_name_ayat);
             this.arabic = (TextView) itemView.findViewById(R.id.arabic);
             this.trans = (TextView) itemView.findViewById(R.id.trans);
             this.banglaAyat = (TextView) itemView.findViewById(R.id.banglaAyat);
             this.english = (TextView) itemView.findViewById(R.id.english);
             //this.banglaAyat = (TextView) itemView.findViewById(R.id.banglaAyat);
             //this.english = (TextView) itemView.findViewById(R.id.english);
             this.fav = (ImageView) itemView.findViewById(R.id.fav);
             this.share = (ImageView) itemView.findViewById(R.id.share);
             this.copy_ayat = (ImageView) itemView.findViewById(R.id.copy_ayat);
             this.bit = (ImageView) itemView.findViewById(R.id.bit);
             this.vNote = (ImageView) itemView.findViewById(R.id.vNote);

             //this.txtSound = (ImageView) itemView.findViewById(R.id.txtSound);
             relativeLayout = (LinearLayout)itemView.findViewById(R.id.ayat_layout);
             arabicLayout = (LinearLayout)itemView.findViewById(R.id.arabicLayout);


             enLayout = (LinearLayout)itemView.findViewById(R.id.enLayout);
             arabicMeana = (LinearLayout)itemView.findViewById(R.id.arabicMeana);


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

             this.arabic.setTextSize(2, Float.valueOf(FontSize.getArabic(mcontext)));
             this.trans.setTextSize(2, Float.valueOf(FontSize.getBangla(mcontext)));
             this.banglaAyat.setTextSize(2, Float.valueOf(FontSize.getBangla(mcontext)));




         }
     }

     public static class banneraddviewholder extends RecyclerView.ViewHolder
     {
         public banneraddviewholder(@NonNull View itemView) {
             super(itemView);
         }
     }


 }
