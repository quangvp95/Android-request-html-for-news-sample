package com.example.demonews.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.example.demonews.entity.News;

import java.util.List;

public class NewsBottomSheetAdapter  extends FragmentStateAdapter {
    private List<News> mList;
    public NewsBottomSheetAdapter(@NonNull Fragment fragment, List<News> newsList) {
        super(fragment);
        mList = newsList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        NewsItemFragment fragment = new NewsItemFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NewsItemFragment.POSITION_TAG, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }
}
