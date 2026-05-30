package com.minbar.tafhimulquran.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.minbar.tafhimulquran.Activity.TafheemActivity;
import com.minbar.tafhimulquran.Model.VerseModel;

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

    public static String toBangla(String s) {
        if (s == null) return "";
        return ENtoBN(s);
    }


    public static String HideNumberBySetting(String s) {
        if (s == null) return "";
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (defaultSharedPreferences.getString("tika", "on").equals("on")) {
            return s.replace("০", "<font color='#E91E63'><sup>০</sup></font>").replace("১", "<font color='#E91E63'><sup>১</sup></font>").replace("২", "<font color='#E91E63'><sup>২</sup></font>").replace("৩", "<font color='#E91E63'><sup>৩</sup></font>").replace("৪", "<font color='#E91E63'><sup>৪</sup></font>").replace("৫", "<font color='#E91E63'><sup>৫</sup></font>").replace("৬", "<font color='#E91E63'><sup>৬</sup></font>").replace("৭", "<font color='#E91E63'><sup>৭</sup></font>").replace("৮", "<font color='#E91E63'><sup>৮</sup></font>").replace("৯", "<font color='#E91E63'><sup>৯</sup></font>");
        }
        return s.replace("০","").replace("১","").replace("২","").replace("৩","").replace("৪","").replace("৫","").replace("৬","").replace("৭","").replace("৮","").replace("৯","");
    }


    public static String HideNumber(String s){
        return s.replace("০","").replace("১","").replace("২","").replace("৩","").replace("৪","").replace("৫","").replace("৬","").replace("৭","").replace("৮","").replace("৯","");
    }

    public static String TagColor(String s){
        return s.replace("০","<font color='#E91E63'><sup>০</sup></font>").replace("১","<font color='#E91E63'><sup>১</sup></font>").replace("২","<font color='#E91E63'><sup>২</sup></font>").replace("৩","<font color='#E91E63'><sup>৩</sup></font>").replace("৪","<font color='#E91E63'><sup>৪</sup></font>").replace("৫","<font color='#E91E63'><sup>৫</sup></font>").replace("৬","<font color='#E91E63'><sup>৬</sup></font>").replace("৭","<font color='#E91E63'><sup>৭</sup></font>").replace("৮","<font color='#E91E63'><sup>৮</sup></font>").replace("৯","<font color='#E91E63'><sup>৯</sup></font>");
    }


    public static String Tajweed(Context context, String s){
        if (context == null) return s;
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context );
        if (defaultSharedPreferences.getString("Tajwid", "on").equals("on")) {
            Spannable kk = QuranArabicUtils.getTajweed(context,s);
            return String.valueOf(kk);
        }
        return s;
    }

    @Deprecated
    public static String Tajweed(String s){
        return Tajweed(context, s);
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

    public static void setHtmlWithLinks(TextView textView, String html, Context context) {
        if (html == null) return;

        Spanned spanned;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT);
        } else {
            spanned = Html.fromHtml(html);
        }

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spanned);
        URLSpan[] urls = spannableStringBuilder.getSpans(0, spanned.length(), URLSpan.class);

        for (URLSpan span : urls) {
            int start = spannableStringBuilder.getSpanStart(span);
            int end = spannableStringBuilder.getSpanEnd(span);
            int flags = spannableStringBuilder.getSpanFlags(span);
            final String url = span.getURL();

            if (url != null && url.contains(":")) {
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        String[] parts = url.split(":");
                        if (parts.length == 2) {
                            try {
                                String sId = parts[0];
                                String explId = parts[1];

                                SqlLiteDbHelper db = SqlLiteDbHelper.getInstance(context);
                                // Resolve expl_id to actual ayat_id (verse id)
                                int ayatId = db.getAyatIdByExplId(Integer.parseInt(sId), Integer.parseInt(explId));
                                if (ayatId != -1) {
                                    VerseModel model = db.getVerseById(Integer.parseInt(sId), ayatId);
                                    if (model != null) {
                                        Intent intent = new Intent(context, TafheemActivity.class);
                                        intent.putExtra("surah_id", sId);
                                        // Pass the resolved verse id
                                        intent.putExtra("verse_id", String.valueOf(ayatId));
                                        intent.putExtra("arabicTxt", model.getArabic());
                                        intent.putExtra("transTxt", model.getTrans());
                                        intent.putExtra("banglaTxt", model.getBangla());
                                        context.startActivity(intent);
                                    }
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                spannableStringBuilder.removeSpan(span);
                spannableStringBuilder.setSpan(clickableSpan, start, end, flags);
            }
        }
        textView.setText(spannableStringBuilder);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
