package me.roinujnosde.wolfbot.listeners;

import me.roinujnosde.wolfbot.WolfBot;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class NoBotSpam extends Listener {

    public NoBotSpam(WolfBot bot) {
        super(bot);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.isFromGuild()) {
            return;
        }

        for (MessageEmbed embed : event.getMessage().getEmbeds()) {
            String title = embed.getTitle();
            if (title != null && title.contains("release\\-please\\-\\-branches\\-\\-")) {
                event.getMessage().delete().queue();
                return;
            }
        }
    }
}
