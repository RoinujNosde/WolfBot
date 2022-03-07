package me.roinujnosde.wolfbot.models;

import java.util.List;

@SuppressWarnings("unused")
public class SearchItem {

    private String title;
    private String url;
    private List<Section> sections;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public List<Section> getSections() {
        return sections;
    }

    public static class Section {

        private String title;
        private String body;
        private String url;

        public String getTitle() {
            return title;
        }

        public String getBody() {
            return body;
        }

        public String getUrl() {
            return url;
        }
    }
}
