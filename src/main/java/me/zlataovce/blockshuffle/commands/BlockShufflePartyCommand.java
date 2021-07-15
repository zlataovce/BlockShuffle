package me.zlataovce.blockshuffle.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.zlataovce.blockshuffle.Main;
import me.zlataovce.blockshuffle.arenas.Arena;
import me.zlataovce.blockshuffle.config.ConfigUtils;
import me.zlataovce.blockshuffle.parties.Party;
import me.zlataovce.blockshuffle.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@CommandAlias("bsparty|blockshuffleparty")
public class BlockShufflePartyCommand extends BaseCommand {
    @Dependency
    private Main plugin;

    @Subcommand("create")
    @Description("Creates a party.")
    @Syntax("<arena>")
    @CommandCompletion("@arena")
    public void onCreate(CommandSender sender, Arena arena) {
        if (arena != null) {
            Player leader = (Player) sender;
            if (this.plugin.getGameManager().getPlayerGames().containsKey(leader)) {
                sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &cYou are in a game!"));
                return;
            }
            if (this.plugin.getPartyManager().getParty(leader).isPresent()) {
                sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &cYou already are in a party!"));
                return;
            }
            this.plugin.getPartyManager().newParty(leader, arena);
            sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &aParty created!"));
        } else {
            sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &cInvalid arena!"));
        }
    }

    @Subcommand("invite")
    @Description("Invites a player into your party.")
    @Syntax("<player>")
    @CommandCompletion("@player")
    public void onInvite(CommandSender sender, Player invitee) {
        final Player player = (Player) sender;
        if (this.plugin.getPartyManager().isLeaderInCurrParty(player) && this.plugin.getPartyManager().getParty(player).isPresent() && !invitee.equals(player)) {
            this.plugin.getPartyManager().invitePlayerToParty(invitee, this.plugin.getPartyManager().getParty(player).get());
        } else {
            sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &cCould not invite player!"));
        }
    }

    @Subcommand("accept")
    @Description("Accepts a party invite.")
    @Syntax("<partyleader>")
    @CommandCompletion("@player")
    public void onAccept(CommandSender sender, Player leader) {
        final Player player = (Player) sender;
        final Optional<Party> party = this.plugin.getPartyManager().getParty(leader);
        if (party.isEmpty() || !this.plugin.getPartyManager().isInvitedToParty(player, party.get())) {
            sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &cInvalid invite!"));
        } else {
            this.plugin.getPartyManager().acceptInvite(player, party.get());
        }
    }

    @Subcommand("kick")
    @Description("Kicks a player from the party.")
    @Syntax("<player>")
    @CommandCompletion("@player")
    public void onKick(CommandSender sender, Player toKick) {
        final Player player = (Player) sender;
        if (!toKick.equals(player) && this.plugin.getPartyManager().isLeaderInCurrParty(player) && this.plugin.getPartyManager().getParty(toKick).isPresent() && this.plugin.getPartyManager().getParty(toKick).get().getLeader().equals(player)) {
            this.plugin.getPartyManager().removePlayerFromParty(toKick, this.plugin.getPartyManager().getParty(toKick).get());
        } else {
            sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &cCould not kick player!"));
        }
    }

    @Subcommand("start")
    @Description("Starts a game.")
    public void onStart(CommandSender sender) {
        Player player = (Player) sender;
        if (this.plugin.getPartyManager().getParty(player).isPresent() && this.plugin.getPartyManager().isLeaderInCurrParty(player)) {
            if (this.plugin.getPartyManager().getParty(player).get().getPlayers().size() < 2) {
                sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &cYou need at least 2 players in the party to play!"));
                return;
            }
            final Party party = this.plugin.getPartyManager().getParty(player).get();
            sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " Generating world... This may take a while."));
            this.plugin.getGameManager().newGame(party.getArena(), party.getPlayers());
            this.plugin.getPartyManager().disbandParty(party);
        } else {
            if (!this.plugin.getPartyManager().isLeaderInCurrParty(player) && this.plugin.getPartyManager().getParty(player).isPresent()) {
                sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &cYou are not leader!"));
            } else {
                sender.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &cYou are not in a party!"));
            }
        }
    }

    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
