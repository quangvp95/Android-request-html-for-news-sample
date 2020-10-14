package com.example.demonews.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.demonews.R;
import com.example.demonews.entity.News;

public class NewsItemFragment extends Fragment {
    public static final String NEWS_TAG = "news";
    News mNews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.item_layout, container, false);

        WebView webView = rootView.findViewById(R.id.newsWebView);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mNews = (News) bundle.getSerializable(NEWS_TAG);

            webView.loadUrl(mNews.getUrl());
            TextView textTitle = rootView.findViewById(R.id.textTitle);
            textTitle.setText(mNews.getTitle());
            TextView textUrl = rootView.findViewById(R.id.textUrl);
            textUrl.setText(mNews.getUrl());
        }

        return rootView;
    }

}
