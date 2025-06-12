package com.minbar.tafhimulquran.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.minbar.tafhimulquran.BuildConfig;
import com.minbar.tafhimulquran.Daily.DailyActivity;
import com.minbar.tafhimulquran.Hadith.HadithChapterActivity;
import com.minbar.tafhimulquran.Prayer.PrayerActivity;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Adapter.ViewPagerAdapter;
import com.minbar.tafhimulquran.Utils.Constant;
import com.minbar.tafhimulquran.Utils.CustomDrawerButton;
import com.minbar.tafhimulquran.Utils.Methods;
import com.onesignal.OneSignal;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,  NavigationView.OnNavigationItemSelectedListener {

    private static final String PREFS_NAME_Donation = "MonthlyPrefs";
    private static final String LAST_RUN_KEY = "lastMonthlyRun";

    private BottomNavigationView mBottomNavigation;
    private ViewPager viewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    private static final String ONESIGNAL_APP_ID = "1c4a0117-7fb4-4843-b0b9-4385a4d0b9e7";

    String surah_id;
    String surah_Name;
    String ayatCount;
    String location;

    private static final String PREFS_NAME = "HadithAppPrefs";
    private static final String KEY_FIRST_LAUNCH = "first_launch";


    private FirebaseAnalytics mFirebaseAnalytics;

    SharedPreferences sharedPreferences, app_preferences;
    SharedPreferences.Editor editor;
    Button button;
    Methods methods;

    int appTheme;
    int themeColor;
    int appColor;
    Constant constant;

    public ActionBarDrawerToggle actionBarDrawerToggle;
    public DrawerLayout drawerLayout;
    NavigationView navigation_view;

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {






        /*


        int theme = FontFamily.getThemePreferenceId(this);
        setTheme(theme);




        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;
        if (themeColor == 0){
            setTheme(Constant.theme);
        }else if (appTheme == 0){
            setTheme(Constant.theme);
        }else{
            setTheme(appTheme);
        }


         */


        //int aa = R.style.AppTheme_green;
        //setTheme(aa);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






//        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        boolean isFirstLaunch = prefs.getBoolean(KEY_FIRST_LAUNCH, true);
//        DatabaseHelper dbHelper = new DatabaseHelper(this);
//
//        if (isFirstLaunch) {
//            DatabaseInitializer.initializeDatabase(this, dbHelper);
//
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.putBoolean(KEY_FIRST_LAUNCH, false);
//            editor.apply();
//        }


        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        CustomDrawerButton customDrawerButton = (CustomDrawerButton) findViewById(R.id.btnOpenDrawer);
        //customDrawerButton.setBackground(null);
        customDrawerButton.setDrawerLayout( drawerLayout );
        customDrawerButton.getDrawerLayout().addDrawerListener( customDrawerButton );
        customDrawerButton.setOnClickListener(v -> customDrawerButton.changeState());



        /*
        ActionBarDrawerToggle actionBarDrawerToggle2 = new ActionBarDrawerToggle(this, this.drawerLayout, R.string.open, R.string.close);
        this.actionBarDrawerToggle = actionBarDrawerToggle2;
        this.drawerLayout.addDrawerListener(actionBarDrawerToggle2);
        this.actionBarDrawerToggle.syncState();


         */




        //MobileAds.initialize(this, initializationStatus -> { });

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.addTrigger("current_app_version", BuildConfig.VERSION_CODE);



        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        surah_id = sp.getString("surah_id", "s");
        surah_Name = sp.getString("surah_Name", "s");
        ayatCount = sp.getString("ayatCount", "s");
        location = sp.getString("location", "s");

        FrameLayout mm_lastread = findViewById(R.id.mm_lastread);
        mm_lastread.setOnClickListener(v -> {
            getRefresh();
            if (surah_id.contains("s")){
                Toasty.warning(getApplicationContext(), "আপনি এখনো পড়া শুরু করেন নি।", Toasty.LENGTH_LONG).show();
            }else {
                goToVerse();
            }
        });


        FrameLayout mm_store = findViewById(R.id.mm_store);
        mm_store.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FavActivity.class));
        });

        FrameLayout mm_search = findViewById(R.id.mm_search);
        mm_search.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        });


        FrameLayout mm_audio = findViewById(R.id.mm_audio);
        mm_audio.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PrayerActivity.class));
        });
