package com.example.demonews.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.demonews.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class NewsBottomSheetFragment extends BottomSheetDialogFragment {

    public static NewsBottomSheetFragment newInstance() {
        return new NewsBottomSheetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet, container, false);
    }


}
