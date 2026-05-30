package com.minbar.tafhimulquran.Activity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.minbar.tafhimulquran.Adapter.VumikaAdapter;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.FontFamily;
import com.minbar.tafhimulquran.Utils.FontSize;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;

public class TafheemIntroduceActivity extends AppCompatActivity {

    VumikaAdapter adapter;
    SqlLiteDbHelper dbHelper;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tafheem_introduce);

        // Setup Toolbar
        MaterialToolbar toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("তাফহীম পরিচিতি");
        }

        // Initialize Views
        TextView aboutF = findViewById(R.id.aboutF);
        TextView vumila = findViewById(R.id.vumila);

        // Apply Custom Fonts and Sizes
        vumila.setTypeface(FontFamily.getBangla(this));
        vumila.setTextSize(2, Float.parseFloat(FontSize.getBangla(this)));
        aboutF.setTypeface(FontFamily.getBangla(this));
        aboutF.setTextSize(2, Float.parseFloat(FontSize.getBangla(this)));

        // Set Introduction Text
        aboutF.setText("তাফহিমুল কুরআন হলো মুসলিম দার্শনিক ও ইসলাম ধর্মের পণ্ডিত সাইয়েদ আবুল আলা মওদুদী (রহ)'র ভাষ্য অনুযায়ী ৬ খণ্ডে কুরআনের ব্যাখ্যামূলক অনুবাদ (তাফসীর)। মাওলানা মওদুদী এ তাফসীর লেখার জন্যে ৩০ বছর সময় ব্যয় করেন। তিনি ১৯৪২ সালে উর্দু ভাষায় শুরু করেন এবং ১৯৭২ সালে এটি সম্পন্ন করেন।\n\nবাংলা ভাষায় তাফহিমুল কুরআন প্রথম অনূদিত হয় খায়রুন প্রকাশনী থেকে মাওলানা মুহাম্মাদ আবদুর রহীম (রহ) দ্বারা। পরবর্তীতে মাওলানা আব্দুল মান্নান তালিব কর্তৃক অনূদিত তাফসীর প্রকাশ পায় আধুনিক প্রকাশনী থেকে। আমরা আমাদের অ্যাপটি আধুনিক প্রকাশনী থেকে প্রকাশিত তাফসীর-এর আলোকে তৈরি করেছি। সাথে ইংরেজি অনুবাদ হিসাবে Saheeh International এড করেছি।\n\nনিচে লেখক ও অনুবাদকের জীবনী তুলে ধরা হলঃ");

        // Set Click Listeners for Author Cards
        findViewById(R.id.card_maududi).setOnClickListener(v -> aboutShow(1));
        findViewById(R.id.card_talib).setOnClickListener(v -> aboutShow(2));
        findViewById(R.id.card_saheeh).setOnClickListener(v -> aboutShow(3));

        // Setup RecyclerView for Vumika
        dbHelper = new SqlLiteDbHelper(this);
        recyclerView = findViewById(R.id.recycler_others);
        recyclerView.setHasFixedSize(true);
        // Disable nested scrolling to allow NestedScrollView to handle the scroll perfectly
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new VumikaAdapter(this, dbHelper.getVumika());
        recyclerView.setAdapter(adapter);
    }

    public void aboutShow(int s) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.about_surah_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;

        TextView title_about = dialog.findViewById(R.id.title_about);
        TextView aboutContent = dialog.findViewById(R.id.aboutContent);
        ImageView copyLayout = dialog.findViewById(R.id.copyLayout);
        ImageView clear_about = dialog.findViewById(R.id.clear_about);

        if (copyLayout != null) {
            copyLayout.setVisibility(View.GONE);
        }

        clear_about.setOnClickListener(v -> dialog.dismiss());

        if (s == 1) {
            title_about.setText("সাইয়েদ আবুল আ'লা মওদুদী");
            aboutContent.setText(Html.fromHtml(a1));
        } else if (s == 2) {
            title_about.setText("আব্দুল মান্নান তালিব");
            aboutContent.setText(Html.fromHtml(a2));
        } else if (s == 3) {
            title_about.setText("Sahih International");
            aboutContent.setText(Html.fromHtml(a3));
        }

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // --- HTML Strings ---
    String a3 = "<p>The&nbsp;<strong>Saheeh International translation</strong>&nbsp;is an&nbsp;English Language translation&nbsp;of the&nbsp;Quran&nbsp;that has been used by Islam's most conservative adherents. Published by the Publishing House (dar),&nbsp;<em>dar&nbsp;Abul Qasim</em>, Saudi Arabia, it is one of the World's most popular Quran translations.</p>\n" +
            "<p>Translated by three American women, Umm Muhammad (Emily Assami), Mary Kennedy, and Amatullah Bantley, Notable conventions include rendering the&nbsp;God in Islam&nbsp;as&nbsp;<em>Allah</em>&nbsp;as they believe it is not okay to use the English word.</p>\n" +
            "<p>The translation has been described as biased towards \"Sunni orthodoxy\", which according to authors, requires words to be inserted in square parentheses.</p>\n" +
            "<p><em>Ṣaḥīḥ</em>&nbsp;(<span title=\"Arabic-language text\"><span lang=\"ar\">صحيح</span></span>) may be translated as \"authentic\" or \"sound.\"</p>\n" +
            "<h2><span id=\"Translators\" class=\"mw-headline\">Translators</span></h2>\n" +
            "<p>Emily Assami was born in&nbsp;California&nbsp;into an&nbsp;atheist&nbsp;family. She was married to an Arab husband. She studied Arabic at&nbsp;Damascus University. She was a former atheist who converted to Islam. She is known as&nbsp;<em>Umm Muhammad</em>&nbsp;or&nbsp;<em>Aminah</em>.</p>\n" +
            "<p>Mary Kennedy was born in&nbsp;Orlando. She was a former Christian who converted to Islam.</p>\n" +
            "<p>Amatullah Bantley was a former Catholic Christian. She was introduced to Islam through international Muslim students. She&nbsp;converted to Islam in 1986 and eventually moved to Saudi Arabia.</p>";

    String a2 = "<p>কবি, সাহিত্যিক, সাংবাদিক, সম্পাদক, সংগঠক, গবেষক, ঐতিহাসিক, প্রাবন্ধিক, শিশু সাহিত্যিক, বহুভাষাবিদ, বহু গ্রন্থকার দ্বীনের একনিষ্ঠ সেবক ও প্রচারক হযরত মাওলানা আবদুল মান্নান তালিব, যাকে আমরা অনেকেই অতি সহজে পরম শ্রদ্ধায় ‘তালিব ভাই’ বলে ডাকতাম, তিনি ছিলেন কুরআন, হাদীস, তাফসির, কাব্য সাহিত্য, সংস্কৃতি, রাজনীতি, অর্থনীতি ও সমাজনীতি সম্পর্কে বহু জ্ঞানের অধিকারী, কিন্তু তিনি ছিলেন প্রশান্ত মহাসাগরের মত শান্ত, ধীরস্থির, ধৈর্যশীল, স্বল্পভাষী মহান এক ব্যক্তি।</p>\n" +
            "<p>মাওলানা আবদুল মান্নান তালিব ১৯৩৬ সালের ১৫ মার্চ পশ্চিমবঙ্গের দক্ষিণ চব্বিশ পরগনা জেলার মগরহাট থানার অর্জুনপুর গ্রামে এক বর্ধিষ্ণু চাষী পরিবারে জন্মগ্রহণ করেন। তার পিতার নাম তালেব আলী মোল্লা এবং মাতার নাম মেহেরুন্নেসা। মান্নান তালিব সাত ভাই ও পাঁচ বোনের মধ্যে ছিলেন পঞ্চম।</p>\n" +
            "<p>তিনি মুরাদাবাদ গিয়ে মাদ্রাসায়ে এমদাদিয়াতে ভর্তি হন এবং সেখানে দুই বছর লেখাপড়া করেন। সেই সময় জালালাবাদ মাদ্রাসার একজন উস্তাদ করাচির সিন্ধু প্রদেশের টুন্ত অলাইয়ার শহরে দারুল উলুম মাদ্রাসায় যান। মান্নান তালিবও তাদের সঙ্গে গিয়ে সেখানে ভর্তি হন ১৯৫৩ সালে।</p>\n" +
            "<p>অতঃপর ঢাকায় এসে সাপ্তাহিক জাহানে নও পত্রিকার প্রথমে সাংবাদিক পরে সম্পাদনার দায়িত্বভার গ্রহণ করেন। পরবর্তীকালে মাসিক পৃথিবী ও মাসিক কলম একযোগে পরিচালনার দায়িত্বপ্রাপ্ত হন। তিনি বাংলা এবং উর্দুতে এমন দক্ষতা অর্জন করেছিলেন যে, ভাষান্তরে তার সমকক্ষ মানুষ খুব কমই নজরে পড়ে।</p>\n" +
            "<p>আবদুল মান্নান তালিবের প্রথম প্রকাশিত বই ‘অবরুদ্ধ জীবনের কথা; (১৯৬২) ইসলামিক ফাউন্ডেশন থেকে প্রকাশিত বাংলাদেশে ইসলাম (১৯৭৯) তার অসাধারণ গবেষণা গ্রন্থ। তিনি সারাজীবন যে মহৎ কাজ করেছেন তা মহান আল্লাহর সন্তুষ্টির জন্য করেছেন। তিনি ১৯৯৪ সালে বাংলাদেশ ইসলামিক স্কুল, দুবাই সাহিত্য পুরস্কার লাভ করেন। ২০০০ সালে কিশোর কণ্ঠ সাহিত্য পুরস্কার এবং ২০১০ সালে বাংলা সাহিত্য পরিষদ পুরস্কার লাভ করেন। তিনি ছিলেন উপমহাদেশের মুসলিম জাগরণের অনন্য সিপাহাসালার।</p>";

    String a1 = "<p><strong>আবুল আ'লা মওদুদী</strong>&nbsp;(২৫ সেপ্টেম্বর ১৯০৩&nbsp;– ২২ সেপ্টেম্বের ১৯৭৯), যিনি&nbsp;<strong>মাওলানা মওদুদী</strong>, বা&nbsp;<strong>শাইখ সাইয়েদ আবুল আ'লা মওদুদী</strong>&nbsp;নামেও পরিচিত, ছিলেন একজন&nbsp;মুসলিম গবেষক,&nbsp;আইনবিদ,&nbsp;ইতিহাসবিদ,&nbsp;সাংবাদিক, রাজনৈতিক নেতা এবং বিংশ শতাব্দীর অন্যতম প্রখ্যাত&nbsp;ইসলামী চিন্তাবিদ&nbsp;ও&nbsp;দার্শনিক।</p>\n" +
            "<p>তিনি নিজ দেশ পাকিস্তানের একজন রাজনৈতিক ব্যক্তিত্বও ছিলেন। তিনি এশিয়ার তৎকালীন বৃহত্তম ইসলামী সংগঠন&nbsp;জামায়াতে ইসলামী&nbsp;নামক একটি ইসলামী রাজনৈতিক দলের প্রতিষ্ঠাতা। তিনি ছিলেন&nbsp;২০ শতাব্দীর আলোচিত মুসলিম আলেমদের&nbsp;মধ্যে একজন। ইসলাম ধর্মে অবদানের স্বীকৃত স্বরূপ তাকে ১৯৭৯ সালে&nbsp;কিং ফয়সাল ফাউন্ডেশন&nbsp;কর্তৃক&nbsp;বাদশাহ ফয়সাল পুরস্কারটি&nbsp;প্রদান করা হয়।</p>\n" +
            "<h2><span class=\"mw-headline\">প্রারম্ভিক জীবন</span></h2>\n" +
            "<p><strong>মাওলানা মওদুদী</strong>&nbsp;ভারতের&nbsp;হায়দারাবাদের&nbsp;(বর্তমান&nbsp;মহারাষ্ট্র)&nbsp;আওরঙ্গবাদ&nbsp;শহরে জন্মগ্রহণ করেন। পিতার নাম সাইয়েদ আহমদ হাসান, তিনি পেশায় ছিলেন আইনজীবী। মাওলানা মওদুদী বংশীয় দিক দিয়ে সাইয়িদুনা হুসাইন শহিদের ৩৬তম উত্তর পুরুষ।</p>\n" +
            "<h2><span class=\"mw-headline\">সাংবাদিকতা ও লেখাজোকা</span></h2>\n" +
            "<p>১৯১৮ সালে মাত্র পনেরো বছর বয়সে কর্মজীবনে প্রবেশ করেন। ১৯২০ সালে ১৭ বছর বয়সে ‘সাপ্তাহিক তাজ’ পত্রিকার সম্পাদক নিযুক্ত হন। ১৯৩২ সালে তিনি&nbsp;<strong>তার্জমানুল কুরআন</strong>&nbsp;পত্রিকায় প্রকাশ শুরু করেন। মওলানার জীবনের সব থেকে গুরুত্বপূর্ণ ও তাৎপর্যপূর্ণ কাজ হচ্ছে তাফসীরে&nbsp;তাফহীমুল কুরআন, উর্দু এই তাফসীরটি লিখতে তার জীবনের বড় অংশ ব্যয় করেন।</p>\n" +
            "<h2><span class=\"mw-headline\">জীবনকাল</span></h2>\n" +
            "<ul>\n" +
            "<li>১৯০৩- জন্ম গ্রহণ করেন। জন্মস্থানঃ আওরঙ্গাবাদ।</li>\n" +
            "<li>১৯৪১- লাহোরে 'জামায়াতে ইসলামী হিন্দ' নামে একটি ইসলামী রাজনৈতিক দল প্রতিষ্ঠা করেন।</li>\n" +
            "<li>১৯৪২ - তাফহীমুল কুরআন নামক তাফসির গ্রন্থ প্রনয়ন শুরু করেন।</li>\n" +
            "<li>১৯৭২- তাফহীমুল কুরআন নামক তাফসির গ্রন্থটির রচনা সম্পন্ন করেন।</li>\n" +
            "<li>১৯৭৯- \"ইসলাম পরিসেবায়\" এই বিভাগে মুসলিম বিশ্বের নোবেলখ্যাত বাদশাহ ফয়সাল আন্তর্জাতিক পুরস্কার লাভ করেন।</li>\n" +
            "<li>১৯৭৯- যুক্তরাষ্ট্রে তার মৃত্যু হয় এবং লাহোরের ইছরায় সমাধিস্থ করা হয়।</li>\n" +
            "</ul>";
}