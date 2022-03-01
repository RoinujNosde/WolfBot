package me.roinujnosde.wolfbot.listeners;

import me.roinujnosde.wolfbot.WolfBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LanguageRole extends ListenerAdapter {

    private final WolfBot bot;

    public LanguageRole(WolfBot bot) {
        this.bot = bot;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) return;

        if (!"admin".equalsIgnoreCase(event.getName())) {
            return;
        }
        if (!"language-message".equalsIgnoreCase(event.getSubcommandName())) {
            return;
        }
        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        MessageChannel channel = event.getChannel();
        if (!channel.canTalk()) {
            hook.sendMessage("I can't talk in this channel!").queue();
            return;
        }

        MessageEmbed embed = new EmbedBuilder().setTitle("Language Selector")
                .setDescription("Click on the buttons below to toggle your language roles!").build();

        hook.sendMessage("Creating your message...").queue();
        channel.sendMessageEmbeds(embed).setActionRows(ActionRow.of(Button.primary("portuguese", "Português")
                .withEmoji(Emoji.fromUnicode("U+1F1E7U+1F1F7")), Button.primary("russian", "Pусский")
                .withEmoji(Emoji.fromUnicode("U+1F1F7U+1F1FA")), Button.primary("turkish", "Türkçe")
                .withEmoji(Emoji.fromUnicode("U+1F1F9U+1F1F7")))
        ).queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getGuild() == null) return;

        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();

        String componentId = event.getComponentId();
        String roleId = bot.getConfig().getLanguageRoles().get(componentId);
        Role role;

        if (roleId == null || (role = event.getGuild().getRoleById(roleId)) == null) {
            bot.getLogger().severe(String.format("No role found for %s!", componentId));
            hook.sendMessage("Error toggling your role!").queue();
            return;
        }

        Member member = Objects.requireNonNull(event.getMember());
        if (member.getRoles().contains(role)) {
            event.getGuild().removeRoleFromMember(member, role).queue();
            hook.sendMessage("Removed your role!").queue();
        } else {
            event.getGuild().addRoleToMember(member, role).queue();
            hook.sendMessage("Added your role!").queue();
        }

    }

}
