package com.example.demonews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demonews.entity.News;
import com.example.demonews.util.Util;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context mContext;
    private ArrayList<News> mList;
    private Drawable mDefaultThumbnail;

    NewsAdapter(Context context, ArrayList<News> mList) {
        mContext = context;
        this.mList = mList;
        mDefaultThumbnail = context.getResources().getDrawable(R.drawable.home_vn, null);
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_layout, parent, false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = mList.get(position);
        holder.mHeadline.setText(news.getTitle());
        holder.mPublisher.setText(news.getAuthor());
        holder.mDate.setText(Util.convertIntDateToString(mContext, news.getTime()));
        if (news.getImage() != null)
            holder.mThumbnail.setImageBitmap(news.getImage());
        else
            holder.mThumbnail.setImageDrawable(mDefaultThumbnail);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        View mView;
        ImageView mThumbnail;
        TextView mHeadline;
        TextView mPublisher;
        TextView mDate;

        NewsViewHolder(View v) {
            super(v);
            mView = v;
            mThumbnail = mView.findViewById(R.id.article_thumbnail);
            mHeadline = mView.findViewById(R.id.article_headline);
            mPublisher = mView.findViewById(R.id.article_publisher);
            mDate = mView.findViewById(R.id.article_date);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }
}
