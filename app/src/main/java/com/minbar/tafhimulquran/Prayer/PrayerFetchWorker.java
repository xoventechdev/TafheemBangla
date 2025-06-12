package com.minbar.tafhimulquran.Prayer;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

public class PrayerFetchWorker extends Worker {
    private static final String TAG = "PrayerFetchWorker";
    private static final String API_URL = "http://api.aladhan.com/v1/timingsByCity?city=%s&country=Bangladesh&method=3&school=1";

    public PrayerFetchWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        Context context = getApplicationContext();
        String[] savedLocation = LocationUtils.loadLocation(context);
        String selectedDistrict = savedLocation[0];
        String city = LocationUtils.getApiCityName(selectedDistrict, "");
        String url = String.format(API_URL, city);

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    PrayerTimesResponse prayerResponse = gson.fromJson(response.toString(), PrayerTimesResponse.class);
                    PrayerNotificationManager.schedulePrayerNotifications(context, prayerResponse.data);
                },
                error -> Log.e(TAG, "Failed to fetch prayer times: " + error.getMessage()));

        queue.add(request);
        return Result.success();
    }


}