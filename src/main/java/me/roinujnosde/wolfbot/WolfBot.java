package me.roinujnosde.wolfbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.roinujnosde.wolfbot.listeners.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class WolfBot {

    private JDA jda;
    private final Configuration config;
    private final Logger logger = Logger.getLogger("WolfBot");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public WolfBot() throws IOException {
        File file = new File("config.json");
        if (!file.exists()) {
            config = new Configuration();
        } else {
            FileReader reader = new FileReader(file);
            config = gson.fromJson(reader, Configuration.class);
            reader.close();
        }
        // writing new fields to file
        saveConfig();
    }

    public static void main(String... args) throws IOException, LoginException {
        WolfBot bot = new WolfBot();
        JDA jda = JDABuilder.createDefault(bot.config.getToken()).build();

        bot.setJda(jda);
    }

    public void setJda(JDA jda) {
        this.jda = jda;
        onEnable();
    }

    public void onEnable() {
        jda.addEventListener(new WikiCommand(this), new ClearCommand(this), new LanguageRole(this),
                new SuggestCommand(this), new AddonCommand(this), new ReadyListener(this));

        getLogger().info("Registered listeners");
        SlashCommandData wikiCommand = Commands.slash("wiki", "Searches the wiki")
                .addOptions(new OptionData(STRING, "project", "The project", true)
                                .addChoices(getChoices(config.getWikiProjects().keySet())),
                        new OptionData(STRING, "keywords", "The search keywords", true));
        SlashCommandData suggestCommand = Commands.slash("suggest", "Creates a suggestion for one of my projects")
                .addOptions(new OptionData(STRING, "project", "The project name", true)
                                .addChoices(getChoices(config.getSuggestionProjects())),
                        new OptionData(STRING, "suggestion", "Your suggestion", true));
        SlashCommandData addonCommand = Commands.slash("addon", "Posts an add-on for one of my projects")
                .addOptions(new OptionData(INTEGER, "spigot-id", "The resource ID on Spigot", true)
                        .setRequiredRange(1, (long) OptionData.MAX_POSITIVE_NUMBER));
        SlashCommandData adminCommand = Commands.slash("admin", "Admin commands")
                .setDefaultEnabled(false).addSubcommands(
                        new SubcommandData("clear", "Deletes messages from the channel").addOptions(
                                new OptionData(INTEGER, "count", "The number of messages to delete", true)
                                        .setRequiredRange(2, 100)),
                        new SubcommandData("language-message",
                                "Creates the message for picking language roles"));
        jda.updateCommands().addCommands(addonCommand, wikiCommand, suggestCommand, adminCommand).queue();
        getLogger().info("Updating commands");
    }

    public Configuration getConfig() {
        return config;
    }

    public Logger getLogger() {
        return logger;
    }

    public void saveConfig() throws IOException {
        File file = new File("config.json");
        FileWriter writer = new FileWriter(file);
        gson.toJson(config, writer);
        writer.close();
    }

    public Gson getGson() {
        return gson;
    }

    private List<Command.Choice> getChoices(Collection<String> list) {
        return list.stream().map(str -> new Command.Choice(str, str)).collect(Collectors.toList());
    }
}
