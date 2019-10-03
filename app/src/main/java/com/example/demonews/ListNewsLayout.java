package com.example.demonews;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.demonews.asynctask.NewsFetcherAsyncTask;
import com.example.demonews.entity.News;

import java.util.ArrayList;

public class ListNewsLayout extends SwipeRefreshLayout implements NewsFetcherAsyncTask.INewFetcher, SwipeRefreshLayout.OnRefreshListener {
    private static final String SHARED_PREF_FILE = BuildConfig.APPLICATION_ID + "sharedprefs";
    private static final String LAST_TIME_REQUEST_KEY = "last_time_key";

    private NewsRecyclerView mRecyclerView;

    public ListNewsLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ListNewsLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
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

    public void setLoaderManager(LoaderManager supportLoaderManager) {
        SharedPreferences mPreference = getContext().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        mRecyclerView.setLoaderManager(supportLoaderManager, mPreference.getLong(LAST_TIME_REQUEST_KEY, 0));
    }

    @Override
    public void onRefresh() {
        fetch();
    }

    @Override
    public void onFetchNewsFinish(ArrayList<News> news) {
        setRefreshing(false);
        SharedPreferences mPreference = getContext().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = mPreference.edit();
        preferencesEditor.putLong(LAST_TIME_REQUEST_KEY, System.currentTimeMillis());
        preferencesEditor.apply();
    }
}
