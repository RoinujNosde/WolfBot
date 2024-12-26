package me.roinujnosde.wolfbot.listeners;

import me.roinujnosde.wolfbot.WolfBot;
import me.roinujnosde.wolfbot.tasks.UpdateAddons;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.jetbrains.annotations.NotNull;

import java.time.temporal.ChronoUnit;
import java.util.Timer;

public class ReadyListener extends Listener {

    public ReadyListener(WolfBot bot) {
        super(bot);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        JDA jda = event.getJDA();
        bot.getLogger().info(String.format("Logged as %s, Guilds %d", jda.getSelfUser().getName(),
                jda.getGuildCache().size()));
        if (jda.getGuildCache().isEmpty()) {
            return;
        }

        new Timer().schedule(new UpdateAddons(bot), 0, ChronoUnit.DAYS.getDuration().toMillis());
    }

}
