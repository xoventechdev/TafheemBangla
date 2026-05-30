package com.minbar.tafhimulquran.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.minbar.tafhimulquran.Prayer.PrayerNotificationManager;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.ThemeManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class SettingFragment extends PreferenceFragmentCompat {

    private static final String FILE_NAME = "fezilalilquran.db";
    private static final String FILE_URL = "https://drive.google.com/uc?export=download&id=1RcOX7KHAib10l8i0yBAuZ4Es2-PwJlDQ";
    
    public static ProgressDialog progressDialog;
    public static AtomicBoolean isDownloadCancelled = new AtomicBoolean(false);
    
    private SharedPreferences sp;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        loadSettings();
    }

    private void loadSettings() {
        this.sp = PreferenceManager.getDefaultSharedPreferences(requireContext().getApplicationContext());

        setupThemePreference();
        setupFezilalilPreference();
        setupTafheemEnglishPreference();
        setupSimpleListPreference("bayaan");
        setupSimpleListPreference("ARABICFONT");
        setupSimpleListPreference("ProFONT");
        setupSimpleListPreference("font_arabi");
        setupSimpleListPreference("font_bangla");
        setupSimpleListPreference("tafheem");
        setupSimpleListPreference("taisirul");
        setupSimpleListPreference("tika");
        setupSimpleListPreference("audioHide");
        setupSimpleListPreference("scollingHide");
        setupSimpleListPreference("banglaOnubadh");
        setupSimpleListPreference("Tajwid");
        setupSimpleListPreference("arabic_pronunciation");
        setupPronunciationPreference();

        // Prayer Settings
        setupAzanSoundPreference();
        setupSimpleListPreference("prayer_calculation_method");
        setupSimpleListPreference("prayer_school");
    }

    private void setupAzanSoundPreference() {
        ListPreference azanPref = findPreference("azan_sound_file");
        if (azanPref != null) {
            azanPref.setSummary(azanPref.getEntry());
            azanPref.setOnPreferenceChangeListener((preference, newValue) -> {
                String soundFile = (String) newValue;
                PrayerNotificationManager.saveAzanSoundPreference(requireContext(), soundFile);
                
                int index = azanPref.findIndexOfValue(soundFile);
                if (index >= 0) {
                    azanPref.setSummary(azanPref.getEntries()[index]);
                }
                return true;
            });
        }
    }

    private void setupThemePreference() {
        ListPreference themePreference = findPreference("app_theme");
        if (themePreference != null) {
            String currentTheme = sp.getString("app_theme", "light");
            int index = themePreference.findIndexOfValue(currentTheme);
            if (index >= 0) {
                themePreference.setSummary(themePreference.getEntries()[index]);
            }

            themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String newValueStr = (String) newValue;
                sp.edit().putString("app_theme", newValueStr).apply();

                ListPreference listPreference = (ListPreference) preference;
                int newIndex = listPreference.findIndexOfValue(newValueStr);
                if (newIndex >= 0) {
                    listPreference.setSummary(listPreference.getEntries()[newIndex]);
                }

                // Recreate activity to apply theme
                if (getActivity() != null) {
                    ThemeManager.recreateActivity(getActivity());
                }
                return true;
            });
        }
    }

    private void setupFezilalilPreference() {
        ListPreference fezilalilList = findPreference("fezilalil");
        if (fezilalilList != null) {
            updateSummaryWithAuthor(fezilalilList, fezilalilList.getEntry());

            fezilalilList.setOnPreferenceChangeListener((preference, newValue) -> {
                String newValueStr = (String) newValue;
                if ("on".equals(newValueStr)) {
                    if (!isDatabaseComplete(requireContext(), "6352")) {
                        showDownloadPopup(preference);
                        return false;
                    }
                }
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(newValueStr);
                if (index >= 0) {
                    updateSummaryWithAuthor(listPreference, listPreference.getEntries()[index]);
                }
                return true;
            });
        }
    }

    // Setup Tafheem English preference with download handling
    private static final String ENGLISH_FILE_NAME = "tafheem_english.db";
    private static final String ENGLISH_FILE_URL = "https://drive.google.com/uc?export=download&id=1umT40D2EKqp8RS7T-loQqAdbAEPG5M1I";
    public static ProgressDialog englishProgressDialog;
    public static AtomicBoolean isEnglishDownloadCancelled = new AtomicBoolean(false);

    private void setupTafheemEnglishPreference() {
        ListPreference tafheemEnglishList = findPreference("tafheem_english");
        if (tafheemEnglishList != null) {
            // Set initial summary based on stored value
            String current = sp.getString("tafheem_english", "off");
            int idx = tafheemEnglishList.findIndexOfValue(current);
            if (idx >= 0) {
                tafheemEnglishList.setSummary(tafheemEnglishList.getEntries()[idx]);
            }
            tafheemEnglishList.setOnPreferenceChangeListener((preference, newValue) -> {
                String newStr = (String) newValue;
                if ("on".equals(newStr)) {
                    // Check if database is present
                    if (!isTafheemEnglishDatabaseComplete(requireContext(), "6348")) {
                        // Show download popup defined in this fragment
                        showTafheemEnglishDownloadPopup(preference);
                        return false; // don't change until download completes
                    }
                }
                // Update summary/value
                int newIdx = tafheemEnglishList.findIndexOfValue(newStr);
                if (newIdx >= 0) {
                    tafheemEnglishList.setSummary(tafheemEnglishList.getEntries()[newIdx]);
                }
                return true;
            });
        }
    }

    private void showTafheemEnglishDownloadPopup(Preference preference) {
        new AlertDialog.Builder(requireContext())
                .setTitle("তাফহীমুল কুরআন (ইংরেজি)")
                .setMessage("এই আপনার মোবাইলে তাফসীর ডাটাবেজটি ডাউনলোড করা হয়নি। কি আপনি ডাউনলোড করতে চান? ফাইলের সাইজ প্রায় ৪৩ এমবি।")
                .setPositiveButton("ডাউনলোড", (dialog, which) -> {
                    isEnglishDownloadCancelled.set(false);
                    new EnglishDownloadTask(preference, requireContext()).execute(ENGLISH_FILE_URL);
                })
                .setNegativeButton("বাতিল", null)
                .show();
    }

    private static class EnglishDownloadTask extends AsyncTask<String, Integer, File> {
        private final Preference preference;
        private final Context context;
        public EnglishDownloadTask(Preference preference, Context context) {
            this.preference = preference;
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            englishProgressDialog = new ProgressDialog(context);
            englishProgressDialog.setTitle("ডেটাবেস ডাউনলোড হচ্ছে");
            englishProgressDialog.setMessage("অনুগ্রহ করে অপেক্ষা করুন...");
            englishProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            englishProgressDialog.setCancelable(false);
            englishProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "বাতিল", (dialog, which) -> {
                isEnglishDownloadCancelled.set(true);
                englishProgressDialog.dismiss();
            });
            englishProgressDialog.show();
        }
        @Override
        protected File doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int fileLength = connection.getContentLength();
                File databaseFile = context.getDatabasePath(ENGLISH_FILE_NAME);
                File parentDir = databaseFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) parentDir.mkdirs();
                InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(databaseFile);
                byte[] buffer = new byte[1024];
                int total = 0, count;
                while ((count = input.read(buffer)) != -1) {
                    if (isEnglishDownloadCancelled.get()) {
                        input.close();
                        output.close();
                        databaseFile.delete();
                        return null;
                    }
                    output.write(buffer, 0, count);
                    total += count;
                    if (fileLength > 0) publishProgress((int) (total * 100 / fileLength));
                }
                output.close();
                input.close();
                return databaseFile;
            } catch (Exception e) {
                Log.e("SettingFragment", "English download failed", e);
                return null;
            }
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            if (englishProgressDialog != null) englishProgressDialog.setProgress(values[0]);
        }
        @Override
        protected void onPostExecute(File file) {
            if (englishProgressDialog != null) englishProgressDialog.dismiss();
            if (file != null && SettingFragment.isTafheemEnglishDatabaseComplete(context, "6348")) {
                ListPreference lp = (ListPreference) preference;
                lp.setValue("on");
                lp.setSummary(lp.getEntry());
                Toast.makeText(context, "ডাউনলোড সম্পূর্ণ হয়েছে", Toast.LENGTH_SHORT).show();
            } else {
                if (!isEnglishDownloadCancelled.get()) {
                    Toast.makeText(context, "ডাউনলোড ব্যর্থ হয়েছে", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private static boolean isTafheemEnglishDatabaseComplete(Context context, String expectedRowCount) {
        File dbFile = context.getDatabasePath(ENGLISH_FILE_NAME);
        if (!dbFile.exists()) return false;
        try (SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
             Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM english_table", null)) {
            if (cursor.moveToFirst()) {
                int rows = cursor.getInt(0);
                return String.valueOf(rows).equals(expectedRowCount);
            }
        } catch (Exception e) {
            dbFile.delete();
        }
        return false;
    }

    private void setupSimpleListPreference(String key) {
        ListPreference lp = findPreference(key);
        if (lp != null) {
            updateSummaryWithAuthor(lp, lp.getEntry());
            lp.setOnPreferenceChangeListener((preference, newValue) -> {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue((String) newValue);
                if (index >= 0) {
                    updateSummaryWithAuthor(listPreference, listPreference.getEntries()[index]);
                }
                return true;
            });
        }
    }

    private void updateSummaryWithAuthor(ListPreference lp, CharSequence status) {
        String author = "";
        String key = lp.getKey();
        
        if ("tafheem".equals(key)) author = getString(R.string.pref_tafheem_author);
        else if ("bayaan".equals(key)) author = getString(R.string.pref_ibn_kasir_author);
        else if ("fezilalil".equals(key)) author = getString(R.string.pref_fi_zilalil_author);

        if (!author.isEmpty()) {
            // Use \n to force two lines in the summary area: Author on one, Status on the next
            lp.setSummary(author + "\n(" + status + ")");
        } else {
            lp.setSummary(status);
        }
    }

    private void showDownloadPopup(Preference preference) {
        new AlertDialog.Builder(requireContext())
                .setTitle("তাফসীর ফী যিলালিল কোরআন")
                .setMessage("তাফসীর ফী যিলালিল কোরআন এর ডাটাবেস আপনার মোবাইলে ডাউনলোড করা নেই। আপনি কি এটা ডাউনলোড করতে চাচ্ছেন? ফাইল সাইজ প্রায় ৪৩ এমবি।")
                .setPositiveButton("ডাউনলোড", (dialog, which) -> {
                    isDownloadCancelled.set(false);
                    new DownloadTask(requireContext(), preference).execute(FILE_URL);
                })
                .setNegativeButton("বাদ দিন", null)
                .show();
    }

    private static class DownloadTask extends AsyncTask<String, Integer, File> {
        private final Context context;
        private final Preference preference;

        public DownloadTask(Context context, Preference preference) {
            this.context = context;
            this.preference = preference;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("ডাটাবেস ডাউনলোড হচ্ছে");
            progressDialog.setMessage("অনুগ্রহ করে অপেক্ষা করুন...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "বাদ দিন", (dialog, which) -> {
                isDownloadCancelled.set(true);
                progressDialog.dismiss();
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

                int fileLength = connection.getContentLength();
                File databaseFile = context.getDatabasePath(FILE_NAME);
                File parentDir = databaseFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }

                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(databaseFile);

                byte[] buffer = new byte[1024];
                int total = 0;
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    if (isDownloadCancelled.get()) {
                        inputStream.close();
                        outputStream.close();
                        databaseFile.delete();
                        return null;
                    }
                    outputStream.write(buffer, 0, length);
                    total += length;
                    if (fileLength > 0) {
                        publishProgress((int) (total * 100 / fileLength));
                    }
                }
                outputStream.close();
                inputStream.close();
                return databaseFile;
            } catch (Exception e) {
                Log.e("SettingFragment", "Download failed", e);
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (progressDialog != null) {
                progressDialog.setProgress(values[0]);
            }
        }

        @Override
        protected void onPostExecute(File file) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (file != null) {
                ListPreference listPreference = (ListPreference) preference;
                listPreference.setValue("on");
                String author = context.getString(R.string.pref_fi_zilalil_author);
                listPreference.setSummary(author + "\n(" + listPreference.getEntry() + ")");
                Toast.makeText(context, "ডাউনলোড সম্পূর্ণ হয়েছে", Toast.LENGTH_SHORT).show();
            } else {
                if (!isDownloadCancelled.get()) {
                    Toast.makeText(context, "ডাউনলোড ব্যর্থ হয়েছে", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static boolean isDatabaseComplete(Context context, String expectedRowCount) {
        File databaseFile = context.getDatabasePath(FILE_NAME);
        if (!databaseFile.exists()) return false;

        try (SQLiteDatabase db = SQLiteDatabase.openDatabase(databaseFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY)) {
            try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM expl", null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int rowCount = cursor.getInt(0);
                    return String.valueOf(rowCount).equals(expectedRowCount);
                }
            }
        } catch (Exception e) {
            databaseFile.delete();
        }
        return false;
    }

    private void setupPronunciationPreference() {
        ListPreference pronunciationPref = findPreference("arabic_pronunciation");
        if (pronunciationPref != null) {
            pronunciationPref.setOnPreferenceChangeListener((preference, newValue) -> {
                // Broadcast a notification that the pronunciation setting has changed
                Intent intent = new Intent("com.minbar.tafhimulquran.PRONUNCIATION_CHANGED");
                getActivity().sendBroadcast(intent);

                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue((String) newValue);
                if (index >= 0) {
                    listPreference.setSummary(listPreference.getEntries()[index]);
                }
                return true;
            });
        }
    }
}
