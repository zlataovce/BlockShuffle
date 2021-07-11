package me.zlataovce.blockshuffle.config;

import lombok.Getter;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {
    private final File dataFolder;
    private final YamlConfigurationLoader loader;
    @Getter private ConfigurationNode configurationNode;

    public ConfigManager(File dataFolder) {
        this.dataFolder = dataFolder;
        this.loader = YamlConfigurationLoader.builder().path(Paths.get(dataFolder.toString(), "config.yml")).build();
    }

    public void loadConfig() throws ConfigurateException {
        Path configPath = Paths.get(this.dataFolder.toPath().toString(), "config.yml");
        if (!configPath.toFile().exists()) {
            this.configurationNode = new ConfigurationNodeBuilder(this.loader.createNode())
                    .quickChildNode("prefix", "[&l&eBlock&r&l&3Shuffle&r]")
                    .quickChildNode("default-rounds", 3)
                    .quickChildNode("default-maxplayers", 10)
                    .buildNode();

            loader.save(this.configurationNode);
        } else {
            this.configurationNode = loader.load();
        }
    }
}
