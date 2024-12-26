package me.roinujnosde.wolfbot.listeners;

import me.roinujnosde.wolfbot.WolfBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClearCommand extends Listener {

    public ClearCommand(WolfBot bot) {
        super(bot);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) return;
        if (!"admin".equalsIgnoreCase(event.getName())) return;
        if (!"clear".equalsIgnoreCase(event.getSubcommandName())) return;

        int count = Objects.requireNonNull(event.getOption("count")).getAsInt();

        event.deferReply(true).queue();

        GuildMessageChannel guildChannel = event.getGuildChannel();
        List<Message> messages = guildChannel.getIterableHistory().stream().limit(count).collect(Collectors.toList());
        guildChannel.deleteMessages(messages).queue();
        event.getHook().sendMessage("Deleted the messages for you!").queue();
    }
}
