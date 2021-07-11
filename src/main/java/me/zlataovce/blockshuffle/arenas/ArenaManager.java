package me.zlataovce.blockshuffle.arenas;

import lombok.Getter;
import me.zlataovce.blockshuffle.Main;
import me.zlataovce.blockshuffle.config.ArenaConfig;
import org.bukkit.Bukkit;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class ArenaManager {
    private final Main plugin;

    @Getter HashMap<String, UUID> alternativeNaming = new HashMap<>();

    @Getter HashMap<UUID, Arena> arenas = new HashMap<>();
    @Getter HashMap<UUID, Arena> arenasInEditMode = new HashMap<>();

    public ArenaManager(Main plugin) {
        this.plugin = plugin;
        try {
            this.arenas = ArenaConfig.loadArenas(Paths.get(plugin.getDataFolder().toPath().toString(), "arenas"));
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load arenas!");
            e.printStackTrace();
        }
    }

    public UUID createArena(String arenaName) {
        UUID arenaUUID = UUID.randomUUID();
        if (this.alternativeNaming.containsKey(arenaName)) {
            return null;
        }
        this.arenasInEditMode.put(arenaUUID, new Arena().setUuid(arenaUUID).setName(arenaName));
        this.alternativeNaming.put(arenaName, arenaUUID);
        return arenaUUID;
    }

    public boolean saveArena(UUID arenaUUID) {
        if (!this.arenasInEditMode.containsKey(arenaUUID) || this.arenas.containsKey(arenaUUID)) {
            return false;
        }

        try {
            ArenaConfig.saveArena(Paths.get(this.plugin.getDataFolder().toPath().toString(), "arenas").toFile(), this.arenasInEditMode.get(arenaUUID));
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return false;
        }

        this.arenas.put(arenaUUID, this.arenasInEditMode.remove(arenaUUID));
        return true;
    }

    public boolean editArena(UUID arenaUUID) {
        if (!this.arenas.containsKey(arenaUUID) || this.arenasInEditMode.containsKey(arenaUUID)) {
            return false;
        }
        this.arenasInEditMode.put(arenaUUID, this.arenas.remove(arenaUUID));

        File arenaFile = Paths.get(this.plugin.getDataFolder().toPath().toString(), "arenas", arenaUUID.toString() + ".yml").toFile();
        if (!arenaFile.exists()) {
            return false;
        }
        //noinspection ResultOfMethodCallIgnored
        arenaFile.delete();
        return true;
    }

    public boolean isArenaInEditMode(UUID arenaUUID) {
        return this.arenasInEditMode.containsKey(arenaUUID);
    }

    public boolean isArenaWorking(UUID arenaUUID) {
        return this.arenas.containsKey(arenaUUID);
    }
}
