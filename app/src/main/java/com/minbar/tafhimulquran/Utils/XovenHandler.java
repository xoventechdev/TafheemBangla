package com.minbar.tafhimulquran.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.CancellationSignal;

import com.minbar.tafhimulquran.Model.VerseModel;

import java.util.ArrayList;

public class XovenHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "xoventechdb";
    private static final int DB_VERSION = 1;



    private static final String TABLE_FAV = "favTable";
    private static final String ID_COL = "id";
    private static final String AYAT_COL = "verseID";


    private static final String TABLE_Note = "noteTable";
    private static final String ID_NOTE = "id";
    private static final String AYAT_NOTE = "verseID";
    private static final String CONTENT_NOTE = "content";
    private static final String DATE_NOTE = "lastDate";






    public XovenHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryFav = "CREATE TABLE " + TABLE_FAV + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AYAT_COL + " INTEGER)";
        db.execSQL(queryFav);


        String queryNote = "CREATE TABLE " + TABLE_Note + " ("
                + ID_NOTE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AYAT_NOTE + " INTEGER,"
                + CONTENT_NOTE + " TEXT,"
                + DATE_NOTE + " TEXT)";
        db.execSQL(queryNote);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Note);
        onCreate(db);
    }




    public void addFav(int courseDuration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AYAT_COL, courseDuration);
        db.insert(TABLE_FAV, null, values);
        db.close();
    }




    @SuppressLint("Range")
    public ArrayList  getAllFav() {
        ArrayList list = new ArrayList<String>();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery("SELECT * FROM "+TABLE_FAV+" ORDER BY id ASC", (String[]) null, (CancellationSignal) null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursor.getString(cursor.getColumnIndex("verseID")));
            }
            cursor.close();
            readableDatabase.close();
        }
        return list;
    }




    @SuppressLint("Range")
    public boolean checkFav(int i) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = null;
        boolean favStatus = false;
        try {
            cursor = readableDatabase.rawQuery("SELECT * FROM "+TABLE_FAV+" WHERE verseID=?", new String[]{i + ""});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                favStatus = true;
            }
            return favStatus;
        } finally {
            cursor.close();
        }
    }

    public void deleteFav(int i) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAV, "verseID="+i+"", null);
        db.close();
    }












    public void addNote(int courseName, String courseDuration, String courseDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AYAT_NOTE, courseName);
        values.put(CONTENT_NOTE, courseDuration);
        values.put(DATE_NOTE, courseDescription);
        db.insert(TABLE_Note, null, values);
        db.close();
    }

    /*
    public ArrayList<NoteModal> readCourses() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_Note, null);
        ArrayList<NoteModal> courseModalArrayList = new ArrayList<>();
        if (cursorCourses.moveToFirst()) {
            do {
                courseModalArrayList.add(new NoteModal(cursorCourses.getInt(0),
                        cursorCourses.getString(1),
                        cursorCourses.getString(2),
                        cursorCourses.getString(3)));
            } while (cursorCourses.moveToNext());

        }
        cursorCourses.close();
        return courseModalArrayList;
    }



     */












}
