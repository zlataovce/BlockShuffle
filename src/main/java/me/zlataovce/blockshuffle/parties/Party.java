package me.zlataovce.blockshuffle.parties;

import lombok.Getter;
import me.zlataovce.blockshuffle.arenas.Arena;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Party {
    @Getter private final Player leader;
    @Getter private final Arena arena;
    @Getter private final ArrayList<Player> players = new ArrayList<>();

    public Party(Player leader, Arena arena) {
        this.leader = leader;
        this.arena = arena;
        this.players.add(leader);
    }
}
