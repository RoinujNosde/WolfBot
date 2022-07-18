package me.roinujnosde.wolfbot.listeners;

import me.roinujnosde.wolfbot.HttpHelper;
import me.roinujnosde.wolfbot.WolfBot;
import me.roinujnosde.wolfbot.models.gitbook.SearchItem;
import me.roinujnosde.wolfbot.models.gitbook.SearchResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
        String query = getOption(event.getInteraction(), "keywords").trim();

        InteractionHook hook = event.getHook();

        try {
            SearchResult result = HttpHelper.get(SearchResult.class, getProperties(), SEARCH_URL, space, query);
            if (result == null || result.getItems().isEmpty()) {
                hook.sendMessage("Your keywords returned 0 results!").setEphemeral(true).queue();
                return;
            }

            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.YELLOW)
                    .setTitle(String.format("%s's Wiki", project), getBaseUrl(project))
                    .setDescription("Found the following page(s) for you:");
            for (SearchItem item : result.getItems()) {
                if (!item.getTitle().equalsIgnoreCase(query)) {
                    continue;
                }
                embedBuilder.addField(item.getTitle(), getContentUrl(project, item.getPath()), true);
            }
            if (embedBuilder.getFields().isEmpty()) {
                hook.sendMessage("Make sure to pick one of the suggested titles!").setEphemeral(true).queue();
                return;
            }
            hook.sendMessageEmbeds(embedBuilder.build()).queue();
        } catch (IOException ex) {
            hook.sendMessage("An error occurred while searching the wiki!").queue();
            bot.getLogger().log(Level.SEVERE, "Error searching wiki", ex);
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if (event.getGuild() == null) return;
        if (!"wiki".equalsIgnoreCase(event.getName())) return;
        if (!"keywords".equalsIgnoreCase(event.getFocusedOption().getName())) return;

        OptionMapping projectOption = event.getOption("project");
        String value = event.getFocusedOption().getValue();

        if (projectOption == null || value.isEmpty()) {
            event.replyChoiceStrings().queue();
            return;
        }
        String project = projectOption.getAsString();
        String space = config.getWikiProjects().get(project);

        try {
            SearchResult searchResult = HttpHelper.get(SearchResult.class, getProperties(), SEARCH_URL, space, value);
            if (searchResult == null || searchResult.getItems().isEmpty()) {
                event.replyChoiceStrings().queue();
                return;
            }
            event.replyChoiceStrings(searchResult.getItems().stream().map(SearchItem::getTitle).collect(Collectors.toSet())).queue();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @NotNull
    private Map<String, String> getProperties() {
        return Map.of("Authorization", String.format("Bearer %s", config.getGitbookToken()));
    }

    private String getBaseUrl(String project) {
        return String.format(WIKI_BASE_URL, project.toLowerCase(Locale.ROOT));
    }

    private String getContentUrl(String project, String pageUrl) {
        return String.format(WIKI_CONTENT_URL, project.toLowerCase(Locale.ROOT), pageUrl);
    }
}
