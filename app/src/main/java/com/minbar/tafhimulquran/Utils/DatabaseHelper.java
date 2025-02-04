package com.minbar.tafhimulquran.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.minbar.tafhimulquran.Model.HadithChapter;
import com.minbar.tafhimulquran.Model.HadithListModel;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "riyadus-salihin.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public List<HadithChapter> getAllChapters() {
        List<HadithChapter> chapters = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM chapter", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int chapterId = cursor.getInt(cursor.getColumnIndex("chapter_id"));
                int bookId = cursor.getInt(cursor.getColumnIndex("book_id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String number = cursor.getString(cursor.getColumnIndex("number"));
                String hadisRange = cursor.getString(cursor.getColumnIndex("hadis_range"));
                String bookName = cursor.getString(cursor.getColumnIndex("book_name"));

                chapters.add(new HadithChapter(id, chapterId, bookId, title, number, hadisRange, bookName));
            }
            cursor.close();
        }
        return chapters;
    }

    public List<HadithListModel> getHadithsByChapterId(int chapterId) {
        List<HadithListModel> hadiths = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM hadith WHERE chapter_id = ? ORDER BY hadith_id ASC", new String[]{String.valueOf(chapterId)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("hadith_id"));

                // Use getBlob() for columns that might be stored as BLOB
                byte[] arabicBlob = cursor.getBlob(cursor.getColumnIndex("ar"));
                byte[] banglaBlob = cursor.getBlob(cursor.getColumnIndex("bn"));
                String arabicText = arabicBlob != null ? new String(arabicBlob) : "";
                String banglaText = banglaBlob != null ? new String(banglaBlob) : "";

                String grade = cursor.getString(cursor.getColumnIndex("grade"));

                hadiths.add(new HadithListModel(id, chapterId, arabicText, banglaText, grade));
            }
            cursor.close();
        }
        return hadiths;
    }



}