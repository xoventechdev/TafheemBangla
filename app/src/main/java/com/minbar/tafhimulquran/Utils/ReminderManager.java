package com.minbar.tafhimulquran.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.Calendar;

public class ReminderManager {
    private static final String PREF_NAME = "ReminderPrefs";
    private static final String KEY_LAST_OPEN_TIME = "last_open_time";

    /**
     * Call this in MainActivity.onCreate to record that the app was opened today.
     */
    public static void updateLastOpen(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putLong(KEY_LAST_OPEN_TIME, System.currentTimeMillis()).apply();
        
        // Ensure a reminder is scheduled for 10 PM
        scheduleReminder(context);
    }

    /**
     * Schedules the next reminder check at 10 PM.
     */
    public static void scheduleReminder(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 2001, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 22); // 10 PM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If it's already past 10 PM, schedule for tomorrow
        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DATE, 1);
        }

        // Use setAndAllowWhileIdle for battery optimization compliance if exactness isn't critical,
        // but since we have permissions for exact alarms, we use them to ensure it hits exactly at 10 PM.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public static boolean wasOpenedToday(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long lastOpenTime = prefs.getLong(KEY_LAST_OPEN_TIME, 0);
        
        if (lastOpenTime == 0) return false;

        Calendar lastOpen = Calendar.getInstance();
        lastOpen.setTimeInMillis(lastOpenTime);
        
        Calendar today = Calendar.getInstance();
        
        return lastOpen.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
               lastOpen.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
    }
}
