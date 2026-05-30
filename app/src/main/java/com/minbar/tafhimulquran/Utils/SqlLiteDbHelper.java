package com.minbar.tafhimulquran.Utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.CancellationSignal;

import com.minbar.tafhimulquran.Model.CharacterModel;
import com.minbar.tafhimulquran.Model.CharacterSubModel;
import com.minbar.tafhimulquran.Model.DarsModel;
import com.minbar.tafhimulquran.Model.HadithModel;
import com.minbar.tafhimulquran.Model.MapsModel;
import com.minbar.tafhimulquran.Model.PageVerseModal;
import com.minbar.tafhimulquran.Model.SenModel;
import com.minbar.tafhimulquran.Model.SenSubModel;
import com.minbar.tafhimulquran.Model.SubModel;
import com.minbar.tafhimulquran.Model.SurahModel;
import com.minbar.tafhimulquran.Model.VerseModel;
import com.minbar.tafhimulquran.Model.VumikaModel;
import com.minbar.tafhimulquran.Model.WordModel;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

public class SqlLiteDbHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "tafheemul_quran1.db";
    private static final int DATABASE_VERSION = 12;
    private static SqlLiteDbHelper instance;

    public static synchronized SqlLiteDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SqlLiteDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    public SqlLiteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade(DATABASE_VERSION);
    }

    public ArrayList<SurahModel> getSurah() {
        SQLiteDatabase writableDatabase = getReadableDatabase();
        ArrayList<SurahModel> arrayList = new ArrayList<>();
        Cursor rawQuery = writableDatabase.rawQuery("SELECT * FROM surah_name ORDER BY _id ASC", (String[]) null, (CancellationSignal) null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new SurahModel(rawQuery.getInt(0), rawQuery.getString(1), rawQuery.getString(2), rawQuery.getString(3), rawQuery.getString(4), rawQuery.getString(5)));
            }
            rawQuery.close();
        }
        return arrayList;
    }

    public ArrayList<SubModel> getSub() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<SubModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM subject ORDER BY id ASC", (String[]) null, (CancellationSignal) null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new SubModel(rawQuery.getInt(0), rawQuery.getString(1), rawQuery.getString(2), rawQuery.getString(3)));
            }
            rawQuery.close();
        }
        return arrayList;
    }

    public ArrayList<DarsModel> getDars() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<DarsModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM dars ORDER BY id ASC", (String[]) null, (CancellationSignal) null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new DarsModel(rawQuery.getInt(0), rawQuery.getString(1), rawQuery.getString(2), rawQuery.getString(3), rawQuery.getString(4)));
            }
            rawQuery.close();
        }
        return arrayList;
    }

    public ArrayList<VerseModel> getAyat(int i) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<VerseModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM alquran WHERE sura_id=? ORDER BY id ASC", new String[]{i + ""});
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new VerseModel(rawQuery.getInt(0), rawQuery.getInt(4), rawQuery.getInt(5), rawQuery.getString(6), rawQuery.getString(11), rawQuery.getString(7), rawQuery.getString(10)));
            }
            rawQuery.close();
        }
        return arrayList;
    }

    public ArrayList<WordModel> getWord(String anyValue) {

        String[] strParts = anyValue.split("=");
        int surah = Integer.parseInt(strParts[0]);
        int verseid = Integer.parseInt(strParts[1]);

        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<WordModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM bywords WHERE surah_id="+ surah+" AND verse_id="+verseid+" ORDER BY _id ASC", (String[]) null, (CancellationSignal) null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new WordModel(rawQuery.getInt(1), rawQuery.getInt(2), rawQuery.getInt(3), rawQuery.getString(4), rawQuery.getString(5)));
            }
            rawQuery.close();
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public String getSurahName(int i) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = null;
        String surahName = "";
        try {
            cursor = readableDatabase.rawQuery("SELECT sura_name FROM surah_name WHERE _id=?", new String[]{i + ""});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                surahName = cursor.getString(cursor.getColumnIndex("sura_name"));
            }
            return surahName;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    @SuppressLint("Range")
    public VerseModel getVerseById(int surahId, int verseId) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM alquran WHERE sura_id=? AND ayat_id=?", new String[]{surahId + "", verseId + ""});
        if (rawQuery != null && rawQuery.moveToFirst()) {
            VerseModel model = new VerseModel(rawQuery.getInt(0), rawQuery.getInt(4), rawQuery.getInt(5), rawQuery.getString(6), rawQuery.getString(11), rawQuery.getString(7), rawQuery.getString(10));
            rawQuery.close();
            return model;
        }
        if (rawQuery != null) rawQuery.close();
        return null;
    }

    // New method to retrieve ayat_id (verseId) based on expl_id for a given surah
    @SuppressLint("Range")
    public int getAyatIdByExplId(int surahId, int explId) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = readableDatabase.rawQuery("SELECT ayat_id FROM expls WHERE sura_id=? AND expl_id=?",
                    new String[]{String.valueOf(surahId), String.valueOf(explId)});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex("ayat_id"));
            }
            return -1;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // New method to get VerseModel directly using expl_id
    public VerseModel getVerseByExplId(int surahId, int explId) {
        int ayatId = getAyatIdByExplId(surahId, explId);
        if (ayatId != -1) {
            return getVerseById(surahId, ayatId);
        }
        return null;
    }

    public ArrayList<VerseModel> getSubVerse(int i) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<VerseModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM alquran WHERE sub_cat=? ORDER BY id ASC", new String[]{i + ""});
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new VerseModel(rawQuery.getInt(0), rawQuery.getInt(4), rawQuery.getInt(5), rawQuery.getString(6), rawQuery.getString(11), rawQuery.getString(7), rawQuery.getString(10)));
            }
            rawQuery.close();
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public String getDarsContent(int i) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = null;
        String content = "";
        try {
            cursor = readableDatabase.rawQuery("SELECT content FROM dars WHERE id=?", new String[]{i + ""});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                content = cursor.getString(cursor.getColumnIndex("content"));
            }
            return content;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    @SuppressLint("Range")
    public ArrayList<String> getAboutContent(int i) {
        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery("SELECT vumika FROM vumika_sura WHERE sura_id=?", new String[]{i + ""});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursor.getString(cursor.getColumnIndex("vumika")));
            }
            cursor.close();
        }
        return list;
    }

    @SuppressLint("Range")
    public ArrayList<String> getTafheem(String anyValue) {
        String[] strParts = anyValue.split("=");
        int surahid = Integer.parseInt(strParts[0]);
        int verseid = Integer.parseInt(strParts[1]);

        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery("SELECT expels FROM expls WHERE sura_id="+surahid+" AND ayat_id="+verseid+" ORDER BY id ASC", (String[]) null, (CancellationSignal) null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursor.getString(cursor.getColumnIndex("expels")));
            }
            cursor.close();
        }
        return list;
    }

    @SuppressLint("Range")
    public ArrayList<String> getBayaan(String anyValue) {
        String[] strParts = anyValue.split("=");
        int surahid = Integer.parseInt(strParts[0]);
        int verseid = Integer.parseInt(strParts[1]);

        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery("SELECT content FROM bayaan WHERE surah_id="+surahid+" AND ayah_id="+verseid+" ORDER BY id ASC", (String[]) null, (CancellationSignal) null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursor.getString(cursor.getColumnIndex("content")));
            }
            cursor.close();
        }
        return list;
    }

    public ArrayList<VerseModel> getFav(String s) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<VerseModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM alquran WHERE id IN ("+s+") ", (String[]) null, (CancellationSignal) null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new VerseModel(rawQuery.getInt(0), rawQuery.getInt(4), rawQuery.getInt(5), rawQuery.getString(6), rawQuery.getString(11), rawQuery.getString(7), rawQuery.getString(10)));
            }
            rawQuery.close();
        }
        return arrayList;
    }
    public ArrayList<VumikaModel> getVumika() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<VumikaModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM tafheem_vk ORDER BY id ASC", (String[]) null, (CancellationSignal) null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new VumikaModel(rawQuery.getInt(0), rawQuery.getString(1), rawQuery.getString(2), rawQuery.getString(3)));
            }
            rawQuery.close();
        }
        return arrayList;
    }

    public ArrayList<VerseModel> getAyatSearchBn(String any) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<VerseModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM alquran WHERE bangla_trans LIKE '%"+any+"%'  ORDER BY id ASC" , (String[]) null, (CancellationSignal) null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new VerseModel(rawQuery.getInt(0), rawQuery.getInt(4), rawQuery.getInt(5), rawQuery.getString(6), rawQuery.getString(11), rawQuery.getString(7), rawQuery.getString(10)));
            }
            rawQuery.close();
        }
        return arrayList;
    }

    public ArrayList<VerseModel> getAyatSearchEn(String any) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<VerseModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM alquran WHERE en_sahih LIKE '%"+any+"%'  ORDER BY id ASC" , (String[]) null, (CancellationSignal) null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new VerseModel(rawQuery.getInt(0), rawQuery.getInt(4), rawQuery.getInt(5), rawQuery.getString(6), rawQuery.getString(11), rawQuery.getString(7), rawQuery.getString(10)));
            }
            rawQuery.close();
        }
        return arrayList;
    }

    public ArrayList<SurahModel> getSurahSerach(String any) {
        SQLiteDatabase writableDatabase = getReadableDatabase();
        ArrayList<SurahModel> arrayList = new ArrayList<>();
        Cursor rawQuery = writableDatabase.rawQuery("SELECT * FROM surah_name WHERE sura_name LIKE '%"+any+"%'  ORDER BY _id ASC" , (String[]) null, (CancellationSignal) null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new SurahModel(rawQuery.getInt(0), rawQuery.getString(1), rawQuery.getString(2), rawQuery.getString(3), rawQuery.getString(4), rawQuery.getString(5)));
            }
            rawQuery.close();
        }
        return arrayList;
    }

    public ArrayList<MapsModel> getMaps() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<MapsModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM maps ORDER BY id ASC", (String[]) null, (CancellationSignal) null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new MapsModel(rawQuery.getInt(0), rawQuery.getString(1), rawQuery.getString(2), rawQuery.getString(3)));
            }
            rawQuery.close();
        }
        return arrayList;
    }

    public ArrayList<CharacterModel> getChara() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<CharacterModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM character_list ", (String[]) null, (CancellationSignal) null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new CharacterModel(rawQuery.getInt(0), rawQuery.getString(1)));
            }
            rawQuery.close();
        }
        return arrayList;
    }

    public ArrayList<CharacterSubModel> getCharaSub(int i) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<CharacterSubModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM word_list WHERE character_id=? ", new String[]{i + ""});
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new CharacterSubModel(rawQuery.getInt(0), rawQuery.getInt(1), rawQuery.getString(2)));
            }
            rawQuery.close();
        }
        return arrayList;
    }

    public ArrayList<SenModel> getSen(String anyValue) {
        String[] strParts = anyValue.split("=");

        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<SenModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM sentence_list WHERE character_id="+ strParts[0]+" AND word_id="+strParts[1]+" ", (String[]) null, (CancellationSignal) null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new SenModel(rawQuery.getString(0), rawQuery.getString(1), rawQuery.getString(2), rawQuery.getString(3)));
            }
            rawQuery.close();
        }
        return arrayList;
    }

    public ArrayList<SenSubModel> getSenSub(String anyValue) {
        String[] strParts = anyValue.split("=");

        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<SenSubModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM ovidhan_list WHERE character_id="+ strParts[0]+" AND word_id="+strParts[1]+" AND sentence_id="+strParts[2]+" ", (String[]) null, (CancellationSignal) null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new SenSubModel(rawQuery.getString(3), rawQuery.getString(4), rawQuery.getString(5)));
            }
            rawQuery.close();
        }
        return arrayList;
    }

    public ArrayList<VerseModel> getAyatOvidan(String i) {
        String[] strParts = i.split("=");
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<VerseModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM alquran WHERE sura_id="+Integer.parseInt(strParts[0])+" AND ayat_id IN ("+strParts[1]+") ORDER BY id ASC", (String[]) null, (CancellationSignal) null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new VerseModel(rawQuery.getInt(0), rawQuery.getInt(4), rawQuery.getInt(5), rawQuery.getString(6), rawQuery.getString(11), rawQuery.getString(7), rawQuery.getString(10)));
            }
            rawQuery.close();
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public String[] getVerseList(int aa) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery("SELECT * FROM alquran WHERE sura_id="+aa+" AND NOT id=0 ORDER BY id ASC ", (String[]) null, (CancellationSignal) null);
        String[] array = new String[cursor.getCount()];

        int q = 0;
        while(cursor.moveToNext()){
            String uname = cursor.getString(cursor.getColumnIndex("ayat_id"));
            array[q] = uname;
            q++;
        }
        cursor.close();
        return array;
    }

    @SuppressLint("Range")
    public String getTestBangla(String anyValue) {
        String[] strParts = anyValue.split("=");
        int surahid = Integer.parseInt(strParts[0]);
        int verseid = Integer.parseInt(strParts[1]);
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = null;
        String content = "";
        try {
            cursor = readableDatabase.rawQuery("SELECT * FROM alquran WHERE sura_id="+surahid+" AND ayat_id="+verseid+" ORDER BY id ASC", (String[]) null, (CancellationSignal) null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                content = cursor.getString(cursor.getColumnIndex("bangla_trans"));
            }
            return content;
        } finally {
            if (cursor != null) cursor.close();
        }
    }


    @SuppressLint("Range")
    public String getTestArabic(String anyValue) {
        String[] strParts = anyValue.split("=");
        int surahid = Integer.parseInt(strParts[0]);
        int verseid = Integer.parseInt(strParts[1]);
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = null;
        String content = "";
        try {
            cursor = readableDatabase.rawQuery("SELECT * FROM alquran WHERE sura_id="+surahid+" AND ayat_id="+verseid+" ORDER BY id ASC", (String[]) null, (CancellationSignal) null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                content = cursor.getString(cursor.getColumnIndex("arabic_text"));
            }
            return content;
        } finally {
            if (cursor != null) cursor.close();
        }
    }



    @SuppressLint("Range")
    public String getTestTras(String anyValue) {
        String[] strParts = anyValue.split("=");
        int surahid = Integer.parseInt(strParts[0]);
        int verseid = Integer.parseInt(strParts[1]);
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = null;
        String content = "";
        try {
            cursor = readableDatabase.rawQuery("SELECT * FROM alquran WHERE sura_id="+surahid+" AND ayat_id="+verseid+" ORDER BY id ASC", (String[]) null, (CancellationSignal) null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                content = cursor.getString(cursor.getColumnIndex("trans"));
            }
            return content;
        } finally {
            if (cursor != null) cursor.close();
        }
    }




    @SuppressLint("Range")
    public String[] getPageList() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery("SELECT * FROM pageList ORDER BY id ASC ", (String[]) null, (CancellationSignal) null);
        String[] array = new String[cursor.getCount()];
        int q = 0;
        while(cursor.moveToNext()){
            String uname = cursor.getString(cursor.getColumnIndex("id"));
            array[q] = uname;
            q++;
        }
        cursor.close();
        return array;
    }





    @SuppressLint("Range")
    public ArrayList<PageVerseModal> getPageVerse(int aa) {

        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<PageVerseModal> arrayList = new ArrayList<>();
        Cursor cursor = readableDatabase.rawQuery("SELECT * FROM alquran WHERE page="+aa+" ORDER BY sura_id ASC , id ASC", (String[]) null, (CancellationSignal) null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                arrayList.add(new PageVerseModal(cursor.getInt(4), cursor.getInt(5), cursor.getString(6)));
            }
            cursor.close();
        }
        return arrayList;
    }


    @SuppressLint("Range")
    public int checkPageNumber(int i) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = null;
        int downStatus = 0;
        try {
            cursor = readableDatabase.rawQuery("SELECT page FROM alquran WHERE sura_id="+i+" AND ayat_id=1 ", (String[]) null, (CancellationSignal) null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                downStatus = cursor.getInt(cursor.getColumnIndex("page"));
            }
            return downStatus;
        } finally {
            if (cursor != null) cursor.close();
        }
    }


    @SuppressLint("Range")
    public String goParaNumber(int i) {

        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = null;
        String content = "";
        try {
            cursor = readableDatabase.rawQuery("SELECT page FROM alquran WHERE joza=? LIMIT 1", new String[]{i + ""});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                content = cursor.getString(cursor.getColumnIndex("page"));
            }
            return content;
        } finally {
            if (cursor != null) cursor.close();
        }

    }




    public ArrayList<VerseModel> getDailyQuran(int i) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<VerseModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM alquran WHERE id=? ORDER BY id ASC", new String[]{i + ""});
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new VerseModel(rawQuery.getInt(0), rawQuery.getInt(4), rawQuery.getInt(5), rawQuery.getString(6), rawQuery.getString(11), rawQuery.getString(7), rawQuery.getString(10)));
            }
            rawQuery.close();
        }
        return arrayList;
    }



    public ArrayList<HadithModel> getDailyHadith(int i) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<HadithModel> arrayList = new ArrayList<>();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM hadithmain WHERE HadithNo=? ORDER BY HadithNo ASC", new String[]{i + ""});
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new HadithModel(rawQuery.getInt(5), rawQuery.getString(6), rawQuery.getString(7), rawQuery.getString(8), rawQuery.getString(9), rawQuery.getInt(11)));
            }
            rawQuery.close();
        }
        return arrayList;
    }
}
