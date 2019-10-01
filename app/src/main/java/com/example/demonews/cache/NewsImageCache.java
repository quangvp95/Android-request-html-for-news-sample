package com.example.demonews.cache;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import com.example.demonews.R;
import com.example.demonews.asynctask.BitmapWorkerTask;
import com.example.demonews.asynctask.SaveDataAsyncTask;
import com.example.demonews.db.NewsProvider;
import com.example.demonews.entity.News;
import com.example.demonews.util.Util;

import java.lang.ref.WeakReference;

import static android.os.Environment.isExternalStorageRemovable;
import static com.example.demonews.db.NewsProvider.CONTENT_URI;
import static com.example.demonews.db.NewsProvider.KEY_IMAGE;
import static com.example.demonews.db.NewsProvider.SELECTION_CLAUSE;

/**
 * https://stuff.mit.edu/afs/sipb/project/android/docs/training/displaying-bitmaps/process-bitmap.html#concurrency
 */
public class NewsImageCache implements BitmapWorkerTask.IBitmapWorker {
    private LruCache<String, Bitmap> mMemoryCache;
    private Bitmap mDefaultBitmap;
    private Context mContext;
    private int mThumbnailWidth, mThumbnailHeight;

    public NewsImageCache(Context context) {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };

        mContext = context;
        mDefaultBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.home_vn);
        mThumbnailWidth = (int) context.getResources().getDimension(R.dimen.news_thumbnail_width);
        mThumbnailHeight = (int) context.getResources().getDimension(R.dimen.news_thumbnail_height);

    }

    public void loadBitmap(ImageView imageView, News news) {
        Bitmap result = mMemoryCache.get(news.getUrl());
        if (result != null) {
            imageView.setImageBitmap(result);
            return;
        }

        if (cancelPotentialDownload(news, imageView)) {
            BitmapWorkerTask task = new BitmapWorkerTask(this, imageView, news, mThumbnailWidth, mThumbnailHeight);
            imageView.setImageDrawable(new WorkerDrawable(mContext.getResources(), mDefaultBitmap, task));
            task.execute();
        }
    }

    private static boolean cancelPotentialDownload(News news, ImageView imageView) {
        BitmapWorkerTask bitmapWorkerTask = getBitmapDownloaderTask(imageView);

        if (bitmapWorkerTask != null) {
            News bitmapNews = bitmapWorkerTask.getNews();
            if ((bitmapNews == null) || (!bitmapNews.equals(news))) {
                bitmapWorkerTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    private static BitmapWorkerTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof WorkerDrawable) {
                WorkerDrawable workerDrawable = (WorkerDrawable) drawable;
                return workerDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    @Override
    public void onFinish(Bitmap bitmap, News news) {
        mMemoryCache.put(news.getUrl(), bitmap);
    }

    static class WorkerDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapDownloaderTaskReference;

        public WorkerDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapDownloaderTaskReference =
                    new WeakReference<>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }

}
