package com.example.demonews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;

import com.example.demonews.asynctask.NewsFetcherAsyncTask;
import com.example.demonews.entity.News;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListNewsLayout mListNewsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListNewsLayout = findViewById(R.id.list_news);
    }

    public void fetch(View view) {
        mListNewsLayout.fetch();
    }
}
