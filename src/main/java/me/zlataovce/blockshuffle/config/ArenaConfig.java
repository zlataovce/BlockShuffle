package me.zlataovce.blockshuffle.config;

import me.zlataovce.blockshuffle.arenas.Arena;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class ArenaConfig {
    public static void saveArena(File dataFolder, Arena arena) throws ConfigurateException {
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(Paths.get(dataFolder.toString(), arena.getUuid().toString() + ".yml")).build();
        new ConfigurationNodeBuilder(loader.createNode())
                .quickChildNode("uuid", arena.getUuid().toString())
                .quickChildNode("name", arena.getName())
                .quickChildNode("rounds", arena.getRounds())
                .quickChildNode("maxInstances", arena.getMaxInstances())
                .quickChildNode("environment", arena.getEnvironment().name())
                .quickChildNode("roundTime", arena.getRoundTime())
                .quickChildNode("worldType", arena.getWorldType())
                .quickChildNode("worldBorder", arena.getWorldBorder())
                .lazySave(loader);
    }

    public static Arena loadArena(Path path) {
        try {
            final ConfigurationNode arenaNode = YamlConfigurationLoader.builder().path(path).build().load();
            final String arenaName = ObjectUtils.defaultIfNull(arenaNode.node("name").getString(), arenaNode.node("uuid").getString());
            if (arenaName == null || arenaNode.node("uuid").getString() == null) {
                return null;
            }
            final UUID arenaUUID = UUID.fromString(Objects.requireNonNull(arenaNode.node("world").getString()));
            final int arenaRounds = arenaNode.node("rounds").getInt();
            if (arenaRounds < 1) {
                return null;
            }
            final int arenaMaxPlayers = arenaNode.node("maxPlayers").getInt();
            if (arenaMaxPlayers < 2) {
                return null;
            }
            final int arenaMaxInstances = arenaNode.node("maxInstances").getInt();
            if (arenaMaxInstances < 1) {
                return null;
            }
            final World.Environment arenaEnvironment;
            try {
                arenaEnvironment = World.Environment.valueOf(Objects.requireNonNull(arenaNode.node("environment").getString()));
            } catch (IllegalArgumentException | NullPointerException e) {
                return null;
            }
            final int arenaRoundTime = arenaNode.node("roundTime").getInt();
            if (arenaRoundTime < 1) {
                return null;
            }
            final WorldType arenaWorldType;
            try {
                arenaWorldType = WorldType.valueOf(Objects.requireNonNull(arenaNode.node("worldType").getString()));
            } catch (IllegalArgumentException | NullPointerException e) {
                return null;
            }
            final double arenaWorldBorder = arenaNode.node("worldBorder").getDouble();
            if (arenaWorldBorder < 4) {
                return null;
            }

            return new Arena()
                    .setUuid(arenaUUID)
                    .setName(arenaName)
                    .setRounds(arenaRounds)
                    .setMaxInstances(arenaMaxInstances)
                    .setEnvironment(arenaEnvironment)
                    .setRoundTime(arenaRoundTime)
                    .setWorldType(arenaWorldType)
                    .setWorldBorder(arenaWorldBorder);
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HashMap<UUID, Arena> loadArenas(Path dataFolder) throws IOException {
        HashMap<UUID, Arena> arenas = new HashMap<>();

        Files.walk(dataFolder).forEach(path -> {
            Arena arena = loadArena(path);
            if (arena == null) {
                Bukkit.getLogger().log(Level.WARNING, "Arena " + path.toFile().getName() + " has broken configuration or a serialization error occurred, skipping.");
                return;
            }
            arenas.put(arena.getUuid(), arena);
        });
        return arenas;
    }

    public static HashMap<String, String> arenaToAttrib(Arena arena) {
        HashMap<String, String> attribs = new HashMap<>();

        attribs.put("UUID", arena.getUuid().toString());
        attribs.put("Name", arena.getName());
        attribs.put("Rounds", Integer.toString(arena.getRounds()));
        attribs.put("Max instances", Integer.toString(arena.getMaxInstances()));
        attribs.put("Environment", WordUtils.capitalizeFully(arena.getEnvironment().toString().replaceAll("_", " ")));
        attribs.put("Round time", Integer.toString(arena.getRoundTime()));
        attribs.put("World type", WordUtils.capitalizeFully(arena.getWorldType().toString().replaceAll("_", " ")));
        attribs.put("World border", Double.toString(arena.getWorldBorder()));

        return attribs;
    }
}
