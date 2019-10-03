package com.example.demonews.cache;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.LruCache;
import android.widget.ImageView;

import com.example.demonews.R;
import com.example.demonews.asynctask.BitmapWorkerAsyncTask;
import com.example.demonews.entity.News;

import java.lang.ref.WeakReference;

/**
 * QuangNHe: Cache để lưu ảnh bitmap của các imageView trong adapter
 * Các bước lấy ảnh:
 *      B1: kiểm tra trong LruCache có không, có thì trả về
 *      B2: kiểm tra imageView này có đang được đính kèm 1 worker nào đang lấy ảnh không
 *          Nếu có thì kiểm tra worker này có đang lấy ảnh mình cần không, nếu phải thì để nguyên như cũ
 *      B3: Nếu worker không phải thì hủy và tạo worker mới cho việc lấy ảnh, hiển thị ảnh mặc định
 *          để đợi khi nào lấy được thì cập nhật ảnh mới
 * Tài liệu:
 *      - https://stuff.mit.edu/afs/sipb/project/android/docs/training/displaying-bitmaps/process-bitmap.html#concurrency
 *      - https://developer.android.com/topic/performance/graphics/cache-bitmap#java
 */
public class NewsImageCache implements BitmapWorkerAsyncTask.IBitmapWorker {
    private LruCache<String, Bitmap> mMemoryCache;
    private Bitmap mDefaultBitmap;
    private Context mContext;

    /**
     * QuangNHe: kích thước view sẽ hiển thị ảnh
     */
    private int mThumbnailWidth;
    private int mThumbnailHeight;

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

    /**
     * QuangNHe: Hàm load ảnh hoặc tạo worker lấy ảnh
     */
    public void loadBitmap(ImageView imageView, News news) {
        Bitmap result = mMemoryCache.get(news.getUrl());
        if (result != null) {
            imageView.setImageBitmap(result);
            return;
        }

        if (cancelPotentialDownload(news, imageView)) {
            BitmapWorkerAsyncTask task = new BitmapWorkerAsyncTask(this, imageView, news, mThumbnailWidth, mThumbnailHeight);
            imageView.setImageDrawable(new WorkerDrawable(mContext.getResources(), mDefaultBitmap, task));
            task.execute();
        }
    }

    /**
     * QuangNhe: Check imageView có link worker load ảnh cần không
     */
    private static boolean cancelPotentialDownload(News news, ImageView imageView) {
        BitmapWorkerAsyncTask bitmapWorkerAsynctask = getBitmapDownloaderTask(imageView);

        if (bitmapWorkerAsynctask != null) {
            News bitmapNews = bitmapWorkerAsynctask.getNews();
            if ((bitmapNews == null) || (!bitmapNews.equals(news))) {
                bitmapWorkerAsynctask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    private static BitmapWorkerAsyncTask getBitmapDownloaderTask(ImageView imageView) {
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

    /**
     * QuangNHe: Lớp Drawable làm holder để đợi image được load
     */
    static class WorkerDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerAsyncTask> mBitmapDownloaderTaskReference;

        WorkerDrawable(Resources res, Bitmap bitmap, BitmapWorkerAsyncTask bitmapWorkerAsynctask) {
            super(res, bitmap);
            mBitmapDownloaderTaskReference =
                    new WeakReference<>(bitmapWorkerAsynctask);
        }

        BitmapWorkerAsyncTask getBitmapDownloaderTask() {
            return mBitmapDownloaderTaskReference.get();
        }
    }

}
