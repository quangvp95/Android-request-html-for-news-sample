package com.example.demonews.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NewsProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.demonews.data.NewsProvider";

    public static final String KEY_ID = NewsDatabaseHelper.KEY_ID;
    public static final String KEY_TITLE = NewsDatabaseHelper.KEY_TITLE;
    public static final String KEY_AUTHOR = NewsDatabaseHelper.KEY_AUTHOR;
    public static final String KEY_URL = NewsDatabaseHelper.KEY_URL;
    public static final String KEY_TIME = NewsDatabaseHelper.KEY_TIME;
    public static final String KEY_IMG_URL = NewsDatabaseHelper.KEY_IMG_URL;
    public static final String KEY_IMAGE = NewsDatabaseHelper.KEY_IMAGE;

    public static final int NEWS = 100;
    public static final int NEWS_ID = 110;

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/com.example.demonews";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/com.example.demonews";

    private static final String NEWS_BASE_PATH = "news";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + NEWS_BASE_PATH);
    public static final String SELECTION_CLAUSE = KEY_ID +  " = ?";

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, NEWS_BASE_PATH, NEWS);
        sURIMatcher.addURI(AUTHORITY, NEWS_BASE_PATH + "/#", NEWS_ID);
    }

    private NewsDatabaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;

    @Override
    public boolean onCreate() {
        mDBHelper = new NewsDatabaseHelper(getContext());
        mDatabase = mDBHelper.getWritableDatabase();
        return mDatabase != null;
    }

    @Override
    public Cursor query(@NotNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Truy van CSDL
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(NewsDatabaseHelper.DB_TABLE);

        switch (sURIMatcher.match(uri)) {
            case NEWS:
                break;

            case NEWS_ID:
                qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }

        if (TextUtils.isEmpty(sortOrder)) {
            /*
              By default sort on student names
             */
            sortOrder = KEY_TIME;
        }

        Cursor cursor = qb.query(mDBHelper.getReadableDatabase(), projection, selection,
                selectionArgs, null, null, sortOrder);
        /*
          register to watch a content URI for changes
         */
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int bulkInsert(@NotNull Uri uri, @NotNull ContentValues[] values) {
        mDatabase.beginTransaction();
        try {
            for (ContentValues cv : values) {
                long newID = mDatabase.insertWithOnConflict(NewsDatabaseHelper.DB_TABLE, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                if (newID <= 0) {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            }
            mDatabase.setTransactionSuccessful();
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        } finally {
            mDatabase.endTransaction();
        }
        return values.length;
    }

    @Override
    public Uri insert(@NotNull Uri uri, ContentValues values) {
        /*
          Add a new student record
         */
        long rowID = mDatabase.insert(NewsDatabaseHelper.DB_TABLE, "", values);

        /*
          If record is added successfully
         */
        if (rowID > 0) {
            Uri uriAppendedId = ContentUris.withAppendedId(CONTENT_URI, rowID);
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uriAppendedId, null);
            return uriAppendedId;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(@NotNull Uri uri, String selection, String[] selectionArgs) {
        int count;
        switch (sURIMatcher.match(uri)) {
            case NEWS:
                count = mDatabase.delete(NewsDatabaseHelper.DB_TABLE, selection, selectionArgs);
                break;

            case NEWS_ID:
                String id = uri.getPathSegments().get(1);
                count = mDatabase.delete(NewsDatabaseHelper.DB_TABLE, KEY_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int update(@NotNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        switch (sURIMatcher.match(uri)) {
            case NEWS:
                count = mDatabase.update(NewsDatabaseHelper.DB_TABLE, values, selection, selectionArgs);
                break;

            case NEWS_ID:
                count = mDatabase.update(NewsDatabaseHelper.DB_TABLE, values,
                        KEY_ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(@NotNull Uri uri) {
        switch (sURIMatcher.match(uri)) {
            /*
              Get all student records
             */
            case NEWS:
                return CONTENT_TYPE;
            /*
              Get a particular student
             */
            case NEWS_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

}
