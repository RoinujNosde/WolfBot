package me.roinujnosde.wolfbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class Configuration {

    private String token = "BOT_TOKEN";
    private String gitbookToken = "GITBOOK_TOKEN";
    private String suggestionsChannel = "CHANNEL_ID";
    private String addonsChannel = "CHANNEL_ID";
    private String logsChannel = "CHANNEL_ID";
    private List<String> suggestionEmotes = new ArrayList<>();
    private List<String> suggestionProjects = new ArrayList<>();
    private Map<String, String> wikiProjects = new HashMap<>();
    private String adminRole = "ADMIN_ROLE_ID";
    private Map<String, String> languageRoles = new HashMap<>();

    public String getToken() {
        return token;
    }

    public String getSuggestionsChannel() {
        return suggestionsChannel;
    }

    public String getAddonsChannel() {
        return addonsChannel;
    }

    public String getLogsChannel() {
        return logsChannel;
    }

    public List<String> getSuggestionProjects() {
        return suggestionProjects;
    }

    public String getProjectFixedCase(String name) {
        for (String p : suggestionProjects) {
            if (p.equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public List<String> getSuggestionEmotes() {
        return suggestionEmotes;
    }

    public String getAdminRole() {
        return adminRole;
    }

    public Map<String, String> getLanguageRoles() {
        return languageRoles;
    }

    public Map<String, String> getWikiProjects() {
        return wikiProjects;
    }

    public String getGitbookToken() {
        return gitbookToken;
    }
}
