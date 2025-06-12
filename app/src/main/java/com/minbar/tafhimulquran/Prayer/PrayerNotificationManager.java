package com.minbar.tafhimulquran.Prayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.gson.Gson;
import com.minbar.tafhimulquran.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class PrayerNotificationManager {

     static final String CHANNEL_ID = "PrayerNotificationChannel";
     static final int NOTIFICATION_ID = 1;
    private static final String PREFS_NAME = "PrayerPrefs";
    private static final String KEY_PRAYER_DATA = "prayerData";
    private static final int REQUEST_CODE_EXACT_ALARM = 1001;

    public static void schedulePrayerNotifications(Context context, PrayerTimesResponse.Data data) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonData = gson.toJson(data);
        prefs.edit().putString(KEY_PRAYER_DATA, jsonData).apply();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            if (context instanceof android.app.Activity) {
                ActivityCompat.requestPermissions((android.app.Activity) context, new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM}, REQUEST_CODE_EXACT_ALARM);
            } else {
                // Handle case where context is not an Activity (e.g., use a foreground service or skip scheduling)
                return;
            }
        } else {
            scheduleAlarms(context, data);
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private static void scheduleAlarms(Context context, PrayerTimesResponse.Data data) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        String[] prayerTimes = {data.timings.Fajr, data.timings.Dhuhr, data.timings.Asr, data.timings.Maghrib, data.timings.Isha};
        String[] prayerNames = {"ফজর", "যোহর", "আসর", "মাগরিব", "ইশা"};

        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dhaka"));
        int currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);

        for (int i = 0; i < prayerTimes.length; i++) {
            String[] timeParts = prayerTimes[i].split(":");
            int prayerMinutes = Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);
            if (prayerMinutes >= currentMinutes || i == 0) {
                Calendar prayerTime = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dhaka"));
                prayerTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
                prayerTime.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
                prayerTime.set(Calendar.SECOND, 0);

                if (prayerTime.before(now)) {
                    prayerTime.add(Calendar.DATE, 1);
                }

                Intent intent = new Intent(context, PrayerAlarmReceiver.class);
                intent.putExtra("prayerName", prayerNames[i]);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, prayerTime.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, prayerTime.getTimeInMillis(), pendingIntent);
                }
            }
        }
    }

    public static void onRequestPermissionsResult(Context context, int requestCode, int[] grantResults) {
        if (requestCode == REQUEST_CODE_EXACT_ALARM && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String jsonData = prefs.getString(KEY_PRAYER_DATA, null);
            if (jsonData != null) {
                PrayerTimesResponse.Data data = gson.fromJson(jsonData, PrayerTimesResponse.Data.class);
                scheduleAlarms(context, data);
            }
        }
    }

    public static void rescheduleFromCache(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonData = prefs.getString(KEY_PRAYER_DATA, null);
        if (jsonData != null) {
            PrayerTimesResponse.Data data = gson.fromJson(jsonData, PrayerTimesResponse.Data.class);
            schedulePrayerNotifications(context, data);
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Prayer Notifications";
            String description = "Notifications for prayer times";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static void scheduleBackgroundFetch(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest fetchRequest = new OneTimeWorkRequest.Builder(PrayerFetchWorker.class)
                .setConstraints(constraints)
                .setInitialDelay(24, java.util.concurrent.TimeUnit.HOURS)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork("PrayerFetch", androidx.work.ExistingWorkPolicy.REPLACE, fetchRequest);
    }
}

class PrayerAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String prayerName = intent.getStringExtra("prayerName");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PrayerNotificationManager.CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("নামাজের সময়")
                .setContentText(prayerName + " নামাজ শুরু হয়েছে!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(PrayerNotificationManager.NOTIFICATION_ID, builder.build());
    }
}