package com.minbar.tafhimulquran.Prayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.minbar.tafhimulquran.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class PrayerActivity extends AppCompatActivity {

    private TextView hijriDateText, locationWeatherText, currentTimeText, nextPrayerText, nextPrayerTimeText;
    private TextView sunriseTimeText, sunsetTimeText, fajrTimeText, dhuhrTimeText;
    private TextView asrTimeText, maghribTimeText, ishaTimeText;
    private CircularProgressBar circularProgressBar;
    private RequestQueue requestQueue;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int EXACT_ALARM_PERMISSION_REQUEST_CODE = 1001;
    private String selectedDistrict = "ঢাকা";
    private boolean useCoordinates = false;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private Handler handler = new Handler();
    private ImageView fajrClock, dhuhrClock, asrClock, maghribClock, ishaClock;
    private Runnable countdownRunnable;
    private static final String TAG = "PrayerActivity";
    private boolean locationPermissionRequested = false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer);



        MaterialToolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("নামাজের সময় সূচী");

        // Initialize views
        hijriDateText = findViewById(R.id.hijri_date_text);
        locationWeatherText = findViewById(R.id.location_weather_text);
        currentTimeText = findViewById(R.id.current_time_text);
        nextPrayerText = findViewById(R.id.next_prayer_text);
        nextPrayerTimeText = findViewById(R.id.next_prayer_time_text);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        sunriseTimeText = findViewById(R.id.sunrise_time_text);
        sunsetTimeText = findViewById(R.id.sunset_time_text);
        fajrTimeText = findViewById(R.id.fajr_time_text);
        dhuhrTimeText = findViewById(R.id.dhuhr_time_text);
        asrTimeText = findViewById(R.id.asr_time_text);
        maghribTimeText = findViewById(R.id.maghrib_time_text);
        ishaTimeText = findViewById(R.id.isha_time_text);
        fajrClock = findViewById(R.id.fajr_clock);
        dhuhrClock = findViewById(R.id.dhuhr_clock);
        asrClock = findViewById(R.id.asr_clock);
        maghribClock = findViewById(R.id.maghrib_clock);
        ishaClock = findViewById(R.id.isha_clock);

        // Initialize Volley and Location Client
        requestQueue = Volley.newRequestQueue(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Load stored location
        String[] savedLocation = LocationUtils.loadLocation(this);
        selectedDistrict = savedLocation[0];
        locationWeatherText.setText(selectedDistrict);

        // Check location permission and fetch device location if granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchDeviceLocation();
        } else {
            fetchPrayerTimes();
            updateNextDayPrayerTimes();
        }

        // Update current time every second
        updateCurrentTime();
        locationWeatherText.setOnClickListener(v -> showLocationSelectionDialog());

        // Start countdown
