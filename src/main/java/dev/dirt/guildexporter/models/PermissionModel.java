package dev.dirt.guildexporter.models;

import dev.dirt.guildexporter.Remapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

@Getter
@AllArgsConstructor
public class PermissionModel {
    private final long allow, deny;
    private final long holderID;
    private final boolean isHolderRole;


    public static PermissionModel toModel(PermissionOverride permissionOverride) {
        return new PermissionModel(permissionOverride.getAllowedRaw(), permissionOverride.getDeniedRaw(), permissionOverride.getIdLong(), permissionOverride.isRoleOverride());
    }

    public static <T extends GuildChannel> ChannelAction<T> fromJson(PermissionModel permissionModel, ChannelAction<T> categoryChannelAction) {
        if (permissionModel.isHolderRole()) {
            if(Remapper.getMappingFromOldID(permissionModel.getHolderID()) ==null){
                Remapper.getMappingFromOldID(permissionModel.getHolderID());
                System.out.println(permissionModel.getHolderID());
            }
            categoryChannelAction = categoryChannelAction.addRolePermissionOverride(Remapper.getMappingFromOldID(permissionModel.getHolderID()).getNewID(), permissionModel.getAllow(), permissionModel.getDeny());
        } else {
            categoryChannelAction = categoryChannelAction.addMemberPermissionOverride(permissionModel.getHolderID(), permissionModel.getAllow(), permissionModel.getDeny());
        }
        return categoryChannelAction;
    }
}
