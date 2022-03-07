package me.roinujnosde.wolfbot.listeners;

import me.roinujnosde.wolfbot.WolfBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.logging.Level;

public class SuggestCommand extends Listener {

    public SuggestCommand(WolfBot bot) {
        super(bot);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if ("suggest".equals(event.getName())) {
            processSuggest(event);
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if ("suggest".equals(event.getName()) &&
                event.getFocusedOption().getName().equalsIgnoreCase("project") ) {
            event.getInteraction().replyChoiceStrings(config.getSuggestionProjects()).queue();
        }
    }

    private void processSuggest(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        if (event.getGuild() == null) {
            return;
        }
        User user = event.getUser();

        String project = getOption(event, "project");
        String suggestion = getOption(event, "suggestion");

        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        if (!bot.getConfig().isSuggestionsProject(project)) {
            hook.sendMessage(String.format("%s is not a valid project name!", project)).queue();
            return;
        }
        if (suggestion.length() < 5) {
            hook.sendMessage("Your suggestion is too short! Write at least 5 words.").queue();
            return;
        }

        TextChannel suggestionsChannel = event.getGuild().getTextChannelById(config.getSuggestionsChannel());
        if (suggestionsChannel == null || !suggestionsChannel.canTalk()) {
            bot.getLogger().warning("null suggestionsChannel or the bot can't talk");
            hook.sendMessage("An error occurred while creating the suggestion!").queue();
            return;
        }

        String avatar = user.getAvatarUrl() != null ? user.getAvatarUrl() : user.getDefaultAvatarUrl();
        MessageEmbed embed = new EmbedBuilder().setTitle(config.getProjectFixedCase(project))
                .setDescription(suggestion).setTimestamp(Instant.now()).setFooter(user.getAsTag(), avatar).build();
        suggestionsChannel.sendMessageEmbeds(embed).submit().thenCompose(message -> {
            RestAction<?> action = null;
            for (String emote : config.getSuggestionEmotes()) {
                if (action == null) {
                    action = message.addReaction(emote);
                } else {
                   action = action.and(message.addReaction(emote));
                }
            }
            //noinspection ConstantConditions
            return action.submit();
        }).whenComplete((o, throwable) -> {
            if (throwable == null) {
                hook.sendMessage("Thank you for the suggestion!").queue();
                return;
            }
            bot.getLogger().log(Level.SEVERE, "Error sending embed", throwable);
        });

    }

}
