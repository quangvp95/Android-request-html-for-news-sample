package com.example.demonews.asynctask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.demonews.db.NewsProvider;
import com.example.demonews.entity.News;
import com.example.demonews.util.Util;

import java.lang.ref.WeakReference;

import static com.example.demonews.db.NewsProvider.CONTENT_URI;
import static com.example.demonews.db.NewsProvider.KEY_IMAGE;
import static com.example.demonews.db.NewsProvider.SELECTION_CLAUSE;

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    public interface IBitmapWorker {
        void onFinish(Bitmap bitmap, News news);
    }

    private News mNews;
    private final WeakReference<ImageView> mImageViewReference;
    private int mThumbnailWidth, mThumbnailHeight;
    private IBitmapWorker mCallback;

    public BitmapWorkerTask(IBitmapWorker callback, ImageView imageView, News news, int mThumbnailWidth, int mThumbnailHeight) {
        mCallback = callback;
        mImageViewReference = new WeakReference<>(imageView);
        mNews = news;
        this.mThumbnailHeight = mThumbnailHeight;
        this.mThumbnailWidth = mThumbnailWidth;
    }

    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
        // params comes from the execute() call: params[0] is the url.
        if (mImageViewReference.get() != null && mImageViewReference.get().getContext() != null) {
            Context context = mImageViewReference.get().getContext();
            Cursor cursor = context.getContentResolver().query(NewsProvider.CONTENT_URI, null, SELECTION_CLAUSE, new String[]{String.valueOf(Util.getNewsId(mNews.getUrl()))}, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    Bitmap bitmap = Util.getImage(cursor.getBlob(5));
                    if (bitmap != null) return bitmap;
                }
                cursor.close();
            }
        }
        Bitmap bitmap = Util.downloadBitmap(mNews, mThumbnailWidth, mThumbnailHeight);
        if (bitmap != null && mImageViewReference.get() != null && mImageViewReference.get().getContext() != null) {
            ContentValues values = new ContentValues();
            values.put(KEY_IMAGE, Util.getBytes(mNews.getImage()));
            Context context = mImageViewReference.get().getContext();
            context.getContentResolver().update(CONTENT_URI, values, SELECTION_CLAUSE, new String[] {String.valueOf(Util.getNewsId(mNews.getUrl()))});
        }
        return bitmap;
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }
        if (bitmap == null) return;

        if (mImageViewReference.get() != null) {
            ImageView imageView = mImageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

        mCallback.onFinish(bitmap, mNews);
    }

    public News getNews() {
        return mNews;
    }

}