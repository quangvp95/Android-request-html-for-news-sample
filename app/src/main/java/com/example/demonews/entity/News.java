package com.example.demonews.entity;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.demonews.R;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class News implements Comparable<News> {
    private static final long MINUTES = 60 * 1000;
    private static final long HOUR = 3600 * 1000;

    private static final String MODULE_NEWS_MOBILE = "<div id='module-news-mobile-";
    private static final String OTHER_NEWS_MOBILE_LEFT = "class='other-news-mobile-left-style";
    private static final String MOBILE_ROW_2_LEFT = "class='mobile-row-2'";
    private static final String MOBILE_COL_LEFT = "class='mobile-col";
    private static final String MOBILE_NEW_BLOCK_LEFT = "class='mobile-new-block";
    private static final String OTHER_NEWS_MOBILE_RIGHT = "class='other-news-mobile-right-style";
    private static final String MOBILE_OTHER_COL = "class='mobile-other-col";

    public static final String HEADER = "HEADER-";

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ROOT);

    private String mTitle;
    private String mAuthor;
    /**
     * QuangNHe: Đôi khi server trả về tin được cập nhật thời gian -> không dùng time làm id, chỉ dùng url để phân biệt
     */
    private String mUrl;
    private long mTime;
    private String mImgUrl;

    private News(String paragraph) throws ParseException {
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
     * QuangNhe: Id được hash từ url để dùng cho provider
     * @return
     */
    public long getNewsId() {
        int hash = mUrl.hashCode();
        return Math.abs(hash);
    }

    /**
     * QuangNHe: Trả về thời gian từ lúc tin được xuất bản cho đến hiện tại để hiện trên UI
     *
     * @return VD: "2 giờ trước" hoặc "53 minutes ago"
     */
    public String getNewsAgeString(Context context) {
        StringBuilder builder = new StringBuilder("| ");
        Resources res = context.getResources();
        Date date = new Date();
        long offset = date.getTime() - mTime;
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

    /**
     * QuangNHe: Hỗ trợ việc so sánh để sắp xếp tin theo thứ tự từ mới nhất đến cũ nhất
     *
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
     */
    @NonNull
    @Override
    public String toString() {
        return getTime() + " | " + getTitle();
    }

    /*
    */

    /**
     * QuangNhe: Hàm tách nội dung html thành tin, Hàm này phụ thuộc cấu trúc html lấy được về
     *           nên cần cập nhật khi html thay đổi
     * Cấu trúc hiện tại của nội dung html:
     *             MODULE_NEWS_MOBILE_LEFT
     *                 NEWS_MOBILE_RSS_LEFT
     *                     HEADER
     *                     MOBILE_ROW_2_LEFT
     *                         MOBILE_COL_LEFT
     *                         ...
     *                 OTHER_NEWS_MOBILE_LEFT
     *                     MOBILE_NEW_BLOCK_LEFT
     *                     ...
     *             MODULE_NEWS_MOBILE_RIGHT
     *                 OTHER_NEWS_MOBILE_RIGHT
     *                     MOBILE_OTHER_COL
     *                     ...
     *                 ...
     * @param html
     * @return
     */
    public static ArrayList<News> processHtml(String html) {
        ArrayList<News> result = new ArrayList<>();

        String[] moduleNewsMobile = html.split(Pattern.quote(MODULE_NEWS_MOBILE)); //= MODULE_NEWS_MOBILE_LEFT + MODULE_NEWS_MOBILE_RIGHT

        String[] mobileNewsLeft = moduleNewsMobile[1].split(Pattern.quote(OTHER_NEWS_MOBILE_LEFT)); //= NEWS_MOBILE_RSS_LEFT + OTHER_NEWS_MOBILE_LEFT

        String[] mobileNewsRssLeft = mobileNewsLeft[0].split(Pattern.quote(MOBILE_ROW_2_LEFT)); //= HEADER + MOBILE_ROW_2_LEFT

        News header = null;
        try {
            header = new News(mobileNewsRssLeft[0]);
            header.mTitle = HEADER + header.mTitle;
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

        /*
            QuangNhe: 1 số trường hợp server trả về 2 tin trùng nhau
        */
        for (int i = 0; i < (result.size() / 2 + 1); i++) {
            for (int j = i + 1; j < result.size(); j++)
                if (result.get(i).mUrl.equals(result.get(j).mUrl)) {
                    result.remove(j);
                    j--;
                }
        }

        /*
            QuangNHe: sắp xếp tin theo thời gian
         */
        Collections.sort(result);
        /*
            QuangNhe: thêm header vào đầu tin
         */
        if (header != null)
            result.add(0, header);

        return result;
    }


    private String getTitle(String paragraph) {
        return getString(paragraph, "title='", "' href='");
    }

    private String getUrl(String paragraph) {
        return getString(paragraph, "href='", "'>");
    }

    private String getTime(String paragraph) {
        return getString(paragraph, "class='date-publish'>", "<");
    }

    private String getImageUrl(String paragraph) {
        return getString(paragraph, "background-image: url('", "')");
    }

    private String getAuthor(String paragraph) {
        return getString(paragraph, "\");'>", "</");
    }

    /**
     * QuangNHe: Lấy nội dung giữa đoạn start và end trong xâu
     * @param paragraph
     * @param start
     * @param end
     * @return
     */
    private static String getString(String paragraph, String start, String end) {
        String result = "";
        if (TextUtils.isEmpty(paragraph)) return result;

        int startIndex = paragraph.indexOf(start);
        if (startIndex == -1) return result;

        int endIndex = paragraph.indexOf(end, startIndex + start.length());
        if (endIndex == -1) return result;

        result = paragraph.substring(startIndex + start.length(), endIndex);
        return result;
    }

    private long convertStringToDate(String dateString) throws ParseException {
        Date date = SIMPLE_DATE_FORMAT.parse(dateString);
        return date.getTime();
    }

}
