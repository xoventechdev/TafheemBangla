package com.minbar.tafhimulquran.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.arges.sepan.argmusicplayer.Callbacks.OnCompletedListener;
import com.arges.sepan.argmusicplayer.Models.ArgAudio;
import com.arges.sepan.argmusicplayer.Models.ArgNotificationOptions;
import com.arges.sepan.argmusicplayer.PlayerViews.ArgPlayerSmallView;
import com.minbar.tafhimulquran.Adapter.AudioAdapter;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;

import java.io.File;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import nl.changer.audiowife.AudioWife;

public class AudioActivity extends AppCompatActivity {


    public static int index = -1;
    public static int top = -1;
    LinearLayoutManager mLayoutManager;
    SharedPreferences sh;

    RecyclerView recyclerView;



    ArgNotificationOptions notificationOptions;

    AudioAdapter adapter;
    SqlLiteDbHelper dbHelper;
    AudioWife audioWife;
    //ConnectivityManager connectivityManager;

    String audioUrl, audio_mishary, audio_basit, audio_bangla, surahName,filrName ;

    ArgAudio audio;
    ArgPlayerSmallView argMusicPlayer;

    int surah_id;
    LinearLayout audioOut;
    SeekBar mMediaSeekBar;
    public DownloadManager downloadManager;
    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
    File filePath;
    ImageView delete, mPlayMedia, mPauseMedia ;
    TextView mRunTime, mTotalTime;
    BroadcastReceiver onComplete = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        public void onReceive(Context ctxt, Intent intent) {
            recyclerView.invalidate();
            //recreate();
            //delete.setVisibility(View.VISIBLE);
            Toasty.success(getApplicationContext(), surahName + " download successfully", Toasty.LENGTH_SHORT, true).show();
        }
    };





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        Objects.requireNonNull(getSupportActionBar()).setTitle("অডিও কুরআন");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        audioOut = findViewById(R.id.audioOut);
        sh = getSharedPreferences("MySharedAudio",MODE_PRIVATE);

        dbHelper = SqlLiteDbHelper.getInstance(this);
        recyclerView = findViewById(R.id.audioViwer);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new AudioAdapter(this, this.dbHelper.getSurah());
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(this.adapter);


