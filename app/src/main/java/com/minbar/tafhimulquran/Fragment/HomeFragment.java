package com.minbar.tafhimulquran.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minbar.tafhimulquran.Activity.VerseActivity;
import com.minbar.tafhimulquran.Adapter.SurahAdapter;
import com.minbar.tafhimulquran.Model.SurahModel;
import com.minbar.tafhimulquran.Prayer.DateUtils;
import com.minbar.tafhimulquran.Prayer.LocationUtils;
import com.minbar.tafhimulquran.Prayer.PrayerActivity;
import com.minbar.tafhimulquran.Prayer.PrayerNotificationManager;
import com.minbar.tafhimulquran.Prayer.PrayerTimesResponse;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment implements SurahAdapter.OnHeaderBoundListener {

    private RecyclerView recyclerView;
    private SurahAdapter adapter;
    private SqlLiteDbHelper dbHelper;
    private SharedPreferences sp;

    private TextView tvLastSurahName, tvLastAyahInfo, tvLocationName;
    private TextView tvNextPrayerName, tvNextPrayerTime, tvRemainingTime;
    private ProgressBar prayerProgress;
    private View btnContinue, btnLastRead, btnNextPrayer;

    private final Handler timeUpdateHandler = new Handler(Looper.getMainLooper());
    private Runnable timeUpdateRunnable;
    private PrayerTimesResponse.Data cachedPrayerData;
    private ProgressBar progressBar;

    private ExecutorService executor;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new SqlLiteDbHelper(getActivity());
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        recyclerView = v.findViewById(R.id.recyclerViewid);
        progressBar = v.findViewById(R.id.progressBar);

        setupSurahList();

        return v;
    }

    private void setupSurahList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        if (executor == null || executor.isShutdown()) {
            executor = Executors.newSingleThreadExecutor();
        }

        executor.execute(() -> {
            final List<SurahModel> surahList = dbHelper.getSurah();
            mainHandler.post(() -> {
                if (isAdded()) {
                    adapter = new SurahAdapter(getActivity(), surahList, this);
                    recyclerView.setAdapter(adapter);
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    @Override
    public void onHeaderBound(View headerView) {
        // Initialize header views when they are bound by the adapter
        tvLastSurahName = headerView.findViewById(R.id.tvLastSurahName);
        tvLastAyahInfo = headerView.findViewById(R.id.tvLastAyahInfo);
        tvLocationName = headerView.findViewById(R.id.tvLocationName);
        btnContinue = headerView.findViewById(R.id.btnContinue);
        btnLastRead = headerView.findViewById(R.id.btnLastRead);
        btnNextPrayer = headerView.findViewById(R.id.btnNextPrayer);

        tvNextPrayerName = headerView.findViewById(R.id.tvNextPrayerName);
        tvNextPrayerTime = headerView.findViewById(R.id.tvNextPrayerTime);
        tvRemainingTime = headerView.findViewById(R.id.tvRemainingTime);
        prayerProgress = headerView.findViewById(R.id.prayerProgress);


        if (btnNextPrayer != null) {
            btnNextPrayer.setOnClickListener(view -> {
                Intent intent = new Intent(getActivity(), PrayerActivity.class);
                startActivity(intent);
            });
        }

        setupLastRead();
        setupPrayerTimesSummary();
    }

    private void setupLastRead() {
        if (tvLastSurahName == null || getActivity() == null) return;

        String surahId = sp.getString("surah_id", "s");
        String surahName = sp.getString("surah_Name", getString(R.string.default_surah_name));
        String ayatCount = sp.getString("ayatCount", "৭");
        String lastVerse = sp.getString("last_verse_id", "1");

        // Always update the text views, regardless of previous state
        if (surahId.equals("s")) {
            tvLastSurahName.setText(getString(R.string.last_read_start));
            if (tvLastAyahInfo != null) {
                tvLastAyahInfo.setText(getString(R.string.last_read_no_info));
            }
        } else {
            tvLastSurahName.setText(surahName);
            if (tvLastAyahInfo != null) {
                tvLastAyahInfo.setText(getString(R.string.ayah_label, Config.ENtoBN(lastVerse)));
            }
        }

        View.OnClickListener lastReadClick = view -> {
            if (surahId.equals("s")) {
                Toast.makeText(getActivity(), getString(R.string.last_read_empty), Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getActivity(), VerseActivity.class);
                intent.putExtra("surah_id", surahId);
                intent.putExtra("surah_Name", surahName);
                intent.putExtra("ayatCount", ayatCount);
                intent.putExtra("location", sp.getString("location", ""));
                startActivity(intent);
            }
        };

        if (btnContinue != null) btnContinue.setOnClickListener(lastReadClick);
        if (btnLastRead != null) btnLastRead.setOnClickListener(lastReadClick);
    }

    private void setupPrayerTimesSummary() {
        if (tvLocationName == null) return;

        String[] location = LocationUtils.loadLocation(getContext());
        tvLocationName.setText(location[0]);

        PrayerTimesResponse.Data data = PrayerNotificationManager.getCachedPrayerData(getContext());
        if (data != null && data.timings != null) {
            // Cache the prayer data for real-time updates
            cachedPrayerData = data;

            // Update next prayer time
            String nextPrayerInfo = DateUtils.getNextPrayer(data);
            if (nextPrayerInfo != null && nextPrayerInfo.contains("|")) {
                String[] parts = nextPrayerInfo.split("\\|");
                if (tvNextPrayerName != null) tvNextPrayerName.setText(parts[0]);
                if (tvNextPrayerTime != null) tvNextPrayerTime.setText(DateUtils.convertToBengaliDigits(parts[1]));
            }

            // Update prayer progress bar and remaining time
            updatePrayerProgress(data);
            updateRemainingTime(data);

            // Start the real-time update timer
            startRealTimeUpdates();
        }
    }

    private void updatePrayerProgress(PrayerTimesResponse.Data data) {
        if (prayerProgress == null) return;

        try {
            long[] times = DateUtils.getElapsedAndTotal(data);
            long elapsed = times[0];
            long total = times[1];

            if (total > 0) {
                int progress = (int) ((elapsed * 100) / total);
                prayerProgress.setProgress(progress);
            } else {
                prayerProgress.setProgress(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            prayerProgress.setProgress(0);
        }
    }

    private void updateRemainingTime(PrayerTimesResponse.Data data) {
        if (tvRemainingTime == null) return;

        try {
            String nextPrayerInfo = DateUtils.getNextPrayer(data);
            if (nextPrayerInfo != null && nextPrayerInfo.contains("|")) {
                String[] parts = nextPrayerInfo.split("\\|");
                String prayerTime = parts[1];

                // Calculate remaining time using the local device timezone

                Log.d("prayerTime", prayerTime);
                String remainingTime = calculateRemainingTime(prayerTime);
                if (tvRemainingTime != null) {
                    tvRemainingTime.setText(remainingTime);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (tvRemainingTime != null) {
                tvRemainingTime.setText("--:--");
            }
        }
    }

    private String calculateRemainingTime(String prayerTime) {
        try {
            // Get the device's local timezone
            TimeZone localTz = TimeZone.getDefault();
            Calendar now = Calendar.getInstance(localTz);

            // 1. Check for AM/PM in both English and Bengali
            boolean isPM = prayerTime.contains("PM") || prayerTime.contains("pm") || prayerTime.contains("পিএম");
            boolean isAM = prayerTime.contains("AM") || prayerTime.contains("am") || prayerTime.contains("এএম");

            // 2. Convert any Bengali digits to English digits to safely parse them
            String safeTime = prayerTime
                    .replace('০', '0').replace('১', '1').replace('২', '2')
                    .replace('৩', '3').replace('৪', '4').replace('৫', '5')
                    .replace('৬', '6').replace('৭', '7').replace('৮', '8')
                    .replace('৯', '9');

            // 3. Strip everything out except numbers and the colon (Handles "06:37পিএম" without spaces)
            String timeOnly = safeTime.replaceAll("[^0-9:]", "");
            String[] timeParts = timeOnly.split(":");

            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            // 4. Adjust the hour for 24-hour format (Calendar.HOUR_OF_DAY expects 0-23)
            if (isPM && hour < 12) {
                hour += 12; // Converts 6 PM to 18
            } else if (isAM && hour == 12) {
                hour = 0;   // Converts 12 AM to 00
            }

            Calendar prayerCalendar = Calendar.getInstance(localTz);
            prayerCalendar.set(Calendar.HOUR_OF_DAY, hour);
            prayerCalendar.set(Calendar.MINUTE, minute);
            prayerCalendar.set(Calendar.SECOND, 0);
            prayerCalendar.set(Calendar.MILLISECOND, 0);

            // If prayer time has passed for today, calculate for tomorrow
            if (prayerCalendar.before(now)) {
                prayerCalendar.add(Calendar.DATE, 1);
            }

            // Calculate difference in milliseconds
            long diff = prayerCalendar.getTimeInMillis() - now.getTimeInMillis();

            if (diff > 0) {
                long hours = diff / (60 * 60 * 1000);
                long minutes = (diff % (60 * 60 * 1000)) / (60 * 1000);
                long seconds = (diff % (60 * 1000)) / 1000;

                // Format the remaining time
                if (hours > 0) {
                    return DateUtils.convertToBengaliDigits(String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds));
                } else {
                    return DateUtils.convertToBengaliDigits(String.format(Locale.US, "%02d:%02d", minutes, seconds));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "--:--";
    }

    private void startRealTimeUpdates() {
        // Cancel any existing runnable
        if (timeUpdateRunnable != null) {
            timeUpdateHandler.removeCallbacks(timeUpdateRunnable);
        }

        // Create new runnable to update every second
        timeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (cachedPrayerData != null && getActivity() != null) {
                    // Update next prayer time (in case it changed)
                    String nextPrayerInfo = DateUtils.getNextPrayer(cachedPrayerData);
                    if (nextPrayerInfo != null && nextPrayerInfo.contains("|")) {
                        String[] parts = nextPrayerInfo.split("\\|");
                        if (tvNextPrayerName != null) tvNextPrayerName.setText(parts[0]);
                        if (tvNextPrayerTime != null) tvNextPrayerTime.setText(DateUtils.convertToBengaliDigits(parts[1]));
                    }

                    // Update remaining time
                    updateRemainingTime(cachedPrayerData);

                    // Update progress bar
                    updatePrayerProgress(cachedPrayerData);
                }

                // Schedule next update
                timeUpdateHandler.postDelayed(this, 1000); // Update every second
            }
        };

        // Start the updates
        timeUpdateHandler.post(timeUpdateRunnable);
    }

    private void stopRealTimeUpdates() {
        if (timeUpdateRunnable != null) {
            timeUpdateHandler.removeCallbacks(timeUpdateRunnable);
            timeUpdateRunnable = null;
        }
    }

    public void refreshLastRead() {
        setupLastRead();
        setupPrayerTimesSummary();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Always refresh last read data when fragment resumes
        if (tvLastSurahName != null) {
            setupLastRead();
            setupPrayerTimesSummary();
        } else if (getActivity() != null) {
            // If header views are not initialized yet, force a refresh
            // This ensures the data is loaded when the header becomes available
            setupLastRead();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
        stopRealTimeUpdates();
    }
}