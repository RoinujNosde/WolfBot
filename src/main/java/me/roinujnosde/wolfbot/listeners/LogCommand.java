package me.roinujnosde.wolfbot.listeners;

import me.roinujnosde.wolfbot.WolfBot;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class LogCommand extends Listener {

    public LogCommand(WolfBot bot) {
        super(bot);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) {
            return;
        }
        if (!"log".equalsIgnoreCase(event.getName())) {
            return;
        }
        event.getInteraction().deferReply(true).queue();
        InteractionHook hook = event.getHook();

        TextChannel logsChannel = requireNonNull(event.getGuild().getTextChannelById(bot.getConfig().getLogsChannel()));
        Attachment attachment = requireNonNull(event.getOption("attachment")).getAsAttachment();

        try {
            File file = attachment.downloadToFile().get();
            logsChannel.sendFile(file).content(format("File sent by %s on channel %s", event.getUser().getAsMention(),
                    event.getChannel().getAsMention())).submit().thenRun(() -> {
                try {
                    Files.delete(file.toPath());
                } catch (IOException ex) {
                    bot.getLogger().log(Level.SEVERE, "Error while deleting the file", ex);
                }
            });
        } catch (InterruptedException | ExecutionException ex) {
            hook.editOriginal("Error while sending your log!").queue();
            bot.getLogger().log(Level.SEVERE, "Error while sending log", ex);
        }
        hook.editOriginal("Thanks! Now wait for someone to take a look at it...").queue();
    }

}
