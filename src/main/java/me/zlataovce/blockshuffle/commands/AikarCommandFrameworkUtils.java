package me.zlataovce.blockshuffle.commands;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import me.zlataovce.blockshuffle.arenas.Arena;
import me.zlataovce.blockshuffle.arenas.ArenaManager;

import java.util.stream.Collectors;

public class AikarCommandFrameworkUtils {
    public static void registerArenaAutocompletions(PaperCommandManager manager, ArenaManager arenaManager) {
        manager.getCommandCompletions().registerCompletion("arena", c -> ImmutableList.copyOf(arenaManager.getArenas().values().stream().map(Arena::getName).collect(Collectors.toList())));
    }

    public static void registerArenaResolver(PaperCommandManager manager, ArenaManager arenaManager) {
        manager.getCommandContexts().registerContext(Arena.class, c -> {
            final String arenaName = c.popFirstArg();
            if (arenaManager.getAlternativeNaming().containsKey(arenaName)) {
                return arenaManager.getArenas().get(arenaManager.getAlternativeNaming().get(arenaName));
            }
            return null;
        });
    }
}
