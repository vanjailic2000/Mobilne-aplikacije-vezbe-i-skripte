package com.example.kolokvijum2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kolokvijum2.model.Post;

import java.util.ArrayList;

// Pomoćna klasa sa CRUD metodama nad tabelom POSTS.
// Analogno metodama insert/query/delete iz vežbi 6 (poglavlje 2 - SQLite).
public class PostDao {

    private final SQLiteHelper helper;

    public PostDao(Context context) {
        helper = new SQLiteHelper(context);
    }

    // Upisuje listu postova u bazu (npr. prvih 10 dobijenih sa servera)
    public void insertAll(ArrayList<Post> posts) {
        SQLiteDatabase db = helper.getWritableDatabase();
        for (Post p : posts) {
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.COLUMN_POST_ID, p.getId());
            values.put(SQLiteHelper.COLUMN_TITLE, p.getTitle());
            values.put(SQLiteHelper.COLUMN_BODY, p.getBody());
            db.insert(SQLiteHelper.TABLE_POSTS, null, values);
        }
        db.close();
    }

    // VAŽNO: "prvi post u tabeli" = najmanji _id (redosled umetanja),
    // NE post čiji je post_id (server ID) jednak 1!
    public Post getFirstPost() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(SQLiteHelper.TABLE_POSTS, null, null, null,
                null, null, SQLiteHelper.COLUMN_ID + " ASC", "1");

        Post post = null;
        if (cursor != null && cursor.moveToFirst()) {
            post = new Post();
            post.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_POST_ID)));
            post.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_TITLE)));
            post.setBody(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_BODY)));
            cursor.close();
        }
        db.close();
        return post;
    }

    // Briše prvi post po redosledu umetanja (najmanji _id)
    public void deleteFirstPost() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM " + SQLiteHelper.TABLE_POSTS
                + " WHERE " + SQLiteHelper.COLUMN_ID
                + " = (SELECT MIN(" + SQLiteHelper.COLUMN_ID + ") FROM " + SQLiteHelper.TABLE_POSTS + ")");
        db.close();
    }

    public int getCount() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + SQLiteHelper.TABLE_POSTS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
}
