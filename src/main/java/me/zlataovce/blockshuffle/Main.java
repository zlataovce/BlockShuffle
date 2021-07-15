package me.zlataovce.blockshuffle;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.NonNull;
import me.zlataovce.blockshuffle.arenas.Arena;
import me.zlataovce.blockshuffle.arenas.ArenaManager;
import me.zlataovce.blockshuffle.commands.BlockShuffleCommand;
import me.zlataovce.blockshuffle.commands.BlockShufflePartyCommand;
import me.zlataovce.blockshuffle.config.ConfigManager;
import me.zlataovce.blockshuffle.game.GameManager;
import me.zlataovce.blockshuffle.listeners.PlayerListener;
import me.zlataovce.blockshuffle.parties.PartyManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurateException;

import java.util.logging.Level;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {
    @Getter
    private final ConfigManager configManager = new ConfigManager(this.getDataFolder());

    @Getter
    private GameManager gameManager;
    @Getter
    private ArenaManager arenaManager;
    @Getter
    private PartyManager partyManager;

    private BukkitAudiences adventure;

    public @NonNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override
    public void onEnable() {
        this.adventure = BukkitAudiences.create(this);
        try {
            this.configManager.loadConfig();
        } catch (ConfigurateException e) {
            this.getLogger().log(Level.SEVERE, "Could not load config.yml!");
            e.printStackTrace();
        }

        this.arenaManager = new ArenaManager(this);
        this.gameManager = new GameManager(this);
        this.partyManager = new PartyManager(this);
        new PlayerListener(this);

        // commands init
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");
        commandManager.getCommandCompletions().registerCompletion("arena", c -> ImmutableList.copyOf(arenaManager.getArenas().values().stream().map(Arena::getName).collect(Collectors.toList())));
        commandManager.getCommandContexts().registerContext(Arena.class, c -> {
            final String arenaName = c.popFirstArg();
            Bukkit.getLogger().info(arenaName);
            Bukkit.getLogger().info(this.arenaManager.getAlternativeNaming().get(arenaName).toString());
            if (this.arenaManager.getAlternativeNaming().containsKey(arenaName)) {
                return this.arenaManager.getArenas().get(this.arenaManager.getAlternativeNaming().get(arenaName));
            }
            return null;
        });
        commandManager.registerCommand(new BlockShuffleCommand());
        commandManager.registerCommand(new BlockShufflePartyCommand());
        // commands init end
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Cleaning up running games.");
        this.gameManager.terminateAllGames(true);

        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }
}
