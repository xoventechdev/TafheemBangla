package com.minbar.tafhimulquran.Fragment;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.minbar.tafhimulquran.Activity.MainActivity;
import com.minbar.tafhimulquran.Prayer.DateUtils;
import com.minbar.tafhimulquran.Prayer.LocationUtils;
import com.minbar.tafhimulquran.Prayer.PrayerNotificationManager;
import com.minbar.tafhimulquran.Prayer.PrayerTimesResponse;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.databinding.DialogCountrySelectionBinding;
import com.minbar.tafhimulquran.databinding.DialogLocationSelectionBinding;
import com.minbar.tafhimulquran.databinding.FragmentPrayerBinding;
import com.minbar.tafhimulquran.databinding.ItemPrayerTimeBinding;
import com.minbar.tafhimulquran.databinding.ItemPrayerTomorrowBinding;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class PrayerFragment extends Fragment {

    private FragmentPrayerBinding binding;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_prayer, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initClients();
        loadSavedLocation();
        setupListeners();
        startClock();
        checkExactAlarmPermission();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        currentMethod = sp.getString("prayer_calculation_method", "3");
        currentSchool = sp.getString("prayer_school", "1");

        checkPermissionsAndFetchData();
    }

    private void initClients() {
        requestQueue = Volley.newRequestQueue(requireContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    private void loadSavedLocation() {
        String[] savedLocation = LocationUtils.loadLocation(requireContext());
        selectedDistrict = savedLocation[0];
        String country = savedLocation.length > 1 ? savedLocation[1] : "Bangladesh";

        useCoordinates = LocationUtils.shouldUseCoordinates(requireContext());
        if (useCoordinates) {
            double[] coords = LocationUtils.getSavedCoordinates(requireContext());
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
//        binding.azanSoundSettings.setOnClickListener(v -> {
//            if (getActivity() instanceof MainActivity) {
//                ((MainActivity) getActivity()).openSettingsFragment();
//            }
//        });

//        binding.azanSoundSettings.setOnLongClickListener(v -> {
//            Context context = getContext();
//            if (context != null) {
//                PrayerNotificationManager.scheduleTestAlarm(context);
//                Toast.makeText(context, "Test Azan will ring in 10 seconds! Lock your phone.", Toast.LENGTH_LONG).show();
//            }
//            return true;
//        });
    }

    private void checkExactAlarmPermission() {
        Context context = getContext();
        if (context == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                new AlertDialog.Builder(context)
                        .setTitle("অ্যালার্ম অনুমতি প্রয়োজন")
                        .setMessage("সঠিক সময়ে আজান দেওয়ার জন্য 'Alarms & Reminders' অনুমতি প্রয়োজন।")
                        .setPositiveButton("সেটিংস", (dialog, which) -> {
                            if (isAdded()) {
                                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                                startActivity(intent);
                            }
                        })
                        .show();
                return;
            }
        }
        checkBatteryOptimizations();
    }

    private void startClock() {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                if (getContext() == null) return;
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
        todayData = PrayerNotificationManager.getCachedPrayerData(requireContext());
        if (todayData != null) {
            updateUI(todayData);
        }

        if (useCoordinates && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchDeviceLocation();
        } else {
            fetchAllPrayerData();
        }
    }

    private void fetchDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            fetchAllPrayerData();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                if (getContext() == null) return;
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                selectedDistrict = LocationUtils.getDistrictFromCoordinates(requireContext(), latitude, longitude);
                useCoordinates = true;
                LocationUtils.saveCoordinates(requireContext(), latitude, longitude, selectedDistrict);
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

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String method = sp.getString("prayer_calculation_method", "3");
        String school = sp.getString("prayer_school", "1");

        try {
            if (useCoordinates) {
                url = String.format(Locale.US, "https://api.aladhan.com/v1/timings/%s?latitude=%f&longitude=%f&method=%s&school=%s", dateStr, latitude, longitude, method, school);
            } else {
                String city = LocationUtils.getApiCityName(selectedDistrict, "");
                String encodedCity = URLEncoder.encode(city, "UTF-8");
                String[] savedLocation = LocationUtils.loadLocation(requireContext());
                String country = savedLocation.length > 1 ? savedLocation[1] : "Bangladesh";
                url = String.format(Locale.US, "https://api.aladhan.com/v1/timingsByCity/%s?city=%s&country=%s&method=%s&school=%s", dateStr, encodedCity, country, method, school);
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        if (getContext() == null) return;
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
                                PrayerNotificationManager.schedulePrayerNotifications(requireContext(), todayData, tomorrowData);
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

            Context context = getContext();
            if (context == null) return;

            SharedPreferences prefs = context.getSharedPreferences("PrayerPrefs", Context.MODE_PRIVATE);
            boolean isEnabled = prefs.getBoolean("alarm_enabled_" + name, true);
            itemBinding.alarmIcon.setImageResource(isEnabled ? R.drawable.ic_alarm_on : R.drawable.ic_alarm_off);
            itemBinding.alarmIcon.setOnClickListener(v -> {
                Context currentContext = getContext();
                if (currentContext == null) return;

                boolean newState = !prefs.getBoolean("alarm_enabled_" + name, true);
                prefs.edit().putBoolean("alarm_enabled_" + name, newState).apply();
                itemBinding.alarmIcon.setImageResource(newState ? R.drawable.ic_alarm_on : R.drawable.ic_alarm_off);
                Toast.makeText(currentContext, name + (newState ? " আজান সচল" : " আজান বন্ধ"), Toast.LENGTH_SHORT).show();
                if (todayData != null && tomorrowData != null) {
                    PrayerNotificationManager.schedulePrayerNotifications(currentContext, todayData, tomorrowData);
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
        Context context = getContext();
        if (context == null) return;

        boolean isInternational = LocationUtils.isInternationalLocation(context);
        if (isInternational) {
            showInternationalLocationDialog();
        } else {
            showBangladeshLocationDialog();
        }
    }

    private void showBangladeshLocationDialog() {
        Context context = getContext();
        if (context == null) return;

        DialogLocationSelectionBinding dialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.dialog_location_selection, null, false);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.subDistrictSpinner.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, LocationUtils.BANGLADESH_DISTRICTS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.districtSpinner.setAdapter(adapter);

        int currentPos = Arrays.asList(LocationUtils.BANGLADESH_DISTRICTS).indexOf(selectedDistrict);
        if (currentPos >= 0) dialogBinding.districtSpinner.setSelection(currentPos);

        dialogBinding.confirmButton.setOnClickListener(v -> {
            Context currentContext = getContext();
            if (currentContext == null) return;

            selectedDistrict = dialogBinding.districtSpinner.getSelectedItem().toString();
            useCoordinates = false;
            LocationUtils.saveLocation(currentContext, selectedDistrict, "");
            binding.locationWeatherText.setText(selectedDistrict);
            fetchAllPrayerData();
            dialog.dismiss();
        });

        dialogBinding.useLocationButton.setOnClickListener(v -> {
            Context currentContext = getContext();
            if (currentContext == null) {
                dialog.dismiss();
                return;
            }

            if (ActivityCompat.checkSelfPermission(currentContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                useCoordinates = true;
                fetchDeviceLocation();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showInternationalLocationDialog() {
        Context context = getContext();
        if (context == null) return;

        DialogCountrySelectionBinding dialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.dialog_country_selection, null, false);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogBinding.getRoot())
                .create();

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, LocationUtils.getAllCountries());
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.countrySpinner.setAdapter(countryAdapter);

        String[] savedLocation = LocationUtils.loadLocation(context);
        String savedCountry = savedLocation.length > 1 ? savedLocation[1] : "Bangladesh";
        int countryPos = Arrays.asList(LocationUtils.getAllCountries()).indexOf(savedCountry);
        if (countryPos >= 0) dialogBinding.countrySpinner.setSelection(countryPos);

        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, LocationUtils.BANGLADESH_DISTRICTS);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.districtSpinner.setAdapter(districtAdapter);

        int districtPos = Arrays.asList(LocationUtils.BANGLADESH_DISTRICTS).indexOf(selectedDistrict);
        if (districtPos >= 0) dialogBinding.districtSpinner.setSelection(districtPos);

        dialogBinding.confirmButton.setOnClickListener(v -> {
            Context currentContext = getContext();
            if (currentContext == null) return;

            selectedDistrict = dialogBinding.districtSpinner.getSelectedItem().toString();
            String selectedCountry = dialogBinding.countrySpinner.getSelectedItem().toString();
            useCoordinates = false;
            LocationUtils.saveInternationalLocation(currentContext, selectedDistrict, selectedCountry);
            binding.locationWeatherText.setText(selectedDistrict + ", " + selectedCountry);
            fetchAllPrayerData();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void checkBatteryOptimizations() {
        Context context = getContext();
        if (context == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm != null && !pm.isIgnoringBatteryOptimizations(context.getPackageName())) {
                new AlertDialog.Builder(context)
                        .setTitle("ব্যাকগ্রাউন্ড রান অনুমতি")
                        .setMessage("অ্যাপটি বন্ধ থাকলেও সঠিক সময়ে আজান পাওয়ার জন্য ব্যাটারি অপটিমাইজেশন বন্ধ করা প্রয়োজন।")
                        .setPositiveButton("সেটিংস এ যান", (dialog, which) -> {
                            Context currentContext = getContext();
                            if (currentContext != null) {
                                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                                intent.setData(Uri.parse("package:" + currentContext.getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("পরে", null)
                        .show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Context context = getContext();
        if (context == null) return;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String newMethod = sp.getString("prayer_calculation_method", "3");
        String newSchool = sp.getString("prayer_school", "1");

        if (!newMethod.equals(currentMethod) || !newSchool.equals(currentSchool)) {
            currentMethod = newMethod;
            currentSchool = newSchool;
            Toast.makeText(context, "সেটিংস আপডেট হচ্ছে...", Toast.LENGTH_SHORT).show();
            showLoader();
            fetchAllPrayerData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timeHandler.removeCallbacks(timeRunnable);
        binding = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            Context context = getContext();
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                useCoordinates = true;
                fetchDeviceLocation();
            } else if (context != null) {
                Toast.makeText(context, "লোকেশন পারমিশন প্রয়োজন", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
