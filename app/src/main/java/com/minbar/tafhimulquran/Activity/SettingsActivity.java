package com.minbar.tafhimulquran.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.minbar.tafhimulquran.Prayer.PrayerNotificationManager;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.ThemeManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class SettingsActivity extends AppCompatActivity {

    private static final String FILE_NAME = "fezilalilquran.db";
    private static final String FILE_URL = "https://drive.google.com/uc?export=download&id=1RcOX7KHAib10l8i0yBAuZ4Es2-PwJlDQ";

    private static final String TAHEEM_ENGLISH_FILE_NAME = "tafheem_english.db";
    private static final String TAHEEM_ENGLISH_FILE_URL = "https://drive.google.com/uc?export=download&id=1umT40D2EKqp8RS7T-loQqAdbAEPG5M1I";
    public static Context settingContext;
    public static  ProgressDialog progressDialog;
    public static AtomicBoolean isDownloadCancelled = new AtomicBoolean(false);
    private boolean themeChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        settingContext = SettingsActivity.this;
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialToolbar toolbar = findViewById(R.id.toolBarS);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.main_toolbar_setting);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        SharedPreferences f159sp;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Load_setting();
        }

        private void setupThemePreference() {
            ListPreference themePreference = (ListPreference) findPreference("app_theme");
            if (themePreference != null) {
                String currentTheme = f159sp.getString("app_theme", "light");
                themePreference.setSummary(themePreference.getEntries()[themePreference.findIndexOfValue(currentTheme)]);

                themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    String newValueStr = (String) newValue;
                    // Save the theme immediately
                    f159sp.edit().putString("app_theme", newValueStr).apply();

                    // Update summary
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue(newValueStr)]);

                    // Notify activity that theme changed
                    if (getActivity() instanceof SettingsActivity) {
                        ((SettingsActivity) getActivity()).onThemeChanged();
                    }
                    return true;
                });
            }
        }
        private void Load_setting() {

            this.f159sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            // Setup theme preference
            setupThemePreference();




            ListPreference fezilalilList = (ListPreference) findPreference("fezilalil");
            String fztring = this.f159sp.getString("fezilalil", (String) null);
            if ("on".equals(fztring)) {
                fezilalilList.setSummary(fezilalilList.getEntry());
            } else if ("off".equals(fztring)) {
                fezilalilList.setSummary(fezilalilList.getEntry());
            }

            fezilalilList.setOnPreferenceChangeListener((preference, newValue) -> {
                String newValueStr = (String) newValue;

                if ("on".equals(newValueStr)) {
                    // Check if the file exists
                    File databaseFile = getActivity().getDatabasePath(FILE_NAME);


//                    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//                    File outputFile = new File(storageDir, FILE_NAME);
//                    Toast.makeText(settingContext, String.valueOf(databaseFile), Toast.LENGTH_LONG).show();

                    if (!isDatabaseComplete(getActivity(), "6352")) {
                        // Show download popup
                        showDownloadPopup(preference);
                        return false; // Don't update the preference until the download is complete
                    } else {
                        // File exists, allow the preference to be updated
                        ListPreference listPreference = (ListPreference) preference;
                        listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue(newValueStr)]);
                        return true;
                    }
                } else {
                    // If turning off, just update the summary
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue(newValueStr)]);
                    return true;
                }
            });

            ListPreference tafheemEnglishList = (ListPreference) findPreference("tafheem_english");
            String feztringEnglish = this.f159sp.getString("tafheem_english", (String) null);
            if ("on".equals(feztringEnglish)) {
                tafheemEnglishList.setSummary(tafheemEnglishList.getEntry());
            } else if ("off".equals(feztringEnglish)) {
                tafheemEnglishList.setSummary(tafheemEnglishList.getEntry());
            }

            tafheemEnglishList.setOnPreferenceChangeListener((preference, newValue) -> {
                String newValueStr = (String) newValue;

                if ("on".equals(newValueStr)) {
                    // Check if the file exists
                    File databaseFile = getActivity().getDatabasePath(TAHEEM_ENGLISH_FILE_NAME);


//                    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//                    File outputFile = new File(storageDir, TAHEEM_ENGLISH_FILE_NAME);
//                    Toast.makeText(settingContext, String.valueOf(databaseFile), Toast.LENGTH_LONG).show();

                    if (!isTafheemEnglishDatabaseComplete(getActivity(), "6348")) {
                        // Show download popup
                        showTafheemEnglishDownloadPopup(preference);
                        return false; // Don't update the preference until the download is complete
                    } else {
                        // File exists, allow the preference to be updated
                        ListPreference listPreference = (ListPreference) preference;
                        listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue(newValueStr)]);
                        return true;
                    }
                } else {
                    // If turning off, just update the summary
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue(newValueStr)]);
                    return true;
                }
            });


