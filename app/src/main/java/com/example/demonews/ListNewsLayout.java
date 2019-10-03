package com.example.demonews;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.demonews.asynctask.NewsFetcherAsyncTask;
import com.example.demonews.db.NewsProvider;
import com.example.demonews.entity.News;

import java.util.ArrayList;

public class ListNewsLayout extends SwipeRefreshLayout implements NewsFetcherAsyncTask.INewFetcher, SwipeRefreshLayout.OnRefreshListener {
    private NewsRecyclerView mRecyclerView;
    private MyObserver mObserver;

    public ListNewsLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ListNewsLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        mObserver = new MyObserver(new Handler());

        setOnRefreshListener(this);
        setColorSchemeResources(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);
        mRecyclerView = new NewsRecyclerView(context, attrs);
        addView(mRecyclerView);
    }

    public void fetch() {
        mRecyclerView.fetch();
        mRecyclerView.setNewsFetcherAsyncTaskCallback(this);
    }

    protected void onResume() {
        getContext().getContentResolver().registerContentObserver(NewsProvider.CONTENT_URI, true, mObserver);
    }

    protected void onPause() {
        getContext().getContentResolver().registerContentObserver(NewsProvider.CONTENT_URI, true, mObserver);
    }

    public void setLoaderManager(LoaderManager supportLoaderManager) {
        mRecyclerView.setLoaderManager(supportLoaderManager);
    }

    @Override
    public void onRefresh() {
        mRecyclerView.fetch();
        mRecyclerView.setNewsFetcherAsyncTaskCallback(this);
    }

    @Override
    public void onFetchNewsFinish(ArrayList<News> news) {
        setRefreshing(false);
    }

    class MyObserver extends ContentObserver {
        public MyObserver(Handler handler) {
            super(handler);
        }


        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            // do s.th.
            // depending on the handler you might be on the UI
            // thread, so be cautious!
            System.out.println("QuangNhe");
        }
    }
}
