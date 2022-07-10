package me.roinujnosde.wolfbot.models.gitbook;

import java.util.List;

public class SearchItem {

    private final String title;
    private final String path;
    private final List<Section> sections;

    public SearchItem(String title, String path, List<Section> sections) {
        this.title = title;
        this.path = path;
        this.sections = sections;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public List<Section> getSections() {
        return sections;
    }

    public static class Section {

        private final String title;
        private final String body;
        private final String path;

        public Section(String title, String body, String path) {
            this.title = title;
            this.body = body;
            this.path = path;
        }

        public String getTitle() {
            return title;
        }

        public String getBody() {
            return body;
        }

        public String getPath() {
            return path;
        }
    }
}
