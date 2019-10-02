package com.example.demonews.asynctask;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

import com.example.demonews.db.NewsProvider;
import com.example.demonews.entity.News;
import com.example.demonews.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

import static com.example.demonews.db.NewsProvider.CONTENT_URI;
import static com.example.demonews.db.NewsProvider.KEY_IMAGE;
import static com.example.demonews.db.NewsProvider.SELECTION_CLAUSE;

public class BitmapWorkerAsyncTask extends AsyncTask<String, Void, Bitmap> {
    public interface IBitmapWorker {
        void onFinish(Bitmap bitmap, News news);
    }

    private News mNews;
    private final WeakReference<ImageView> mImageViewReference;
    private ContentResolver mContentResolver;
    private int mThumbnailWidth, mThumbnailHeight;
    private IBitmapWorker mCallback;

    public BitmapWorkerAsyncTask(IBitmapWorker callback, ImageView imageView, News news, int mThumbnailWidth, int mThumbnailHeight) {
        mCallback = callback;
        mImageViewReference = new WeakReference<>(imageView);
        mNews = news;
        this.mThumbnailHeight = mThumbnailHeight;
        this.mThumbnailWidth = mThumbnailWidth;
        mContentResolver = imageView.getContext().getContentResolver();
    }

    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
        Cursor cursor = mContentResolver.query(NewsProvider.CONTENT_URI, null, SELECTION_CLAUSE, new String[]{String.valueOf(Util.getNewsId(mNews.getUrl()))}, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                Bitmap bitmap = Util.getImage(cursor.getBlob(5));
                if (bitmap != null) {
                    cursor.close();
                    return bitmap;
                }
            }
            cursor.close();
        }

        Bitmap bitmap = downloadBitmap(mNews, mThumbnailWidth, mThumbnailHeight);
        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE, Util.getBytes(bitmap));
        mContentResolver.update(CONTENT_URI, values, SELECTION_CLAUSE, new String[]{String.valueOf(Util.getNewsId(mNews.getUrl()))});
        return bitmap;
    }

    private Bitmap downloadBitmap(News news, int preferWidth, int preferHeight) {
        if (!TextUtils.isEmpty(news.getImgUrl())) {
            try {
                // TODO: Nhiều link ảnh không lấy được do "java.security.cert.CertPathValidatorException: Trust anchor for certification path not found"
                URLConnection connection = (new URL(news.getImgUrl())).openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();

                if (preferHeight > 0 && preferWidth > 0) {
                    Bitmap bm = BitmapFactory.decodeStream(input);
                    if (bm == null) {
                        System.out.println("QuangNHe onFetchImageFinish ERR bm == null " + news.getImgUrl());
                        return null;
                    }
                    Bitmap myBitmap = getResizedBitmap(bm, preferHeight, preferWidth);
                    input.close();
                    return myBitmap;
                } else {
                    return BitmapFactory.decodeStream(input);
                }
            } catch (IOException e) {
                System.out.println("QuangNHe onFetchImageFinish ERR " + news.getImgUrl());
                e.printStackTrace();
            }
        }
        return null;
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        float scale = Math.max(scaleWidth, scaleHeight);

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scale, scale);

        int offsetX = (int) ((width * scale - newWidth) / 2);
        int offsetY = (int) ((height * scale - newHeight) / 2);

        // "RECREATE" THE NEW BITMAP
        return Bitmap.createBitmap(bm, offsetX, offsetY, width - offsetX * 2, height - offsetY * 2,
                matrix, false);
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