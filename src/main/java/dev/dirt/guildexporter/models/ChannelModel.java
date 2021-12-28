package dev.dirt.guildexporter.models;

import dev.dirt.guildexporter.Remapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class ChannelModel {
    private final String name;
    private final int position;
    private final long id;
    private final boolean sync;
    private final List<PermissionModel> permissions;
    private final ChannelType channelType;

    public static ChannelModel toModel(GuildChannel channel) {
        if (channel != null) {
            ChannelModel channelModel = new ChannelModel(channel.getName(), channel.getPosition(), channel.getIdLong(), channel.isSynced(), new ArrayList<>(), ChannelType.getTypeFromChannel(channel));
            for (PermissionOverride permissionOverride : channel.getPermissionOverrides()) {
                channelModel.getPermissions().add(PermissionModel.toModel(permissionOverride));
            }
            return channelModel;
        } else {
            return null; // this can be null for the system or afk channel
        }
    }

    public static void fromJson(ChannelModel channel, Guild guild, Category category) {
        ChannelAction<?> channelAction = null;
        switch (channel.getChannelType()) {
            case TEXT: {
                channelAction = guild.createTextChannel(channel.getName(), category);
                break;
            }
            case VOICE: {
                channelAction = guild.createVoiceChannel(channel.getName(), category);
                break;
            }
            case STAGE: {
                channelAction = guild.createStageChannel(channel.getName(), category);
                break;
            }
        }
        if (channelAction != null) {
            channelAction = channelAction.setPosition(channel.getPosition());
            for (PermissionModel permission : channel.getPermissions()) {
                channelAction = PermissionModel.fromJson(permission, channelAction);
            }
            channelAction.queue();
        }
    }

    public enum ChannelType {
        INVALID, TEXT, VOICE, STAGE;

        public static ChannelType getTypeFromChannel(GuildChannel channel) {
            if (channel instanceof TextChannel) {
                return TEXT;
            } else if (channel instanceof StageChannel) {
                return STAGE;
            } else if (channel instanceof VoiceChannel) {
                return VOICE;
            }
            return INVALID;
        }
    }
}
