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
                    URLConnection connection = (new URL(news.getImgUrl())).openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setDoInput(true);
                    connection.connect();

                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = getResizedBitmap(BitmapFactory.decodeStream(input), mThumbnailHeight, mThumbnailWidth);
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

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
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
