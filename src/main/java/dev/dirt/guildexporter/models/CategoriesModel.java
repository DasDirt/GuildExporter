package dev.dirt.guildexporter.models;

import dev.dirt.guildexporter.Remapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class CategoriesModel {
    private final String name;
    private final int position;
    private final long id;
    private final List<PermissionModel> permissions;
    private final List<ChannelModel> channels;

    public static CategoriesModel toModel(Category category) {
        CategoriesModel categoriesModel = new CategoriesModel(category.getName(), category.getPosition(), category.getIdLong(), new ArrayList<>(), new ArrayList<>());
        for (GuildChannel channel : category.getChannels()) {
            categoriesModel.getChannels().add(ChannelModel.toModel(channel));
        }
        for (PermissionOverride permissionOverride : category.getPermissionOverrides()) {
            categoriesModel.getPermissions().add(PermissionModel.toModel(permissionOverride));
        }
        return categoriesModel;
    }

    public static void fromJson(CategoriesModel categoriesModel, Guild guild) {
        ChannelAction<Category> categoryChannelAction = guild.
                createCategory(categoriesModel.getName())
                .setPosition(categoriesModel.getPosition());
        for (PermissionModel permission : categoriesModel.getPermissions()) {
            categoryChannelAction = PermissionModel.fromJson(permission, categoryChannelAction);
        }
        Category category = categoryChannelAction.complete();
        for (ChannelModel channel : categoriesModel.getChannels()) {
            ChannelModel.fromJson(channel, guild, category);
        }
    }
}
