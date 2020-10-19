package com.example.demonews.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.demonews.R;
import com.example.demonews.entity.News;
import com.example.demonews.viewmodel.BottomSheetViewModel;
import com.example.demonews.viewmodel.ViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class NewsBottomSheetFragment extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    public static final String KEY_NEWS_LIST = "list_news";
    public static final String KEY_NEWS_POSITION = "position";
    Delegate mDelegate;

    public static NewsBottomSheetFragment newInstance(ArrayList<News> list, int position) {
        NewsBottomSheetFragment newsBottomSheetFragment = new NewsBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_NEWS_LIST, list);
        bundle.putInt(KEY_NEWS_POSITION, position);

        newsBottomSheetFragment.setArguments(bundle);
        return newsBottomSheetFragment;
    }

    public static NewsBottomSheetFragment newInstance(News news) {
        ArrayList<News> list = new ArrayList<>(1);
        list.add(news);
        return newInstance(list, 0);
    }

    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialogTheme;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ccnews_bottom_sheet_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomSheetViewModel viewModel = new ViewModelProvider(this, new ViewModelFactory()).get(BottomSheetViewModel.class);

        viewModel.getEventListener().observe(getViewLifecycleOwner(), this::onActionChanged);

        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(1);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(KEY_NEWS_LIST)) {
            List<News> newsList = (List<News>) bundle.getSerializable(KEY_NEWS_LIST);
            if (newsList == null) return;
            viewModel.setNewsList(newsList);
            NewsBottomSheetAdapter adapter = new NewsBottomSheetAdapter(this, newsList);
            viewPager.setAdapter(adapter);
            viewPager.setUserInputEnabled(newsList.size() > 1);

            viewModel.setCurrentPosition(bundle.getInt(KEY_NEWS_POSITION, 0));
            viewPager.setCurrentItem(viewModel.getCurrentPosition(), false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if (dialog == null) return;
        View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
        bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
        BottomSheetBehavior<FrameLayout> bottomSheetBehavior =
                (BottomSheetBehavior<FrameLayout>) params.getBehavior();
        if (bottomSheetBehavior == null) return;
        bottomSheetBehavior.setSkipCollapsed(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                bottomSheet.toString();
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                bottomSheet.toString();
            }
        });
    }

    private void onActionChanged(BottomSheetViewModel.Event event) {
        if (event.isNotDone()) {
            event.done();
            NewsBottomSheetFragment.this.dismiss();
        }
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    public interface Delegate {
        void share(News news);

        void openInNewTab(News news);

        void bookmarks(News news);
    }
}
