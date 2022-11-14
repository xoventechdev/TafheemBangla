package com.minbar.tafhimulquran.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class FontSize {


    public static String getArabic(Context context){
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);
        String string2 = sh.getString("ARABICFONT","26");
        return string2+".0f";
    }

    public static String getBangla(Context context){
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);
        String string2 = sh.getString("ProFONT", "20");
        return string2+".0f";
    }





}
