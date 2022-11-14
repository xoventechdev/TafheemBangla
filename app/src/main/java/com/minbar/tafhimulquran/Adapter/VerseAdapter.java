package com.minbar.tafhimulquran.Adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Activity.BitActivity;
import com.minbar.tafhimulquran.Activity.SingleActivity;
import com.minbar.tafhimulquran.Activity.VerseActivity;
import com.minbar.tafhimulquran.Model.VerseModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.XovenHandler;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class VerseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
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
    private static int currentPlayingPosition;

    static MediaPlayer mediaPlayer;
    static String vv;
    static String singleMp3;

    static VerseModel model;

    private static VerseAdapter.myviewholder playingHolder;



     public VerseAdapter(Context context, List<VerseModel> recycleritems) {
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
     public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
         int viwtype= getItemViewType(position);

         switch (viwtype){
             case item_data:
                 myviewholder mvh  =(myviewholder) holder;
                 model= (VerseModel) recycleritems.get(position);

                 dbHelper = new SqlLiteDbHelper(mcontext);
                 String verse_id = String.valueOf(model.getVerseID());


                 mvh.ayat_no.setText(Config.ENtoBN(verse_id));
                 mvh.arabic.setText(model.getArabic());
                 mvh.trans.setText(Html.fromHtml(model.getTrans()));
                 mvh.banglaAyat.setText(Html.fromHtml(new Config(mcontext).HideNumber(model.getBangla())+"<i><small> - তাফহীমুল কুরআন</small></i>"));
                 mvh.english.setText(Html.fromHtml(model.getEnglish()+"<i><small> - Sahih International</small></i>"));


/*
                 if (position == currentPlayingPosition) {
                     playingHolder = mvh;
                 } else {
                     updateNonPlayingView(mvh);
                 }


 */







                 mvh.playSingle.setOnClickListener(v -> {

                     if(model.getSurahID()==1 || model.getSurahID()==9){
                         vv  = String.format("%03d", model.getSurahID())+String.format("%03d", position+1);
                     }else {
                         vv  = String.format("%03d", model.getSurahID())+String.format("%03d", position);
                     }
                     //singleMp3 = "https://www.everyayah.com/data/Alafasy_64kbps/"+vv+".mp3";

                     singleMp3 = vv+".mp3";
                     File filePath = new File(mcontext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator+ String.format("%03d", model.getSurahID()) + File.separator  + singleMp3);
                     Toasty.warning(mcontext, String.valueOf(filePath), Toasty.LENGTH_SHORT).show();
                     if (filePath.exists()){

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
                                 //mvh.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_play);
                                 mediaPlayer.release();
                             }
                             PlaySound(String.valueOf(filePath), mvh.playSingle);//put your audio file
                         }
                         if (mediaPlayer != null){
                             if (mediaPlayer.isPlaying()) {
                                 mvh.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_pause);
                             } else {
                                 mvh.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_play);
                             }
                         }



                     }else {
                         Toasty.warning(mcontext, "Please, Download File", Toasty.LENGTH_SHORT).show();
                     }



                     //if (mediaPlayer != null)
                        // updatePlayingView();


        /*
                     if (position == currentPlayingPosition) {
                         if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                             mediaPlayer.pause();
                             mvh.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_play);
                         } else {
                             if (mediaPlayer != null) {
                                 if (null != playingHolder) {
                                     updateNonPlayingView(playingHolder);
                                 }
                                 mediaPlayer.release();
                             }
                                 mediaPlayer.start();
                             mvh.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_pause);
                         }
                     } else {
                         currentPlayingPosition = position;
                         if (mediaPlayer != null) {
                             mediaPlayer.release();
                         }
                         mediaPlayer = MediaPlayer.create(mcontext, Uri.parse(singleMp3));
                         mediaPlayer.setOnCompletionListener(mp ->{
                             releaseMediaPlayer();
                             mvh.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_play);
                          }
                         );
                         mediaPlayer.start();
                         mvh.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_pause);
                     }



         */
                 });






                 quranSpeech = new TextToSpeech(mcontext, status -> {
                     if(status != TextToSpeech.ERROR) {
                         quranSpeech.setLanguage(new Locale("ar_QA"));
                     }
                 });

                 mvh.arabicLayout.setOnClickListener(v -> {
                     //Toasty.success(mcontext,String.valueOf(model.getSurahID()+" = "+model.getVerseID()),Toasty.LENGTH_LONG).show();
                     String qqq = String.valueOf(model.getSurahID()+"="+verse_id);
                     ((VerseActivity) v.getContext()).wordByword(qqq);

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
                     }else {
                         xovenHandler.addFav(ID);
                         mvh.fav.setImageResource(R.drawable.ic_baseline_favorite_24);
                     }
                 });

                 mvh.share.setOnClickListener(v -> {
                     Intent intent = new Intent("android.intent.action.SEND");
                     intent.setType("text/plain");
                     intent.putExtra("android.intent.extra.TEXT", VerseActivity.surah_Name +" : "+mvh.ayat_no.getText().toString()+"\n" + mvh.arabic.getText().toString() + "\n" + getBangla + "\n\n"+"তাফহীমুল কুরআন"+"\nhttp://play.google.com/store/apps/details?id=" + VerseAdapter.mcontext.getPackageName());
                     VerseAdapter.mcontext.startActivity(Intent.createChooser(intent, "আয়াতটি শেয়ার করুন"));
                 });
                 mvh.copy_ayat.setOnClickListener(v -> {
                     Context context = VerseAdapter.mcontext;
                     ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Ayah", VerseActivity.surah_Name +" : "+mvh.ayat_no.getText().toString()+"\n" + mvh.arabic.getText().toString() + "\n" + getBangla + "\n\n"+"তাফহীমুল কুরআন"+"\nhttp://play.google.com/store/apps/details?id=" + VerseAdapter.mcontext.getPackageName()));
                     //Toast.makeText(VerseAdapter.mcontext, "This verse has been copied", Toast.LENGTH_SHORT).show();
                     Toasty.success(VerseAdapter.mcontext, "আয়াত কপি হয়েছে", Toast.LENGTH_SHORT, true).show();

                 });

                 mvh.relativeLayout.setOnClickListener(v -> {

                     Intent intent = new Intent(mcontext, SingleActivity.class);
                     //Intent intent = new Intent(mcontext, TafheemActivity.class);
                     intent.putExtra("surah_id",String.valueOf(model.getSurahID()));
                     intent.putExtra("verse_id",position);
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

    private static void updateNonPlayingView(VerseAdapter.myviewholder mvh) {
        mvh.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_play);
    }





    @Override
     public int getItemCount() {
         return recycleritems.size();
     }


     public int getItemViewType(int position) {
         if(VerseActivity.surahid==1 || VerseActivity.surahid==9){
                 return item_data;
         } else {
             if(position == 0)
                 return item_banner;
             else
                 return item_data;
         }

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





    public static class myviewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
         public TextView ayat_no, arabic,banglaAyat,english, trans;
         //, banglaAyat, english;
         public LinearLayout relativeLayout, arabicLayout, enLayout, arabicMeana, banglaOnubadh;
         public ImageView share, copy_ayat, fav, bit, playSingle;


         public myviewholder(@NonNull View itemView)
         {
             super(itemView);
             this.ayat_no = (TextView) itemView.findViewById(R.id.ayat_no);
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
             this.playSingle = (ImageView) itemView.findViewById(R.id.playSingle);
             //this.playSingle.setOnClickListener(this);
             relativeLayout = (LinearLayout)itemView.findViewById(R.id.ayat_layout);
             arabicLayout = (LinearLayout)itemView.findViewById(R.id.arabicLayout);
             enLayout = (LinearLayout)itemView.findViewById(R.id.enLayout);
             arabicMeana = (LinearLayout)itemView.findViewById(R.id.arabicMeana);
             banglaOnubadh = (LinearLayout)itemView.findViewById(R.id.VerseTai);



             Config.BanglaOnubadh(banglaOnubadh, mcontext);


             SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(VerseAdapter.mcontext);
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

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.playSingle: {


                    if(model.getSurahID()==1 || model.getSurahID()==9){
                        vv  = String.format("%03d", model.getSurahID())+String.format("%03d", model.getVerseID()-1);
                    }else {
                        vv  = String.format("%03d", model.getSurahID())+String.format("%03d", model.getVerseID());
                    }
                    singleMp3 = "https://www.everyayah.com/data/Alafasy_64kbps/"+vv+".mp3";

                    Toasty.success(mcontext, singleMp3, Toasty.LENGTH_SHORT).show();

                    if (getAdapterPosition() == currentPlayingPosition) {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        } else {
                            if (mediaPlayer != null)
                                mediaPlayer.start();
                        }
                    } else {
                        currentPlayingPosition = getAdapterPosition();
                        if (mediaPlayer != null) {
                            if (null != playingHolder) {
                                updateNonPlayingView(playingHolder);
                            }
                            mediaPlayer.release();
                        }
                        playingHolder = this;

                        //PlaySound(singleMp3);//put your audio file
                    }


                    if (mediaPlayer != null){
                        if (mediaPlayer.isPlaying()) {
                            playingHolder.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_pause);
                        } else {
                            playingHolder.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_play);
                        }
                    }
                }
                break;
            }

        }
    }

     public static class banneraddviewholder extends RecyclerView.ViewHolder{
         public banneraddviewholder(@NonNull View itemView) {
             super(itemView);
         }
     }


    private static void PlaySound(String filesound, ImageView imageView) {
        mediaPlayer = MediaPlayer.create(mcontext, Uri.parse(filesound));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                releaseMediaPlayer();
                imageView.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_play);
            }
        });
        mediaPlayer.start();

    }


    public static void releaseMediaPlayer() {
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        currentPlayingPosition = -1;

    }





 }
