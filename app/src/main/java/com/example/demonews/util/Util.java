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
    private static final long MINUTES = 60 * 1000;
    private static final long HOUR = 3600 * 1000;

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

    public static String convertIntDateToSewsAge(Context context, long dateInt) {
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

}