//        startCountdown();

        checkLocationPermission();
    }


    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!locationPermissionRequested) {
                locationPermissionRequested = true;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            fetchDeviceLocation();
            updateNextDayPrayerTimes();
            fetchPrayerTimes();
        }
    }

    private void updateNextDayPrayerTimes() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dhaka"));
        calendar.add(Calendar.DATE, 1); // Move to next day
        String nextDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
        String url = useCoordinates
                ? "http://api.aladhan.com/v1/timings/" + nextDay + "?latitude=" + latitude + "&longitude=" + longitude + "&method=3&school=1"
                : "http://api.aladhan.com/v1/timingsByCity/" + nextDay + "?city=" + LocationUtils.getApiCityName(selectedDistrict, "") + "&country=Bangladesh&method=3&school=1";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    PrayerTimesResponse nextDayResponse = gson.fromJson(response.toString(), PrayerTimesResponse.class);
                    PrayerTimesResponse.Data nextDayData = nextDayResponse.data;

                    TextView fajrTimeTextT = findViewById(R.id.fajr_time_textT);
                    TextView dhuhrTimeTextT = findViewById(R.id.dhuhr_time_textT);
                    TextView asrTimeTextT = findViewById(R.id.asr_time_textT);
                    TextView maghribTimeTextT = findViewById(R.id.maghrib_time_textT);
                    TextView ishaTimeTextT = findViewById(R.id.isha_time_textT);

                    fajrTimeTextT.setText(DateUtils.formatTimeTo12Hour(nextDayData.timings.Fajr));
                    dhuhrTimeTextT.setText(DateUtils.formatTimeTo12Hour(nextDayData.timings.Dhuhr));
                    asrTimeTextT.setText(DateUtils.formatTimeTo12Hour(nextDayData.timings.Asr));
                    maghribTimeTextT.setText(DateUtils.formatTimeTo12Hour(nextDayData.timings.Maghrib));
                    ishaTimeTextT.setText(DateUtils.formatTimeTo12Hour(nextDayData.timings.Isha));
                },
                error -> Toast.makeText(PrayerActivity.this, "Error fetching next day prayer times: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCurrentTime() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));
                currentTimeText.setText(sdf.format(Calendar.getInstance().getTime()));
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    private void showLocationSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_location_selection, null);
        builder.setView(dialogView);

        Spinner districtSpinner = dialogView.findViewById(R.id.district_spinner);
        Button useLocationButton = dialogView.findViewById(R.id.use_location_button);
        Button confirmButton = dialogView.findViewById(R.id.confirm_button);

        // Hide sub-district spinner
        Spinner subDistrictSpinner = dialogView.findViewById(R.id.sub_district_spinner);
        subDistrictSpinner.setVisibility(View.GONE);

        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, LocationUtils.getDistricts());
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtAdapter);

        int districtIndex = Arrays.asList(LocationUtils.getDistricts()).indexOf(selectedDistrict);
        if (districtIndex >= 0) districtSpinner.setSelection(districtIndex);

        AlertDialog dialog = builder.create();

        useLocationButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(PrayerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fetchDeviceLocation();
                dialog.dismiss();
            } else {
                ActivityCompat.requestPermissions(PrayerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        });

        confirmButton.setOnClickListener(v -> {
            selectedDistrict = (String) districtSpinner.getSelectedItem();
            useCoordinates = false;
            LocationUtils.saveLocation(PrayerActivity.this, selectedDistrict, "");
            locationWeatherText.setText(selectedDistrict);
            fetchPrayerTimes();
            updateNextDayPrayerTimes();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void fetchDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                selectedDistrict = LocationUtils.getDistrictFromCoordinates(this, latitude, longitude);
                useCoordinates = true;
                LocationUtils.saveLocation(this, selectedDistrict, "");
                locationWeatherText.setText(selectedDistrict);
                fetchPrayerTimesWithCoordinates(latitude, longitude);

                updateNextDayPrayerTimes();
            } else {
                Toast.makeText(this, "Unable to detect location, using saved location", Toast.LENGTH_SHORT).show();
                fetchPrayerTimes();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Location error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            fetchPrayerTimes();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchDeviceLocation();
                updateNextDayPrayerTimes();
                fetchPrayerTimes();
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Location permission denied, using saved location", Toast.LENGTH_LONG).show();
                fetchPrayerTimes();
                updateNextDayPrayerTimes();
            }
        } else if (requestCode == EXACT_ALARM_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            PrayerNotificationManager.onRequestPermissionsResult(this, requestCode, grantResults);
        }
    }

    private void fetchPrayerTimes() {
        if (useCoordinates) {
            fetchPrayerTimesWithCoordinates(latitude, longitude);
        } else {
            String city = LocationUtils.getApiCityName(selectedDistrict, "");
            String url = "http://api.aladhan.com/v1/timingsByCity?city=" + city + "&country=Bangladesh&method=3&school=1";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        Gson gson = new Gson();
                        PrayerTimesResponse prayerResponse = gson.fromJson(response.toString(), PrayerTimesResponse.class);
                        updateUI(prayerResponse.data);
                    },
                    error -> Toast.makeText(PrayerActivity.this, "Error fetching prayer times: " + error.getMessage(), Toast.LENGTH_SHORT).show());

            requestQueue.add(request);
        }
    }

    private void fetchPrayerTimesWithCoordinates(double latitude, double longitude) {
        String url = "http://api.aladhan.com/v1/timings?latitude=" + latitude + "&longitude=" + longitude + "&method=3&school=1";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    PrayerTimesResponse prayerResponse = gson.fromJson(response.toString(), PrayerTimesResponse.class);
                    updateUI(prayerResponse.data);
                },
                error -> Toast.makeText(PrayerActivity.this, "Error fetching prayer times: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }

    private void updateUI(PrayerTimesResponse.Data data) {
        // Update Hijri date
        hijriDateText.setText(DateUtils.formatHijriDate(data.date.hijri.date, data.date.hijri.month.number));

        // Update prayer times
        fajrTimeText.setText(DateUtils.formatTimeTo12Hour(data.timings.Fajr));
        dhuhrTimeText.setText(DateUtils.formatTimeTo12Hour(data.timings.Dhuhr));
        asrTimeText.setText(DateUtils.formatTimeTo12Hour(data.timings.Asr));
        maghribTimeText.setText(DateUtils.formatTimeTo12Hour(data.timings.Maghrib));
        ishaTimeText.setText(DateUtils.formatTimeTo12Hour(data.timings.Isha));
        sunriseTimeText.setText(DateUtils.formatTimeTo12Hour(data.timings.Sunrise));
        sunsetTimeText.setText(DateUtils.formatTimeTo12Hour(data.timings.Maghrib)); // Assuming Sunset = Maghrib

        // Determine next prayer and update progress
        String nextPrayer = DateUtils.getNextPrayer(data.timings);
        String[] prayerParts = nextPrayer.split(" ", 2);
        nextPrayerText.setText(prayerParts[0]);
        String[] next = prayerParts[1].split(" ", 2);
        nextPrayerTimeText.setText(next[0]);
        updateProgressBar(data.timings);

        // Update clock tints
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dhaka"));
        int currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
        String[] times = {data.timings.Fajr, data.timings.Dhuhr, data.timings.Asr, data.timings.Maghrib, data.timings.Isha};
        int[] minutes = new int[5];
        for (int i = 0; i < times.length; i++) {
            String[] timeParts = times[i].split(":");
            minutes[i] = Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);
        }
        fajrClock.setImageTintList(null);
        dhuhrClock.setImageTintList(null);
        asrClock.setImageTintList(null);
        maghribClock.setImageTintList(null);
        ishaClock.setImageTintList(null);
        for (int i = 0; i < minutes.length; i++) {
            if (currentMinutes >= minutes[i]) {
                if (i == 0) fajrClock.setImageTintList(androidx.core.content.ContextCompat.getColorStateList(this, android.R.color.holo_red_dark));
                else if (i == 1) dhuhrClock.setImageTintList(androidx.core.content.ContextCompat.getColorStateList(this, android.R.color.holo_red_dark));
                else if (i == 2) asrClock.setImageTintList(androidx.core.content.ContextCompat.getColorStateList(this, android.R.color.holo_red_dark));
            } else if (i == nextPrayerIndex(nextPrayer)) {
                if (i == 3) maghribClock.setImageTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.teal_200));
                else if (i == 4) ishaClock.setImageTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.teal_200));
            } else {
                if (i == 3) maghribClock.setImageTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.light_gray));
                else if (i == 4) ishaClock.setImageTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.light_gray));
            }
        }

        PrayerNotificationManager.schedulePrayerNotifications(this, data);
    }

    private int nextPrayerIndex(String nextPrayer) {
        if (nextPrayer == null || nextPrayer.isEmpty()) {
            Log.e(TAG, "nextPrayer is null or empty");
            return -1;
        }
        String prayerName = nextPrayer.split(" ")[0].trim();
        switch (prayerName) {
            case "আসর": return 2;
            case "মাগরিব": return 3;
            case "ইশা": return 4;
            case "ফজর": return 0;
            case "যোহর": return 1;
            default:
                Log.e(TAG, "Unexpected prayer name: " + prayerName);
                return -1;
        }
    }

    private void updateProgressBar(PrayerTimesResponse.Data.Timings timings) {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dhaka"));
        int currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);

        String[] prayerTimes = {timings.Fajr, timings.Dhuhr, timings.Asr, timings.Maghrib, timings.Isha};
        int[] prayerMinutes = new int[5];
        for (int i = 0; i < prayerTimes.length; i++) {
            String[] timeParts = prayerTimes[i].split(":");
            prayerMinutes[i] = Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);
        }

        int nextPrayerIndex = 0;
        for (int i = 0; i < prayerMinutes.length; i++) {
            if (currentMinutes < prayerMinutes[i]) {
                nextPrayerIndex = i;
                break;
            }
        }
        if (currentMinutes >= prayerMinutes[4]) nextPrayerIndex = 0;

        int startMinutes = (nextPrayerIndex > 0) ? prayerMinutes[nextPrayerIndex - 1] : prayerMinutes[4];
        int endMinutes = prayerMinutes[nextPrayerIndex];
        int totalDuration = (endMinutes - startMinutes + 1440) % 1440;
        int timeLeft = (endMinutes - currentMinutes + 1440) % 1440;
        int progress = 100 - (timeLeft * 100 / totalDuration);

        circularProgressBar.setProgressWithAnimation((float) progress, 1000L);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownRunnable != null) {
            handler.removeCallbacks(countdownRunnable);
        }
    }
}