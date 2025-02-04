package com.minbar.tafhimulquran.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.minbar.tafhimulquran.R;

public class DatabaseInitializer {
    private static final String TAG = "DatabaseInitializer";

    public static void initializeDatabase(Context context, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Resources resources = context.getResources();

        int[] txtFiles = {
                R.raw.bookcontent,
                R.raw.bookname,
                R.raw.booksection,
                R.raw.booktype,
                R.raw.bookwriter,
                R.raw.hadithbook,
                R.raw.hadithchapter,
                R.raw.hadithexplanation,
                R.raw.hadithmain,
                R.raw.hadithpublisher,
                R.raw.hadithsection,
                R.raw.hadithstatus,
                R.raw.rabihadith
        };

        for (int file : txtFiles) {
            processTxtFile(resources, file, db);
        }
        db.close();
    }

    private static void processTxtFile(Resources resources, int fileResId, SQLiteDatabase db) {
        try {
            InputStream inputStream = resources.openRawResource(fileResId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            db.beginTransaction();
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) continue;

                try {
                    db.execSQL(line);
                } catch (Exception e) {
                    Log.e(TAG, "Error executing SQL: " + line, e);
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            reader.close();
        } catch (Exception e) {
            Log.e(TAG, "Error processing file: " + fileResId, e);
        }
    }
}