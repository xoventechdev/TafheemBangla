package com.minbar.tafhimulquran.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.minbar.tafhimulquran.Model.NoteModel;

import java.util.ArrayList;
import java.util.List;

public class NoteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "notes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SURAH_ID = "surah_id";
    public static final String COLUMN_VERSE_ID = "verse_id";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SURAH_ID + " TEXT, " +
                    COLUMN_VERSE_ID + " TEXT, " +
                    COLUMN_NOTE + " TEXT, " +
                    COLUMN_TIMESTAMP + " INTEGER DEFAULT 0);";

    public NoteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_TIMESTAMP + " INTEGER DEFAULT 0");
        }
    }

    public void saveOrUpdateNote(String surahId, String verseId, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SURAH_ID, surahId);
        values.put(COLUMN_VERSE_ID, verseId);
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());

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

    public void updateNote(int id, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
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

    public List<NoteModel> getAllNotes(SqlLiteDbHelper dbHelper) {
        List<NoteModel> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID, COLUMN_SURAH_ID, COLUMN_VERSE_ID, COLUMN_NOTE, COLUMN_TIMESTAMP},
                null, null, null, null, COLUMN_TIMESTAMP + " DESC");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String surahId = cursor.getString(1);
            String verseId = cursor.getString(2);
            String note = cursor.getString(3);
            String surahName = dbHelper.getSurahName(Integer.parseInt(surahId));

            notes.add(new NoteModel(id, surahId, verseId, note, surahName));
        }
        cursor.close();
        db.close();
        return notes;
    }

    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
