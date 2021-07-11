package me.zlataovce.blockshuffle.arenas;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.World;
import org.bukkit.WorldType;

import java.io.Serializable;
import java.util.UUID;

@Accessors(chain = true)
public class Arena implements Serializable {
    @Getter @Setter
    private World arenaWorld = null;
    @Getter @Setter
    private String name = null;
    @Getter @Setter
    private UUID uuid = null;
    @Getter @Setter
    private int rounds = 3;
    @Getter @Setter
    private int maxInstances = 1;
    @Getter @Setter
    private World.Environment environment = World.Environment.NORMAL;
    @Getter @Setter
    private WorldType worldType = WorldType.NORMAL;
    @Getter @Setter
    private int roundTime = 600;
    @Getter @Setter
    private double worldBorder = 600;

    public Arena() {}
}
