package com.example.demonews.ui.fragment.pager;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.demonews.R;
import com.example.demonews.entity.News;
import com.example.demonews.ui.fragment.Delegate;
import com.example.demonews.viewmodel.BottomSheetViewModel;
import com.example.demonews.viewmodel.ViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class NewsPagerSheetFragment extends BottomSheetDialogFragment {
    public static final String KEY_NEWS_LIST = "list_news";
    public static final String KEY_NEWS_POSITION = "position";
    Delegate mDelegate;

    public static NewsPagerSheetFragment newInstance(ArrayList<News> list, int position) {
        NewsPagerSheetFragment newsPagerSheetFragment = new NewsPagerSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_NEWS_LIST, list);
        bundle.putInt(KEY_NEWS_POSITION, position);

        newsPagerSheetFragment.setArguments(bundle);
        return newsPagerSheetFragment;
    }

    public static NewsPagerSheetFragment newInstance(News news) {
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
        return inflater.inflate(R.layout.ccnews_pager_sheet_fragment, container, false);
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
            NewsPagerSheetAdapter adapter = new NewsPagerSheetAdapter(this, newsList);
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
    }

    private void onActionChanged(BottomSheetViewModel.Event event) {
        if (event.isNotDone()) {
            event.done();
            NewsPagerSheetFragment.this.dismiss();
        }
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    static class NewsPagerSheetAdapter extends FragmentStateAdapter {
        private List<News> mList;
        public NewsPagerSheetAdapter(@NonNull Fragment fragment, List<News> newsList) {
            super(fragment);
            mList = newsList;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            NewsPagerItemFragment fragment = new NewsPagerItemFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(NewsPagerItemFragment.POSITION_TAG, position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }
}
