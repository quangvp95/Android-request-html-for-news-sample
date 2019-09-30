package com.example.demonews.entity;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

public class News implements Comparable<News> {
    private String mTitle;
    private String mAuthor;
    /**
     * QuangNHe: Đôi khi server trả về tin được cập nhật thời gian -> không dùng time làm id, chỉ dùng url để phân biệt
     */
    private String mUrl;
    private long mTime;
    private String mImgUrl;
    private Bitmap mImage;

    public News(String mTitle, String mAuthor, String mUrl, long mTime, String mImgUrl) {
        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
        this.mUrl = mUrl;
        this.mTime = mTime;
        this.mImgUrl = mImgUrl;
        this.mImage = null;
    }

    public News(String mTitle, String mAuthor, String mUrl, long mTime, String mImgUrl, Bitmap mImage) {
        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
        this.mUrl = mUrl;
        this.mTime = mTime;
        this.mImgUrl = mImgUrl;
        this.mImage = mImage;
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

    public Bitmap getImage() {
        return mImage;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public void setImgUrl(String mImgUrl) {
        this.mImgUrl = mImgUrl;
    }

    public void setImage(Bitmap mImage) {
        this.mImage = mImage;
    }

    /**
     * QuangNHe: Hỗ trợ việc so sánh để sắp xếp tin theo thứ tự từ mới nhất đến cũ nhất
     * @param news
     * @return  1 là tin cũ hơn
     *          0 là tin cùng thời gian
     *          -1 là tin mới hơn
     */
    @Override
    public int compareTo(News news) {
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
     * @return
     */
    @NonNull
    @Override
    public String toString() {
        return getTime() + " | " +getTitle();
    }
}
