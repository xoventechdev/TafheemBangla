package com.minbar.tafhimulquran.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Spannable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import com.minbar.tafhimulquran.Adapter.VerseAdapter;

import org.w3c.dom.Text;

public class Config {

    private static Context context;
    public Config(Context context) {
        this.context = context;
    }

    public static boolean isConnected(Context context) {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }


    public static String ENtoBN(String s){
        return s.replace("0","০").replace("1","১").replace("2","২").replace("3","৩").replace("4","৪").replace("5","৫").replace("6","৬").replace("7","৭").replace("8","৮").replace("9","৯");
    }

    public static String BntoEN(String s){
        return s.replace("০","0").replace("১","1").replace("২","2").replace("৩","3").replace("৪","4").replace("৫","5").replace("৬","6").replace("৭","7").replace("৮","8").replace("৯","9");
    }


    public static String HideNumber(String s){
//        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context );
//        if (defaultSharedPreferences.getString("tika", "on").equals("on")) {
//
//            return s.replace("০","<font color='#E91E63'><sup>০</sup></font>").replace("১","<font color='#E91E63'><sup>১</sup></font>").replace("২","<font color='#E91E63'><sup>২</sup></font>").replace("৩","<font color='#E91E63'><sup>৩</sup></font>").replace("৪","<font color='#E91E63'><sup>৪</sup></font>").replace("৫","<font color='#E91E63'><sup>৫</sup></font>").replace("৬","<font color='#E91E63'><sup>৬</sup></font>").replace("৭","<font color='#E91E63'><sup>৭</sup></font>").replace("৮","<font color='#E91E63'><sup>৮</sup></font>").replace("৯","<font color='#E91E63'><sup>৯</sup></font>");
//        }
        return s.replace("০","").replace("১","").replace("২","").replace("৩","").replace("৪","").replace("৫","").replace("৬","").replace("৭","").replace("৮","").replace("৯","");
    }


    public static String Tajweed(String s){
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context );
        if (defaultSharedPreferences.getString("Tajwid", "on").equals("on")) {
            Spannable kk = QuranArabicUtils.getTajweed(context,s);
            return String.valueOf(kk);
        }
        return s;
    }


    public static void BanglaOnubadh(LinearLayout linearLayout, Context context){
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context );
        if (defaultSharedPreferences.getString("banglaOnubadh", "on").equals("off")) {
            linearLayout.setVisibility(View.GONE);
        }else {
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    public static String getStringInBangla(String string) {
        Character[] bangla_number = {'০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'};
        Character[] eng_number = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        StringBuilder values = new StringBuilder();
        char[] character = string.toCharArray();
        for (char value : character) {
            char c = ' ';
            for (int j = 0; j < eng_number.length; j++) {
                if (value == eng_number[j]) {
                    c = bangla_number[j];
                    break;
                } else {
                    c = value;
                }
            }
            values.append(c);
        }
        return values.toString();
    }
    
    
    
    


}
