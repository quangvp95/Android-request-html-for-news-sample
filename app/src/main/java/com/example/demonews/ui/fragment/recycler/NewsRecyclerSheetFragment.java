package com.example.demonews.ui.fragment.recycler;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.demonews.R;
import com.example.demonews.entity.News;
import com.example.demonews.ui.fragment.Delegate;
import com.example.demonews.ui.fragment.recycler.CCNArticleDetailHolder.RecyclerActionListener;
import com.example.demonews.viewmodel.BottomSheetViewModel;
import com.example.demonews.viewmodel.ViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class NewsRecyclerSheetFragment extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    public static final String KEY_NEWS_LIST = "list_news";
    public static final String KEY_NEWS_POSITION = "position";
    Delegate mDelegate;
    RecyclerActionListener mActionListener = (position, view1, viewHolder) -> {

    };

    public static NewsRecyclerSheetFragment newInstance(ArrayList<News> list, int position) {
        NewsRecyclerSheetFragment NewsRecyclerSheetFragment = new NewsRecyclerSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_NEWS_LIST, list);
        bundle.putInt(KEY_NEWS_POSITION, position);

        NewsRecyclerSheetFragment.setArguments(bundle);
        return NewsRecyclerSheetFragment;
    }

    public static NewsRecyclerSheetFragment newInstance(News news) {
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ccnews_recycler_sheet_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomSheetViewModel viewModel = new ViewModelProvider(this, new ViewModelFactory()).get(
                BottomSheetViewModel.class);

        viewModel.getEventListener().observe(getViewLifecycleOwner(), this::onActionChanged);

        RecyclerView recyclerView = view.findViewById(R.id.news_recycler);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(KEY_NEWS_LIST)) {
            List<News> newsList = (List<News>) bundle.getSerializable(KEY_NEWS_LIST);
            if (newsList == null) return;
            viewModel.setNewsList(newsList);
            NewsRecyclerSheetAdapter adapter = new NewsRecyclerSheetAdapter(newsList,
                    mActionListener);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(3);

            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);

            viewModel.setCurrentPosition(bundle.getInt(KEY_NEWS_POSITION, 0));
            layoutManager.scrollToPosition(viewModel.getCurrentPosition());
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
            dismiss();
        }
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    static class NewsRecyclerSheetAdapter extends RecyclerView.Adapter<CCNArticleDetailHolder> {
        private List<News> mList;
        protected RecyclerActionListener actionListener;

        public NewsRecyclerSheetAdapter(List<News> list, RecyclerActionListener actionListener) {
            mList = list;
            this.actionListener = actionListener;
        }

        @NonNull
        @Override
        public CCNArticleDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.ccnews_bottom_sheet_item_layout, parent, false);
            CCNArticleDetailHolder viewHolder = new CCNArticleDetailHolder(view);
            viewHolder.setupClickableViews(actionListener);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CCNArticleDetailHolder holder, int position) {
            News item = mList.get(position);
            holder.bindViewHolder(item);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }
}
