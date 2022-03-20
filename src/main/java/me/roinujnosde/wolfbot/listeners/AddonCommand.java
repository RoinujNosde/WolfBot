package me.roinujnosde.wolfbot.listeners;

import me.roinujnosde.wolfbot.WolfBot;
import me.roinujnosde.wolfbot.models.spiget.Author;
import me.roinujnosde.wolfbot.models.spiget.Icon;
import me.roinujnosde.wolfbot.models.spiget.Resource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;

public class AddonCommand extends Listener {

    private static final String USER_AGENT = "WolfBot";
    private static final String SPIGET_RESOURCES_URL = "https://api.spiget.org/v2/resources/%d";
    private static final String SPIGET_AUTHORS_URL = "https://api.spiget.org/v2/resources/%d/author";
    private static final String SPIGOT_BASE_URL = "https://www.spigotmc.org/";
    private static final String SPIGOT_PURCHASE_URL = "https://www.spigotmc.org/resources/%d/purchase";
    private static final String SPIGOT_RESOURCES_URL = "https://www.spigotmc.org/resources/%d";
    private static final String SPIGOT_AUTHORS_URL = "https://www.spigotmc.org/resources/authors/%d";

    public AddonCommand(WolfBot bot) {
        super(bot);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) return;
        if (!event.getName().equalsIgnoreCase("addon")) return;

        event.getInteraction().deferReply(true).queue();
        InteractionHook hook = event.getHook();
        TextChannel channel = event.getGuild().getTextChannelById(bot.getConfig().getAddonsChannel());

        if (channel == null || !channel.canTalk()) {
            hook.sendMessage("Addons channel not found!").queue();
            return;
        }

        long id = Objects.requireNonNull(event.getOption("spigot-id")).getAsLong();

        try {
            Resource resource = get(SPIGET_RESOURCES_URL, Resource.class, id);
            if (resource == null) {
                hook.sendMessage("Resource not found!").queue();
                return;
            }
            Author author = get(SPIGET_AUTHORS_URL, Author.class, id);
            if (author == null) {
                hook.sendMessage("Author not found!").queue(); //is this even possible?
                return;
            }

            MessageEmbed embed = new EmbedBuilder().setTitle(resource.getName())
                    .setDescription(resource.getTag())
                    .setColor(Color.WHITE)
                    .setAuthor(author.getName(), getUrl(author), getUrl(author.getIcon()))
                    .setThumbnail(getUrl(resource.getIcon()))
                    .addField("Downloads", String.valueOf(resource.getDownloads()), true)
                    .addField("Rating", resource.getRating().toString(), true)
                    .addField("Bukkit version(s)", String.join(", ", resource.getTestedVersions()), true)
                    .addField("Price", resource.getPrice(), true)
                    .build();

            channel.sendMessageEmbeds(embed)
                    .setActionRow(
                            Button.link(getUrl(resource), "Project Page"),
                            getDownloadButton(resource))
                    .queue();
            hook.sendMessage("Addon posted successfully!").queue();

        } catch (IOException ex) {
            hook.sendMessage("An error occurred while fetching this addon...").queue();
            bot.getLogger().log(Level.SEVERE, "Error fetching addon", ex);
        }
    }

    @NotNull
    private Button getDownloadButton(Resource resource) {
        String label = resource.isPremium() ? "Buy" : "Download";

        return Button.link(getDownloadUrl(resource), label);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        System.out.println(event.getButton());
    }

    private @Nullable <T> T get(final String apiUrl, Class<T> clazz, final long id) throws IOException {
        URL url = new URL(String.format(apiUrl, id));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.addRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == 404) {
            return null;
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        return bot.getGson().fromJson(reader, clazz);
    }

    private String getDownloadUrl(Resource resource) {
        if (resource.isPremium()) {
            return String.format(SPIGOT_PURCHASE_URL, resource.getId());
        }
        return SPIGOT_BASE_URL + resource.getFile().getUrl();
    }

    private String getUrl(Resource resource) {
        return String.format(SPIGOT_RESOURCES_URL, resource.getId());
    }

    private String getUrl(Author author) {
        return String.format(SPIGOT_AUTHORS_URL, author.getId());
    }

    private String getUrl(Icon icon) {
        String url = icon.getUrl();
        if (url.startsWith(SPIGOT_BASE_URL)) { // API inconsistency
            return url;
        } else {
            return SPIGOT_BASE_URL + url;
        }
    }

}
