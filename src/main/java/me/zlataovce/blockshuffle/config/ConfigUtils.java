package me.zlataovce.blockshuffle.config;

import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;

public class ConfigUtils {
    @NotNull
    public static String getPrefix(ConfigurationNode node) {
        return ObjectUtils.defaultIfNull(node.node("prefix").getString(), "[&l&eBlock&r&l&3Shuffle&r]");
    }
}
