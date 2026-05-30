package com.minbar.tafhimulquran.Prayer;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.minbar.tafhimulquran.Prayer.PrayerTimesResponse.Data;
import com.minbar.tafhimulquran.R;

import java.util.Calendar;
import java.util.TimeZone;

public class PrayerNotificationManager {

    // Removed the static CHANNEL_ID. We will generate it dynamically.
    static final int NOTIFICATION_ID = 1001;
    private static final String PREFS_NAME = "PrayerPrefs";
    private static final String KEY_PRAYER_DATA = "prayerData";
    private static final String KEY_TOMORROW_DATA = "tomorrowData";
    private static final String KEY_LAST_UPDATE = "last_prayer_update";
    private static final long OFFLINE_CACHE_VALIDITY = 24 * 60 * 60 * 1000; // 24 hours

    // Generate a unique Channel ID based on the selected sound
    public static String getDynamicChannelId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("azan_prefs", Context.MODE_PRIVATE);
        String azanFileName = prefs.getString("azan_sound_file", "azan");
        return "PrayerChannel_" + azanFileName; // Changing the ID forces Android to apply the new sound
    }

    public static void schedulePrayerNotifications(Context context, PrayerTimesResponse.Data today) {
        schedulePrayerNotifications(context, today, null);
    }

    public static void schedulePrayerNotifications(Context context, PrayerTimesResponse.Data today, PrayerTimesResponse.Data tomorrow) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        SharedPreferences.Editor editor = prefs.edit();
        if (today != null) {
            editor.putString(KEY_PRAYER_DATA, gson.toJson(today));
        }
        if (tomorrow != null) {
            editor.putString(KEY_TOMORROW_DATA, gson.toJson(tomorrow));
        }
        editor.putLong(KEY_LAST_UPDATE, System.currentTimeMillis());
        editor.apply();

        createNotificationChannel(context);
        checkExactAlarmPermission(context);

        if (today != null) scheduleAlarms(context, today, 0); // IDs 0-4
        if (tomorrow != null) scheduleAlarms(context, tomorrow, 5); // IDs 5-9
    }

    private static void checkExactAlarmPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                // PrayerActivity should handle the rationale UI
            }
        }
    }

    public static PrayerTimesResponse.Data getCachedPrayerData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String jsonData = prefs.getString(KEY_PRAYER_DATA, null);
        long lastUpdate = prefs.getLong(KEY_LAST_UPDATE, 0);

        if (jsonData != null && (System.currentTimeMillis() - lastUpdate) < OFFLINE_CACHE_VALIDITY) {
            return new Gson().fromJson(jsonData, PrayerTimesResponse.Data.class);
        }
        return null;
    }

    private static void scheduleAlarms(Context context, PrayerTimesResponse.Data data, int startId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null || data == null || data.timings == null) return;

        String[] prayerTimes = {data.timings.Fajr, data.timings.Dhuhr, data.timings.Asr, data.timings.Maghrib, data.timings.Isha};
        String[] prayerNames = {"ফজর", "যোহর", "আসর", "মাগরিব", "ইশা"};

        String timezone = (data.meta != null && data.meta.timezone != null) ? data.meta.timezone : "Asia/Dhaka";
        TimeZone tz = TimeZone.getTimeZone(timezone);
        Calendar now = Calendar.getInstance(tz);
        long currentTimeMillis = now.getTimeInMillis();

        for (int i = 0; i < prayerTimes.length; i++) {
            if (prayerTimes[i] == null || !prayerTimes[i].contains(":")) continue;

            String cleanTime = prayerTimes[i].split(" ")[0];
            String[] timeParts = cleanTime.split(":");

            Calendar prayerTime = Calendar.getInstance(tz);
            if (startId >= 5) {
                prayerTime.add(Calendar.DATE, 1);
            }

            prayerTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
            prayerTime.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
            prayerTime.set(Calendar.SECOND, 0);
            prayerTime.set(Calendar.MILLISECOND, 0);

            if (prayerTime.getTimeInMillis() <= currentTimeMillis) {
                continue; // Skip past prayers
            }

            Intent intent = new Intent(context, PrayerAlarmReceiver.class);
            intent.putExtra("prayerName", prayerNames[i]);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, startId + i, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            setExactAlarm(alarmManager, prayerTime.getTimeInMillis(), pendingIntent);
        }
    }

    private static void setExactAlarm(AlarmManager alarmManager, long timeMillis, PendingIntent pendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
            }
        } catch (Exception e) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
        }
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager == null) return;

            String channelId = getDynamicChannelId(context);
            CharSequence name = "নামাজের সময় সূচী";
            String description = "নামাজের সময়ের নোটিফিকেশন এবং আজান";
            int importance = NotificationManager.IMPORTANCE_HIGH; // Must be HIGH for sound to play prominently

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);

            boolean vibrationEnabled = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean("prayer_vibration", true);
            channel.enableVibration(vibrationEnabled);
            if (vibrationEnabled) {
                channel.setVibrationPattern(new long[]{0, 500, 500, 500});
            }
            channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);

            // Audio attributes MUST be USAGE_ALARM to bypass some Do Not Disturb states
            Uri soundUri = getSelectedAzanSoundUri(context);
            if (soundUri != null) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
                channel.setSound(soundUri, audioAttributes);
            }

            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void saveAzanSoundPreference(Context context, String soundFile) {
        // 1. Delete the OLD channel first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.deleteNotificationChannel(getDynamicChannelId(context));
            }
        }

        // 2. Save the new sound file name
        SharedPreferences prefs = context.getSharedPreferences("azan_prefs", Context.MODE_PRIVATE);
        prefs.edit().putString("azan_sound_file", soundFile).apply();

        // 3. Create the NEW channel (it will have a new ID because of step 2)
        createNotificationChannel(context);
    }

    public static Uri getSelectedAzanSoundUri(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("azan_prefs", Context.MODE_PRIVATE);
        String azanFileName = prefs.getString("azan_sound_file", "azan");

        // Much safer way to get the raw resource URI
        int resId = context.getResources().getIdentifier(azanFileName, "raw", context.getPackageName());
        if (resId != 0) {
            return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + resId);
        }
        return null; // Fallback if file isn't found
    }

    public static void rescheduleFromCache(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String todayJson = prefs.getString(KEY_PRAYER_DATA, null);
        String tomorrowJson = prefs.getString(KEY_TOMORROW_DATA, null);

        PrayerTimesResponse.Data today = todayJson != null ? gson.fromJson(todayJson, PrayerTimesResponse.Data.class) : null;
        PrayerTimesResponse.Data tomorrow = tomorrowJson != null ? gson.fromJson(tomorrowJson, PrayerTimesResponse.Data.class) : null;

        if (today != null || tomorrow != null) {
            schedulePrayerNotifications(context, today, tomorrow);
        }
    }


    // ---------------- TEST METHOD ----------------
    public static void scheduleTestAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        // Ensure channel exists
        createNotificationChannel(context);

        Intent intent = new Intent(context, PrayerAlarmReceiver.class);
        intent.putExtra("prayerName", "টেস্ট (Test)"); // Pass a fake prayer name

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 999, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Schedule for exactly 10 seconds from right now
        long triggerTime = System.currentTimeMillis() + 10000;

        setExactAlarm(alarmManager, triggerTime, pendingIntent);
    }
    // ---------------------------------------------
}