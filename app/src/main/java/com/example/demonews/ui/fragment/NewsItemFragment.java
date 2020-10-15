package com.example.demonews.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.demonews.R;
import com.example.demonews.entity.News;
import com.example.demonews.viewmodel.BottomSheetViewModel;
import com.example.demonews.viewmodel.ViewModelFactory;

public class NewsItemFragment extends Fragment {
    public static final String NEWS_TAG = "news";
    News mNews;
    private BottomSheetViewModel model;

    private View.OnClickListener mCloseListener = (view) -> {
        model.close(new BottomSheetViewModel.Event(mNews));
    };

    private View.OnClickListener mOpenListener = (view) -> {
        model.open(new BottomSheetViewModel.Event(mNews));
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.item_layout, container, false);

        setRetainInstance(true);
        model = new ViewModelProvider(getActivity(), new ViewModelFactory()).get(
                BottomSheetViewModel.class);
        TextView textTitle = rootView.findViewById(R.id.textTitle);
        textTitle.setOnClickListener(mOpenListener);
        TextView textUrl = rootView.findViewById(R.id.textUrl);
        textUrl.setOnClickListener(mOpenListener);
        ImageView imgFavicon = rootView.findViewById(R.id.imgFavicon);
        imgFavicon.setOnClickListener(mOpenListener);

        ImageView imgClose = rootView.findViewById(R.id.imgClose);
        imgClose.setOnClickListener(mCloseListener);

        WebView webView = rootView.findViewById(R.id.newsWebView);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mNews = (News) bundle.getSerializable(NEWS_TAG);

            webView.loadUrl(mNews.getUrl());
            textTitle.setText(mNews.getTitle());
            textUrl.setText(mNews.getUrl());
        }

        return rootView;
    }

}
