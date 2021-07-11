package me.zlataovce.blockshuffle.parties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.zlataovce.blockshuffle.Main;
import me.zlataovce.blockshuffle.arenas.Arena;
import me.zlataovce.blockshuffle.config.ConfigUtils;
import me.zlataovce.blockshuffle.utils.ChatUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
public class PartyManager {
    private final Main plugin;

    @Getter private final ArrayList<Party> parties = new ArrayList<>();
    @Getter private final HashMap<Party, ArrayList<Player>> invites = new HashMap<>();

    public Party newParty(Player leader, Arena arena) {
        if (this.getParty(leader).isPresent()) {
            return null;
        }
        final Party party = new Party(leader, arena);
        this.parties.add(party);
        this.invites.put(party, new ArrayList<>());
        return party;
    }

    public void removePlayerFromParty(Player player, Party party) {
        if (party.getPlayers().contains(player)) {
            if (player == party.getLeader()) {
                this.disbandParty(party);
            } else {
                party.getPlayers().remove(player);
            }
        }
    }

    public void addPlayerToParty(Player player, Party party) {
        party.getPlayers().add(player);
        player.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &aAdded you to &r" + party.getLeader().getName() + "&a's party!"));
    }

    public Optional<Party> getParty(Player player) {
        return this.parties.stream().filter(x -> x.getPlayers().contains(player)).findFirst();
    }

    public boolean isLeader(Party party, Player player) {
        return party.getLeader().equals(player);
    }

    public boolean isLeaderInCurrParty(Player player) {
        if (this.getParty(player).isEmpty()) {
            return false;
        }
        return this.isLeader(this.getParty(player).get(), player);
    }

    public void disbandParty(Party party) {
        this.invites.remove(party);
        this.parties.remove(party);
    }

    public void invitePlayerToParty(Player player, Party party) {
        if (this.invites.get(party).contains(player)) {
            return;
        }
        this.invites.get(party).add(player);
        player.sendMessage(ChatUtils.color(ConfigUtils.getPrefix(this.plugin.getConfigManager().getConfigurationNode()) + " &aYou've been invited to &r" + party.getLeader().getName() + "&a's party! &r&l&6Type &r/bsparty accept " + party.getLeader().getName() + "&l&6 to accept!"));
    }

    public boolean acceptInvite(Player player, Party party) {
        if (this.invites.get(party).contains(player) && !party.getPlayers().contains(player)) {
            this.addPlayerToParty(player, party);
            this.invites.get(party).remove(player);
            return true;
        } else {
            return false;
        }
    }

    public boolean isInvitedToParty(Player player, Party party) {
        return this.invites.get(party).contains(player);
    }
}
