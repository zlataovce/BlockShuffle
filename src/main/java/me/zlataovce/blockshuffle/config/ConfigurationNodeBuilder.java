package me.zlataovce.blockshuffle.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

@Accessors(chain = true)
public class ConfigurationNodeBuilder {
    private final ConfigurationNode node;
    @Setter private Object nodeValue = null;
    @Getter private final ConfigurationNodeBuilder parentBuilder;

    public ConfigurationNodeBuilder(ConfigurationNode node) {
        this.node = node;
        this.parentBuilder = null;
    }

    public ConfigurationNodeBuilder(ConfigurationNode node, ConfigurationNodeBuilder parentBuilder) {
        this.node = node;
        this.parentBuilder = parentBuilder;
    }

    public ConfigurationNodeBuilder childNode(String nodeName) {
        return new ConfigurationNodeBuilder(this.node.node(nodeName), this);
    }

    public ConfigurationNodeBuilder quickChildNode(String nodeName, Object nodeValue) throws SerializationException {
        this.node.node(nodeName).set(nodeValue);
        return this;
    }

    public ConfigurationNode buildNode() throws SerializationException {
        if (!this.node.hasChild()) {
            this.node.set(this.nodeValue);
        }
        return this.node;
    }

    public void lazySave(YamlConfigurationLoader loader) throws ConfigurateException {
        loader.save(this.buildNode());
    }
}
