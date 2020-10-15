package com.example.demonews.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BottomSheetViewModel.class)) {
            BottomSheetViewModel viewModel = new BottomSheetViewModel();
            return (T) viewModel;
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
