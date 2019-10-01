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
    /**
     * Database Version:
     * 4: Thêm cột id là hashcode của url để dùng cho content provider
     */
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "news_db";

    // Table Names
    static final String DB_TABLE = "news_table";

    /**
     * QuangNhe: Lưu ý: các tin được phân biệt dựa trên url nhưng vẫn cần id cho provider query
     * -> dùng hashcode của url làm id, id này không cần lưu, khi nào cần thì lấy url hashcode ra
     */
    static final String KEY_ID = "news_id";
    // column names
    static final String KEY_TITLE = "news_title";
    static final String KEY_AUTHOR = "news_author";
    static final String KEY_URL = "news_url";
    static final String KEY_TIME = "news_time";
    static final String KEY_IMG_URL = "news_img_url";
    static final String KEY_IMAGE = "news_image";

    // Table create statement
    private static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + DB_TABLE + "("+
            KEY_TITLE + " TEXT," +
            KEY_AUTHOR + " TEXT," +
            KEY_URL + " TEXT," +
            KEY_TIME + " INTEGER," +
            KEY_IMG_URL + " TEXT," +
            KEY_IMAGE + " BLOB," +
            KEY_ID + " INTEGER PRIMARY KEY);";

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
}
