package com.example.demonews.ui.fragment;

import static com.example.demonews.ui.fragment.NewsItemFragment.NEWS_TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.demonews.R;
import com.example.demonews.entity.News;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class NewsBottomSheetFragment extends BottomSheetDialogFragment {
    public interface Delegate {
        void refresh(News news);
        void share(News news);
        void openInNewTab(News news);
        void bookmarks(News news);
    }

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

        return inflate;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
//
//                FrameLayout bottomSheet =
//                        d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
//
//                if (bottomSheet == null) return;
//                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
//                behavior.setSkipCollapsed(true);
//                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if (dialog == null) return;
        final View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
        bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

        final View view = getView();
        if (view == null) return;
        view.post(new Runnable() {
            @Override
            public void run() {
                View parent = (View) view.getParent();
                CoordinatorLayout.LayoutParams params =
                        (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
                BottomSheetBehavior bottomSheetBehavior =
                        (BottomSheetBehavior) params.getBehavior();
                bottomSheetBehavior.setSkipCollapsed(true);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior.setPeekHeight(view.getMeasuredHeight());
                ((View) bottomSheet.getParent()).setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }
}
