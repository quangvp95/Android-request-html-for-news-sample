package com.example.demonews.ui.fragment.recycler;

import android.annotation.SuppressLint;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demonews.R;
import com.example.demonews.entity.News;

public class CCNArticleDetailHolder extends RecyclerView.ViewHolder {
    TextView textTitle;
    TextView textUrl;
    ImageView imgFavicon;
    ImageView imgClose;
    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    public CCNArticleDetailHolder(@NonNull View itemView) {
        super(itemView);
        textTitle = itemView.findViewById(R.id.textTitle);

        textUrl = itemView.findViewById(R.id.textUrl);

        imgFavicon = itemView.findViewById(R.id.imgFavicon);

        imgClose = itemView.findViewById(R.id.imgClose);

        webView = itemView.findViewById(R.id.newsWebView);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
    }

    public void bindViewHolder(News news) {
        webView.loadUrl(news.getUrl());
        textTitle.setText(news.getTitle());
        textUrl.setText(news.getUrl());
    }

    public void setupClickableViews(RecyclerActionListener actionListener) {
        View.OnClickListener listener = v -> actionListener.onViewClick(
                getAbsoluteAdapterPosition(), v,
                CCNArticleDetailHolder.this);
        textTitle.setOnClickListener(listener);
        textUrl.setOnClickListener(listener);
        imgFavicon.setOnClickListener(listener);
        imgClose.setOnClickListener(listener);
    }

    interface RecyclerActionListener {
        void onViewClick(int position, View view, RecyclerView.ViewHolder viewHolder);
    }
}
