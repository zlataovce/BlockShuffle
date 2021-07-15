package me.zlataovce.blockshuffle.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.zlataovce.blockshuffle.Main;
import me.zlataovce.blockshuffle.arenas.Arena;
import me.zlataovce.blockshuffle.config.ArenaConfig;
import me.zlataovce.blockshuffle.config.ConfigUtils;
import me.zlataovce.blockshuffle.utils.ChatUtils;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@CommandAlias("bs|blockshuffle")
public class BlockShuffleCommand extends BaseCommand {
    @Dependency
    private Main plugin;

    @Subcommand("list")
    @Description("Lists all working arenas.")
    public void onList(CommandSender sender) {
        final List<String> arenas = this.plugin.getArenaManager().getArenas().values().stream().map(Arena::getName).collect(Collectors.toList());
        sender.sendMessage(ChatUtils.color("Working arenas: &a" + String.join("&r, &a", arenas)));
    }

    @Subcommand("info")
    @Description("Lists info about an arena.")
    @Syntax("<arena>")
    @CommandCompletion("@arena")
    public void onInfo(CommandSender sender, Arena arena) {
        if (arena != null) {
            for (Map.Entry<String, String> entry : ArenaConfig.arenaToAttrib(arena).entrySet()) {
                sender.sendMessage("&a&l" + entry.getKey() + "&r: &e" + entry.getValue());
            }
        } else {
            sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &cInvalid arena!"));
        }
    }

    @Subcommand("createarena")
    @Description("Creates an arena and puts it into edit mode.")
    @Syntax("<name>")
    public void onCreate(CommandSender sender, String name) {
        final UUID arena = this.plugin.getArenaManager().createArena(name);
        if (arena != null) {
            sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &aArena successfully created with the UUID &r" + arena + "&a!"));
        } else {
            final UUID altName = this.plugin.getArenaManager().getAlternativeNaming().get(name);
            if (altName != null && (this.plugin.getArenaManager().isArenaWorking(altName) || this.plugin.getArenaManager().isArenaInEditMode(altName))) {
                sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &cArena already exists!"));
            } else {
                sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &cCould not create the arena!"));
            }
        }
    }

    @Subcommand("savearena")
    @Description("Saves an arena and puts it out of edit mode.")
    @Syntax("<name>")
    public void onSave(CommandSender sender, String name) {
        final UUID arena = this.plugin.getArenaManager().getAlternativeNaming().get(name);
        if (arena != null && this.plugin.getArenaManager().saveArena(arena)) {
            sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &aArena saved successfully!"));
        } else {
            if (arena != null && this.plugin.getArenaManager().isArenaWorking(arena)) {
                sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &cArena is already saved!"));
            } else {
                sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &cCould not save arena!"));
            }
        }
    }

    @Subcommand("editarena")
    @Description("Puts an arena in edit mode.")
    @Syntax("<name>")
    public void onEdit(CommandSender sender, String name) {
        final UUID arena = this.plugin.getArenaManager().getAlternativeNaming().get(name);
        if (arena != null && this.plugin.getArenaManager().editArena(arena)) {
            sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &aArena put into edit mode!"));
        } else {
            if (arena != null && this.plugin.getArenaManager().isArenaInEditMode(arena)) {
                sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &cArena is already in edit mode!"));
            } else {
                sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &cCould not save arena!"));
            }
        }
    }

    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