//            fezilalilList.setOnPreferenceChangeListener((preference, obj) -> {
//                ListPreference listPreference = (ListPreference) preference;
//                listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
//                return true;
//            });


            ListPreference bayaanList = (ListPreference) findPreference("bayaan");
            String bstring = this.f159sp.getString("bayaan", (String) null);
            if ("on".equals(bstring)) {
                bayaanList.setSummary(bayaanList.getEntry());
            } else if ("off".equals(bstring)) {
                bayaanList.setSummary(bayaanList.getEntry());
            }
            bayaanList.setOnPreferenceChangeListener((preference, obj) -> {
                ListPreference listPreference = (ListPreference) preference;
                listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                return true;
            });



/*
            ListPreference theme = (ListPreference) findPreference("THEME_PREFERENCE");
            String stringTheme = this.f159sp.getString("THEME_PREFERENCE", (String) null);
            if ("THEME_WHITE_BLUE".equals(stringTheme)) {
                theme.setSummary(theme.getEntry());
            } else if ("THEME_DARK_ORANGE".equals(stringTheme)) {
                assert theme != null;
                theme.setSummary(theme.getEntry());
            } else if ("THEME_DARK_TURQUOISE".equals(stringTheme)) {
                theme.setSummary(theme.getEntry());
            }


            theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });


 */


            /*
            ((CheckBoxPreference) findPreference("NIGHT")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    if (((Boolean) obj).booleanValue()) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    return true;
                }
            });

             */




            ListPreference listPreference = (ListPreference) findPreference("ARABICFONT");
            String string = this.f159sp.getString("ARABICFONT", (String) null);
            if ("12".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("14".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("16".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("18".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("20".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("22".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("24".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("26".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("28".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("30".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("32".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("34".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            }
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });


            ListPreference listPreference2 = (ListPreference) findPreference("ProFONT");
            String string2 = this.f159sp.getString("ProFONT", (String) null);
            if ("12".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("14".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("16".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("18".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("20".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("22".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("24".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("26".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("28".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("30".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            }
            listPreference2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });


            /*
            ListPreference listPreference3 = (ListPreference) findPreference("TransFONT");
            String string3 = this.f159sp.getString("TransFONT", (String) null);
            if ("12".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("14".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("16".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("18".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("20".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("22".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("24".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("26".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("28".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("30".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            }
            listPreference3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });

             */
            ListPreference listPreference4 = (ListPreference) findPreference("font_arabi");
            String string4 = this.f159sp.getString("font_arabi", (String) null);
            if ("hafs".equals(string4)) {
                listPreference4.setSummary(listPreference4.getEntry());
            } else if ("me_quran".equals(string4)) {
                listPreference4.setSummary(listPreference4.getEntry());
            } else if ("kitab".equals(string4)) {
                listPreference4.setSummary(listPreference4.getEntry());
            } else if ("noorehidayat".equals(string4)) {
                listPreference4.setSummary(listPreference4.getEntry());
            }
            listPreference4.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });
            ListPreference listPreference5 = (ListPreference) findPreference("font_bangla");
            String string5 = this.f159sp.getString("font_bangla", (String) null);
            if ("lipi".equals(string5)) {
                listPreference5.setSummary(listPreference5.getEntry());
            } else if ("charu".equals(string5)) {
                listPreference5.setSummary(listPreference5.getEntry());
            } else if ("kal".equals(string5)) {
                listPreference5.setSummary(listPreference5.getEntry());
            }
            listPreference5.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });

            /*
            ListPreference listPreference6 = (ListPreference) findPreference("AnotherFONT");
            String string6 = this.f159sp.getString("AnotherFONT", (String) null);
            if ("12".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("14".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("16".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("18".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("20".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("22".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("24".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("26".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("28".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("30".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            }
            listPreference6.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });


             */



            ListPreference listPreference7 = (ListPreference) findPreference("tafheem");
            String string7 = this.f159sp.getString("tafheem", (String) null);
            if ("on".equals(string7)) {
                listPreference7.setSummary(listPreference7.getEntry());
            } else if ("off".equals(string7)) {
                listPreference7.setSummary(listPreference7.getEntry());
            }
            listPreference7.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });


            ListPreference listPreference8 = (ListPreference) findPreference("taisirul");
            String string8 = this.f159sp.getString("taisirul", (String) null);
            if ("on".equals(string8)) {
                listPreference8.setSummary(listPreference8.getEntry());
            } else if ("off".equals(string8)) {
                listPreference8.setSummary(listPreference8.getEntry());
            }
            listPreference8.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });



            ListPreference listtika = (ListPreference) findPreference("tika");
            String stringlisttika = this.f159sp.getString("tika", (String) null);
            if ("on".equals(stringlisttika)) {
                listtika.setSummary(listtika.getEntry());
            } else if ("off".equals(stringlisttika)) {
                listtika.setSummary(listtika.getEntry());
            }
            listtika.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });





            ListPreference audioScolling = (ListPreference) findPreference("audioHide");
            String stringaudio = this.f159sp.getString("audioHide", (String) null);
            if ("on".equals(stringaudio)) {
                audioScolling.setSummary(audioScolling.getEntry());
            } else if ("off".equals(stringaudio)) {
                audioScolling.setSummary(audioScolling.getEntry());
            }
            audioScolling.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });



            ListPreference scollingHide = (ListPreference) findPreference("scollingHide");
            String stringscolling = this.f159sp.getString("scollingHide", (String) null);
            if ("on".equals(stringscolling)) {
                scollingHide.setSummary(scollingHide.getEntry());
            } else if ("off".equals(stringscolling)) {
                scollingHide.setSummary(scollingHide.getEntry());
            }
            scollingHide.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });






            ListPreference banglaOnubadhList = (ListPreference) findPreference("banglaOnubadh");
            String strinBangla = this.f159sp.getString("banglaOnubadh", (String) null);
            if ("on".equals(strinBangla)) {
                banglaOnubadhList.setSummary(banglaOnubadhList.getEntry());
            } else if ("off".equals(strinBangla)) {
                banglaOnubadhList.setSummary(banglaOnubadhList.getEntry());
            }
            banglaOnubadhList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });

            // Azan Sound Selection Preference
            ListPreference azanSoundPreference = (ListPreference) findPreference("azan_sound_file");
            if (azanSoundPreference != null) {
                String currentAzanSound = f159sp.getString("azan_sound_file", "azan"); // Default to azan
                azanSoundPreference.setSummary(azanSoundPreference.getEntries()[azanSoundPreference.findIndexOfValue(currentAzanSound)]);

                azanSoundPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    String newValueStr = (String) newValue;
                    // Save the azan sound preference immediately
                    f159sp.edit().putString("azan_sound_file", newValueStr).apply();

                    // Update summary
                    ListPreference azanListPreference = (ListPreference) preference;
                    azanListPreference.setSummary(azanListPreference.getEntries()[azanListPreference.findIndexOfValue(newValueStr)]);

                    // Notify PrayerNotificationManager to update the notification channel sound
                    PrayerNotificationManager.saveAzanSoundPreference(getActivity().getApplicationContext(), newValueStr);

                    return true;
                });
            }






            ListPreference listTajwid = (ListPreference) findPreference("Tajwid");
            String stringTajwid = this.f159sp.getString("Tajwid", (String) null);
            if ("on".equals(stringTajwid)) {
                listTajwid.setSummary(listTajwid.getEntry());
            } else if ("off".equals(stringTajwid)) {
                listTajwid.setSummary(listTajwid.getEntry());
            }
            listTajwid.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });








        }




        public void onResume() {
            Load_setting();
            super.onResume();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



    public static void showDownloadPopup(Preference preference) {
        new AlertDialog.Builder(settingContext)
                .setTitle("তাফসীর ফী যিলালিল কোরআন")
                .setMessage("তাফসীর ফী যিলালিল কোরআন এর ডাটাবেস আপনার মোবাইলে ডাউনলোড করা নেই। আপনি কি এটা ডাউনলোড করতে চাচ্ছেন? ফাইল সাইজ প্রায় ৪৩ এমবি।")
                .setPositiveButton("ডাউনলোড", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Start the download
                        isDownloadCancelled.set(false); // Reset the cancellation flag
                        new DownloadTask(preference).execute(FILE_URL);
                    }
                })
                .setNegativeButton("বাদ দিন", null)
                .show();
    }

    public static  class DownloadTask extends AsyncTask<String, Integer, File> {
        private Preference preference;

        public DownloadTask(Preference preference) {
            this.preference = preference;
        }

        @Override
        protected void onPreExecute() {
            // Show a ProgressDialog with a Cancel button
            progressDialog = new ProgressDialog(settingContext);
            progressDialog.setTitle("ডাটাবেস ডাউনলোড হচ্ছে");
            progressDialog.setMessage("অনুগ্রহ করে অপেক্ষা করুন...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "বাদ দিন", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isDownloadCancelled.set(true); // Set the cancellation flag
                    progressDialog.dismiss();
                }
            });
            progressDialog.show();
        }

        @Override
        protected File doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                // Get the file length for progress calculation
                int fileLength = connection.getContentLength();

                // Create a file in the app's database directory
                File databaseFile = settingContext.getDatabasePath(FILE_NAME);
                File parentDir = databaseFile.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs(); // Create the databases directory if it doesn't exist
                }

                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(databaseFile);

                byte[] buffer = new byte[1024];
                int total = 0;
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    if (isDownloadCancelled.get()) {
                        // Stop the download if cancelled
                        inputStream.close();
                        outputStream.close();
                        databaseFile.delete(); // Delete the partially downloaded file
                        return null;
                    }

                    outputStream.write(buffer, 0, length);
                    total += length;

                    // Publish progress as a percentage
                    if (fileLength > 0) { // Only if file length is known
                        int progress = (int) (total * 100 / fileLength);
                        publishProgress(progress);
                    }
                }

                outputStream.close();
                inputStream.close();

                return databaseFile;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (progressDialog != null) {
                progressDialog.setProgress(values[0]); // Update the progress bar
            }
        }

        @Override
        protected void onPostExecute(File file) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            if (file != null) {
                // File downloaded successfully, update the preference
                ListPreference listPreference = (ListPreference) preference;
                listPreference.setValue("on");
                listPreference.setSummary(listPreference.getEntry());
                Toast.makeText(settingContext, "ডাউনলোড সম্পূর্ণ হয়েছে", Toast.LENGTH_SHORT).show();
            } else {
                if (isDownloadCancelled.get()) {
                    Toast.makeText(settingContext, "ডাউনলোড বাদ দেওয়া হয়েছে", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(settingContext, "ডাউনলোড ব্যর্থ হয়েছে", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static boolean isDatabaseComplete(Context context, String expectedRowCount) {

        boolean isComplete = false;
        File databaseFile = settingContext.getDatabasePath(FILE_NAME);
        if(!databaseFile.exists()){
            return isComplete;
        }

        // Open the database
        String dbPath = settingContext.getDatabasePath("fezilalilquran.db").getAbsolutePath();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);

        // Query to count the rows in the table
        String tableName = "expl";

        try {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int rowCount = cursor.getInt(0);  // Get the row count from the query
                Log.d("RowCount", "Total rows in " + tableName + ": " + rowCount);

                // Check if the row count matches the expected value
                if (String.valueOf(rowCount).equals(expectedRowCount)) {
                    isComplete = true;
                }
                cursor.close();
            }

            // Close the database
            db.close();
        }catch (Exception e){
            databaseFile.delete();
            isComplete = false;
        }



        return isComplete;
    }

    public static void showTafheemEnglishDownloadPopup(Preference preference) {
        new AlertDialog.Builder(settingContext)
                .setTitle("তাফহীমুল কুরআন (ইংরেজি)")
                .setMessage("এই আপনার মোবাইলে তাফসীর ডাটাবেজটি ডাউনলোড করা হয়নি। কি আপনি ডাউনলোড করতে চান? ফাইলের সাইজ প্রায় ১৪.৩৬ এমবি।")
                .setPositiveButton("ডাউনলোড", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Start the download
                        isDownloadCancelled.set(false); // Reset the cancellation flag
                        new TafheemEnglishDownloadTask(preference).execute(TAHEEM_ENGLISH_FILE_URL);
                    }
                })
                .setNegativeButton("বাতিল", null)
                .show();
    }

    public static class TafheemEnglishDownloadTask extends AsyncTask<String, Integer, File> {
        private Preference preference;

        public TafheemEnglishDownloadTask(Preference preference) {
            this.preference = preference;
        }

        @Override
        protected void onPreExecute() {
            // Show a ProgressDialog with a Cancel button
            progressDialog = new ProgressDialog(settingContext);
            progressDialog.setTitle("ডেটাবেস ডাউনলোড হচ্ছে");
            progressDialog.setMessage("অনুগ্রহ করে অপেক্ষা করুন...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isDownloadCancelled.set(true); // Set the cancellation flag
                    progressDialog.dismiss();
                }
            });
            progressDialog.show();
        }

        @Override
        protected File doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                // Get the file length for progress calculation
                int fileLength = connection.getContentLength();

                // Create a file in the app's database directory
                File databaseFile = settingContext.getDatabasePath(TAHEEM_ENGLISH_FILE_NAME);
                File parentDir = databaseFile.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs(); // Create the databases directory if it doesn't exist
                }

                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(databaseFile);

                byte[] buffer = new byte[1024];
                int total = 0;
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    if (isDownloadCancelled.get()) {
                        // Stop the download if cancelled
                        inputStream.close();
                        outputStream.close();
                        databaseFile.delete(); // Delete the partially downloaded file
                        return null;
                    }

                    outputStream.write(buffer, 0, length);
                    total += length;

                    // Publish progress as a percentage
                    if (fileLength > 0) { // Only if file length is known
                        int progress = (int) (total * 100 / fileLength);
                        publishProgress(progress);
                    }
                }

                outputStream.close();
                inputStream.close();

                return databaseFile;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (progressDialog != null) {
                progressDialog.setProgress(values[0]); // Update the progress bar
            }
        }

        @Override
        protected void onPostExecute(File file) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            if (file != null) {
                // Verify the downloaded database
                if (isTafheemEnglishDatabaseComplete(settingContext, "6348")) {
                    // File downloaded and verified successfully, update the preference
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setValue("on");
                    listPreference.setSummary(listPreference.getEntry());
                    Toast.makeText(settingContext, "ডাউনলোড সম্পূর্ণ হয়েছে", Toast.LENGTH_SHORT).show();
                } else {
                    // Delete the incomplete database
                    File databaseFile = settingContext.getDatabasePath(TAHEEM_ENGLISH_FILE_NAME);
                    if (databaseFile.exists()) {
                        databaseFile.delete();
                    }
                    Toast.makeText(settingContext, "ডাউনলোড সম্পূর্ণ হয়নি। অনুগ্রহ করে আবার চেষ্টা করুন।", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (isDownloadCancelled.get()) {
                    Toast.makeText(settingContext, "ডাউনলোড বাতিল করা হয়েছে", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(settingContext, "ডাউনলোড ব্যর্থ হয়েছে", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static boolean isTafheemEnglishDatabaseComplete(Context context, String expectedRowCount) {

        boolean isComplete = false;
        File databaseFile = settingContext.getDatabasePath(TAHEEM_ENGLISH_FILE_NAME);
        if(!databaseFile.exists()){
            return isComplete;
        }

        // Open the database
        String dbPath = settingContext.getDatabasePath("tafheem_english.db").getAbsolutePath();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);

        // Query to count the rows in the table
        String tableName = "english_table";

        try {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int rowCount = cursor.getInt(0);  // Get the row count from the query
                Log.d("RowCount", "Total rows in " + tableName + ": " + rowCount);

                // Check if the row count matches the expected value
                if (String.valueOf(rowCount).equals(expectedRowCount)) {
                    isComplete = true;
                }
                cursor.close();
            }

            // Close the database
            db.close();
        }catch (Exception e){
            databaseFile.delete();
            isComplete = false;
        }



        return isComplete;
    }

    // Called when theme is changed in preferences
    public void onThemeChanged() {
        themeChanged = true;
        setResult(RESULT_OK);
        // Recreate this activity to apply new theme immediately
        ThemeManager.recreateActivity(this);
    }

    @Override
    public void onBackPressed() {
        if (themeChanged) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }
}