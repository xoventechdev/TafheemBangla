package com.minbar.tafhimulquran.Prayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.minbar.tafhimulquran.Utils.ReminderManager;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            PrayerNotificationManager.rescheduleFromCache(context);
            // Ensure daily reminder is rescheduled after boot
            ReminderManager.scheduleReminder(context);
        }
    }
}
