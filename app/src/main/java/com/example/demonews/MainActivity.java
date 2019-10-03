package com.example.demonews;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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
}
