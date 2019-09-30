package com.example.demonews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;

import com.example.demonews.asynctask.NewsFetcherAsyncTask;
import com.example.demonews.entity.News;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, NewsFetcherAsyncTask.INewFetcherDelegate {

    NewsRecyclerView mNewsRecyclerView;
    ListNewsLayout mListNewsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNewsRecyclerView = findViewById(R.id.news_recycler);

        mListNewsLayout = findViewById(R.id.list_news);
        mListNewsLayout.setOnRefreshListener(this);
        mListNewsLayout.setColorSchemeResources(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);
    }

    public void fetch(View view) {
        mNewsRecyclerView.fetch(this);
    }

    @Override
    public void onRefresh() {
        mNewsRecyclerView.fetch(this);
    }


    @Override
    public void onFetchNewsFinish(ArrayList<News> news) {
        mListNewsLayout.setRefreshing(false);
    }
}
