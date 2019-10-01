package com.example.demonews.asynctask;

import android.os.AsyncTask;

import com.example.demonews.entity.News;
import com.example.demonews.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;

public class NewsFetcherAsyncTask extends AsyncTask<Void, Void, ArrayList<News>> {
    public interface INewFetcher {
        void onFetchNewsFinish(ArrayList<News> news);
    }

    private static String HOST = "http://m.home.vn/web/guest/danh-sach-tin-tuc/-/category/newsMobile";

    private INewFetcher mCallback;

    public NewsFetcherAsyncTask(INewFetcher callback) {
        mCallback = callback;
    }

    @Override
    protected ArrayList<News> doInBackground(Void... voids) {
        ArrayList<News> result = new ArrayList<>();
        StringBuilder html = new StringBuilder();

        try {
            URLConnection connection = (new URL(HOST)).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            for (String line; (line = reader.readLine()) != null; ) {
                html.append(line);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
        String[] splits = html.toString().split(START_ARTICLE);
        for (String i : splits) {
            String mTitle = getTitle(i);
            String mAuthor = getAuthor(i);
            String mUrl = getUrl(i);
            long mTime;
            try {
                mTime = Util.convertStringToDate(getTime(i));
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }
            String mImgUrl = getImageUrl(i);

            News news = new News(mTitle, mAuthor, mUrl, mTime, mImgUrl);
            result.add(news);
        }

        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<News> news) {
        mCallback.onFetchNewsFinish(news);
    }

    private static String START_ARTICLE = "class='mobile-row";
    private static String getTitle(String paragraph) {
        return Util.getString(paragraph, "title='", "' href='");
    }

    private static String getUrl(String paragraph) {
        return Util.getString(paragraph, "href='", "'>");
    }

    private static String getTime(String paragraph) {
        return Util.getString(paragraph, "class='date-publish'>", "<");
    }

    private static String getImageUrl(String paragraph) {
        return Util.getString(paragraph, "background-image: url('", "')");
    }

    private static String getAuthor(String paragraph) {
        return Util.getString(paragraph, "\");'>", "</");
    }

}
