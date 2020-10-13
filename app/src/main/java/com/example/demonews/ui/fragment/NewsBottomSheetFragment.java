package com.example.demonews.ui.fragment;

import static com.example.demonews.ui.fragment.NewsItemFragment.NEWS_TAG;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.example.demonews.R;
import com.example.demonews.entity.News;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class NewsBottomSheetFragment extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    public static final String LIST_NEWS_TAG = "list_news";
    public static final String POSITION_TAG = "position";

    ArrayList<News> mList = null;
    int mPos = -1;

    public static NewsBottomSheetFragment newInstance(ArrayList<News> list, int position) {
        NewsBottomSheetFragment newsBottomSheetFragment = new NewsBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LIST_NEWS_TAG, list);
        bundle.putSerializable(POSITION_TAG, position);

        newsBottomSheetFragment.setArguments(bundle);
        return newsBottomSheetFragment;
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
            viewPager.setCurrentItem(mPos);
        }

        return inflate;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}
