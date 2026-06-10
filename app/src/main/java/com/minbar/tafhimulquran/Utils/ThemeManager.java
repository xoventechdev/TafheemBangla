package com.minbar.tafhimulquran.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.minbar.tafhimulquran.R;

public class ThemeManager {
    private static final String THEME_PREFERENCE_KEY = "app_theme";
    private static final String DEFAULT_THEME = "light";

    // Theme constants
    public static final String THEME_LIGHT = "light";
    public static final String THEME_PURPLE = "purple";
    public static final String THEME_RED = "red";
    public static final String THEME_PINK = "pink";
    public static final String THEME_DARK_PINK = "darkpink";
    public static final String THEME_BLUE = "blue";
    public static final String THEME_SKYBLUE = "skyblue";
    public static final String THEME_GREEN = "green";
    public static final String THEME_GREY = "grey";
    public static final String THEME_BROWN = "brown";
    public static final String THEME_DARK_ORANGE = "dark_orange";
    public static final String THEME_DARK_TURQUOISE = "dark_turquoise";

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void applyTheme(Activity activity) {
        String theme = getTheme(activity);
        int themeResId = getThemeResId(theme);

        if (themeResId != 0) {
            activity.setTheme(themeResId);
        }
    }

    public static int getThemeResId(String theme) {
        switch (theme) {
            case THEME_LIGHT:
                return R.style.AppTheme_light;
            case THEME_PURPLE:
                return R.style.AppTheme_purple;
            case THEME_RED:
                return R.style.AppTheme_red;
            case THEME_PINK:
                return R.style.AppTheme_pink;
            case THEME_DARK_PINK:
                return R.style.AppTheme_darkpink;
            case THEME_BLUE:
                return R.style.AppTheme_blue;
            case THEME_SKYBLUE:
                return R.style.AppTheme_skyblue;
            case THEME_GREEN:
                return R.style.AppTheme_green;
            case THEME_GREY:
                return R.style.AppTheme_grey;
            case THEME_BROWN:
                return R.style.AppTheme_brown;
            case THEME_DARK_ORANGE:
                return R.style.DarkOrangeTheme;
            case THEME_DARK_TURQUOISE:
                return R.style.DarkTurquoiseTheme;
            default:
                return R.style.AppTheme_light;
        }
    }

    public static String getTheme(Context context) {
        return getSharedPreferences(context).getString(THEME_PREFERENCE_KEY, DEFAULT_THEME);
    }

    public static void setTheme(Context context, String theme) {
        getSharedPreferences(context).edit().putString(THEME_PREFERENCE_KEY, theme).apply();
    }

    public static boolean isDarkTheme(String theme) {
        return theme.equals(THEME_DARK_ORANGE) || theme.equals(THEME_DARK_TURQUOISE);
    }

    public static boolean isDarkTheme(Context context) {
        return isDarkTheme(getTheme(context));
    }

    public static void recreateActivity(Activity activity) {
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        activity.recreate();
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
