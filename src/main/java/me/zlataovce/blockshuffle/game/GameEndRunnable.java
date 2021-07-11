package me.zlataovce.blockshuffle.game;

import io.papermc.lib.PaperLib;
import lombok.RequiredArgsConstructor;
import me.zlataovce.blockshuffle.Main;
import me.zlataovce.blockshuffle.utils.MiscUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

@RequiredArgsConstructor
public class GameEndRunnable extends BukkitRunnable {
    private final Main plugin;
    private final Game game;

    public void run() {
        for (Map.Entry<Player, Location> entry : this.game.getOldLocations().entrySet()) {
            PaperLib.teleportAsync(entry.getKey(), entry.getValue());
        }
        MiscUtils.deleteWorld(this.game.getArena().getArenaWorld());
        this.game.getParticipatingPlayers().forEach(player -> this.plugin.getGameManager().getPlayerGames().remove(player));
        this.game.getBlockFindTask().cancel();
        this.plugin.getGameManager().getRunningGames().remove(this.game).cancel();
    }
}
