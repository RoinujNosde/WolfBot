package me.roinujnosde.wolfbot.models.gitbook;

import java.util.List;


public class SearchResult {

    private final List<SearchItem> items;

    public SearchResult(List<SearchItem> items) {
        this.items = items;
    }

    public List<SearchItem> getItems() {
        return items;
    }
}
