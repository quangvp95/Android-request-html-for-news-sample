package com.example.demonews;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

public class MainActivity extends AppCompatActivity{

    private ListNewsLayout mListNewsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListNewsLayout = findViewById(R.id.list_news);
        mListNewsLayout.setLoaderManager(getSupportLoaderManager());
    }

    public void fetch(View view) {
        mListNewsLayout.fetch();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        mListNewsLayout.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mListNewsLayout.onPause();
//    }

}
