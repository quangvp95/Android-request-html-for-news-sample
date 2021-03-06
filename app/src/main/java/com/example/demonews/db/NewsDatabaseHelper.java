package com.example.demonews.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NewsDatabaseHelper extends SQLiteOpenHelper {
    /**
     * Database Version:
     * 4: Thêm cột id là hashcode của url để dùng cho content provider
     * 5: Bỏ cột image, image giờ lưu ra bộ nhớ ngoài
     */
    private static final int DATABASE_VERSION = 5;

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

    // Table create statement
    private static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + DB_TABLE + "("+
            KEY_TITLE + " TEXT," +
            KEY_AUTHOR + " TEXT," +
            KEY_URL + " TEXT," +
            KEY_TIME + " INTEGER," +
            KEY_IMG_URL + " TEXT," +
            KEY_ID + " INTEGER PRIMARY KEY);";

    NewsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_IMAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // QuangNHe: Tạm thời không làm gì
    }
}
