package com.minbar.tafhimulquran.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import com.minbar.tafhimulquran.R;

import javax.inject.Inject;

public class FontFamily {


    private final Context context;

    @Inject
    public FontFamily(Context context) {
        this.context = context;
    }



    // Theme
    public static final String THEME_PREFERENCE = "THEME_PREFERENCE";
    public static final String THEME_PREFERENCE_NAME_THEME_WHITE_BLUE = "THEME_WHITE_BLUE";
    public static final String THEME_PREFERENCE_NAME_THEME_DARK_ORANGE = "THEME_DARK_ORANGE";
    public static final String THEME_PREFERENCE_NAME_THEME_DARK_TURQUOISE = "THEME_DARK_TURQUOISE";




    public static int getThemePreferenceId(Context context) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String preferencesString = defaultSharedPreferences.getString("THEME_PREFERENCE", "THEME_WHITE_BLUE");

        switch (preferencesString) {
            case "THEME_DARK_ORANGE":
                return R.style.DarkOrangeTheme;
            case "THEME_DARK_TURQUOISE":
                return R.style.DarkTurquoiseTheme;
            case "THEME_WHITE_BLUE":
            default:
                return R.style.BlueWhiteTheme;
        }
    }






    public static Typeface getBangla(Context context){SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);
        String stringBn = sh.getString("font_bangla", "lipi");
        Typeface fontBn = ResourcesCompat.getFont(context, R.font.solaimanlipi);
        Typeface font2Bn = ResourcesCompat.getFont(context, R.font.charukola);
        Typeface font3Bn = ResourcesCompat.getFont(context, R.font.kalpana);
        if ("lipi".equals(stringBn)) {
            return fontBn;
        } else if ("charu".equals(stringBn)) {
            return font2Bn;
        } else if ("kal".equals(stringBn)) {
            return font3Bn;
        }
        return fontBn;
    }


    public static Typeface getArabic(Context context){
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);
        String string = sh.getString("font_arabi", "me_quran");
        Typeface font = ResourcesCompat.getFont(context, R.font.noorehuda);
        Typeface font2 = ResourcesCompat.getFont(context, R.font.lateef);
        Typeface font3 = ResourcesCompat.getFont(context, R.font.me_quran);
        Typeface font4 = ResourcesCompat.getFont(context, R.font.qalammajeed);
        if ("noorehuda".equals(string)) {
            return font;
        } else if ("noorehira".equals(string)) {
            return font2;
        } else if ("me_quran".equals(string)) {
            return font3;
        } else if ("scheherazade".equals(string)) {
            return font4;
        }
        return font;
    }


}
