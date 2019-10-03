package com.example.demonews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demonews.cache.NewsImageCache;
import com.example.demonews.entity.News;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context mContext;
    private ArrayList<News> mList;
    private NewsImageCache mImageCache;

    NewsAdapter(Context context, ArrayList<News> mList) {
        mContext = context;
        this.mList = mList;
        mImageCache = new NewsImageCache(context);
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
        final News news = mList.get(position);
        holder.mHeadline.setText(news.getTitle());
        holder.mPublisher.setText(news.getAuthor());
        holder.mDate.setText(news.getNewsAgeString(mContext));
        mImageCache.loadBitmap(holder.mThumbnail, news);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, news.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
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


}
