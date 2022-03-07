package me.roinujnosde.wolfbot.listeners;

import me.roinujnosde.wolfbot.WolfBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

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
        jda.retrieveCommands().queue(list -> {
            Guild guild = jda.getGuilds().get(0);
            Role role = guild.getRoleById(config.getAdminRole());
            if (role == null) return;

            String commandId = getCommandId(list, "admin");
            if (commandId == null) return;

            guild.updateCommandPrivileges(Map.of(commandId, List.of(CommandPrivilege.enable(role)))).queue();

        });

    }

    @SuppressWarnings("SameParameterValue")
    private String getCommandId(List<Command> commands, String name) {
        for (Command command : commands) {
            if (command.getName().equalsIgnoreCase(name)) {
                return command.getId();
            }
        }
        return null;
    }
}
