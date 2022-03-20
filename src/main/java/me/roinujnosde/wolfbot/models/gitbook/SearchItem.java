package me.roinujnosde.wolfbot.models.gitbook;

import java.util.List;

public class SearchItem {

    private final String title;
    private final String url;
    private final List<Section> sections;

    public SearchItem(String title, String url, List<Section> sections) {
        this.title = title;
        this.url = url;
        this.sections = sections;
    }

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

        private final String title;
        private final String body;
        private final String url;

        public Section(String title, String body, String url) {
            this.title = title;
            this.body = body;
            this.url = url;
        }

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
