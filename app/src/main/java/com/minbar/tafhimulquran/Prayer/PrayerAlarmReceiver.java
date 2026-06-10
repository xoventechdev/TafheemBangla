package com.minbar.tafhimulquran.Prayer;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.minbar.tafhimulquran.Activity.MainActivity;
import com.minbar.tafhimulquran.R;

public class PrayerAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String prayerName = intent.getStringExtra("prayerName");
        boolean isAlarmEnabled = context.getSharedPreferences("PrayerPrefs", Context.MODE_PRIVATE)
                .getBoolean("alarm_enabled_" + prayerName, true);

        if (!isAlarmEnabled) return;

        // Ensure Notification Channel is created
        PrayerNotificationManager.createNotificationChannel(context);

        // Check for POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        Intent openAppIntent = new Intent(context, MainActivity.class);
        openAppIntent.putExtra("OPEN_FRAGMENT", "prayer");
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Uri soundUri = PrayerNotificationManager.getSelectedAzanSoundUri(context);

        // GET THE DYNAMIC CHANNEL ID HERE
        String channelId = PrayerNotificationManager.getDynamicChannelId(context);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // USE THE DYNAMIC CHANNEL ID
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_namaz)
                .setContentTitle("নামাজের সময় হয়েছে")
                .setContentText(prayerName + " নামাজের ওয়াক্ত শুরু হয়েছে।")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setOngoing(false)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        if (notificationManager != null) {
            // Use a consistent ID for prayer notifications or a unique one if you want them to stack
            notificationManager.notify(PrayerNotificationManager.NOTIFICATION_ID, builder.build());
        }

        // Reschedule notifications to ensure next day's times are set
        PrayerNotificationManager.rescheduleFromCache(context);
    }
}