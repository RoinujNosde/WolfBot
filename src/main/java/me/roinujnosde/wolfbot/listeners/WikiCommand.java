package me.roinujnosde.wolfbot.listeners;

import me.roinujnosde.wolfbot.WolfBot;
import me.roinujnosde.wolfbot.models.SearchItem;
import me.roinujnosde.wolfbot.models.SearchResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;

public class WikiCommand extends Listener {

    private static final String WIKI_BASE_URL = "https://wiki.roinujnosde.me/%s";
    private static final String WIKI_CONTENT_URL = "https://wiki.roinujnosde.me/%s/%s";
    private static final String SEARCH_URL = "https://api.gitbook.com/v1/spaces/%s/search?query=%s";

    public WikiCommand(WolfBot bot) {
        super(bot);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) return;
        if (!"wiki".equalsIgnoreCase(event.getName())) return;

        event.deferReply(false).queue();
        String project = getOption(event.getInteraction(), "project");
        String space = config.getWikiProjects().get(project);
        String query = getOption(event.getInteraction(), "keywords");

        InteractionHook hook = event.getHook();

        try {
            SearchResult result = getSearchResult(space, query);
            if (result.getItems().isEmpty()) {
                hook.setEphemeral(true).sendMessage("Your keywords returned 0 results!").queue();
                return;
            }

            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.YELLOW)
                    .setTitle(String.format("%s's Wiki", project), getBaseUrl(project))
                    .setDescription("Found the following page(s) for you:");
            for (SearchItem item : result.getItems()) {
                embedBuilder.addField(item.getTitle(), getContentUrl(project, item.getUrl()), true);
            }
            hook.sendMessageEmbeds(embedBuilder.build()).queue();
        } catch (IOException ex) {
            hook.sendMessage("An error occurred while searching the wiki!").queue();
            bot.getLogger().log(Level.SEVERE, "Error searching wiki", ex);
        }
    }

    private String getBaseUrl(String project) {
        return String.format(WIKI_BASE_URL, project.toLowerCase(Locale.ROOT));
    }

    private SearchResult getSearchResult(String space, String query) throws IOException {
        URL url = new URL(String.format(SEARCH_URL, space, query));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", String.format("Bearer %s", config.getGitbookToken()));

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        SearchResult result = bot.getGson().fromJson(reader, SearchResult.class);
        reader.close();
        return result;
    }

    private String getContentUrl(String project, String pageUrl) {
        return String.format(WIKI_CONTENT_URL, project.toLowerCase(Locale.ROOT), pageUrl);
    }
}
