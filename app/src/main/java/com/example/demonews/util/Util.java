package com.example.demonews.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.example.demonews.R;
import com.example.demonews.entity.News;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
    public static String getString(String paragraph, String start, String end) {
        String result = "";
        if (TextUtils.isEmpty(paragraph)) return result;

        int startIndex = paragraph.indexOf(start);
        if (startIndex == -1) return result;

        int endIndex = paragraph.indexOf(end, startIndex + start.length());
        if (endIndex == -1) return result;

        result = paragraph.substring(startIndex + start.length(), endIndex);
        return result;
    }

    public static long getNewsId(String url) {
        int hash = url.hashCode();
        return Math.abs(hash);
    }

    private static final SimpleDateFormat sTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ROOT);
    public static long convertStringToDate(String dateString) throws ParseException {
        Date date = sTime.parse(dateString);
        return date.getTime();
    }

    public static long MINUTES = 60 * 1000;
    public static long HOUR = 3600 * 1000;
    public static String convertIntDateToString(Context context, long dateInt) {
        StringBuilder builder = new StringBuilder("| ");
        Resources res = context.getResources();
        Date date = new Date();
        long offset = date.getTime() - dateInt;
        if (offset < HOUR) {
            long minutes = offset / MINUTES;
            if (minutes <= 0) minutes = 1;
            builder.append(minutes).append(" ").append(res.getString(R.string.minutes));
        } else {
            long hour = offset / HOUR;
            if (hour <= 0) hour = 1;
            builder.append(hour).append(" ").append(res.getString(R.string.hours));
        }
        return builder.append(" ").append(res.getString(R.string.ago)).toString();
    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        if (image == null) return null;
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static Bitmap downloadBitmap(News news, int preferWidth, int preferHeight) {
        if (news.getImage() == null && !TextUtils.isEmpty(news.getImgUrl())) {
            try {
                // TODO: Nhiều link ảnh không lấy được do "java.security.cert.CertPathValidatorException: Trust anchor for certification path not found"
                URLConnection connection = (new URL(news.getImgUrl())).openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();

                if (preferHeight > 0 && preferWidth > 0) {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(input, null, options);
                    int width = options.outWidth;
                    int height = options.outHeight;
                    final int heightRatio = Math.round((float) height / (float) preferHeight);
                    final int widthRatio = Math.round((float) width / (float) preferWidth);
                    options.inSampleSize = Math.min(widthRatio, heightRatio);

                    // QuangNHe: Decode image
                    options.inJustDecodeBounds = false;
                    Bitmap bm = BitmapFactory.decodeStream(input, null, options);
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

    private static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
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


}
