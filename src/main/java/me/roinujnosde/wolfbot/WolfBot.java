package me.roinujnosde.wolfbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import me.roinujnosde.wolfbot.listeners.*;
import me.roinujnosde.wolfbot.server.PingHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions.enabledFor;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class WolfBot {

    private JDA jda;
    private final Configuration config;
    private static final Logger LOGGER = Logger.getLogger("WolfBot");
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
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOGGER.info("Shutting down WolfBot...")));
        WolfBot bot = new WolfBot();
        JDA jda = JDABuilder.createDefault(bot.config.getToken(), GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT).build();

        bot.setJda(jda);
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(9653), 0);
            server.createContext("/", new PingHandler());
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setJda(JDA jda) {
        this.jda = jda;
        onEnable();
    }

    public void onEnable() {
        jda.addEventListener(new WikiCommand(this), new ClearCommand(this), new LanguageRole(this),
                new SuggestCommand(this), new AddonCommand(this), new ReadyListener(this),
                new LogCommand(this), new NoBotSpam(this));

        getLogger().info("Registered listeners");
        SlashCommandData logCommand = Commands.slash("log", "Sends a log file privately")
                .addOption(ATTACHMENT, "attachment", "The log file to be examined", true);
        SlashCommandData wikiCommand = Commands.slash("wiki", "Searches the wiki")
                .addOptions(new OptionData(STRING, "project", "The project", true)
                                .addChoices(getChoices(config.getWikiProjects().keySet())),
                        new OptionData(STRING, "keywords", "The search keywords", true, true));
        SlashCommandData suggestCommand = Commands.slash("suggest", "Creates a suggestion for one of my projects")
                .addOptions(new OptionData(STRING, "project", "The project name", true)
                                .addChoices(getChoices(config.getSuggestionProjects())),
                        new OptionData(STRING, "suggestion", "Your suggestion", true));
        SlashCommandData addonCommand = Commands.slash("addon", "Posts an add-on for one of my projects")
                .addOptions(new OptionData(INTEGER, "spigot-id", "The resource ID on Spigot", true)
                        .setRequiredRange(1, (long) OptionData.MAX_POSITIVE_NUMBER));
        SlashCommandData adminCommand = Commands.slash("admin", "Admin commands")
                .setDefaultPermissions(enabledFor(Permission.MANAGE_SERVER)).addSubcommands(
                        new SubcommandData("clear", "Deletes messages from the channel").addOptions(
                                new OptionData(INTEGER, "count", "The number of messages to delete", true)
                                        .setRequiredRange(2, 100)),
                        new SubcommandData("language-message",
                                "Creates the message for picking language roles"));
        jda.updateCommands().addCommands(logCommand, addonCommand, wikiCommand, suggestCommand, adminCommand).queue();
        getLogger().info("Updating commands");
    }

    public Configuration getConfig() {
        return config;
    }

    public Logger getLogger() {
        return LOGGER;
    }

    public void saveConfig() throws IOException {
        File file = new File("config.json");
        FileWriter writer = new FileWriter(file);
        gson.toJson(config, writer);
        writer.close();
    }

    public JDA getJda() {
        return jda;
    }

    private List<Command.Choice> getChoices(Collection<String> list) {
        return list.stream().map(str -> new Command.Choice(str, str)).collect(Collectors.toList());
    }
}
