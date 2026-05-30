package com.minbar.tafhimulquran.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class PronunciationUtils {

    private static final String PREF_KEY_ARABIC_PRONUNCIATION = "arabic_pronunciation";
    private static final String DEFAULT_VALUE = "on";

    public static boolean isArabicPronunciationVisible(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = preferences.getString(PREF_KEY_ARABIC_PRONUNCIATION, DEFAULT_VALUE);
        return "on".equals(value);
    }
}