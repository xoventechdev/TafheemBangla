package com.minbar.tafhimulquran.Activity;

import androidx.annotation.NonNull;
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
import android.app.AlertDialog;
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
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
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
import com.minbar.tafhimulquran.Utils.ThemeManager;
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
    private static final int STORAGE_PERMISSION_CODE = 100;

    public DownloadManager downloadManager;
    String filrName;


    ProgressDialog progressDoalog;

    int asa;
    int jhhh = 0;
    SharedPreferences sp;

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            if (binding != null) {
                binding.delete.setVisibility(View.VISIBLE);
                binding.play.setVisibility(View.VISIBLE);
                binding.download.setVisibility(View.GONE);
                
                // Re-initialize AudioWife after download
                initAudioPlayer();

            }
            Toasty.success(getApplicationContext(), surah_Name + " ডাউনলোড হয়েছে", Toast.LENGTH_SHORT, true).show();
        }
    };

    File filePath;
    VerseAdapter adapter;
    MaterialToolbar toolbar;


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
        ThemeManager.applyTheme(this);
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

        audioUrl = "https://server11.mp3quran.net/sds/"+playID+".mp3";
        audio_mishary = "https://podcasts.qurancentral.com/mishary-rashid-alafasy/mishary-rashid-alafasy-"+playID+"-muslimcentral.com.mp3";
        audio_basit = "https://podcasts.qurancentral.com/abdul-basit/"+playID+".mp3";
        audio_bangla = "https://www.truemuslims.net/Quran/Bangla/"+playID+".mp3";


        filrName = String.valueOf(surahid)+".mp3";
        filePath = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + this.filrName);



        if(surahid==9 || surahid == 1){
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





        ImageView mPlayMedia = binding.play;
        delete = binding.delete;
        ImageView mPauseMedia = binding.pause;
        ImageView mDownloadMedia = binding.download;
        SeekBar mMediaSeekBar = binding.mediaSeekbar;
        TextView mRunTime = binding.runTime;
        TextView mTotalTime = binding.totalTime;


        binding.upOut.setOnClickListener(v -> {

            if (isUp) {
                viewGoneAnimator(binding.lowerOut);
                binding.upOut.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_white);
            } else {
                viewVisibleAnimator(binding.lowerOut);
                binding.upOut.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_white);
            }
            isUp = !isUp;
        });



        if (filePath.exists()) {
            mPlayMedia.setVisibility(View.VISIBLE);
            mDownloadMedia.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);

            initAudioPlayer();
        } else {
            mPlayMedia.setVisibility(View.GONE);
            mDownloadMedia.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
            binding.mediaSeekbar.setEnabled(false);
        }

        mDownloadMedia.setOnClickListener(v -> {
            showdOWNLOAD();
        });

        mPlayMedia.setOnClickListener(v -> {
            if (filePath.exists()) {
                initAudioPlayer();
            }
        });


        if (filePath.exists()){
            delete.setVisibility(View.VISIBLE);
        }
        delete.setOnClickListener(v -> {
            deleteFile();
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
                if( surahid==9 || surahid == 1){
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



    public void deleteFile() {

        File fileToDelete = new File(
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                        + File.separator + filrName);

        if (fileToDelete.exists()) {

            fileToDelete.delete();

            binding.delete.setVisibility(View.GONE);
            binding.play.setVisibility(View.GONE);
            binding.download.setVisibility(View.VISIBLE);

            binding.mediaSeekbar.setEnabled(false);
            binding.mediaSeekbar.setProgress(0);

            AudioWife.getInstance().release();
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
//            if (checkPermission()) {
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

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // API level 26 and above
                                bookDetails.registerReceiver(onComplete, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"),
                                        Context.RECEIVER_NOT_EXPORTED);  // Explicitly specify export status
                            } else {
                                bookDetails.registerReceiver(onComplete, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));
                            }
                        }
                    }
                    return;
                }
