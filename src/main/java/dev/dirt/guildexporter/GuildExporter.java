package dev.dirt.guildexporter;


import com.google.gson.GsonBuilder;
import dev.dirt.guildexporter.models.GuildModel;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class GuildExporter extends ListenerAdapter {
    public static final Logger LOGGER = LoggerFactory.getLogger("GuildExporter");
    private final long[] ids;
    private final String token;
    private JDA jda;

    public GuildExporter(String token, long[] ids) {
        this.token = token;
        this.ids = ids;
    }

    public void startBot() {
        try {
            jda = JDABuilder.createDefault(token).addEventListeners(this).enableCache(CacheFlag.MEMBER_OVERRIDES).setStatus(OnlineStatus.ONLINE).build();
        } catch (LoginException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info("Bot started");
        LOGGER.info("Invite it: https://discord.com/oauth2/authorize?client_id={}&permissions=8&scope=bot+applications.commands", jda.getSelfUser().getApplicationId());
        for (Guild guild : jda.getGuilds()) {
            if (!jda.isUnavailable(guild.getIdLong())) {
                registerCommands(guild);
            }
        }
        super.onReady(event);
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        LOGGER.info("Joined Guild:" + event.getGuild().getId());
        registerCommands(event.getGuild());
        super.onGuildJoin(event);
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getName().equals("export")) {
            if (event.getGuild() != null) {
                event.deferReply().queue();
                GuildModel guildModel = GuildModel.toModel(event.getGuild());
                File file = new File(Objects.requireNonNull(event.getOption("filename")).getAsString() + ".json");
                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                    bufferedWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(guildModel));
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                event.getHook().sendMessage("Done!").queue();
            } else {
                event.reply("This only works on guilds").queue();
            }
        }
        if (event.getName().equals("import")) {
            if (event.getGuild() != null) {
                File file = new File(Objects.requireNonNull(event.getOption("filename")).getAsString() + ".json");
                if (file.exists()) {
                    event.reply("Ok lets go").queue();
                    try {
                        String json = new String(Files.readAllBytes(file.toPath()));
                        for (Role role : event.getGuild().getRoles()) {
                            if (!event.getGuild().getSelfMember().getRoles().contains(role) && !role.isPublicRole()) { //  we cannot delete the bot or public role
                                role.delete().queue();
                            }
                        }
                        for (GuildChannel channel : event.getGuild().getChannels()) {
                            channel.delete().queue();
                        }
                        GuildModel.fromJson(event.getGuild(), json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    event.reply("File not found!").queue();
                }
            } else {
                event.reply("This only works on guilds").queue();
            }
        }
        super.onSlashCommand(event);
    }

    private void registerCommands(Guild guild) {
        LOGGER.info("Registering commands for guild: {} ({})", guild.getName(), guild.getId());
        guild.upsertCommand("export", "Export the server as JSON document").addOption(OptionType.STRING, "filename", "The name of the JSON file", true).setDefaultEnabled(false) // disable the command by default
                .queue(command -> {
                    // enable the commands for the given user ids
                    for (long id : ids) {
                        command.updatePrivileges(guild, CommandPrivilege.enableUser(id)).queue();
                    }
                });
        guild.upsertCommand("import", "Import a JSON document").addOption(OptionType.STRING, "filename", "The name of the JSON file", true).setDefaultEnabled(false)// disable the command by default
                .queue(command -> {
                    // enable the commands for the given user ids
                    for (long id : ids) {
                        command.updatePrivileges(guild, CommandPrivilege.enableUser(id)).queue();
                    }
                });
    }
}
