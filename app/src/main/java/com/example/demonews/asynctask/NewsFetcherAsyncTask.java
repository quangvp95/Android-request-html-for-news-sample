package com.example.demonews.asynctask;

import android.os.AsyncTask;

import com.example.demonews.entity.News;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

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

    public NewsFetcherAsyncTask(INewFetcher callback) {
        mCallback = callback;
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
        return News.processHtml(html.toString());
    }

    @Override
    protected void onPostExecute(ArrayList<News> news) {
        mCallback.onFetchNewsFinish(news);
    }
}
