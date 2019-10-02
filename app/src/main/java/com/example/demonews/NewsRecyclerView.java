package com.example.demonews;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demonews.asynctask.NewsFetcherAsyncTask;
import com.example.demonews.asynctask.SaveDataAsyncTask;
import com.example.demonews.db.NewsProvider;
import com.example.demonews.entity.News;
import com.example.demonews.util.Util;

import java.util.ArrayList;
import java.util.Collections;

public class NewsRecyclerView extends RecyclerView implements NewsFetcherAsyncTask.INewFetcher {
    private ArrayList<News> mList;
    private RecyclerView.Adapter mAdapter;

    public NewsRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }

    public NewsRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mList = new ArrayList<>();
        LayoutManager layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);

        mAdapter = new NewsAdapter(getContext(), mList);
        setAdapter(mAdapter);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Cursor cursor = getContext().getContentResolver().query(NewsProvider.CONTENT_URI,null, null, null, null);
        if (cursor != null) {
            mList.clear();
            if (cursor.moveToFirst()) {
                do {
                    News news = new News(cursor);
                    mList.add(news);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        fetch();
    }

    private NewsFetcherAsyncTask.INewFetcher mCallback;

    public void setNewsFetcherAsyncTaskCallback(NewsFetcherAsyncTask.INewFetcher mCallback) {
        this.mCallback = mCallback;
    }

    public void fetch() {
        new NewsFetcherAsyncTask(this).execute();
    }

    @Override
    public void onFetchNewsFinish(ArrayList<News> listNews) {
        if (listNews.isEmpty()) return;
        ArrayList<News> listForUpdate = new ArrayList<>(),
                listForInsert = new ArrayList<>(),
                listForDelete = new ArrayList<>(mList);
        for (News news : listNews) {
            boolean isOld = false;
            for (int i = 0; i < mList.size(); i++) {
                News oldNews = mList.get(i);
                if (news.getUrl().equals(oldNews.getUrl())) {
                    // QuangNHe: mList sẽ lấy tất cả obj mới -> cần lưu ảnh của các obj cũ để tái sử dụng
                    // Các thông tin khác thì không cần lưu để update lại database
                    isOld = true;
                    listForUpdate.add(news);
                    listForDelete.remove(oldNews);
                    break;
                }
            }
            if (!isOld) {
                listForInsert.add(news);
            }
        }
        mList.clear();
        mList.addAll(listNews);

        Collections.sort(mList);

        mAdapter.notifyDataSetChanged();

        if (mCallback != null)
            mCallback.onFetchNewsFinish(listNews);
        new SaveDataAsyncTask(getContext(), SaveDataAsyncTask.TYPE.INSERT, listForInsert).execute();
        new SaveDataAsyncTask(getContext(), SaveDataAsyncTask.TYPE.UPDATE_INFO, listForUpdate).execute();
        new SaveDataAsyncTask(getContext(), SaveDataAsyncTask.TYPE.DELETE, listForDelete).execute();
    }
}
