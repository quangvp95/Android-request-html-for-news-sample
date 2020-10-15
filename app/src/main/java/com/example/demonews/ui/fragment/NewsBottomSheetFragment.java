package com.example.demonews.ui.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
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

public class NewsBottomSheetFragment extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    public static final String LIST_NEWS_TAG = "list_news";
    public static final String POSITION_TAG = "position";
    ArrayList<News> mList = null;
    int mPos = -1;
    Delegate mDelegate;

    public static NewsBottomSheetFragment newInstance(ArrayList<News> list, int position) {
        NewsBottomSheetFragment newsBottomSheetFragment = new NewsBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LIST_NEWS_TAG, list);
        bundle.putSerializable(POSITION_TAG, position);

        newsBottomSheetFragment.setArguments(bundle);
        return newsBottomSheetFragment;
    }

    public static NewsBottomSheetFragment newInstance(News news) {
        ArrayList<News> list = new ArrayList<>(1);
        list.add(news);
        return newInstance(list, 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.bottom_sheet, container, false);

        ViewPager2 viewPager = inflate.findViewById(R.id.viewPager);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mList = (ArrayList<News>) bundle.getSerializable(LIST_NEWS_TAG);
            mPos = bundle.getInt(POSITION_TAG);
            NewsBottomSheetAdapter adapter = new NewsBottomSheetAdapter(this, mList);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(mPos, false);
        }

        BottomSheetViewModel viewModel = new ViewModelProvider(requireActivity(),
                new ViewModelFactory()).get(
                BottomSheetViewModel.class);
        viewModel.getOpened().observe(getViewLifecycleOwner(), event -> {
            if (!event.isDone()) {
                event.done();
                dismiss();
            }
        });
        viewModel.getClosed().observe(getViewLifecycleOwner(), event -> {
            if (!event.isDone()) {
                event.done();
                dismiss();
            }
        });
        return inflate;
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
