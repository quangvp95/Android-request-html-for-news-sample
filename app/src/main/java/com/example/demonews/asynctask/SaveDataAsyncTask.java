package com.example.demonews.asynctask;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.example.demonews.entity.News;
import com.example.demonews.util.Util;

import java.util.ArrayList;

import static com.example.demonews.db.NewsProvider.*;

public class SaveDataAsyncTask extends AsyncTask<Void, Void, Void> {

    public enum TYPE {
        INSERT, UPDATE_INFO, DELETE
    }

    private ContentResolver mResolver;
    private TYPE mType;
    private ArrayList<News> mList;

    public SaveDataAsyncTask(Context mContext, TYPE mType, ArrayList<News> mList) {
        mResolver = mContext.getContentResolver();
        this.mType = mType;
        this.mList = mList;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        switch (mType) {
            case INSERT:
                for (News news : mList) {
                    ContentValues values = new ContentValues();
                    values.put(KEY_ID, Util.getNewsId(news.getUrl()));
                    values.put(KEY_TITLE, news.getTitle());
                    values.put(KEY_AUTHOR, news.getAuthor());
                    values.put(KEY_URL, news.getUrl());
                    values.put(KEY_TIME, news.getTime());
                    values.put(KEY_IMG_URL, news.getImgUrl());
                    mResolver.insert(CONTENT_URI, values);
                }
                break;
            case UPDATE_INFO:
                for (News news : mList) {
                    ContentValues values = new ContentValues();
                    values.put(KEY_TITLE, news.getTitle());
                    values.put(KEY_AUTHOR, news.getAuthor());
                    values.put(KEY_TIME, news.getTime());
                    values.put(KEY_IMG_URL, news.getImgUrl());
                    mResolver.update(CONTENT_URI, values, SELECTION_CLAUSE, new String[] {String.valueOf(Util.getNewsId(news.getUrl()))});
                }
                break;
            case DELETE:
                for (News news : mList) {
                    mResolver.delete(CONTENT_URI, SELECTION_CLAUSE, new String[] {String.valueOf(Util.getNewsId(news.getUrl()))});
                }
                break;
        }

        return null;
    }
}
