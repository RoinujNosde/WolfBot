package me.roinujnosde.wolfbot.tasks;

import me.roinujnosde.wolfbot.HttpHelper;
import me.roinujnosde.wolfbot.WolfBot;
import me.roinujnosde.wolfbot.listeners.AddonCommand;
import me.roinujnosde.wolfbot.models.spiget.Author;
import me.roinujnosde.wolfbot.models.spiget.Resource;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.io.IOException;
import java.util.Objects;
import java.util.TimerTask;
import java.util.logging.Level;

import static me.roinujnosde.wolfbot.listeners.AddonCommand.SPIGET_AUTHORS_URL;
import static me.roinujnosde.wolfbot.listeners.AddonCommand.SPIGET_RESOURCES_URL;

public class UpdateAddons extends TimerTask {

    private final WolfBot bot;

    public UpdateAddons(WolfBot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        bot.getLogger().info("Running UpdateAddons");
        TextChannel channel = bot.getJda().getTextChannelById(bot.getConfig().getAddonsChannel());

        for (Message message : Objects.requireNonNull(channel).getIterableHistory()) {
            if (message.getEmbeds().isEmpty() || message.getActionRows().isEmpty()) {
                continue;
            }
            Button button = (Button) message.getActionRows().get(0).getComponents().get(0);
            String url = Objects.requireNonNull(button.getUrl());
            long id = Long.parseLong(url.replace("https://www.spigotmc.org/resources/", ""));

            try {
                Resource resource = HttpHelper.get(Resource.class, SPIGET_RESOURCES_URL, id);
                if (resource == null) {
                    return;
                }
                Author author = HttpHelper.get(Author.class, SPIGET_AUTHORS_URL, id);
                if (author == null) {
                    return;
                }
                message.editMessageEmbeds(AddonCommand.buildEmbed(resource, author)).queue();
                Thread.sleep(10000);
            } catch (InterruptedException | IOException ex) {
                bot.getLogger().log(Level.SEVERE, String.format("Error updating addon %d", id), ex);
            }
        }
    }
}
