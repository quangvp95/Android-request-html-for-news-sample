package com.example.demonews.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.demonews.R;
import com.example.demonews.entity.News;
import com.example.demonews.viewmodel.BottomSheetViewModel;
import com.example.demonews.viewmodel.ViewModelFactory;

public class NewsItemFragment extends Fragment {
    public static final String POSITION_TAG = "position";
    private BottomSheetViewModel mViewModel;
    private WebView webView;

    private final View.OnClickListener mCloseListener = (View view) -> mViewModel.action(
            new BottomSheetViewModel.Event(BottomSheetViewModel.EventType.CLOSE));

    private final View.OnClickListener mOpenListener = (View view) -> mViewModel.action(
            new BottomSheetViewModel.Event(BottomSheetViewModel.EventType.OPEN));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ccnews_bottom_sheet_item_layout,
                container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(
                getParentFragment() != null ? getParentFragment() : requireActivity(),
                new ViewModelFactory()).get(
                BottomSheetViewModel.class);
        TextView textTitle = view.findViewById(R.id.textTitle);
        textTitle.setOnClickListener(mOpenListener);

        TextView textUrl = view.findViewById(R.id.textUrl);
        textUrl.setOnClickListener(mOpenListener);

        ImageView imgFavicon = view.findViewById(R.id.imgFavicon);
        imgFavicon.setOnClickListener(mOpenListener);

        ImageView imgClose = view.findViewById(R.id.imgClose);
        imgClose.setOnClickListener(mCloseListener);

        webView = view.findViewById(R.id.newsWebView);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        Bundle bundle = getArguments();
        if (bundle != null) {
            int position = bundle.getInt(POSITION_TAG);
            News news = mViewModel.getNewsAtIndex(position);

            if (news != null) {
                if (savedInstanceState == null) {
                    webView.loadUrl(news.getUrl());
                } else {
                    webView.restoreState(savedInstanceState);
                }
                textTitle.setText(news.getTitle());
                textUrl.setText(news.getUrl());
            }
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }
}
