package com.minbar.tafhimulquran.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PronunciationChangeReceiver extends BroadcastReceiver {
    public static final String ACTION_PRONUNCIATION_CHANGED = "com.minbar.tafhimulquran.PRONUNCIATION_CHANGED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // This receiver can be used to notify activities/fragments to refresh
        // their views when the pronunciation setting changes
        if (ACTION_PRONUNCIATION_CHANGED.equals(intent.getAction())) {
            // Activities/fragments can listen for this broadcast and refresh their views
        }
    }
}