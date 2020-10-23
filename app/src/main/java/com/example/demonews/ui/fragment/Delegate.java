package com.example.demonews.ui.fragment;

import com.example.demonews.entity.News;

public interface Delegate {
    void share(News news);

    void openInNewTab(News news);

    void bookmarks(News news);
}

