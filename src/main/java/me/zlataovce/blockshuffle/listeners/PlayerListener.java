package me.zlataovce.blockshuffle.listeners;

import me.zlataovce.blockshuffle.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final Main plugin;

    public PlayerListener(Main plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (this.plugin.getGameManager().getPlayerGames().containsKey(e.getPlayer())) {
            this.plugin.getGameManager().getPlayerGames().get(e.getPlayer()).removePlayer(e.getPlayer());
        }
        if (this.plugin.getPartyManager().getParty(e.getPlayer()).isPresent()) {
            this.plugin.getPartyManager().removePlayerFromParty(e.getPlayer(), this.plugin.getPartyManager().getParty(e.getPlayer()).get());
        }
    }
}
