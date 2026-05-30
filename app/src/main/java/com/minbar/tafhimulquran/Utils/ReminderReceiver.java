package com.minbar.tafhimulquran.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.minbar.tafhimulquran.Activity.MainActivity;
import com.minbar.tafhimulquran.R;

import java.util.Random;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "DailyReminderChannel";
    private static final int NOTIFICATION_ID = 2001;
    private static final String PREF_NAME = "ReminderPrefs";
    private static final String KEY_LAST_OPEN_TIME = "last_open_time";

    private static final String[] MESSAGES = {
            "পবিত্র কুরআন পাঠের মাধ্যমে আপনার দিনটি বরকতময় করে তুলুন। আজ কি তাফসীর পড়েছেন?",
            "একটি আয়াত হলেও আজ কুরআন পড়ুন। আপনার অন্তরে নূর প্রবেশ করবে।",
            "রাসূলুল্লাহ (সা.) বলেছেন, 'তোমাদের মধ্যে সেই উত্তম যে কুরআন শিখে এবং শেখায়।' আজ কি কিছুটা সময় দিয়েছেন?",
            "কুরআন ও হাদীস পাঠের মাধ্যমে দ্বীনি জ্ঞান অর্জন করুন। আজ অ্যাপটি ওপেন করুন।",
            "আল্লাহর জিকির ও কুরআন তিলাওয়াতের মাধ্যমে মানসিক শান্তি খুঁজুন।",
            "আজকের দিনটি কি কুরআন তিলাওয়াত ছাড়া পার হয়ে যাবে? কিছুটা সময় আল্লাহর কালামের জন্য দিন।",
            "হাদীস পাঠের মাধ্যমে সুন্নাহ সম্পর্কে জানুন। আজ কি একটি হাদীস পড়েছেন?",
            "কুরআন হলো মুমিনের জন্য নূর। এই নূর দিয়ে নিজের হৃদয়কে আলোকিত করুন।",
            "নিশ্চয়ই আল্লাহর জিকিরেই অন্তরসমূহ প্রশান্ত হয়। আজ কি কুরআন পড়েছেন?",
            "কুরআনের প্রতিটি হরফ পাঠে রয়েছে দশটি নেকি। আজ আপনার ঝুলি পূর্ণ করেছেন তো?",
            "রাসূল (সা.) বলেছেন, 'কুরআন কিয়ামতের দিন তার পাঠকারীর জন্য সুপারিশ করবে।'",
            "দ্বীনি ইলম অর্জন করা প্রত্যেক মুসলমানের ওপর ফরজ। আজ কি একটি হাদীস পড়েছেন?",
            "সাফল্য ও শান্তির মূল চাবিকাঠি হলো আল্লাহর পথে চলা। আজ কুরআন তিলাওয়াত করুন।"
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long lastOpenTime = prefs.getLong(KEY_LAST_OPEN_TIME, 0);
        long currentTime = System.currentTimeMillis();

        // Check if more than 22 hours have passed since last open. 
        // We use 22h instead of 24h because we check daily at 10 PM.
        // If they opened it earlier today (e.g., 8 AM), the diff would be 14 hours.
        // If they last opened it yesterday at 10 PM, the diff would be 24 hours.
        if (currentTime - lastOpenTime > 22 * 60 * 60 * 1000) {
            showNotification(context);
        }

        // Reschedule for next day 10 PM
        ReminderManager.scheduleReminder(context);
    }

    private void showNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Daily Reminder", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Islamic Motivational Reminders");
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String randomMessage = MESSAGES[new Random().nextInt(MESSAGES.length)];

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo) // Using the logo icon
                .setContentTitle("ইসলামিক রিমাইন্ডার")
                .setContentText(randomMessage)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(randomMessage))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
