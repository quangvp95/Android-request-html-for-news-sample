package com.example.demonews.ui.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class CustomBottomSheetBehavior extends BottomSheetBehavior {

    public CustomBottomSheetBehavior() {}

    public CustomBottomSheetBehavior(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
        setSkipCollapsed(true);
        setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child,
            @NonNull View dependency) {
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child,
            @NonNull MotionEvent event) {
        return super.onTouchEvent(parent, child, event);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child,
            @NonNull MotionEvent event) {
        return super.onInterceptTouchEvent(parent, child, event);
    }
}

