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
import java.util.regex.Pattern;

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
        ArrayList<News> result = new ArrayList<>();
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
            return result;
        }
        News.processHtml(html.toString());
//        String[] splits = html.toString().split(Pattern.quote(START_ARTICLE));
//        for (String i : splits) {
//            String mUrl = getUrl(i);
//            /*
//                QuangNhe: 1 số trường hợp server trả về 2 tin trùng nhau
//             */
//            boolean isContain = false;
//            for (News n : result)
//                if (n.getUrl().equals(mUrl)) {
//                    isContain = true;
//                    break;
//                }
//            if (isContain) continue;
//
//            String mTitle = getTitle(i);
//            String mAuthor = getAuthor(i);
//            long mTime;
//            try {
//                mTime = Util.convertStringToDate(getTime(i));
//            } catch (ParseException e) {
//                e.printStackTrace();
//                continue;
//            }
//            String mImgUrl = getImageUrl(i);
//
//            News news = new News(mTitle, mAuthor, mUrl, mTime, mImgUrl);
//            result.add(news);
//        }

        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<News> news) {
        mCallback.onFetchNewsFinish(news);
    }
}
