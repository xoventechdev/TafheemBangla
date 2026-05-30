package com.minbar.tafhimulquran.Prayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PrayerFetchWorker extends Worker {
    private static final String TAG = "PrayerFetchWorker";

    public PrayerFetchWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        RequestQueue queue = Volley.newRequestQueue(context);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String method = sp.getString("prayer_calculation_method", "3");
        String school = sp.getString("prayer_school", "1");

        try {
            boolean useCoords = LocationUtils.shouldUseCoordinates(context);
            PrayerTimesResponse.Data todayData;
            PrayerTimesResponse.Data tomorrowData;

            if (useCoords) {
                double[] coords = LocationUtils.getSavedCoordinates(context);
                todayData = fetchSyncCoords(queue, coords[0], coords[1], "timings", method, school);
                
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 1);
                String tomorrowStr = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(calendar.getTime());
                tomorrowData = fetchSyncCoords(queue, coords[0], coords[1], tomorrowStr, method, school);
            } else {
                String[] savedLocation = LocationUtils.loadLocation(context);
                String city = LocationUtils.getApiCityName(savedLocation[0], "");
                String encodedCity = URLEncoder.encode(city, "UTF-8");
                String country = savedLocation.length > 1 ? savedLocation[1] : "Bangladesh";

                todayData = fetchSyncCity(queue, encodedCity, "timings", method, school, country);

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 1);
                String tomorrowStr = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(calendar.getTime());
                tomorrowData = fetchSyncCity(queue, encodedCity, tomorrowStr, method, school, country);
            }

            if (todayData != null) {
                PrayerNotificationManager.schedulePrayerNotifications(context, todayData, tomorrowData);
                return Result.success();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in background fetch", e);
        }

        return Result.retry();
    }

    private PrayerTimesResponse.Data fetchSyncCity(RequestQueue queue, String encodedCity, String dateStr, String method, String school, String country) {
        String url = String.format(Locale.US, "https://api.aladhan.com/v1/timingsByCity/%s?city=%s&country=%s&method=%s&school=%s", dateStr, encodedCity, country, method, school);
        return executeRequest(queue, url);
    }

    private PrayerTimesResponse.Data fetchSyncCoords(RequestQueue queue, double lat, double lon, String dateStr, String method, String school) {
        String url = String.format(Locale.US, "https://api.aladhan.com/v1/timings/%s?latitude=%f&longitude=%f&method=%s&school=%s", dateStr, lat, lon, method, school);
        return executeRequest(queue, url);
    }

    private PrayerTimesResponse.Data executeRequest(RequestQueue queue, String url) {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
        queue.add(request);

        try {
            JSONObject response = future.get(30, TimeUnit.SECONDS);
            PrayerTimesResponse prayerResponse = new Gson().fromJson(response.toString(), PrayerTimesResponse.class);
            if (prayerResponse != null) {
                return prayerResponse.data;
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Log.e(TAG, "Fetch failed for URL: " + url, e);
        }
        return null;
    }
}
