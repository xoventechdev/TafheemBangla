package com.minbar.tafhimulquran.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class tafheemEnglishDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tafheem_english.db";
    private static final int DATABASE_VERSION = 1;


    public tafheemEnglishDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @SuppressLint("Range")
    public ArrayList getTafheemEnglish(String anyValue) {
        String[] strParts = anyValue.split("=");
        String surahid = strParts[0];
        String verseid = strParts[1];

        ArrayList list = new ArrayList<String>();
        try {
            SQLiteDatabase readableDatabase = getReadableDatabase();
            String query = "SELECT english_expl FROM english_table WHERE surah_id=? AND ayah_id=?";
            Cursor cursor = readableDatabase.rawQuery(query, new String[]{surahid, verseid});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(cursor.getColumnIndex("english_expl")));
                }
                cursor.close();
            }
            readableDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


}