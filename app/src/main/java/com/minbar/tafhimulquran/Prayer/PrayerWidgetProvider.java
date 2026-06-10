package com.minbar.tafhimulquran.Prayer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.minbar.tafhimulquran.Activity.MainActivity;
import com.minbar.tafhimulquran.R;

public class PrayerWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.prayer_widget);

        // Set Intent to open MainActivity with prayer fragment extra on click
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("OPEN_FRAGMENT", "prayer");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);

        // Get cached data
        PrayerTimesResponse.Data today = PrayerNotificationManager.getCachedPrayerData(context);

        if (today != null && today.timings != null) {
            String nextPrayerInfo = DateUtils.getNextPrayer(today);
            if (nextPrayerInfo != null && nextPrayerInfo.contains("|")) {
                String[] parts = nextPrayerInfo.split("\\|");
                views.setTextViewText(R.id.next_prayer_text, parts[0]);
                views.setTextViewText(R.id.next_prayer_time_text, DateUtils.convertToBengaliDigits(parts[1]));
            }

            views.setTextViewText(R.id.sunrise_time_text, "সূর্যোদয়: " + DateUtils.convertToBengaliDigits(DateUtils.formatTimeTo12Hour(today.timings.Sunrise)));
            views.setTextViewText(R.id.sunset_time_text, "সূর্যাস্ত: " + DateUtils.convertToBengaliDigits(DateUtils.formatTimeTo12Hour(today.timings.Maghrib)));

            // Update Progress
            long[] progressTimes = DateUtils.getElapsedAndTotal(today);
            if (progressTimes[1] > 0) {
                int progress = (int) ((float) progressTimes[0] * 100 / progressTimes[1]);
                views.setProgressBar(R.id.widget_progress, 100, Math.min(100, Math.max(0, progress)), false);
            }
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, PrayerWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }
}
