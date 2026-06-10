package com.minbar.tafhimulquran.Prayer;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.minbar.tafhimulquran.Activity.SettingsActivity;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.ThemeManager;
import com.minbar.tafhimulquran.databinding.ActivityPrayerBinding;
import com.minbar.tafhimulquran.databinding.DialogCountrySelectionBinding;
import com.minbar.tafhimulquran.databinding.DialogLocationSelectionBinding;
import com.minbar.tafhimulquran.databinding.ItemPrayerTimeBinding;
import com.minbar.tafhimulquran.databinding.ItemPrayerTomorrowBinding;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class PrayerActivity extends AppCompatActivity {

    private ActivityPrayerBinding binding;
    private RequestQueue requestQueue;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private String selectedDistrict = "ঢাকা";
    private boolean useCoordinates = false;
    private double latitude = 0.0;
    private double longitude = 0.0;

    private final Handler timeHandler = new Handler(Looper.getMainLooper());
    private Runnable timeRunnable;

    private PrayerTimesResponse.Data todayData;
    private PrayerTimesResponse.Data tomorrowData;
    private String currentMethod = "";
    private String currentSchool = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_prayer);

        setupToolbar();
        initClients();
        loadSavedLocation();
        setupListeners();
        startClock();
        checkExactAlarmPermission();
        
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentMethod = sp.getString("prayer_calculation_method", "3");
        currentSchool = sp.getString("prayer_school", "1");
        
        checkPermissionsAndFetchData();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("নামাজের সময় সূচী");
        }
    }

    private void initClients() {
        requestQueue = Volley.newRequestQueue(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void loadSavedLocation() {
        String[] savedLocation = LocationUtils.loadLocation(this);
        selectedDistrict = savedLocation[0];
        String country = savedLocation.length > 1 ? savedLocation[1] : "Bangladesh";

        useCoordinates = LocationUtils.shouldUseCoordinates(this);
        if (useCoordinates) {
            double[] coords = LocationUtils.getSavedCoordinates(this);
            latitude = coords[0];
            longitude = coords[1];
        }

        if (!"Bangladesh".equals(country)) {
            binding.locationWeatherText.setText(selectedDistrict + ", " + country);
        } else {
            binding.locationWeatherText.setText(selectedDistrict);
        }
    }

    private void setupListeners() {
        binding.locationWeatherText.setOnClickListener(v -> showLocationSelectionDialog());
        binding.locationWeatherText.setOnLongClickListener(v -> {
            LocationUtils.saveInternationalLocation(this, "Riyadh", "Saudi Arabia");
            selectedDistrict = "Riyadh";
            useCoordinates = false;
            binding.locationWeatherText.setText("Riyadh, Saudi Arabia");
            fetchAllPrayerData();
            Toast.makeText(this, "Location set to Riyadh", Toast.LENGTH_SHORT).show();
            return true;
        });
        binding.azanSoundSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        binding.azanSoundSettings.setOnLongClickListener(v -> {
            PrayerNotificationManager.scheduleTestAlarm(this);
            Toast.makeText(this, "Test Azan will ring in 10 seconds! Lock your phone.", Toast.LENGTH_LONG).show();
            return true;
        });
    }

    private void checkExactAlarmPermission() {
        // 1. Check Exact Alarm Permission (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                new AlertDialog.Builder(this)
                        .setTitle("অ্যালার্ম অনুমতি প্রয়োজন")
                        .setMessage("সঠিক সময়ে আজান দেওয়ার জন্য 'Alarms & Reminders' অনুমতি প্রয়োজন।")
                        .setPositiveButton("সেটিংস", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                            startActivity(intent);
                        })
                        .show();
                return;
            }
        }

        // 2. Check Battery Optimization
        checkBatteryOptimizations();
    }
    private void startClock() {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a", Locale.getDefault());
                binding.currentTimeText.setText(DateUtils.convertToBengaliDigits(sdf.format(Calendar.getInstance().getTime())));

                if (todayData != null) {
                    String nextPrayerInfo = DateUtils.getNextPrayer(todayData);
                    if (nextPrayerInfo != null && nextPrayerInfo.contains("|")) {
                        String[] parts = nextPrayerInfo.split("\\|");
                        binding.nextPrayerText.setText(parts[0]);
                        binding.nextPrayerTimeText.setText(DateUtils.convertToBengaliDigits(parts[1]));
                    }

                    long[] times = DateUtils.getElapsedAndTotal(todayData);
                    long elapsed = times[0];
                    long total = times[1];

                    if (total > 0) {
                        float progress = (float) elapsed * 100 / total;
                        binding.circularProgressBar.setProgress(Math.min(100, Math.max(0, progress)));
                    } else {
                        binding.circularProgressBar.setProgress(0);
                    }
                }

                timeHandler.postDelayed(this, 1000);
            }
        };
        timeHandler.post(timeRunnable);
    }

    private void showLoader() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        binding.progressBar.setVisibility(View.GONE);
    }

    private void checkPermissionsAndFetchData() {
        showLoader();
        todayData = PrayerNotificationManager.getCachedPrayerData(this);
        if (todayData != null) {
            updateUI(todayData);
        }

        // Only fetch device location automatically if the user has previously opted to use coordinates
        if (useCoordinates && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchDeviceLocation();
        } else {
            fetchAllPrayerData();
        }
    }

    private void fetchDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            fetchAllPrayerData();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                selectedDistrict = LocationUtils.getDistrictFromCoordinates(this, latitude, longitude);
                useCoordinates = true;
                LocationUtils.saveCoordinates(this, latitude, longitude, selectedDistrict);
                binding.locationWeatherText.setText(selectedDistrict);
            }
            fetchAllPrayerData();
        }).addOnFailureListener(e -> fetchAllPrayerData());
    }

    private void fetchAllPrayerData() {
        fetchPrayerTimes(true);
        fetchPrayerTimes(false);
    }

    private void fetchPrayerTimes(boolean isToday) {
        String url;
        String dateStr = "timings";
        if (!isToday) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            dateStr = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(calendar.getTime());
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String method = sp.getString("prayer_calculation_method", "3");
        String school = sp.getString("prayer_school", "1");

        try {
            if (useCoordinates) {
                url = String.format(Locale.US, "https://api.aladhan.com/v1/timings/%s?latitude=%f&longitude=%f&method=%s&school=%s", dateStr, latitude, longitude, method, school);
            } else {
                String city = LocationUtils.getApiCityName(selectedDistrict, "");
                String encodedCity = URLEncoder.encode(city, "UTF-8");
                String[] savedLocation = LocationUtils.loadLocation(this);
                String country = savedLocation.length > 1 ? savedLocation[1] : "Bangladesh";
                url = String.format(Locale.US, "https://api.aladhan.com/v1/timingsByCity/%s?city=%s&country=%s&method=%s&school=%s", dateStr, encodedCity, country, method, school);
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        PrayerTimesResponse prayerResponse = new Gson().fromJson(response.toString(), PrayerTimesResponse.class);
                        if (prayerResponse != null && prayerResponse.data != null) {
                            if (isToday) {
                                todayData = prayerResponse.data;
                                updateUI(todayData);
                                hideLoader();
                            } else {
                                tomorrowData = prayerResponse.data;
                                updateTomorrowUI(tomorrowData);
                            }

                            if (todayData != null && tomorrowData != null) {
                                PrayerNotificationManager.schedulePrayerNotifications(this, todayData, tomorrowData);
                            }
                        }
                    },
                    error -> {
                        if (isToday) hideLoader();
                    });
            requestQueue.add(request);
        } catch (Exception e) {
            if (isToday) hideLoader();
            e.printStackTrace();
        }
    }

    private void updateUI(PrayerTimesResponse.Data data) {
        if (data.date != null && data.date.hijri != null) {
            binding.hijriDateText.setText(DateUtils.formatHijriDate(data.date.hijri.date, data.date.hijri.month.number));
        }

        if (data.timings != null) {
            setupPrayerItem(binding.itemFajr, "ফজর", data.timings.Fajr, R.drawable.ic_namaz);
            setupPrayerItem(binding.itemDhuhr, "যোহর", data.timings.Dhuhr, R.drawable.ic_namaz);
            setupPrayerItem(binding.itemAsr, "আসর", data.timings.Asr, R.drawable.ic_namaz);
            setupPrayerItem(binding.itemMaghrib, "মাগরিব", data.timings.Maghrib, R.drawable.ic_namaz);
            setupPrayerItem(binding.itemIsha, "ইশা", data.timings.Isha, R.drawable.ic_namaz);

            binding.sunriseTimeText.setText(DateUtils.convertToBengaliDigits(DateUtils.formatTimeTo12Hour(data.timings.Sunrise)));
            binding.sunsetTimeText.setText(DateUtils.convertToBengaliDigits(DateUtils.formatTimeTo12Hour(data.timings.Maghrib)));

            String nextPrayerInfo = DateUtils.getNextPrayer(data);
            if (nextPrayerInfo != null && nextPrayerInfo.contains("|")) {
                String[] parts = nextPrayerInfo.split("\\|");
                binding.nextPrayerText.setText(parts[0]);
                binding.nextPrayerTimeText.setText(DateUtils.convertToBengaliDigits(parts[1]));
            }

            updateProgressBar(data);
        }
    }

    private void setupPrayerItem(ItemPrayerTimeBinding itemBinding, String name, String time, int iconRes) {
        if (itemBinding != null) {
            itemBinding.prayerName.setText(name);
            itemBinding.prayerTime.setText(DateUtils.convertToBengaliDigits(DateUtils.formatTimeTo12Hour(time)));
            itemBinding.prayerIcon.setImageResource(iconRes);

            SharedPreferences prefs = getSharedPreferences("PrayerPrefs", MODE_PRIVATE);
            boolean isEnabled = prefs.getBoolean("alarm_enabled_" + name, true);
            itemBinding.alarmIcon.setImageResource(isEnabled ? R.drawable.ic_alarm_on : R.drawable.ic_alarm_off);
            itemBinding.alarmIcon.setOnClickListener(v -> {
                boolean newState = !prefs.getBoolean("alarm_enabled_" + name, true);
                prefs.edit().putBoolean("alarm_enabled_" + name, newState).apply();
                itemBinding.alarmIcon.setImageResource(newState ? R.drawable.ic_alarm_on : R.drawable.ic_alarm_off);
                Toast.makeText(this, name + (newState ? " আজান সচল" : " আজান বন্ধ"), Toast.LENGTH_SHORT).show();
                if (todayData != null && tomorrowData != null) {
                    PrayerNotificationManager.schedulePrayerNotifications(this, todayData, tomorrowData);
                }
            });
        }
    }

    private void updateTomorrowUI(PrayerTimesResponse.Data data) {
        if (data != null && data.timings != null) {
            setupTomorrowItem(binding.tomFajr, "ফজর", data.timings.Fajr);
            setupTomorrowItem(binding.tomDhuhr, "যোহর", data.timings.Dhuhr);
            setupTomorrowItem(binding.tomAsr, "আসর", data.timings.Asr);
            setupTomorrowItem(binding.tomMaghrib, "মাগরিব", data.timings.Maghrib);
            setupTomorrowItem(binding.tomIsha, "ইশা", data.timings.Isha);
        }
    }

    private void setupTomorrowItem(ItemPrayerTomorrowBinding itemBinding, String name, String time) {
        if (itemBinding != null) {
            itemBinding.prayerName.setText(name);
            itemBinding.prayerTime.setText(DateUtils.convertToBengaliDigits(DateUtils.formatTimeTo12Hour(time)));
        }
    }

    private void updateProgressBar(PrayerTimesResponse.Data data) {
        long[] times = DateUtils.getElapsedAndTotal(data);
        long elapsed = times[0];
        long total = times[1];

        if (total > 0) {
            float progress = (float) elapsed * 100 / total;
            binding.circularProgressBar.setProgressWithAnimation(Math.min(100, Math.max(0, progress)), 1000L);
        }
    }

    private void showLocationSelectionDialog() {
        boolean isInternational = LocationUtils.isInternationalLocation(this);
        if (isInternational) {
            showInternationalLocationDialog();
        } else {
            showBangladeshLocationDialog();
        }
    }

    private void showBangladeshLocationDialog() {
        DialogLocationSelectionBinding dialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_location_selection, null, false);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.subDistrictSpinner.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, LocationUtils.BANGLADESH_DISTRICTS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.districtSpinner.setAdapter(adapter);

        int currentPos = Arrays.asList(LocationUtils.BANGLADESH_DISTRICTS).indexOf(selectedDistrict);
        if (currentPos >= 0) dialogBinding.districtSpinner.setSelection(currentPos);

        dialogBinding.confirmButton.setOnClickListener(v -> {
            selectedDistrict = dialogBinding.districtSpinner.getSelectedItem().toString();
            useCoordinates = false;
            LocationUtils.saveLocation(this, selectedDistrict, "");
            binding.locationWeatherText.setText(selectedDistrict);
            fetchAllPrayerData();
            dialog.dismiss();
        });

        dialogBinding.useLocationButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                useCoordinates = true;
                fetchDeviceLocation();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showInternationalLocationDialog() {
        DialogCountrySelectionBinding dialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_country_selection, null, false);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .create();

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, LocationUtils.getAllCountries());
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.countrySpinner.setAdapter(countryAdapter);

        String[] savedLocation = LocationUtils.loadLocation(this);
        String savedCountry = savedLocation.length > 1 ? savedLocation[1] : "Bangladesh";
        int countryPos = Arrays.asList(LocationUtils.getAllCountries()).indexOf(savedCountry);
        if (countryPos >= 0) dialogBinding.countrySpinner.setSelection(countryPos);

        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, LocationUtils.BANGLADESH_DISTRICTS);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.districtSpinner.setAdapter(districtAdapter);

        int districtPos = Arrays.asList(LocationUtils.BANGLADESH_DISTRICTS).indexOf(selectedDistrict);
        if (districtPos >= 0) dialogBinding.districtSpinner.setSelection(districtPos);

        dialogBinding.confirmButton.setOnClickListener(v -> {
            selectedDistrict = dialogBinding.districtSpinner.getSelectedItem().toString();
            String selectedCountry = dialogBinding.countrySpinner.getSelectedItem().toString();
            useCoordinates = false;
            LocationUtils.saveInternationalLocation(this, selectedDistrict, selectedCountry);
            binding.locationWeatherText.setText(selectedDistrict + ", " + selectedCountry);
            fetchAllPrayerData();
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacks(timeRunnable);
    }

    private void checkBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
                new AlertDialog.Builder(this)
                        .setTitle("ব্যাকগ্রাউন্ড রান অনুমতি")
                        .setMessage("অ্যাপটি বন্ধ থাকলেও সঠিক সময়ে আজান পাওয়ার জন্য ব্যাটারি অপটিমাইজেশন বন্ধ করা প্রয়োজন।")
                        .setPositiveButton("সেটিংস এ যান", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        })
                        .setNegativeButton("পরে", null)
                        .show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String newMethod = sp.getString("prayer_calculation_method", "3");
        String newSchool = sp.getString("prayer_school", "1");

        if (!newMethod.equals(currentMethod) || !newSchool.equals(currentSchool)) {
            currentMethod = newMethod;
            currentSchool = newSchool;
            Toast.makeText(this, "সেটিংস আপডেট হচ্ছে...", Toast.LENGTH_SHORT).show();
            showLoader();
            fetchAllPrayerData();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                useCoordinates = true;
                fetchDeviceLocation();
            } else {
                Toast.makeText(this, "লোকেশন পারমিশন প্রয়োজন", Toast.LENGTH_SHORT).show();
            }
        }
    }
}