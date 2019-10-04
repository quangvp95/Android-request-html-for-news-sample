package com.example.demonews.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

import com.example.demonews.cache.BitmapDiskCache;
import com.example.demonews.entity.News;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

/**
 * QuangNHe: AsyncTask ứng với 1 ImageView, có nhiệm vụ lấy ảnh từ database hoặc request lên mạng
 */
public class BitmapWorkerAsyncTask extends AsyncTask<String, Void, Bitmap> {
    public interface IBitmapWorker {
        void onFinish(Bitmap bitmap, News news);
    }

    private News mNews;
    private final WeakReference<ImageView> mImageViewReference;
    private BitmapDiskCache mBitmapDiskCache;

    /**
     * QuangNHe: kích thước view sẽ hiển thị ảnh
     */
    private int mThumbnailWidth;
    private int mThumbnailHeight;

    private IBitmapWorker mCallback;

    public BitmapWorkerAsyncTask(IBitmapWorker callback, ImageView imageView, News news, int mThumbnailWidth, int mThumbnailHeight, BitmapDiskCache bitmapDiskCache) {
        mCallback = callback;
        mImageViewReference = new WeakReference<>(imageView);
        mNews = news;
        this.mThumbnailHeight = mThumbnailHeight;
        this.mThumbnailWidth = mThumbnailWidth;
        mBitmapDiskCache = bitmapDiskCache;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        /*
            QuangNHe: tìm trong database
         */
        String key = String.valueOf(mNews.getNewsId());
        Bitmap bitmap = mBitmapDiskCache.getInBackgroundThread(key);
        if (bitmap != null)
            return bitmap;

        /*
            QuangNhe: Nếu database không có thì request lên mạng
         */
        bitmap = downloadBitmap(mNews, mThumbnailWidth, mThumbnailHeight);
        if (bitmap == null)
            return null;

        /*
            QuangNHe: Lưu lại lần sau dùng
         */
        mBitmapDiskCache.addInBackgroundThread(bitmap, key);
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

                Bitmap bm = BitmapFactory.decodeStream(input);
                if (preferHeight > 0 && preferWidth > 0) {
                    if (bm == null) {
                        System.out.println("QuangNHe onFetchImageFinish ERR bm == null " + news.getImgUrl());
                        return null;
                    }
                    Bitmap myBitmap = getResizedBitmap(bm, preferHeight, preferWidth);
                    input.close();
                    return myBitmap;
                }
                return bm;
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

        Matrix matrix = new Matrix();
        /*
            QuangNHe: thiết lập độ scale theo chiều rộng và dài cho ảnh để hợp view
         */
        matrix.postScale(scale, scale);

        /*
            QuangNHe: crop ảnh thì lấy trung tâm làm gốc, crop đều các cạnh
         */
        int offsetX = (int) ((width * scale - newWidth) / 2);
        int offsetY = (int) ((height * scale - newHeight) / 2);

        /*
            QuangNHe: chỉnh lại bitmap, scale về kích thước cần, crop các cạnh để vừa nội dung,
            tránh làm ảnh bị co dãn
         */
        return Bitmap.createBitmap(bm, offsetX, offsetY, width - offsetX * 2, height - offsetY * 2,
                matrix, false);
    }

    /**
     * QuangNHe: Nếu lấy được bitmap thì cập nhât
     */
    @Override
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