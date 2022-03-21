package me.roinujnosde.wolfbot.listeners;

import me.roinujnosde.wolfbot.HttpHelper;
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

import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

public class AddonCommand extends Listener {

    public static final String SPIGET_RESOURCES_URL = "https://api.spiget.org/v2/resources/%d";
    public static final String SPIGET_AUTHORS_URL = "https://api.spiget.org/v2/resources/%d/author";
    private static final String SPIGOT_BASE_URL = "https://www.spigotmc.org/";
    private static final String SPIGOT_PURCHASE_URL = "https://www.spigotmc.org/resources/%d/purchase";
    private static final String SPIGOT_RESOURCES_URL = "https://www.spigotmc.org/resources/%d";
    private static final String SPIGOT_AUTHORS_URL = "https://www.spigotmc.org/resources/authors/%d";
    private static final String SPIGOT_DEFAULT_RESOURCE_ICON = "https://i.imgur.com/mOlGeMz.png";
    private static final String SPIGOT_DEFAULT_AUTHOR_ICON = "https://i.imgur.com/l8N7BR6.png";

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
            Resource resource = HttpHelper.get(Resource.class, SPIGET_RESOURCES_URL, id);
            if (resource == null) {
                hook.sendMessage("Resource not found!").queue();
                return;
            }
            Author author = HttpHelper.get(Author.class, SPIGET_AUTHORS_URL, id);
            if (author == null) {
                hook.sendMessage("Author not found!").queue(); //is this even possible?
                return;
            }

            channel.sendMessageEmbeds(buildEmbed(resource, author))
                    .setActionRow(Button.link(getUrl(resource), "Project Page"), getDownloadButton(resource))
                    .queue();
            hook.sendMessage("Addon posted successfully!").queue();

        } catch (IOException ex) {
            hook.sendMessage("An error occurred while fetching this addon...").queue();
            bot.getLogger().log(Level.SEVERE, "Error fetching addon", ex);
        }
    }

    @NotNull
    public static MessageEmbed buildEmbed(Resource resource, Author author) {
        return new EmbedBuilder().setTitle(resource.getName())
                .setDescription(resource.getTag())
                .setColor(Color.WHITE)
                .setAuthor(author.getName(), getUrl(author), getIconUrl(author))
                .setThumbnail(getIconUrl(resource))
                .addField("Downloads", String.valueOf(resource.getDownloads()), true)
                .addField("Rating", resource.getRating().toString(), true)
                .addField("Bukkit version(s)", String.join(", ", resource.getTestedVersions()), true)
                .addField("Price", resource.getPrice(), true)
                .build();
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

    private String getDownloadUrl(Resource resource) {
        if (resource.isPremium()) {
            return String.format(SPIGOT_PURCHASE_URL, resource.getId());
        }
        return SPIGOT_BASE_URL + resource.getFile().getUrl();
    }

    private String getUrl(Resource resource) {
        return String.format(SPIGOT_RESOURCES_URL, resource.getId());
    }

    private static String getUrl(Author author) {
        return String.format(SPIGOT_AUTHORS_URL, author.getId());
    }

    private static String getIconUrl(Resource resource) {
        String url = resource.getIcon().getUrl();
        if (url.isEmpty()) {
            return SPIGOT_DEFAULT_RESOURCE_ICON;
        }
        return SPIGOT_BASE_URL + url;
    }

    private static String getIconUrl(Author author) {
        Icon icon = author.getIcon();
        if (icon == null) {
            return SPIGOT_DEFAULT_AUTHOR_ICON;
        }
        return icon.getUrl();
    }

}
