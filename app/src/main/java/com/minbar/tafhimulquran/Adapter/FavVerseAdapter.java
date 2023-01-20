package com.minbar.tafhimulquran.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.minbar.tafhimulquran.Activity.FavActivity;
import com.minbar.tafhimulquran.Activity.TafheemActivity;
import com.minbar.tafhimulquran.Activity.VerseActivity;
import com.minbar.tafhimulquran.Model.VerseModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;
import com.minbar.tafhimulquran.Utils.QuranArabicUtils;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.XovenHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class FavVerseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
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

     public FavVerseAdapter(Context context, List<VerseModel> recycleritems) {
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
                 mvh.trans.setText(Html.fromHtml(model.getTrans()));

                // getBangla = model.getBangla().replace("০","").replace("১","").replace("২","").replace("৩","").replace("৪","").replace("৫","").replace("৬","").replace("৭","").replace("৮","").replace("৯","");



                 mvh.banglaAyat.setText(Html.fromHtml(new Config(mcontext).HideNumber(model.getBangla())+"<i><small> - তাফহীমুল কুরআন</small></i>"));
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
                 quranSpeech = new TextToSpeech(mcontext, new TextToSpeech.OnInitListener() {
                     @Override
                     public void onInit(int status) {
                         if(status != TextToSpeech.ERROR) {
                             quranSpeech.setLanguage(new Locale("ar_QA"));
                         }
                     }
                 });

                 mvh.arabicLayout.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         //Toasty.success(mcontext,String.valueOf(model.getSurahID()+" = "+model.getVerseID()),Toasty.LENGTH_LONG).show();
                         String qqq = String.valueOf(dbHelper.getSurahName(model.getSurahID())+"@"+model.getSurahID()+"="+model.getVerseID());
                         ((FavActivity) v.getContext()).onClickCalled(qqq);

                     }
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
                         notifytoview(mvh.getAdapterPosition());
                         ((FavActivity) v.getContext()).checkList();
                     }else {
                         xovenHandler.addFav(ID);
                         mvh.fav.setImageResource(R.drawable.ic_baseline_favorite_24);

                     }
                 });

                 mvh.share.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         Intent intent = new Intent("android.intent.action.SEND");
                         intent.setType("text/plain");
                         intent.putExtra("android.intent.extra.TEXT", VerseActivity.surah_Name +" : "+mvh.ayat_no.getText().toString()+"\n" + mvh.arabic.getText().toString() + "\n" + mvh.banglaAyat.getText().toString() + "\n\n"+"তাফহীমুল কুরআন"+"\nhttp://play.google.com/store/apps/details?id=" + FavVerseAdapter.mcontext.getPackageName());
                         FavVerseAdapter.mcontext.startActivity(Intent.createChooser(intent, "Share the verse"));
                     }
                 });
                 mvh.copy_ayat.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         Context context = FavVerseAdapter.mcontext;
                         ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Ayah", VerseActivity.surah_Name +" : "+mvh.ayat_no.getText().toString()+"\n" + mvh.arabic.getText().toString() + "\n" + mvh.banglaAyat.getText().toString() + "\n\n"+"তাফহীমুল কুরআন"+"\nhttp://play.google.com/store/apps/details?id=" + FavVerseAdapter.mcontext.getPackageName()));
                         //Toast.makeText(VerseAdapter.mcontext, "This verse has been copied", Toast.LENGTH_SHORT).show();
                         Toasty.success(FavVerseAdapter.mcontext, "The verse is copied.", Toast.LENGTH_SHORT, true).show();

                     }
                 });

                 mvh.relativeLayout.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {

                         Intent intent = new Intent(mcontext, TafheemActivity.class);
                         intent.putExtra("surah_id",String.valueOf(model.getSurahID()));
                         intent.putExtra("verse_id",String.valueOf(model.getVerseID()));
                         intent.putExtra("arabicTxt",model.getArabic());
                         intent.putExtra("transTxt",model.getTrans());
                         intent.putExtra("banglaTxt",model.getBangla());
                         mcontext.startActivity(intent);
                     }
                 });

                 break;

             case item_banner:
             default:
                 banneraddviewholder bvh=(banneraddviewholder) holder;
                 //AdView adView =(AdView) recycleritems.get(position);

                 /*
                 ViewGroup adcardview= (ViewGroup) bvh.itemView;

                 if(adcardview.getChildCount()>0)
                 adcardview.removeAllViews();
                 if(adcardview.getParent()!=null)
                     ((ViewGroup)adView.getParent()).removeView(adView);

                 adcardview.addView(adView);

                  */
         }


     }

    public void notifytoview(int position) {
        recycleritems.remove(position);
        notifyItemRemoved(position);
    }


    @Override
     public int getItemCount() {
         return recycleritems.size();
     }


     public int getItemViewType(int position) {
         return item_data;
         /*
         if(VerseActivity.surahid==9){
                 return item_data;
         } else {
             if(position == 0)
                 return item_banner;
             else
                 return item_data;
         }


          */
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
         public LinearLayout relativeLayout, arabicLayout, enLayout, arabicMeana, banglaOnubadh;
         public ImageView share, copy_ayat, fav, txtSound;


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
             //this.txtSound = (ImageView) itemView.findViewById(R.id.txtSound);
             relativeLayout = (LinearLayout)itemView.findViewById(R.id.ayat_layout);
             arabicLayout = (LinearLayout)itemView.findViewById(R.id.arabicLayout);


             enLayout = (LinearLayout)itemView.findViewById(R.id.enLayout);
             arabicMeana = (LinearLayout)itemView.findViewById(R.id.arabicMeana);
             banglaOnubadh = (LinearLayout)itemView.findViewById(R.id.VerseTai);

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
