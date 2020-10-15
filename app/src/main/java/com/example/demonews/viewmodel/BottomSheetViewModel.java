package com.example.demonews.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demonews.entity.News;

public class BottomSheetViewModel extends ViewModel {
    private final MutableLiveData<Event> opened = new MutableLiveData<>();
    private final MutableLiveData<Event> closed = new MutableLiveData<>();
    private final MutableLiveData<Event> bookmarked = new MutableLiveData<>();
    private final MutableLiveData<Event> shared = new MutableLiveData<>();

    public void open(Event item) {
        opened.setValue(item);
    }

    public LiveData<Event> getOpened() {
        return opened;
    }

    public void close(Event item) {
        closed.setValue(item);
    }

    public LiveData<Event> getClosed() {
        return closed;
    }

    public void bookmark(Event item) {
        bookmarked.setValue(item);
    }

    public LiveData<Event> getBookmarked() {
        return bookmarked;
    }

    public void share(Event item) {
        shared.setValue(item);
    }

    public LiveData<Event> getShared() {
        return shared;
    }

    public static class Event {
        News news;
        boolean isDone;

        public Event(News news) {
            this.news = news;
            isDone = false;
        }

        public void done() {
            isDone = true;
        }

        public boolean isDone() {
            return isDone;
        }
    }
}
