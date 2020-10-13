package com.example.demonews.ui.fragment;

import static com.example.demonews.ui.fragment.NewsItemFragment.NEWS_TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.demonews.entity.News;

import java.util.ArrayList;
import java.util.List;

public class NewsBottomSheetAdapter  extends FragmentStateAdapter {
    private List<News> mList;
    public NewsBottomSheetAdapter(@NonNull Fragment fragment, ArrayList<News> newsList) {
        super(fragment);
        mList = newsList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        NewsItemFragment fragment = new NewsItemFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NEWS_TAG, mList.get(position));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