/*
        findViewById(R.id.mm_setting).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });


 */

        ((FloatingActionButton) findViewById(R.id.daily_fab)).setOnClickListener(v -> startActivity(new Intent(this, DailyActivity.class)));

        navigation_view = (NavigationView) findViewById(R.id.navigation_view);
        navigation_view.setNavigationItemSelectedListener(this);


        mBottomNavigation = findViewById(R.id.bottomnav);
        mBottomNavigation.setOnNavigationItemSelectedListener(this);

        viewPager = findViewById(R.id.viewpager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mViewPagerAdapter);

        viewPager.setCurrentItem(2);
        mBottomNavigation.getMenu().findItem(R.id.home).setChecked(true);
        //mBottomNavigation.getMenu().findItem(R.id.home).setIconTintList(ColorStateList.valueOf(Color.BLACK));


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mBottomNavigation.getMenu().findItem(R.id.player).setChecked(true);
                        //mBottomNavigation.getMenu().findItem(R.id.player).setIconTintList(ColorStateList.valueOf(Color.BLACK));
                        break;
                    case 1:
                        mBottomNavigation.getMenu().findItem(R.id.bmark).setChecked(true);
                       //mBottomNavigation.getMenu().findItem(R.id.bmark).setIconTintList(ColorStateList.valueOf(Color.BLACK));
                        break;
                    case 2:
                        mBottomNavigation.getMenu().findItem(R.id.home).setChecked(true);
                       // mBottomNavigation.getMenu().findItem(R.id.home).setIconTintList(ColorStateList.valueOf(Color.BLACK));
                        break;
                    case 3:
                        mBottomNavigation.getMenu().findItem(R.id.fav).setChecked(true);
                       // mBottomNavigation.getMenu().findItem(R.id.fav).setIconTintList(ColorStateList.valueOf(Color.BLACK));
                        break;
                    case 4:

                        mBottomNavigation.getMenu().findItem(R.id.settings).setChecked(true);
                        //mBottomNavigation.getMenu().findItem(R.id.settings).setIconTintList(ColorStateList.valueOf(Color.BLACK));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        checkAndRunMonthlyTask(this);
    }


    public void checkAndRunMonthlyTask(Context context) {
        Calendar today = Calendar.getInstance();
        int day = today.get(Calendar.DAY_OF_MONTH);

        if (day == 1) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME_Donation, Context.MODE_PRIVATE);
            String lastRun = prefs.getString(LAST_RUN_KEY, "");

            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(today.getTime());

            if (!todayDate.equals(lastRun)) {

                new AlertDialog.Builder(this)
                        .setTitle("📖 কুরআন অ্যাপকে সহায়তা করুন")
                        .setMessage("আপনার সামান্য সহযোগিতাও এই অ্যাপকে আরও উন্নত ও সবার জন্য উপকারী করে তুলতে সাহায্য করবে।\n\nআল্লাহ তাআলা আপনার সদকা কবুল করুন এবং আপনাকে উত্তম প্রতিদান দিন। 🤲")
                        .setPositiveButton("ডোনেট করুন", (dialog, which) -> {
                            Intent intent = new Intent(MainActivity.this, DonationActivity.class);
                            startActivity(intent);
                        })
                        .setNegativeButton("পরে", null)
                        .show();


                prefs.edit().putString(LAST_RUN_KEY, todayDate).apply();
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.player:
                viewPager.setCurrentItem(0);
                return true;
            case R.id.bmark:
                viewPager.setCurrentItem(1);
                return true;
            case R.id.home:
                viewPager.setCurrentItem(2);
                return true;
            case R.id.fav:
                viewPager.setCurrentItem(3);
                return true;
            case R.id.settings:
                viewPager.setCurrentItem(4);
                return true;


            case R.id.drawer_about :
                showDialogAbout();
                this.drawerLayout.closeDrawer((int) GravityCompat.START);
                return true;
            case R.id.drawer_exit_app :
                appExit();
                this.drawerLayout.closeDrawer((int) GravityCompat.START);
                return true;
            case R.id.drawer_rate :
            case R.id.drawer_update:
                try {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + getPackageName())));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                this.drawerLayout.closeDrawer((int) GravityCompat.START);
                return true;
            case R.id.banglaOvidan :
                startActivity(new Intent(this, OvidhanActivity.class));
                this.drawerLayout.closeDrawer((int) GravityCompat.START);
                return true;
            case R.id.tajwid :
                startActivity(new Intent(this, TajwidActivity.class));
                this.drawerLayout.closeDrawer((int) GravityCompat.START);
                return true;
            case R.id.donation :
                startActivity(new Intent(this, DonationActivity.class));
                this.drawerLayout.closeDrawer((int) GravityCompat.START);
                return true;
            case R.id.drawer_request_submit :
                openWhatsApp("+17204492312", "আচ্ছালামু আলাইকুম, আমি 'তাফহীমুল কুরআন' অ্যাপ এর একজন ব্যবহারকারী। ");
                this.drawerLayout.closeDrawer((int) GravityCompat.START);
                return true;

            case R.id.drawer_setting :
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                this.drawerLayout.closeDrawer((int) GravityCompat.START);
                return true;

            case R.id.chapaQuran :
                startActivity(new Intent(MainActivity.this, PageMainActivity.class));
                this.drawerLayout.closeDrawer((int) GravityCompat.START);
                return true;
            case R.id.menu_dailyQuran :
                startActivity(new Intent(MainActivity.this, DailyActivity.class));
                this.drawerLayout.closeDrawer((int) GravityCompat.START);
                return true;

            case R.id.menu_riadus :
                startActivity(new Intent(MainActivity.this, HadithChapterActivity.class));
                this.drawerLayout.closeDrawer((int) GravityCompat.START);
                return true;

            case R.id.drawer_share :
                Intent intent = new Intent();
                intent.setAction("android.intent.action.SEND");
                intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name));
                intent.putExtra("android.intent.extra.TEXT", getString(R.string.share_text) + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
                intent.setType("text/plain");
                startActivity(intent);
                this.drawerLayout.closeDrawer((int) GravityCompat.START);
                return true;
            default:
                return false;
        }
    }
    private void openWhatsApp(String numero, String mensaje) {
        try {
            PackageManager packageManager = getApplication().getPackageManager();
            Intent i = new Intent("android.intent.action.VIEW");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + numero + "&text=" + URLEncoder.encode(mensaje, "UTF-8")));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            } else {
                Toasty.info(getApplication(), "আপনার মোবাইলে Whatsapp অ্যাপ্লিকেশানটি নেই। Whatsapp ইন্সটল করে আবার চেষ্ঠা করুন।", Toast.LENGTH_SHORT, true).show();
                //Toast.makeText(getApplication(), "আপনার মোবাইলে Whatsapp অ্যাপ্লিকেশানটি নেই। Whatsapp ইন্সটল করে আবার চেষ্ঠা করুন।", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("ERROR WHATSAPP", e.toString());
            Toasty.info(getApplication(), "আপনার মোবাইলে Whatsapp অ্যাপ্লিকেশানটি নেই। Whatsapp ইন্সটল করে আবার চেষ্ঠা করুন।", Toast.LENGTH_SHORT, true).show();
            //Toast.makeText(getApplication(), "আপনার মোবাইলে Whatsapp অ্যাপ্লিকেশানটি নেই। Whatsapp ইন্সটল করে আবার চেষ্ঠা করুন।", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint({"ResourceType", "SetTextI18n"})
    public void showDialogAbout() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_about);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = -2;
        lp.height = -2;
        ((TextView) dialog.findViewById(R.id.tv_version)).setText("App version : "+String.valueOf(BuildConfig.VERSION_NAME));
        dialog.findViewById(R.id.bt_getcode).setOnClickListener(v -> {
            try {
                MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + MainActivity.this.getPackageName())));
            } catch (ActivityNotFoundException e) {
                MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName())));
            }
        });
        dialog.findViewById(R.id.bt_close).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.bt_more_apps).setOnClickListener(v -> MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(MainActivity.this.getResources().getString(R.string.more_apps)))));
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }




    public void getRefresh(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        surah_id = sp.getString("surah_id", "s");
        surah_Name = sp.getString("surah_Name", "s");
        ayatCount = sp.getString("ayatCount", "s");
        location = sp.getString("location", "s");
    }
    public void goToVerse(){
        Intent intent = new Intent(this, VerseActivity.class);
        intent.putExtra("surah_id",surah_id);
        intent.putExtra("surah_Name", surah_Name);
        intent.putExtra("ayatCount", ayatCount);
        intent.putExtra("location", location);
        startActivity(intent);
    }

    private void appExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setIcon((int) R.drawable.logo);
        builder.setTitle((CharSequence) "অ্যাপটি বন্ধ করতে চাচ্ছেন?");
        builder.setMessage((CharSequence) "এপসটি ভালো লেগে থাকলে, দয়াকরে এপসটিতে একটি ৫-স্টার রেটিং দিন। এটি আমাদের আরো ভালো অ্যাপস উপহার দিতে উৎসাহিত করে এবং অবশ্যই অ্যাপসটি শেয়ার করতে ভুলবেন না কারন আপনার শেয়ার অন্যদের অ্যাপসটি পেতে সাহায্য করে থাকে।");
        builder.setPositiveButton((CharSequence) "হ্যাঁ", (dialogInterface, i) -> MainActivity.this.finish());
        builder.setNegativeButton((CharSequence) "না", (dialogInterface, i) -> dialogInterface.cancel());
        builder.setNeutralButton((CharSequence) "৫ স্টার দিন", (dialogInterface, i) -> {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName()));
            startActivity(intent);
        });
        builder.create().show();
    }
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen((int) GravityCompat.START)) {
            this.drawerLayout.closeDrawer((int) GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            appExit();
        } else {
            super.onBackPressed();
        }
    }
}