package com.example.demonews;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demonews.asynctask.NewsFetcherAsyncTask;
import com.example.demonews.db.NewsProvider;
import com.example.demonews.entity.News;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

/**
 * QuangNHe
 * <p>
 * <p>
 * notify loader --> update UI
 * init loader --+--> get News from db --> update UI                     ^
 * |                                                       |
 * v                                                       |
 * fetch News --> convert Html to News --> delete db --> update new db
 */
public class NewsRecyclerView extends RecyclerView implements LoaderManager.LoaderCallbacks<Cursor> {
    private ArrayList<News> mList;
    private RecyclerView.Adapter mAdapter;

    private NewsFetcherAsyncTask.INewFetcher mCallback;

    public NewsRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }

    public NewsRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setId(R.id.news_recycler_view);
        mList = new ArrayList<>();
        LayoutManager layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);

        mAdapter = new NewsAdapter(getContext(), mList);
        setAdapter(mAdapter);
    }

    /**
     * QuangNhe: Tạo loader để load và đăng kí observer database, đồng thời fetch dữ liệu từ server
     */
    public void initLoader(LoaderManager supportLoaderManager, long lastTimeRequest) {
        supportLoaderManager.initLoader(0, null, this);

        // QuangNHe: Nếu lần cuối request cách đây 1 giờ thì request lại
        if ((System.currentTimeMillis() - lastTimeRequest) > 3600 * 1000)
            fetch();
    }

    public void setNewsFetcherAsyncTaskCallback(NewsFetcherAsyncTask.INewFetcher mCallback) {
        this.mCallback = mCallback;
    }

    public void fetch() {
        new NewsFetcherAsyncTask(getContext(), mCallback).execute();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // QuangNHe: Query tất cả các tin trong database
        return new CursorLoader(getContext(), NewsProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                mList.clear();
                News header = null;
                do {
                    News news = new News(cursor);
                    if (!TextUtils.isEmpty(news.getTitle()) && news.getTitle().startsWith(News.HEADER)) {
                        header = news;
                        header.setTitle(header.getTitle().substring(News.HEADER.length()));
                        continue;
                    }
                    mList.add(news);
                } while (cursor.moveToNext());

                Collections.sort(mList);
                if (header != null)
                    mList.add(0, header);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {}
}
