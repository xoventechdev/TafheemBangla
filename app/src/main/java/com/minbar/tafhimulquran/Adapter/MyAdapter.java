package com.minbar.tafhimulquran.Adapter;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Model.VerseModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter  extends RecyclerView.Adapter<MyAdapter.AudioItemsViewHolder> {

    static  Context mcontext;
    static MediaPlayer mediaPlayer;

    private  List<VerseModel> recycleritems = new ArrayList<>();
    private List<VerseModel> patientListAll = new ArrayList<>();

    VerseModel model;
    SqlLiteDbHelper dbHelper;
    String vv, singleMp3;


    private int currentPlayingPosition = -1;
    private AudioItemsViewHolder playingHolder;

    public MyAdapter(Context context, List<VerseModel> recycleritems) {
        mcontext = context;
        this.recycleritems = recycleritems;
        //this.recycleritems = recycleritems;   //U
        this.patientListAll = new ArrayList<>(recycleritems);

    }



    @Override
    public AudioItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //put YourItemsLayout;
        return new AudioItemsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ayat, parent, false));
    }

    @Override
    public void onBindViewHolder(AudioItemsViewHolder holder, int position) {

        model= (VerseModel) recycleritems.get(position);

        if(model.getSurahID()==1 || model.getSurahID()==9){
            vv  = String.format("%03d", model.getSurahID())+String.format("%03d", position+1);
        }else {
            vv  = String.format("%03d", model.getSurahID())+String.format("%03d", position);
        }
        singleMp3 = "https://www.everyayah.com/data/Alafasy_64kbps/"+vv+".mp3";



        dbHelper = new SqlLiteDbHelper(mcontext);
        String verse_id = String.valueOf(model.getVerseID());


        holder.ayat_no.setText(Config.ENtoBN(verse_id));
        holder.arabic.setText(new Config(mcontext).Tajweed(model.getArabic()));
        holder.trans.setText(Html.fromHtml(model.getTrans()));
        holder.banglaAyat.setText(Html.fromHtml(new Config(mcontext).HideNumber(model.getBangla())+"<i><small> - তাফহীমুল কুরআন</small></i>"));
        holder.english.setText(Html.fromHtml(model.getEnglish()+"<i><small> - Sahih International</small></i>"));






        if (position == currentPlayingPosition) {
            playingHolder = holder;
            updatePlayingView();
        } else {
            updateNonPlayingView(holder);
        }
    }
    private void updateNonPlayingView(AudioItemsViewHolder mvh) {
       mvh.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_play);
    }

    private void updatePlayingView() {
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying()) {
                playingHolder.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_pause);
            } else {
                playingHolder.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_play);
            }
        }else {
            playingHolder.playSingle.setImageResource(com.arges.sepan.argmusicplayer.R.drawable.arg_play);
        }
    }


    @Override
    public int getItemCount() {
        return recycleritems.size();
    }

    class AudioItemsViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{

        public TextView ayat_no, arabic,banglaAyat,english, trans;
        public LinearLayout relativeLayout, arabicLayout, enLayout, arabicMeana, banglaOnubadh;
        public ImageView share, copy_ayat, fav, bit, playSingle;

        AudioItemsViewHolder(View itemView) {

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
            this.playSingle.setOnClickListener(this);
            relativeLayout = (LinearLayout)itemView.findViewById(R.id.ayat_layout);
            arabicLayout = (LinearLayout)itemView.findViewById(R.id.arabicLayout);
            enLayout = (LinearLayout)itemView.findViewById(R.id.enLayout);
            arabicMeana = (LinearLayout)itemView.findViewById(R.id.arabicMeana);

            banglaOnubadh = (LinearLayout)itemView.findViewById(R.id.VerseTai);

            Config.BanglaOnubadh(banglaOnubadh, mcontext);


        }
        @Override
        public void onClick(View v) {


            switch (v.getId()) {
                case R.id.playSingle: {
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




                        PlaySound(singleMp3);//put your audio file


                    }
                    if (mediaPlayer != null)
                        updatePlayingView();
                }
                break;
            }


        }


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
    private void releaseMediaPlayer() {
        if (null != playingHolder) {
            updateNonPlayingView(playingHolder);
        }

        mediaPlayer.release();
        mediaPlayer = null;
        currentPlayingPosition = -1;
    }


}
