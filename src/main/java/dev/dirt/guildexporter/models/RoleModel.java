package dev.dirt.guildexporter.models;

import dev.dirt.guildexporter.Remapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

@Getter
@AllArgsConstructor
public class RoleModel {
    private final String name;
    private final int position;
    private final long id;
    private final boolean hoisted;
    private final boolean mentionable;
    private final boolean publicRole;
    private final long permissions;
    private final int color;

    public static RoleModel toModel(Role role) {
        return new RoleModel(role.getName(), role.getPosition(), role.getIdLong(), role.isHoisted(), role.isMentionable(), role.isPublicRole(), role.getPermissionsRaw(), role.getColorRaw());
    }

    // This step is synchronised and blocks the current thread witch is needed to collect all role ids
    public static void fromJson(RoleModel role, Guild guild) {
        if (role.isPublicRole()) { // the public role is everyone since we cannot delete that role we just edit it
            guild.getPublicRole().getManager().setHoisted(role.isHoisted()).setMentionable(role.isMentionable()).setPermissions(role.getPermissions()).setColor(role.getColor()).complete();
            Remapper.addMapping(role.getId(), guild.getPublicRole().getIdLong());
        } else {
            Role r = guild.createRole().setName(role.name).setHoisted(role.isHoisted()).setMentionable(role.isMentionable()).setPermissions(role.getPermissions()).setColor(role.getColor()).complete();
            Remapper.addMapping(role.getId(), r.getIdLong());
        }
    }
}