/*

        mPlayMedia = findViewById(R.id.play);
        delete = findViewById(R.id.delete);
        mPauseMedia = findViewById(R.id.pause);
        mMediaSeekBar = (SeekBar) findViewById(R.id.media_seekbar);
        mRunTime = (TextView) findViewById(R.id.run_time);
        mTotalTime = (TextView) findViewById(R.id.total_time);



 */


        argMusicPlayer = (ArgPlayerSmallView) findViewById(R.id.argmusicplayer);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {

        if (argMusicPlayer.isPlaying()){
            argMusicPlayer.stop();
        }


        //audioWife.getInstance().pause();
        //audioWife.getInstance().release();
        super.onBackPressed();
    }

    public void playAudio(String id) {

        boolean waitFor = false;

        String[] strParts = id.split("@");
        surah_id = Integer.parseInt(strParts[0]);
        surahName = strParts[1];
        @SuppressLint("DefaultLocale") String playID = String.format("%03d",surah_id);
        filrName = String.valueOf(surah_id)+".mp3";
        //Toasty.success(getApplicationContext(), filrName, Toasty.LENGTH_LONG).show();
        filePath = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + filrName);

        if (!filePath.exists()){
            //Toasty.success(getApplicationContext(), String.valueOf(filrName), Toasty.LENGTH_LONG).show();
        }else {


            audioOut.setVisibility(View.VISIBLE);
            audio = ArgAudio.createFromFilePath("", surahName, String.valueOf(filePath));
            ArgNotificationOptions notificationOptions= new ArgNotificationOptions(this);
            notificationOptions.setImageResoureId(R.drawable.logo);
            notificationOptions.setProgressEnabled(true);
            argMusicPlayer.enableNotification(notificationOptions);

            if (argMusicPlayer.isPlaying()){
                argMusicPlayer.stop();
                argMusicPlayer.play(audio);
            }else {
                argMusicPlayer.play(audio);
            };

            OnCompletedListener completedListener = () -> filePath = new File(AudioActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "xxx.mp3");
            completedListener.onCompleted();
        }
    }

    public void showdOWNLOAD(String id) {

        String[] strParts = id.split("@");
        int surah_id = Integer.parseInt(strParts[0]);
        surahName = strParts[1];

        @SuppressLint("DefaultLocale") String playID = String.format("%03d",surah_id);
        audioUrl = "https://server11.mp3quran.net/sds/"+playID+".mp3";
        audio_mishary = "https://podcasts.qurancentral.com/mishary-rashid-alafasy/mishary-rashid-alafasy-"+playID+"-muslimcentral.com.mp3";
        audio_basit = "https://podcasts.qurancentral.com/abdul-basit/abdul-basit-64-surah-"+playID+".mp3";
        audio_bangla = "http://www.truemuslims.net/Quran/Bangla/"+playID+".mp3";

        filrName = String.valueOf(surah_id)+".mp3";
        filePath = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + filrName);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.download);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = -2;
        lp.height = -2;

        AppCompatButton bt_sds = (AppCompatButton) dialog.findViewById(R.id.bt_sds);
        bt_sds.setOnClickListener(v -> {
            dowbloadFile(audioUrl);
            dialog.dismiss();
        });

        AppCompatButton bt_basit = (AppCompatButton) dialog.findViewById(R.id.bt_basit);
        bt_basit.setOnClickListener(v -> {
            dowbloadFile(audio_basit);
            dialog.dismiss();
        });

        AppCompatButton bt_mishary = (AppCompatButton) dialog.findViewById(R.id.bt_mishary);
        bt_mishary.setOnClickListener(v -> {
            dowbloadFile(audio_mishary);
            dialog.dismiss();
        });

        AppCompatButton bt_misharyBn = (AppCompatButton) dialog.findViewById(R.id.bt_misharyBn);
        bt_misharyBn.setOnClickListener(v -> {
            dowbloadFile(audio_bangla);
            dialog.dismiss();
        });

        AppCompatButton closeButton = (AppCompatButton) dialog.findViewById(R.id.popEnd);
        closeButton.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void dowbloadFile(String url){
        if (checkPermission()) {
            if (!Config.isConnected(this)) {
                Toasty.warning(getApplicationContext(), "আপনার ইন্টারনেট বদ্ধ থাকায় ডাউনলোড সম্ভব না।", Toast.LENGTH_SHORT, true).show();
                //Toast.makeText(getApplicationContext(),"আপনার ইন্টারনেট বদ্ধ থাকায় ডাউনলোড সম্ভব না।",Toast.LENGTH_SHORT).show();
            } else {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setTitle(surahName).setDescription("File is downloading...").setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, this.filrName).setNotificationVisibility(1);
                downloadManager.enqueue(request);
                Toasty.info(getApplicationContext(), surahName+ " ডাউনলোড হচ্ছে....", Toast.LENGTH_SHORT, true).show();
                //Toast.makeText(VerseActivity.this.getApplicationContext(), surah_Name + "  ডাউনলোড হচ্ছে....", Toast.LENGTH_SHORT).show();
                AudioActivity bookDetails = AudioActivity.this;
                bookDetails.registerReceiver(bookDetails.onComplete, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));
                return;
            }
        }
        requestPermission();
    }

    public boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return true;
        }
        return false;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, this.EXTERNAL_STORAGE_PERMISSION_CODE);
        //ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 100);
    }

    @Override
    public void onPause(){
        super.onPause();
        index = mLayoutManager.findFirstVisibleItemPosition();
        View v = recyclerView.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - recyclerView.getPaddingTop());
        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.putInt(surahName, index);
        myEdit.putInt(String.valueOf(surah_id),top);
        myEdit.commit();
    }

    @Override
    public void onResume(){
        super.onResume();
        index = sh.getInt(surahName, 0);
        top = sh.getInt(String.valueOf(surah_id), 0);
        if(index != -1){
            mLayoutManager.scrollToPositionWithOffset( index, top);
        }
    }
}
