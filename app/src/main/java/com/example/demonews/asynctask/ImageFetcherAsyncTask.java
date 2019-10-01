package com.example.demonews.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.example.demonews.entity.News;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ImageFetcherAsyncTask extends AsyncTask<Void, ImageFetcherAsyncTask.Pair, Void> {
    public interface INewsImageFetcherDelegate {
        void onFetchImageFinish(Bitmap bitmap, String position);
        void onFetchDone();
    }

    private INewsImageFetcherDelegate mCallback;
    private ArrayList<News> mList;
    private int mThumbnailHeight, mThumbnailWidth;

    public ImageFetcherAsyncTask(int mThumbnailWidth, int mThumbnailHeight, ArrayList<News> mList, INewsImageFetcherDelegate mCallback) {
        this.mCallback = mCallback;
        this.mList = mList;
        this.mThumbnailHeight = mThumbnailHeight;
        this.mThumbnailWidth = mThumbnailWidth;
    }

    @Override
    protected Void doInBackground(Void... values) {
        for (int i = 0; i < mList.size(); i++) {
            News news = mList.get(i);
            if (news.getImage() == null && !TextUtils.isEmpty(news.getImgUrl())) {
                try {
                    // TODO: Nhiều link ảnh không lấy được do "java.security.cert.CertPathValidatorException: Trust anchor for certification path not found"
                    URLConnection connection = (new URL(news.getImgUrl())).openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setDoInput(true);
                    connection.connect();

                    InputStream input = connection.getInputStream();

                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(input, null, options);
                    int width = options.outWidth;
                    int height = options.outHeight;
                    final int heightRatio = Math.round((float) height / (float) mThumbnailHeight);
                    final int widthRatio = Math.round((float) width / (float) mThumbnailWidth);
                    options.inSampleSize = Math.min(widthRatio, heightRatio);

                    // QuangNHe: Decode image
                    options.inJustDecodeBounds = false;
                    Bitmap myBitmap = getResizedBitmap(BitmapFactory.decodeStream(input, null, options), mThumbnailHeight, mThumbnailWidth);
                    publishProgress(new Pair(myBitmap, news.getUrl()));
                    input.close();
                } catch (IOException e) {
                    System.out.println("QuangNHe onFetchImageFinish ERR " + news.getImgUrl());
                    e.printStackTrace();
                }
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
    protected void onProgressUpdate(Pair... values) {
        super.onProgressUpdate(values);
        for (Pair i : values) {
            mCallback.onFetchImageFinish(i.mBitmap, i.mPosition);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mCallback.onFetchDone();
    }

    class Pair {
        Bitmap mBitmap;
        String mPosition;

        Pair(Bitmap mBitmap, String mPosition) {
            this.mBitmap = mBitmap;
            this.mPosition = mPosition;
        }
    }
}
