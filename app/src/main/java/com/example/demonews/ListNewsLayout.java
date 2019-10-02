package com.example.demonews;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.demonews.asynctask.NewsFetcherAsyncTask;
import com.example.demonews.entity.News;

import java.util.ArrayList;

public class ListNewsLayout extends SwipeRefreshLayout implements NewsFetcherAsyncTask.INewFetcher, SwipeRefreshLayout.OnRefreshListener {
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

    @Override
    public void onRefresh() {
        mRecyclerView.fetch();
        mRecyclerView.setNewsFetcherAsyncTaskCallback(this);
    }

    @Override
    public void onFetchNewsFinish(ArrayList<News> news) {
        setRefreshing(false);
    }
}
