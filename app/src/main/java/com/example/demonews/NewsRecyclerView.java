package com.example.demonews;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demonews.asynctask.ImageFetcherAsyncTask;
import com.example.demonews.asynctask.NewsFetcherAsyncTask;
import com.example.demonews.db.NewsDatabaseHelper;
import com.example.demonews.entity.News;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NewsRecyclerView extends RecyclerView implements NewsFetcherAsyncTask.INewFetcherDelegate, ImageFetcherAsyncTask.INewsImageFetcherDelegate {
    private ArrayList<News> mList;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private int mThumbnailHeight, mThumbnailWidth;
    private NewsDatabaseHelper mHelper;

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
        layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);

        mAdapter = new NewsAdapter(getContext(), mList);
        setAdapter(mAdapter);

        mThumbnailWidth = (int) getResources().getDimension(R.dimen.news_thumbnail_width);
        mThumbnailHeight = (int) getResources().getDimension(R.dimen.news_thumbnail_height);

        mHelper = new NewsDatabaseHelper(getContext());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mList.addAll(mHelper.getNews());

        fetch(null);
    }

    NewsFetcherAsyncTask.INewFetcherDelegate mCallback;

    public void fetch(NewsFetcherAsyncTask.INewFetcherDelegate callback) {
        mCallback = callback;
        new NewsFetcherAsyncTask(this).execute();
    }

    @Override
    public void onFetchNewsFinish(ArrayList<News> listNews) {
        Collections.sort(listNews);
        for (News news : listNews) {
            boolean isOld = false;
            for (int i = 0; i < mList.size(); i++) {
                News oldNews = mList.get(i);
                if (news.getUrl().equals(oldNews.getUrl())) {
                    oldNews.setAuthor(news.getAuthor());
                    oldNews.setImgUrl(news.getImgUrl());
                    oldNews.setTitle(news.getTitle());
                    isOld = true;
                    break;
                }
            }
            if (!isOld) {
                mList.add(0, news);
                mHelper.addNews(news);
            }
        }

        Collections.sort(mList);

        mAdapter.notifyDataSetChanged();

        if (mCallback != null)
            mCallback.onFetchNewsFinish(listNews);
        new ImageFetcherAsyncTask(mThumbnailWidth, mThumbnailHeight, mList, this).execute();
    }

    @Override
    public void onFetchImageFinish(Bitmap bitmap, String id) {
        for (int i = 0; i < mList.size(); i++) {
            News news = mList.get(i);
            if (id.equals(news.getUrl())) {
                System.out.println("QuangNHe onFetchImageFinish " + bitmap);
                news.setImage(bitmap);
//                mHelper.updateNews(news);
                mAdapter.notifyItemChanged(i);
                break;
            }
        }

    }

    @Override
    public void onFetchDone() {
        System.out.println("QuangNHe onFetchDone DONE " + mList.size());
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getImage() != null)
                System.out.println("QuangNHe onFetchDone i: " + mList.get(i).getImage().getByteCount());
            else
                System.out.println("QuangNHe onFetchDone i: NULL");
        }
        System.out.println("QuangNHe onFetchDone DONE " + mList.size());
    }

    public static final int sizeOf(Object object) throws IOException {

        if (object == null)
            return -1;

        // Special output stream use to write the content
        // of an output stream to an internal byte array.
        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();

        // Output stream that can write object
        ObjectOutputStream objectOutputStream =
                new ObjectOutputStream(byteArrayOutputStream);

        // Write object and close the output stream
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
        objectOutputStream.close();

        // Get the byte array
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // TODO can the toByteArray() method return a
        // null array ?
        return byteArray == null ? 0 : byteArray.length;


    }
}
