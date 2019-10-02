package com.example.demonews.entity;

import android.database.Cursor;

import androidx.annotation.NonNull;

import com.example.demonews.util.Util;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class News implements Comparable<News> {
    private String mTitle;
    private String mAuthor;
    /**
     * QuangNHe: Đôi khi server trả về tin được cập nhật thời gian -> không dùng time làm id, chỉ dùng url để phân biệt
     */
    private String mUrl;
    private long mTime;
    private String mImgUrl;

    public News(String paragraph) throws ParseException {
        this.mUrl = getUrl(paragraph);
        this.mTitle = getTitle(paragraph);
        this.mAuthor = getAuthor(paragraph);
        this.mTime = convertStringToDate(getTime(paragraph));
        this.mImgUrl = getImageUrl(paragraph);
    }

    public News(Cursor cursor) {
        this.mTitle = cursor.getString(0);
        this.mAuthor = cursor.getString(1);
        this.mUrl = cursor.getString(2);
        this.mTime = cursor.getLong(3);
        this.mImgUrl = cursor.getString(4);
    }

    public News(String mTitle, String mAuthor, String mUrl, long mTime, String mImgUrl) {
        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
        this.mUrl = mUrl;
        this.mTime = mTime;
        this.mImgUrl = mImgUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getUrl() {
        return mUrl;
    }

    public long getTime() {
        return mTime;
    }

    public String getImgUrl() {
        return mImgUrl;
    }

    /**
     * QuangNHe: Hỗ trợ việc so sánh để sắp xếp tin theo thứ tự từ mới nhất đến cũ nhất
     *
     * @param news
     * @return 1 là tin cũ hơn
     * 0 là tin cùng thời gian
     * -1 là tin mới hơn
     */
    @Override
    public int compareTo(@NotNull News news) {
//        if (getTime() > news.getTime()) {
//            return 1;
//        } else if (getTime() < news.getTime()) {
//            return -1;
//        } else {
//            return 0;
//        }
        return Long.compare(getTime(), news.getTime()) * -1;
    }

    /**
     * QuangNHe: không có gì đặc biệt, chỉ để nhìn tool debug cho dễ
     *
     * @return
     */
    @NonNull
    @Override
    public String toString() {
        return getTime() + " | " + getTitle();
    }

    private static final String START_ARTICLE = "class='mobile-row";
    private static final String MODULE_NEWS_MOBILE = "<div id='module-news-mobile-";
    private static final String OTHER_NEWS_MOBILE_LEFT = "class='other-news-mobile-left-style";
    private static final String MOBILE_ROW_2_LEFT = "class='mobile-row-2'";
    private static final String MOBILE_COL_LEFT = "class='mobile-col";
    private static final String MOBILE_NEW_BLOCK_LEFT = "class='mobile-new-block";
    private static final String OTHER_NEWS_MOBILE_RIGHT = "class='other-news-mobile-right-style";
    private static final String MOBILE_OTHER_COL = "class='mobile-other-col";

    /*
        MODULE_NEWS_MOBILE_LEFT
            NEWS_MOBILE_RSS_LEFT
                HEADER
                MOBILE_ROW_2_LEFT
                    MOBILE_COL_LEFT
                    ...
            OTHER_NEWS_MOBILE_LEFT
                MOBILE_NEW_BLOCK_LEFT
                ...
        MODULE_NEWS_MOBILE_RIGHT
            OTHER_NEWS_MOBILE_RIGHT
                MOBILE_OTHER_COL
                ...
            ...

     */
    public static ArrayList<News> processHtml(String html) {
        ArrayList<News> result = new ArrayList<>();

        String[] moduleNewsMobile = html.split(Pattern.quote(MODULE_NEWS_MOBILE)); //= MODULE_NEWS_MOBILE_LEFT + MODULE_NEWS_MOBILE_RIGHT

        String[] mobileNewsLeft = moduleNewsMobile[1].split(Pattern.quote(OTHER_NEWS_MOBILE_LEFT)); //= NEWS_MOBILE_RSS_LEFT + OTHER_NEWS_MOBILE_LEFT

        String[] mobileNewsRssLeft = mobileNewsLeft[0].split(Pattern.quote(MOBILE_ROW_2_LEFT)); //= HEADER + MOBILE_ROW_2_LEFT

        News header;
        try {
            header = new News(mobileNewsRssLeft[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] mobileNewsRssColLeft = mobileNewsRssLeft[1].split(Pattern.quote(MOBILE_COL_LEFT)); //= MOBILE_COL_LEFT...
        for (String i : mobileNewsRssColLeft) {
            try {
                result.add(new News(i));
            } catch (ParseException e) {
                System.out.println("QuangNHe: processHtml ERR " + i);
                e.printStackTrace();
            }
        }

        String[] mobileNewsOtherLeft = mobileNewsLeft[1].split(Pattern.quote(MOBILE_NEW_BLOCK_LEFT)); //= MOBILE_NEW_BLOCK_LEFT...
        for (String i : mobileNewsOtherLeft) {
            try {
                result.add(new News(i));
            } catch (ParseException e) {
                System.out.println("QuangNHe: processHtml MOBILE_NEW_BLOCK_LEFT ERR " + i);
                e.printStackTrace();
            }
        }

        //MODULE_NEWS_MOBILE_RIGHT
        String[] otherMobileNewsRight = moduleNewsMobile[2].split(Pattern.quote(OTHER_NEWS_MOBILE_RIGHT)); //= OTHER_NEWS_MOBILE_RIGHT...
        for (String i : otherMobileNewsRight) {
            String[] otherMobileNewsColRight = i.split(Pattern.quote(MOBILE_OTHER_COL)); //= OTHER_NEWS_MOBILE_RIGHT + MOBILE_OTHER_COL...
            for (String j : otherMobileNewsColRight) {
                try {
                    result.add(new News(j));
                } catch (ParseException e) {
                    System.out.println("QuangNHe: processHtml MOBILE_NEW_BLOCK_LEFT ERR " + i);
                    e.printStackTrace();
                }
            }
        }

//            /*
//                QuangNhe: 1 số trường hợp server trả về 2 tin trùng nhau
//             */
//        boolean isContain = false;
//        for (News n : result)
//            if (n.getUrl().equals(mUrl)) {
//                isContain = true;
//                break;
//            }
//        if (isContain) continue;

        return result;
    }

    private static String getTitle(String paragraph) {
        return Util.getString(paragraph, "title='", "' href='");
    }

    private static String getUrl(String paragraph) {
        return Util.getString(paragraph, "href='", "'>");
    }

    private static String getTime(String paragraph) {
        return Util.getString(paragraph, "class='date-publish'>", "<");
    }

    private static String getImageUrl(String paragraph) {
        return Util.getString(paragraph, "background-image: url('", "')");
    }

    private static String getAuthor(String paragraph) {
        return Util.getString(paragraph, "\");'>", "</");
    }

    private static final SimpleDateFormat sTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ROOT);

    private static long convertStringToDate(String dateString) throws ParseException {
        Date date = sTime.parse(dateString);
        return date.getTime();
    }

}
