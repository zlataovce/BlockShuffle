package me.zlataovce.blockshuffle.utils;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MiscUtils {
    public static void deleteDirectoryRecursively(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDirectoryRecursively(f);
                } else {
                    //noinspection ResultOfMethodCallIgnored
                    f.delete();
                }
            }
        }
        //noinspection ResultOfMethodCallIgnored
        folder.delete();
    }

    public static <T> T getRandomListElement(List<T> items) {
        return items.get(ThreadLocalRandom.current().nextInt(items.size()));
    }

    public static void deleteWorld(@NonNull World world) {
        Bukkit.getServer().unloadWorld(world, true);
        deleteDirectoryRecursively(world.getWorldFolder());
    }
}
