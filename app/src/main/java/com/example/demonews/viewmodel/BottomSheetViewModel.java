package com.example.demonews.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demonews.entity.News;

import java.util.List;

public class BottomSheetViewModel extends ViewModel {
    private final MutableLiveData<Event> eventListener = new MutableLiveData<>();
    private List<News> newsList;
    private int currentPosition = -1;

    public LiveData<Event> getEventListener() {
        return eventListener;
    }

    public void action(Event item) {
        eventListener.setValue(item);
    }

    public News getNewsAtIndex(int index) {
        if (newsList == null || newsList.size() == 0)
            return null;
        return newsList.get(index % newsList.size());
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public static class Event {
        EventType mType;
        boolean isDone;

        public Event(EventType type) {
            this.mType = type;
            isDone = false;
        }

        public void done() {
            isDone = true;
        }

        public boolean isNotDone() {
            return !isDone;
        }
    }

    public enum EventType {
        OPEN, CLOSE, SHARE, BOOKMARK
    }
}
