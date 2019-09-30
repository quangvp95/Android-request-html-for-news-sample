package com.example.demonews.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import com.example.demonews.entity.News;
import com.example.demonews.util.Util;

import java.util.ArrayList;

public class NewsDatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "news_db";

    // Table Names
    private static final String DB_TABLE = "news_table";

    // column names
    private static final String KEY_TITLE = "news_title";
    private static final String KEY_AUTHOR = "news_author";
    private static final String KEY_URL = "news_url";
    private static final String KEY_TIME = "news_time";
    private static final String KEY_IMG_URL = "news_img_url";
    private static final String KEY_IMAGE = "news_image";

    // Table create statement
    private static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + DB_TABLE + "("+
            KEY_TITLE + " TEXT," +
            KEY_AUTHOR + " TEXT," +
            KEY_URL + " TEXT PRIMARY KEY," +
            KEY_TIME + " INTEGER," +
            KEY_IMG_URL + " TEXT," +
            KEY_IMAGE + " BLOB);";
    private static final String QUERY_ALL_NEWS = "SELECT  * FROM " + DB_TABLE + " ORDER BY " +KEY_TIME + " DESC LIMIT 30";

    public NewsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_IMAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

        // create new table
        onCreate(db);
    }

    public void addNews(News news) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, news.getTitle());
        values.put(KEY_AUTHOR, news.getAuthor());
        values.put(KEY_URL, news.getUrl());
        values.put(KEY_TIME, news.getTime());
        values.put(KEY_IMG_URL, news.getImgUrl());
        if (news.getImage() != null)
            values.put(KEY_IMAGE, Util.getBytes(news.getImage()));

        db.insertWithOnConflict(DB_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void updateNews(News news) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, news.getTitle());
        values.put(KEY_AUTHOR, news.getAuthor());
        values.put(KEY_IMG_URL, news.getImgUrl());
        if (news.getImage() != null)
            values.put(KEY_IMAGE, Util.getBytes(news.getImage()));

        db.update(DB_TABLE, values, KEY_URL + " = ?", new String[] {news.getUrl()});
        db.close();
    }

    public ArrayList<News> getNews() {
        ArrayList<News> wordList;
        wordList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(QUERY_ALL_NEWS, null);
        if (cursor.moveToFirst()) {
            do {
                String mTitle = cursor.getString(0);
                String mAuthor = cursor.getString(1);
                String mUrl = cursor.getString(2);
                long mTime = cursor.getLong(3);
                String mImgUrl = cursor.getString(4);
                Bitmap bitmap = Util.getImage(cursor.getBlob(5));

                News news = new News(mTitle, mAuthor, mUrl, mTime, mImgUrl, bitmap);
                wordList.add(news);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return wordList;
    }
}
