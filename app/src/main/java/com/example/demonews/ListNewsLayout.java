package com.example.demonews;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ListNewsLayout extends SwipeRefreshLayout {
    NewsRecyclerView mRecyclerView;

    public ListNewsLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public ListNewsLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
//        mRecyclerView = findViewById(R.id.news_recycler);
//        setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mRecyclerView.fetch();
//            }
//        });
    }
}
