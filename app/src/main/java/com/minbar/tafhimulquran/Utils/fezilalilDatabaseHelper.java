package com.minbar.tafhimulquran.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.CancellationSignal;
import android.widget.Toast;

import java.util.ArrayList;

public class fezilalilDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fezilalilquran.db";
    private static final int DATABASE_VERSION = 1;



    public fezilalilDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @SuppressLint("Range")
    public ArrayList getFezilalil(String anyValue) {
        String[] strParts = anyValue.split("=");
        String surahid = strParts[0];
        String verseid = strParts[1];

        ArrayList list = new ArrayList<String>();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String query = "SELECT tafsir_text FROM expl WHERE sura_id=? AND verse_id=?";
        Cursor cursor = readableDatabase.rawQuery(query, new String[]{surahid, verseid});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursor.getString(cursor.getColumnIndex("tafsir_text")));
            }
            cursor.close();
            readableDatabase.close();
        }
        return list;
    }



}