//            }
//            requestPermission();
        }

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
    public void ShowSurah1by1() {
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
        titleVerse.setText("সুরাহ ফাতিহা, আয়াত - ০");


        ((ImageView) dialog.findViewById(R.id.clearLayout)).setOnClickListener(v -> {
            dialog.dismiss();
        });

        ImageView copyLayout = (ImageView) dialog.findViewById(R.id.copyLayout);
        copyLayout.setVisibility(View.VISIBLE);

        TextView viewOne = (TextView) dialog.findViewById(R.id.viewOne);
        viewOne.setVisibility(View.VISIBLE);

        String s = "1"+"="+"0";
        //Toasty.success(getApplicationContext(), s, Toasty.LENGTH_LONG).show();
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < mDatabase.getTafheem(s).size(); i++) {
            //Toasty.success(getApplicationContext(), dbHelper.getTafheem(s).get(i).toString(), Toasty.LENGTH_LONG).show();
            String sss = mDatabase.getTafheem(s).get(i).toString();
            //String[] strParts = sss.split("@");
            query.append(sss).append("<br>");
        }
        String main = query.toString().replace("\\n","<br>").replace("[[","").replace("]]","");
        if (main.isEmpty()){
            viewOne.setText("এই আয়াতের তাফসীর নেই।");
        } else {
            viewOne.setText(Html.fromHtml(main));
        }

        // --- ADDED COPY FUNCTION ---
        copyLayout.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            String textToCopy = titleVerse.getText().toString() + "\n\n" + viewOne.getText().toString();
            ClipData clip = ClipData.newPlainText("Tafseer", textToCopy);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        // ---------------------------

        dialog.show();
        dialog.getWindow().setAttributes(lp);
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
        int id = item.getItemId();
        if (id == R.id.about) {
            aboutShow(surahid);
            //Toasty.success(getApplicationContext(), "Okey", Toasty.LENGTH_LONG).show();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        } else {
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
        
        // Use FontSize.Bangla(this) to correctly fetch the set font size
        aboutContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, FontSize.Bangla(this));

        ((ImageView) dialog.findViewById(R.id.copyLayout)).setOnClickListener(v -> {
            ((ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(mDatabase.getSurahName(surahid)+" এর ভূমিকা", mDatabase.getSurahName(surahid) +"  এর ভূমিকা"+"\n" + aboutContent.getText().toString() + "\n\n"+"তাফহীমুল কুরআন"+"\nhttps://play.google.com/store/apps/details?id=" + this.getPackageName()));
            //Toast.makeText(VerseAdapter.mcontext, "This verse has been copied", Toast.LENGTH_SHORT).show();
            Toasty.success(this, "ভূমিকা কপি হয়েছে", Toast.LENGTH_SHORT, true).show();
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void onBackPressed() {
        binding.mediaSeekbar.setEnabled(false);

        try {
            AudioWife.getInstance().release();
        } catch (Exception ignored) {
        }        super.onBackPressed();
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14 (API 34+)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                android.Manifest.permission.READ_MEDIA_IMAGES,
                                android.Manifest.permission.READ_MEDIA_VIDEO,
                                android.Manifest.permission.READ_MEDIA_AUDIO
                        }, STORAGE_PERMISSION_CODE);
            } else {
                return true;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 (API 33)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                android.Manifest.permission.READ_MEDIA_IMAGES,
                                android.Manifest.permission.READ_MEDIA_VIDEO,
                                android.Manifest.permission.READ_MEDIA_AUDIO
                        }, STORAGE_PERMISSION_CODE);
            } else {
                return true;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10-12 (API 29-32)
            return true;
        } else { // Android 6-9 (API 23-28)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, STORAGE_PERMISSION_CODE);
            } else {
                return true;
            }
        }
        return false;
    }

//    public void requestPermission() {
//        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, this.EXTERNAL_STORAGE_PERMISSION_CODE);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            } else {
                boolean showRationale = false;
                for (String permission : permissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        showRationale = true;
                        break;
                    }
                }

                if (showRationale) {
                    // User denied permission but did NOT select "Don't ask again" → Ask again
                    Toast.makeText(this, "Permission Required!", Toast.LENGTH_SHORT).show();
//                    checkPermission(); // Request permission again
                } else {
                    // User denied permission and selected "Don't ask again" → Redirect to settings
                    showSettingsDialog();
                }
            }
        }
    }

    // Show dialog to go to app settings
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required")
                .setMessage("Storage permission is needed. Please enable it in settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
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

//        if (checkPermission()) {

//            Toasty.info(getApplicationContext(), "হচ্ছে.... PP", Toast.LENGTH_SHORT, true).show();

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

                IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bookDetails.registerReceiver(bookDetails.onComplete, filter, Context.RECEIVER_EXPORTED);
                } else {
                    bookDetails.registerReceiver(bookDetails.onComplete, filter);
                }
                return;
            }
//        }

//        requestPermission();
    }


    @Override
    public void onPause(){
        super.onPause();

        binding.mediaSeekbar.setEnabled(false);

        try {
            AudioWife.getInstance().release();
        } catch (Exception ignored) {
        }

        index = mLayoutManager.findFirstVisibleItemPosition();
        View v = recyclerView.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - recyclerView.getPaddingTop());

        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.putInt(surah_Name, index);
        myEdit.putInt(surah_id, top);
        myEdit.apply();

        // Safely extract the actual verse number from the currently visible item
        String lastVerseNumber = "1";
        if (verseModels != null && index >= 0 && index < verseModels.size()) {
            lastVerseNumber = String.valueOf(verseModels.get(index).getVerseID());

            // If the visible item is the banner (Verse ID might be 0), default to 1
            if (lastVerseNumber.equals("0")) {
                lastVerseNumber = "1";
            }
        }

        sp.edit().putString("surah_id", surah_id).apply();
        sp.edit().putString("surah_Name", surah_Name).apply();
        sp.edit().putString("ayatCount", ayatCount).apply();
        sp.edit().putString("location", location).apply();

        // ADDED: Save the actual verse number so HomeFragment can read it
        sp.edit().putString("last_verse_id", lastVerseNumber).apply();
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

        if (filePath.exists()) {
            initAudioPlayer();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.releaseMediaPlayer(); // calling the method inside your adapter
    }

    private void initAudioPlayer() {

        if (!filePath.exists()) {
            binding.mediaSeekbar.setEnabled(false);
            return;
        }

        try {
            AudioWife.getInstance()
                    .init(this, Uri.fromFile(filePath))
                    .setPlayView(binding.play)
                    .setPauseView(binding.pause)
                    .setSeekBar(binding.mediaSeekbar)
                    .setRuntimeView(binding.runTime)
                    .setTotalTimeView(binding.totalTime);

            binding.mediaSeekbar.setEnabled(true);

        } catch (Exception e) {
            binding.mediaSeekbar.setEnabled(false);
            e.printStackTrace();
        }
    }
}
