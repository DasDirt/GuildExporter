package dev.dirt.guildexporter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// Currently used only for roles
public class Remapper {
    private static final List<Mapping> mappings = new ArrayList<>();

    public static void addMapping(long oldID, long newID) {
        synchronized (mappings) {
            mappings.add(new Mapping(oldID, newID));
        }
    }

    public static Mapping getMappingFromOldID(long id) {
        synchronized (mappings) {
            return mappings.stream().filter(mapping -> mapping.oldID == id).findFirst().orElse(null);
        }
    }

    public static Mapping getMappingFromNewID(long id) {
        synchronized (mappings) {
            return mappings.stream().filter(mapping -> mapping.newID == id).findFirst().orElse(null);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Mapping {
        private final long oldID;
        private final long newID;
    }
}
