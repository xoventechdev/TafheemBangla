package com.minbar.tafhimulquran.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.appbar.MaterialToolbar;
import com.minbar.tafhimulquran.Adapter.MyAdapter;
import com.minbar.tafhimulquran.Adapter.VerseAdapter;
import com.minbar.tafhimulquran.Adapter.WordAdapter;
import com.minbar.tafhimulquran.Model.VerseModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.databinding.ActivityVerseBinding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import nl.changer.audiowife.AudioWife;

public class VerseActivity extends AppCompatActivity {

    ActivityVerseBinding binding;
    static  public  int surahid;
    static  public String surah_Name;
    String playID;
    ImageView delete;
    String audioUrl, audio_mishary, audio_basit,audio_bangla ;
    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
    public DownloadManager downloadManager;
    String filrName;


    ProgressDialog progressDoalog;

    int asa;
    int jhhh = 0;
    SharedPreferences sp;

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        public void onReceive(Context ctxt, Intent intent) {
            //progressDoalog.setProgress(1);



/*
            asa = sp.getInt("tttttt",0);
            jhhh = asa+1;
            sp.edit().putInt("tttttt", jhhh).apply();
            progressDoalog.setProgress(jhhh);

            if (jhhh==totalVerse){
                Toast.makeText(getApplicationContext(), String.valueOf(jhhh),Toast.LENGTH_SHORT).show();
                progressDoalog.cancel();
            }


 */

            delete.setVisibility(View.VISIBLE);
            //Toast.makeText(getApplicationContext(), surah_Name + " ডাউনলোড হয়েছে",Toast.LENGTH_SHORT).show();
            //Toasty.success(getApplicationContext(), surah_Name + " download successfully", Toasty.LENGTH_SHORT, true).show();
        }
    };

    File filePath;
    VerseAdapter adapter;
    MaterialToolbar toolbar;
    AudioWife audioWife;


    RecyclerView recyclerView;
    String surah_id;

    SqlLiteDbHelper mDatabase;

    public static int index = -1;
    public static int top = -1;
    LinearLayoutManager mLayoutManager;
    SharedPreferences sh;


    WordAdapter wordAdapter;
    LinearLayoutManager layoutManager;
    String location, ayatCount;

    boolean isUp;
    int totalVerse;

    ArrayList<VerseModel> verseModels;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verse);


        sp = PreferenceManager.getDefaultSharedPreferences(this);


        sh = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences defaultSharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        if (defaultSharedPreferences.getString("audioHide", "on").equals("off")) {
            binding.audioContorl.setVisibility(View.GONE);
            if (defaultSharedPreferences.getString("scollingHide", "on").equals("off")) {
                binding.lowerOut.setVisibility(View.GONE);
            }else {
                binding.lowerOut.setVisibility(View.VISIBLE);
            }
        }else {
            binding.audioContorl.setVisibility(View.VISIBLE);
            binding.lowerOut.setVisibility(View.GONE);

        }

        if (defaultSharedPreferences.getString("scollingHide", "on").equals("off")) {
            binding.upOut.setVisibility(View.GONE);
        }else {
            binding.upOut.setVisibility(View.VISIBLE);
        }






        this.downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Intent is = getIntent();
        surah_id = is.getStringExtra("surah_id");
        surahid = Integer.parseInt(is.getStringExtra("surah_id"));
        surah_Name = is.getStringExtra("surah_Name");
        location = is.getStringExtra("location");
        ayatCount = is.getStringExtra("ayatCount");

        toolbar = findViewById(R.id.toolBar);


        if(toolbar != null){
            setSupportActionBar(toolbar);
        }
        toolbar.post(new Runnable(){
            @Override
            public void run(){
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(surah_Name);
                getSupportActionBar().setSubtitle(ayatCount);
            }
        });


        mDatabase = new SqlLiteDbHelper(this);
        verseModels = mDatabase.getAyat(surahid);




        playID = String.format("%03d", surahid);
        //https://server11.mp3quran.net/sds/111.mp3
        audioUrl = "https://server11.mp3quran.net/sds/"+playID+".mp3";
        //https://podcasts.qurancentral.com/mishary-rashid-alafasy/mishary-rashid-alafasy-111-muslimcentral.com.mp3
        audio_mishary = "https://podcasts.qurancentral.com/mishary-rashid-alafasy/mishary-rashid-alafasy-"+playID+"-muslimcentral.com.mp3";
        //https://podcasts.qurancentral.com/abdul-basit/abdul-basit-64-surah-111.mp3
        audio_basit = "https://podcasts.qurancentral.com/abdul-basit/abdul-basit-64-surah-"+playID+".mp3";
        //http://www.truemuslims.net/Quran/Bangla/111.mp3
        audio_bangla = "http://www.truemuslims.net/Quran/Bangla/"+playID+".mp3";


        filrName = String.valueOf(surahid)+".mp3";
        filePath = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + this.filrName);



        if(surahid==9){
            totalVerse = verseModels.size();
            binding.seekVerse.setMin(0);
            binding.seekVerse.setMax(verseModels.size()-1);
            binding.totalItem.setText(Config.ENtoBN(String.valueOf(verseModels.size())));
        }else {
            totalVerse = verseModels.size()-1;
            binding.seekVerse.setMin(1);
            binding.seekVerse.setMax(verseModels.size());
            binding.totalItem.setText(Config.ENtoBN(String.valueOf(verseModels.size()-1)));
        }

       // Toasty.success(getApplicationContext(), String.valueOf(totalVerse),Toasty.LENGTH_SHORT).show();





        ImageView mPlayMedia = findViewById(R.id.play);
        delete = findViewById(R.id.delete);
        ImageView mPauseMedia = findViewById(R.id.pause);
        SeekBar mMediaSeekBar = (SeekBar) findViewById(R.id.media_seekbar);
        TextView mRunTime = (TextView) findViewById(R.id.run_time);
        TextView mTotalTime = (TextView) findViewById(R.id.total_time);


        binding.upOut.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_white);

        binding.upOut.setOnClickListener(v -> {

            if (isUp) {
                viewGoneAnimator(binding.lowerOut);
                binding.upOut.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_white);

                //myButton.setText("Slide up");
            } else {
                viewVisibleAnimator(binding.lowerOut);
                binding.upOut.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_white);

                //myButton.setText("Slide down");
            }
            isUp = !isUp;
        });



        if (filePath.exists()){
            audioWife.getInstance()
                    .init(VerseActivity.this, Uri.fromFile(filePath))
                    .setPlayView(mPlayMedia)
                    .setPauseView(mPauseMedia)
                    .setSeekBar(mMediaSeekBar)
                    .setRuntimeView(mRunTime)
                    .setTotalTimeView(mTotalTime);
        }

        mPlayMedia.setOnClickListener(v -> {
            if (!filePath.exists()){
                showdOWNLOAD();
                //askFile();
            }else {
                audioWife.getInstance()
                        .init(VerseActivity.this, Uri.fromFile(filePath))
                        .setPlayView(mPlayMedia)
                        .setPauseView(mPauseMedia)
                        .setSeekBar(mMediaSeekBar)
                        .setRuntimeView(mRunTime)
                        .setTotalTimeView(mTotalTime);
            }


        });


        /*
        File filePathaaaa = null;
        String fileNeme = null;
        for (int i = 1; i < totalVerse+1; i++) {
            fileNeme = playID + String.format("%03d", i) + ".mp3";
            filePathaaaa = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + playID + File.separator + fileNeme);

        }



         */


        if (filePath.exists()){
            delete.setVisibility(View.VISIBLE);
        }
        delete.setOnClickListener(v -> {
            deleteFile();
            delete.setVisibility(View.GONE);
            //if (filePath.exists()){   filePath.delete();   }
        });



        recyclerView = (RecyclerView) findViewById(R.id.verseViwer);
        adapter = new VerseAdapter(this,verseModels);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);



        binding.seekVerse.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if( surahid==9){
                    binding.nowItem.setText(Config.ENtoBN(String.valueOf(progress+1)));
                }else {
                    binding.nowItem.setText(Config.ENtoBN(String.valueOf(progress)));
                }
                recyclerView.scrollToPosition(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });





    }



    public void deleteFile(){

        String fileNeme = null;
        for (int i = 1; i < totalVerse+1; i++) {
            fileNeme = playID + String.format("%03d", i) + ".mp3";
            File filePath = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + playID + File.separator + fileNeme);
            if (filePath.exists()){
                filePath.delete();
            }
        }
    }

    public void askFile(){

        File mListofFiles = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + playID);
        File f = new File(mListofFiles.toString());
        File file[] = f.listFiles();
        Toasty.success(getApplicationContext(), String.valueOf(file.length), Toasty.LENGTH_SHORT).show();

        if (file.length==totalVerse){


            Toasty.success(getApplicationContext(), "all okey", Toasty.LENGTH_SHORT).show();
        }else {
            if (checkPermission()) {
                if (!Config.isConnected(this)) {
                    Toasty.warning(getApplicationContext(), "আপনার ইন্টারনেট বদ্ধ থাকায় ডাউনলোড সম্ভব না।", Toast.LENGTH_SHORT, true).show();
                } else {
                    progressDoalog = new ProgressDialog(VerseActivity.this);
                    progressDoalog.setMax(totalVerse);
                    progressDoalog.setMessage("Its loading....");
                    progressDoalog.setTitle("ProgressDialog bar example");
                    progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    String fileUrl = null, fileNeme = null;

                    progressDoalog.show();
                    for (int i = 1; i < totalVerse+1; i++) {
                        fileUrl = "http://www.everyayah.com/data/Alafasy_64kbps/"+playID+String.format("%03d", i)+".mp3";
                        fileNeme = playID+String.format("%03d", i)+".mp3";
                        File filePath = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + playID + File.separator + fileNeme);
                        if (!filePath.exists()){
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
                            request.setTitle(surah_Name).setDescription("File is downloading...").setDestinationInExternalFilesDir(VerseActivity.this, Environment.DIRECTORY_DOWNLOADS,  playID + File.separator +fileNeme).setNotificationVisibility(1);
                            VerseActivity.this.downloadManager.enqueue(request);
                            VerseActivity bookDetails = VerseActivity.this;
                            bookDetails.registerReceiver(bookDetails.onComplete, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));
                        }
                    }
                    return;
                }
            }
            requestPermission();
        }






        /*
        boolean persent = true;
        String fileNemee = null;
        for (int a = 1; a < totalVerse+1; a++) {
            fileNemee = playID + String.format("%03d", a) + ".mp3";

            File fileqq = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + fileNemee);



            File[] filePathh = new File[0];
            if (filePathh.length<totalVerse){
                persent = false;



            }
        }

        if (persent){
            Toasty.success(getApplicationContext(),"100%", Toasty.LENGTH_SHORT).show();
        }



         */

    }




    private void viewGoneAnimator(final View view) {
        view.animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }
    private void viewVisibleAnimator(final View view) {
        view.animate().alpha(1f).setDuration(500).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }
                });
    }
    @SuppressLint("SetTextI18n")
    public void wordByword(String anyValue) {
        //Toasty.success(getApplicationContext(), anyValue , Toasty.LENGTH_LONG).show();
        String[] strParts = anyValue.split("=");
        int surah = Integer.parseInt(strParts[0]);
        String verseBN = strParts[1];

        final Dialog dialog = new Dialog(this);
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
        titleVerse.setText(surah_Name+" : "+Config.ENtoBN(verseBN));


        ((ImageView) dialog.findViewById(R.id.clearLayout)).setOnClickListener(v -> {
            dialog.dismiss();
        });
        ((ImageView) dialog.findViewById(R.id.copyLayout)).setOnClickListener(v -> {
            ((ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("শব্দে শন্দে তাফহীমুল কুরআন",
                    "শব্দে শন্দে তাফহীমুল কুরআন \n"+surah_Name+" : "+Config.ENtoBN(verseBN)+"\n"+
                    copyWord(anyValue) ));
            Toasty.success(this, "আয়াতটি শব্দে শন্দে কপি হয়েছে", Toast.LENGTH_SHORT, true).show();

        });

        RecyclerView recycler = (RecyclerView) dialog.findViewById(R.id.wordListview);
        wordAdapter = new WordAdapter(this,mDatabase.getWord(anyValue));
        layoutManager = new LinearLayoutManager(this);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(wordAdapter);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private String copyWord(String any){
        StringBuilder query = new StringBuilder();
        for (int i = 0; i <  mDatabase.getWord(any).size(); i++) {
            String arabic = mDatabase.getWord(any).get(i).getArabic();
            String bangla = mDatabase.getWord(any).get(i).getBangla();
            query.append("আরাবিক - বাংলা : ").append(arabic).append(" - ").append(bangla).append("\n");
        }
        return query.toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                aboutShow(surahid);
                //Toasty.success(getApplicationContext(), "Okey", Toasty.LENGTH_LONG).show();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void aboutShow(int s) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.about_surah_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = -1;
        lp.height = -1;

        ((TextView) dialog.findViewById(R.id.title_about)).setText(surah_Name+" এর ভূমিকা");


        ((ImageView) dialog.findViewById(R.id.clear_about)).setOnClickListener(v -> dialog.dismiss());


        TextView aboutContent = (TextView) dialog.findViewById(R.id.aboutContent);
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < mDatabase.getAboutContent(s).size(); i++) {
            String sss = mDatabase.getAboutContent(s).get(i).toString();
            String[] strParts = sss.split("@");
            query.append(strParts[1]).append("<br>");
        }
        String main = query.toString().replace("\\n","<br>");
        aboutContent.setText(Html.fromHtml(main));
        aboutContent.setTypeface(FontFamily.getBangla(this));
        aboutContent.setTextSize(2, Float.valueOf(FontSize.getArabic(this)));

        ((ImageView) dialog.findViewById(R.id.copyLayout)).setOnClickListener(v -> {
            ((ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(mDatabase.getSurahName(surahid)+" এর ভূমিকা", mDatabase.getSurahName(surahid) +"  এর ভূমিকা"+"\n" + aboutContent.getText().toString() + "\n\n"+"তাফহীমুল কুরআন"+"\nhttp://play.google.com/store/apps/details?id=" + this.getPackageName()));
            //Toast.makeText(VerseAdapter.mcontext, "This verse has been copied", Toast.LENGTH_SHORT).show();
            Toasty.success(this, "ভূমিকা কপি হয়েছে", Toast.LENGTH_SHORT, true).show();
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void onBackPressed() {
        audioWife.getInstance().release();
        super.onBackPressed();
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





    public void showdOWNLOAD() {
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
                request.setTitle(surah_Name).setDescription("File is downloading...").setDestinationInExternalFilesDir(VerseActivity.this, Environment.DIRECTORY_DOWNLOADS, VerseActivity.this.filrName).setNotificationVisibility(1);
                VerseActivity.this.downloadManager.enqueue(request);
                Toasty.info(getApplicationContext(), surah_Name+ " ডাউনলোড হচ্ছে....", Toast.LENGTH_SHORT, true).show();
                //Toast.makeText(VerseActivity.this.getApplicationContext(), surah_Name + "  ডাউনলোড হচ্ছে....", Toast.LENGTH_SHORT).show();
                VerseActivity bookDetails = VerseActivity.this;
                bookDetails.registerReceiver(bookDetails.onComplete, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));
                return;
            }
        }
        requestPermission();
    }


    @Override
    public void onPause(){
        super.onPause();
        index = mLayoutManager.findFirstVisibleItemPosition();
        View v = recyclerView.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - recyclerView.getPaddingTop());
        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.putInt(surah_Name, index);
        myEdit.putInt(surah_id,top);
        myEdit.apply();

        sp.edit().putString("surah_id", surah_id).apply();
        sp.edit().putString("surah_Name", surah_Name).apply();
        sp.edit().putString("ayatCount", ayatCount).apply();
        sp.edit().putString("location", location).apply();
    }

    @Override
    public void onResume(){
        super.onResume();
        index = sh.getInt(surah_Name, 0);
        top = sh.getInt(surah_id, 0);
        if(index != -1)
        {
            mLayoutManager.scrollToPositionWithOffset( index, top);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.releaseMediaPlayer(); // calling the method inside your adapter
    }
}