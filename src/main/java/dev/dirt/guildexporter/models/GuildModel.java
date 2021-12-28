package dev.dirt.guildexporter.models;

import com.google.gson.GsonBuilder;
import dev.dirt.guildexporter.GuildExporter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class GuildModel {
    private String name;
    private final List<RoleModel> roles;
    private final List<CategoriesModel> categories;
    // channels without categories
    private final List<ChannelModel> channels;
    private ChannelModel systemMessagesChannel;
    private ChannelModel afkChannel;
    private int verificationLevel;
    private int explicitContentLevel;

    public static GuildModel toModel(Guild guild) {
        GuildExporter.LOGGER.info("Creating model of guild: {} ({})", guild.getName(), guild.getId());
        // todo guild icon
        ChannelModel systemMessagesChannel = ChannelModel.toModel(guild.getSystemChannel());
        ChannelModel afkChannel = ChannelModel.toModel(guild.getAfkChannel());
        GuildModel guildModel = new GuildModel(guild.getName(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), systemMessagesChannel, afkChannel, guild.getVerificationLevel().getKey(), guild.getExplicitContentLevel().getKey());

        for (Category category : guild.getCategories()) {
            guildModel.getCategories().add(CategoriesModel.toModel(category));
        }

        for (GuildChannel channel : guild.getChannels()) {
            if (!(channel instanceof Category) && channel.getParent() == null) {
                guildModel.getChannels().add(ChannelModel.toModel(channel));
            }
        }

        for (Role role : guild.getRoles()) {
            if (!guild.getSelfMember().getRoles().contains(role)) { // prevent the bot from adding his own role to the json
                guildModel.roles.add(RoleModel.toModel(role));
            }
        }

        return guildModel;
    }

    public static void fromJson(Guild guild, String json) {
        GuildExporter.LOGGER.info("Importing model for guild: {} ({})", guild.getName(), guild.getId());
        GuildModel guildModel = new GsonBuilder().create().fromJson(json, GuildModel.class);

        // create roles
        for (RoleModel role : guildModel.roles) {
            RoleModel.fromJson(role, guild);
        }

        // create categories and channels of the categories
        for (CategoriesModel category : guildModel.getCategories()) {
            CategoriesModel.fromJson(category, guild);
        }

        // create channels without categories
        for (ChannelModel channel : guildModel.getChannels()) {
            ChannelModel.fromJson(channel, guild, null);
        }

        // set everything else
        guild.getManager().setName(guildModel.getName()).queue();
    }
}
