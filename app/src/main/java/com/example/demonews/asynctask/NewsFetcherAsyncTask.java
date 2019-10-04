package com.example.demonews.asynctask;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.example.demonews.entity.News;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static com.example.demonews.db.NewsProvider.CONTENT_URI;
import static com.example.demonews.db.NewsProvider.KEY_AUTHOR;
import static com.example.demonews.db.NewsProvider.KEY_ID;
import static com.example.demonews.db.NewsProvider.KEY_IMG_URL;
import static com.example.demonews.db.NewsProvider.KEY_TIME;
import static com.example.demonews.db.NewsProvider.KEY_TITLE;
import static com.example.demonews.db.NewsProvider.KEY_URL;

/**
 * QuangNHe: AsyncTask để lấy tin
 */
public class NewsFetcherAsyncTask extends AsyncTask<Void, Void, ArrayList<News>> {
    public interface INewFetcher {
        void onFetchNewsFinish(ArrayList<News> news);
    }

    private static final String HOST = "http://m.home.vn/web/guest/danh-sach-tin-tuc/-/category/newsMobile";

    private static final int TIME_OUT = 5000;

    private INewFetcher mCallback;
    private ContentResolver mResolver;

    public NewsFetcherAsyncTask(Context context, INewFetcher callback) {
        mCallback = callback;
        mResolver = context.getContentResolver();
    }

    @Override
    protected ArrayList<News> doInBackground(Void... voids) {
        StringBuilder html = new StringBuilder();

        try {
            URLConnection connection = (new URL(HOST)).openConnection();
            connection.setConnectTimeout(TIME_OUT);
            connection.setReadTimeout(TIME_OUT);
            connection.connect();

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            for (String line; (line = reader.readLine()) != null; ) {
                html.append(line);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        ArrayList<News> newsArrayList;
        try {
            newsArrayList = News.processHtml(html.toString());
        } catch (Exception e) {
            System.out.println("QuangNhe: Cấu trúc HTML thay đổi");
            return new ArrayList<>();
        }
        mResolver.delete(CONTENT_URI, null, null);
        ContentValues[] values = new ContentValues[newsArrayList.size()];
        for (int i = 0; i < newsArrayList.size(); i++) {
            News news = newsArrayList.get(i);
            ContentValues value = new ContentValues();
            value.put(KEY_ID, news.getNewsId());
            value.put(KEY_TITLE, news.getTitle());
            value.put(KEY_AUTHOR, news.getAuthor());
            value.put(KEY_URL, news.getUrl());
            value.put(KEY_TIME, news.getTime());
            value.put(KEY_IMG_URL, news.getImgUrl());
            values[i] = value;
        }
        mResolver.bulkInsert(CONTENT_URI, values);

        return newsArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<News> news) {
        if (mCallback != null)
            mCallback.onFetchNewsFinish(news);
    }
}
