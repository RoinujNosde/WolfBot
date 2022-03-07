package me.roinujnosde.wolfbot.listeners;

import me.roinujnosde.wolfbot.Configuration;
import me.roinujnosde.wolfbot.WolfBot;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Listener extends ListenerAdapter {

    protected final WolfBot bot;
    protected final Configuration config;

    public Listener(WolfBot bot) {
        this.bot = bot;
        this.config = bot.getConfig();
    }

    @NotNull
    protected String getOption(CommandInteractionPayload payload, String name) {
        return Objects.requireNonNull(payload.getOption(name)).getAsString();
    }
}
