package me.zlataovce.blockshuffle.game;

import io.papermc.lib.PaperLib;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.zlataovce.blockshuffle.Main;
import me.zlataovce.blockshuffle.arenas.Arena;
import me.zlataovce.blockshuffle.utils.MiscUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class GameManager {
    private final Main plugin;
    @Getter private final HashMap<Game, BukkitTask> runningGames = new HashMap<>();
    @Getter private final HashMap<Player, Game> playerGames = new HashMap<>();

    public void terminateGame(Game game, boolean force) {
        if (force) {
            for (Map.Entry<Player, Location> entry : game.getOldLocations().entrySet()) {
                PaperLib.teleportAsync(entry.getKey(), entry.getValue());
            }
            MiscUtils.deleteWorld(game.getArena().getArenaWorld());
            game.getParticipatingPlayers().forEach(this.playerGames::remove);
            game.getBlockFindTask().cancel();
            this.runningGames.remove(game).cancel();
        } else {
            game.endGame();
        }
    }

    public void terminateAllGames(boolean force) {
        new ArrayList<>(this.runningGames.keySet()).forEach(game -> this.terminateGame(game, force));
    }

    public void newGame(Arena arena, ArrayList<Player> partPlayers) {
        // Arena instance init start
        final Arena arenaInstance = SerializationUtils.clone(arena);
        final WorldCreator wc = new WorldCreator(arenaInstance.getUuid().toString())
                .environment(arenaInstance.getEnvironment())
                .type(arenaInstance.getWorldType());

        final World arenaWorld = Objects.requireNonNull(wc.createWorld());
        arenaWorld.getWorldBorder().setSize(arena.getWorldBorder());
        arenaWorld.getWorldBorder().setCenter(arenaWorld.getSpawnLocation());
        arenaInstance.setArenaWorld(arenaWorld);
        // Arena instance init end

        final Game game = new Game(this.plugin, partPlayers, arenaInstance);
        final BukkitTask task = game.runTaskTimer(this.plugin, 0L, 20L);
        this.runningGames.put(game, task);
    }
}
