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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

import static com.example.demonews.db.NewsProvider.CONTENT_URI;
import static com.example.demonews.db.NewsProvider.KEY_IMAGE;
import static com.example.demonews.db.NewsProvider.SELECTION_CLAUSE;

/**
 * QuangNHe: AsyncTask ứng với 1 ImageView, có nhiệm vụ lấy ảnh từ database hoặc request lên mạng
 */
public class BitmapWorkerAsyncTask extends AsyncTask<String, Void, Bitmap> {
    public interface IBitmapWorker {
        void onFinish(Bitmap bitmap, News news);
    }

    private News mNews;
    private final WeakReference<ImageView> mImageViewReference;
    private ContentResolver mContentResolver;

    /**
     * QuangNHe: kích thước view sẽ hiển thị ảnh
     */
    private int mThumbnailWidth;
    private int mThumbnailHeight;

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
    protected Bitmap doInBackground(String... params) {
        /*
            QuangNHe: tìm trong database
         */
        Cursor cursor = mContentResolver.query(NewsProvider.CONTENT_URI, null, SELECTION_CLAUSE, new String[]{String.valueOf(mNews.getNewsId())}, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                Bitmap bitmap = getImage(cursor.getBlob(5));
                if (bitmap != null) {
                    cursor.close();
                    return bitmap;
                }
            }
            cursor.close();
        }

        /*
            QuangNhe: Nếu database không có thì request lên mạng
         */
        Bitmap bitmap = downloadBitmap(mNews, mThumbnailWidth, mThumbnailHeight);
        if (bitmap == null)
            return null;
        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE, getBytes(bitmap));
        mContentResolver.update(CONTENT_URI, values, SELECTION_CLAUSE, new String[]{String.valueOf(mNews.getNewsId())});
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

    // convert from bitmap to byte array
    private static byte[] getBytes(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    private static Bitmap getImage(byte[] image) {
        if (image == null) return null;
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}