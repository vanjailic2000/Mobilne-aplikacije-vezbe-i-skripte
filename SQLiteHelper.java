package com.example.kolokvijum2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Identično obrascu iz vežbi 6 (SQLiteHelper za PRODUCTS), prilagođeno za POSTS.
public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_POSTS = "POSTS";
    public static final String COLUMN_ID = "_id";          // autoincrement primarni ključ (redosled umetanja!)
    public static final String COLUMN_POST_ID = "post_id"; // id koji dolazi sa servera
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_BODY = "body";

    private static final String DATABASE_NAME = "posts.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DB_CREATE = "create table "
            + TABLE_POSTS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_POST_ID + " integer, "
            + COLUMN_TITLE + " text, "
            + COLUMN_BODY + " text"
            + ");";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        onCreate(db);
    }
}
