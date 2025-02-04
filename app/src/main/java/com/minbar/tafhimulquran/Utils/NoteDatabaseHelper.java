package com.minbar.tafhimulquran.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "notes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SURAH_ID = "surah_id";
    public static final String COLUMN_VERSE_ID = "verse_id";
    public static final String COLUMN_NOTE = "note";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SURAH_ID + " TEXT, " +
                    COLUMN_VERSE_ID + " TEXT, " +
                    COLUMN_NOTE + " TEXT);";

    public NoteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void saveOrUpdateNote(String surahId, String verseId, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SURAH_ID, surahId);
        values.put(COLUMN_VERSE_ID, verseId);
        values.put(COLUMN_NOTE, note);

        // Check if note already exists
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID},
                COLUMN_SURAH_ID + "=? AND " + COLUMN_VERSE_ID + "=?",
                new String[]{surahId, verseId}, null, null, null);

        if (cursor.moveToFirst()) {
            // Update existing note
            db.update(TABLE_NAME, values, COLUMN_SURAH_ID + "=? AND " + COLUMN_VERSE_ID + "=?",
                    new String[]{surahId, verseId});
        } else {
            // Insert new note
            db.insert(TABLE_NAME, null, values);
        }
        cursor.close();
        db.close();
    }

    public String getNote(String surahId, String verseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_NOTE},
                COLUMN_SURAH_ID + "=? AND " + COLUMN_VERSE_ID + "=?",
                new String[]{surahId, verseId}, null, null, null);

        if (cursor.moveToFirst()) {
            String note = cursor.getString(0);
            cursor.close();
            db.close();
            return note;
        }
        cursor.close();
        db.close();
        return "";
    }
}
