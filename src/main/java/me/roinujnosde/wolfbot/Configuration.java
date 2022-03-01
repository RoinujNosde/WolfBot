package me.roinujnosde.wolfbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class Configuration {

    private String token = "BOT_TOKEN";
    private String suggestionsChannel = "CHANNEL_ID";
    private List<String> suggestionEmotes = new ArrayList<>();
    private List<String> projects = new ArrayList<>();
    private String adminRole = "ADMIN_ROLE_ID";
    private Map<String, String> languageRoles = new HashMap<>();

    public String getToken() {
        return token;
    }

    public String getSuggestionsChannel() {
        return suggestionsChannel;
    }

    public List<String> getProjects() {
        return projects;
    }

    public boolean isProject(String name) {
        for (String p : projects) {
            if (p.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public String getProjectFixedCase(String name) {
        for (String p : projects) {
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
}
