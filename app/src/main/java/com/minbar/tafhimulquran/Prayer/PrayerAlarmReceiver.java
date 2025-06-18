package com.minbar.tafhimulquran.Prayer;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.minbar.tafhimulquran.R;

public class PrayerAlarmReceiver extends BroadcastReceiver {
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
