package com.example.demonews;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.demonews.entity.News;
import com.example.demonews.ui.fragment.NewsBottomSheetFragment;

public class MainActivity extends AppCompatActivity implements NewsBottomSheetFragment.Delegate {

    private ListNewsLayout mListNewsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListNewsLayout = findViewById(R.id.list_news_layout);
        mListNewsLayout.initLoader(getSupportLoaderManager());
    }

    public void fetch(View view) {
        mListNewsLayout.fetch();
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    public void share(News news) {

    }

    @Override
    public void openInNewTab(News news) {

    }

    @Override
    public void bookmarks(News news) {

    }
}
