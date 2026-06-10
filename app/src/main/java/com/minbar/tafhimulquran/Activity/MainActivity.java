package com.minbar.tafhimulquran.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.minbar.tafhimulquran.BuildConfig;
import com.minbar.tafhimulquran.Daily.DailyActivity;
import com.minbar.tafhimulquran.Fragment.FavFragment;
import com.minbar.tafhimulquran.Fragment.HomeFragment;
import com.minbar.tafhimulquran.Fragment.LibraryFragment;
import com.minbar.tafhimulquran.Fragment.PrayerFragment;
import com.minbar.tafhimulquran.Fragment.SettingFragment;
import com.minbar.tafhimulquran.Hadith.HadithChapterActivity;
import com.minbar.tafhimulquran.Prayer.PrayerFetchWorker;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.ReminderManager;
import com.minbar.tafhimulquran.Utils.ThemeManager;
import com.onesignal.OneSignal;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String PREFS_NAME_Donation = "MonthlyPrefs";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final String ONESIGNAL_APP_ID = "1c4a0117-7fb4-4843-b0b9-4385a4d0b9e7";

    private BottomNavigationView mBottomNavigation;
    private DrawerLayout drawerLayout;
    private NavigationView navigation_view;
    private TextView toolbarTitle;
    private FirebaseAnalytics mFirebaseAnalytics;

    private HomeFragment homeFragment;
    private LibraryFragment libraryFragment;
    private FavFragment favFragment;
    private PrayerFragment prayerFragment;
    private SettingFragment settingFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        initViews();
        setupOneSignal();
        schedulePrayerUpdates();
        
        // Initialize daily reminder system
        ReminderManager.updateLastOpen(this);
        
        homeFragment = new HomeFragment();
        libraryFragment = new LibraryFragment();
        favFragment = new FavFragment();
        prayerFragment = new PrayerFragment();
        settingFragment = new SettingFragment();

        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }

        checkAndRunMonthlyTask(this);
        checkPermissions();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }


    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigation_view = findViewById(R.id.navigation_view);
        mBottomNavigation = findViewById(R.id.bottomnav);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        ImageView btnOpenDrawer = findViewById(R.id.btnOpenDrawer);
        ImageView btnSearch = findViewById(R.id.btnSearch);

        navigation_view.setNavigationItemSelectedListener(this);
        mBottomNavigation.setOnNavigationItemSelectedListener(this);

        btnOpenDrawer.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        btnSearch.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));
    }

    private void setupOneSignal() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.addTrigger("current_app_version", BuildConfig.VERSION_CODE);
    }

    private void schedulePrayerUpdates() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest prayerUpdateRequest = new PeriodicWorkRequest.Builder(
                PrayerFetchWorker.class, 12, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "PrayerUpdateWork",
                ExistingPeriodicWorkPolicy.KEEP,
                prayerUpdateRequest
        );
    }

    private void loadFragment(Fragment fragment, String title, String tag) {
        if (activeFragment != null && activeFragment.getClass().equals(fragment.getClass())) {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        
        // Handle fragment display with proper transitions if desired
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.commit();
        
        activeFragment = fragment;
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.nav_home) {
            loadFragment(homeFragment, getString(R.string.app_name), "home");
        } else if (id == R.id.nav_library) {
            loadFragment(libraryFragment, getString(R.string.main_bottom_nav_library), "library");
        } else if (id == R.id.nav_fav) {
            loadFragment(favFragment, getString(R.string.main_bottom_nav_fav), "fav");
        } else if (id == R.id.nav_prayer) {
            loadFragment(prayerFragment, getString(R.string.main_bottom_nav_prayer), "prayer");
        } else if (id == R.id.nav_setting || id == R.id.drawer_setting) {
            loadFragment(settingFragment, getString(R.string.main_bottom_nav_settings), "settings");
        } else if (id == R.id.menu_riadus) {
            startActivity(new Intent(this, HadithChapterActivity.class));
        } else if (id == R.id.menu_dailyQuran) {
            startActivity(new Intent(this, DailyActivity.class));
        } else if (id == R.id.chapaQuran) {
            startActivity(new Intent(this, PageMainActivity.class));
        } else if (id == R.id.banglaOvidan) {
            startActivity(new Intent(this, OvidhanActivity.class));
        } else if (id == R.id.tajwid) {
            startActivity(new Intent(this, TajwidActivity.class));
        } else if (id == R.id.donation) {
            startActivity(new Intent(this, DonationActivity.class));
        } else if (id == R.id.drawer_request_submit) {
            Toasty.info(this, "Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.drawer_share) {
            shareApp();
        } else if (id == R.id.drawer_rate) {
            rateApp();
        } else if (id == R.id.drawer_update) {
            moreApps();
        } else if (id == R.id.drawer_exit_app) {
            showExitDialog();
        } else if (id == R.id.drawer_about) {
            showAboutDialog();
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAboutDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_about, null);
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(view)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvVersion = view.findViewById(R.id.tv_version);
        if (tvVersion != null) {
            tvVersion.setText("Version " + BuildConfig.VERSION_NAME);
        }

        view.findViewById(R.id.bt_close).setOnClickListener(v -> dialog.dismiss());
        
        view.findViewById(R.id.bt_getcode).setOnClickListener(v -> {
            rateApp();
            dialog.dismiss();
        });

        view.findViewById(R.id.bt_more_apps).setOnClickListener(v -> {
            moreApps();
            dialog.dismiss();
        });

        view.findViewById(R.id.bt_contact).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:mdkamalhosennn@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback: Tafheem Bangla");
            try {
                startActivity(Intent.createChooser(intent, "Send Email"));
            } catch (Exception e) {
                Toasty.error(this, "No email app found", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showExitDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_exit, null);
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(view)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Handle Rating Button
        view.findViewById(R.id.btn_rate).setOnClickListener(v -> {
            rateApp();
            dialog.dismiss();
        });

        // Handle Cancel Button
        view.findViewById(R.id.btn_no).setOnClickListener(v -> dialog.dismiss());

        // Handle Exit Button
        view.findViewById(R.id.btn_yes).setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "পবিত্র কুরআন ও তাফসীর পড়ার জন্য সেরা অ্যাপ। ডাউনলোড করুন: https://play.google.com/store/apps/details?id=" + getPackageName());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "শেয়ার করুন"));
    }

    private void rateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void moreApps() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:Minbar")));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Minbar")));
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void checkAndRunMonthlyTask(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME_Donation, Context.MODE_PRIVATE);
        Calendar current = Calendar.getInstance();
        int day = current.get(Calendar.DAY_OF_MONTH);
        String currentMonthYear = (current.get(Calendar.MONTH) + 1) + "-" + current.get(Calendar.YEAR);
        String todayDateString = day + "-" + currentMonthYear;

        String donatedMonthYear = prefs.getString("donated_month_year", "");
        String lastShownDate = prefs.getString("last_shown_date", "");
        String declinedMonthYear = prefs.getString("declined_month_year", "");

        // Rule: If donated once this month, do not ask again.
        if (currentMonthYear.equals(donatedMonthYear)) {
            return;
        }

        // Prevent showing the popup multiple times on the same day.
        if (todayDateString.equals(lastShownDate)) {
            return;
        }

        // Rule: Show popup on the 5th of every month.
        if (day == 5) {
            showDonationReminder(prefs, currentMonthYear, todayDateString);
        } 
        // Rule: If they clicked no on the 5th, ask again on the 15th.
        else if (day == 15) {
            if (currentMonthYear.equals(declinedMonthYear)) {
                showDonationReminder(prefs, currentMonthYear, todayDateString);
            }
        }
    }

    private void showDonationReminder(SharedPreferences prefs, String currentMonthYear, String todayDateString) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.donation_title)
                .setMessage(R.string.donation_desc)
                .setCancelable(false)
                .setPositiveButton(R.string.donation_yes, (dialog, which) -> {
                    // Record that they chose to donate this month
                    prefs.edit().putString("donated_month_year", currentMonthYear).apply();
                    startActivity(new Intent(MainActivity.this, DonationActivity.class));
                })
                .setNegativeButton(R.string.donation_no, (dialog, which) -> {
                    // If it's the 5th, remember that they declined to ask again on the 15th
                    Calendar current = Calendar.getInstance();
                    if (current.get(Calendar.DAY_OF_MONTH) == 5) {
                        prefs.edit().putString("declined_month_year", currentMonthYear).apply();
                    }
                    dialog.dismiss();
                })
                .show();

        // Mark as shown today
        prefs.edit().putString("last_shown_date", todayDateString).apply();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (activeFragment != null && !(activeFragment instanceof HomeFragment)) {
            loadFragment(homeFragment, getString(R.string.app_name), "home");
            mBottomNavigation.setSelectedItemId(R.id.nav_home);
        } else {
            showExitDialog();
        }
    }

    public void openSettingsFragment() {
        mBottomNavigation.setSelectedItemId(R.id.nav_setting);
    }

    private void handleIntent(Intent intent) {

        String fragmentToOpen =
                intent != null ? intent.getStringExtra("OPEN_FRAGMENT") : null;

        if ("settings".equals(fragmentToOpen)) {

            loadFragment(
                    settingFragment,
                    getString(R.string.main_bottom_nav_settings),
                    "settings"
            );

            mBottomNavigation.setSelectedItemId(R.id.nav_setting);

        } else if ("prayer".equals(fragmentToOpen)) {

            loadFragment(
                    prayerFragment,
                    getString(R.string.main_bottom_nav_prayer),
                    "prayer"
            );

            mBottomNavigation.setSelectedItemId(R.id.nav_prayer);

        } else {

            loadFragment(
                    homeFragment,
                    getString(R.string.app_name),
                    "home"
            );

            mBottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }
}
