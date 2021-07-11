package me.zlataovce.blockshuffle.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.zlataovce.blockshuffle.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

@RequiredArgsConstructor
public class GameFindRunnable extends BukkitRunnable {
    private final Main plugin;
    @Getter private final ArrayList<Player> players;

    @Override
    public void run() {
        this.players.forEach(player -> {
            final Game game = this.plugin.getGameManager().getPlayerGames().get(player);
            final Material playerCurrentBlock = game.getBlocks().get(player).get(game.getRoundsPassed());
            for (BlockFace face : BlockFace.values()) {
                final Block toSearch = player.getLocation().getBlock().getRelative(face);
                if (toSearch.getType() == playerCurrentBlock) {
                    game.registerPlayerFind(player);
                    return;
                }
            }
        });
    }
}
