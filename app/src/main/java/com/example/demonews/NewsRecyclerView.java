package com.example.demonews;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demonews.asynctask.ImageFetcherAsyncTask;
import com.example.demonews.asynctask.NewsFetcherAsyncTask;
import com.example.demonews.asynctask.SaveDataAsyncTask;
import com.example.demonews.db.NewsProvider;
import com.example.demonews.entity.News;
import com.example.demonews.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NewsRecyclerView extends RecyclerView implements NewsFetcherAsyncTask.INewFetcher, ImageFetcherAsyncTask.INewsImageFetcher {
    private ArrayList<News> mList, mListUpdateImg;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private int mThumbnailHeight, mThumbnailWidth;

    public static final boolean USE_GOOGLE_CACHE_IMG_GUIDE = false;

    public static final Comparator<News> comparator = new Comparator<News>() {
        @Override
        public int compare(News o1, News o2) {
            if (o1.getTime() == o2.getTime())
                return 0;
            return o1.getTime() > o2.getTime() ? -1 : 1;
        }
    };

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
        mListUpdateImg = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);

        mAdapter = new NewsAdapter(getContext(), mList);
        setAdapter(mAdapter);

        mThumbnailWidth = (int) getResources().getDimension(R.dimen.news_thumbnail_width);
        mThumbnailHeight = (int) getResources().getDimension(R.dimen.news_thumbnail_height);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Cursor cursor = getContext().getContentResolver().query(NewsProvider.CONTENT_URI,null, null, null, null);
        if (cursor != null) {
            mList.clear();
            if (cursor.moveToFirst()) {
                do {
                    String mTitle = cursor.getString(0);
                    String mAuthor = cursor.getString(1);
                    String mUrl = cursor.getString(2);
                    long mTime = cursor.getLong(3);
                    String mImgUrl = cursor.getString(4);
                    Bitmap bitmap = Util.getImage(cursor.getBlob(5));

                    News news = new News(mTitle, mAuthor, mUrl, mTime, mImgUrl, bitmap);
                    mList.add(news);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        fetch(null);
    }

    NewsFetcherAsyncTask.INewFetcher mCallback;

    public void fetch(NewsFetcherAsyncTask.INewFetcher callback) {
        mCallback = callback;
        new NewsFetcherAsyncTask(this).execute();
    }

    @Override
    public void onFetchNewsFinish(ArrayList<News> listNews) {
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
                    if (TextUtils.isEmpty(news.getImgUrl()))
                        news.setImgUrl(oldNews.getImgUrl());
                    if (news.getImgUrl().equals(oldNews.getImgUrl()))
                        news.setImage(oldNews.getImage());
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
        if (!USE_GOOGLE_CACHE_IMG_GUIDE)
            new ImageFetcherAsyncTask(mThumbnailWidth, mThumbnailHeight, mList, this).execute();
        mListUpdateImg.clear();
    }

    @Override
    public void onFetchImageFinish(Bitmap bitmap, String id) {
        for (int i = 0; i < mList.size(); i++) {
            News news = mList.get(i);
            if (id.equals(news.getUrl())) {
                System.out.println("QuangNHe onFetchImageFinish " + bitmap);
                news.setImage(bitmap);
//                mHelper.updateNews(news);
                mListUpdateImg.add(news);
                mAdapter.notifyItemChanged(i);
                break;
            }
        }

    }

    @Override
    public void onFetchDone() {
        new SaveDataAsyncTask(getContext(), SaveDataAsyncTask.TYPE.UPDATE_IMG, mListUpdateImg).execute();
        System.out.println("QuangNHe onFetchDone DONE " + mList.size());
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getImage() != null)
                System.out.println("QuangNHe onFetchDone i: " + mList.get(i).getImage().getByteCount());
            else
                System.out.println("QuangNHe onFetchDone i: NULL");
        }
        System.out.println("QuangNHe onFetchDone DONE " + mList.size());
    }
}
