package me.zlataovce.blockshuffle.game;

import io.papermc.lib.PaperLib;
import lombok.Getter;
import me.zlataovce.blockshuffle.Main;
import me.zlataovce.blockshuffle.arenas.Arena;
import me.zlataovce.blockshuffle.materials.SolidMaterials;
import me.zlataovce.blockshuffle.utils.ChatUtils;
import me.zlataovce.blockshuffle.utils.MiscUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;
import java.util.stream.Collectors;

public class Game extends BukkitRunnable {
    private final Main plugin;
    @Getter private final ArrayList<Player> participatingPlayers;
    @Getter private final Arena arena;
    @Getter private final HashMap<Player, Location> oldLocations = new HashMap<>();
    @Getter private int secondsPassed = 0;
    @Getter private int roundSecondsPassed = 0;
    @Getter private int roundsPassed = 0;
    @Getter private final HashMap<Player, Integer> founds = new HashMap<>();
    @Getter private final HashMap<Player, ArrayList<Material>> blocks = new HashMap<>();
    @Getter private final BukkitTask blockFindTask;
    @Getter private final GameFindRunnable blockFinder;

    private final ScoreboardManager manager = Bukkit.getScoreboardManager();

    public Game(Main plugin, ArrayList<Player> partPlayers, Arena arena) {
        partPlayers.forEach(player -> {
            this.oldLocations.put(player, player.getLocation());
            PaperLib.teleportAsync(player, arena.getArenaWorld().getSpawnLocation());
        });

        this.plugin = plugin;
        this.participatingPlayers = partPlayers;
        this.arena = arena;
        this.participatingPlayers.forEach(player -> {
            this.founds.put(player, 0);
            this.plugin.getGameManager().getPlayerGames().put(player, this);
        });

        this.generateBlocks();
        this.blockFinder = new GameFindRunnable(plugin, partPlayers);
        this.blockFindTask = this.blockFinder.runTaskTimer(plugin, 0L, 10L);
    }

    @Override
    public void run() {
        this.secondsPassed += 1;
        this.roundSecondsPassed += 1;
        if (this.roundSecondsPassed > this.arena.getRoundTime()) {
            this.nextRound();
        }
        this.updateScoreboards();
    }

    public void registerPlayerFind(Player player) {
        this.founds.put(player, this.founds.get(player) + 1);
        this.participatingPlayers.forEach(partPlayer -> partPlayer.sendMessage(ChatUtils.color("&ePlayer &r&c&l" + player.getName() + "&r&e found their block!")));
        this.nextRound();
    }

    public void endGame() {
        List<Player> winners = this.determineWinners();
        final Component subtitle;
        if (winners.size() > 1) {
            subtitle = Component.text("The winners are " + winners.stream().map(Player::getName).collect(Collectors.joining(", ")) + "!", NamedTextColor.GRAY);
        } else if (winners.size() == 1) {
            subtitle = Component.text("The winner is " + winners.get(0) + "!", NamedTextColor.GRAY);
        } else {
            subtitle = Component.text("No one won the game!", NamedTextColor.GRAY);
        }
        this.participatingPlayers.forEach(player -> {
            player.setScoreboard(Objects.requireNonNull(this.manager).getNewScoreboard());
            final Component mainTitle = (winners.contains(player)) ? Component.text("Winner!", NamedTextColor.GOLD) : Component.text("Game over!", NamedTextColor.RED);
            final Title title = Title.title(mainTitle, subtitle);
            this.plugin.adventure().player(player).showTitle(title);
        });
        new GameEndRunnable(this.plugin, this).runTaskLater(this.plugin, 60L);
    }

    private List<Player> determineWinners() {
        int max = Collections.max(this.founds.values());
        return this.founds.entrySet().stream().filter(entry -> entry.getValue() == max).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    private void updateScoreboards() {
        this.participatingPlayers.forEach(player -> {
            Scoreboard board = Objects.requireNonNull(this.manager).getNewScoreboard();
            Objective objective = board.registerNewObjective("objective" + player.getName(), "dummy", "BlockShuffle");

            final Material playerCurrentBlock = this.blocks.get(player).get(roundsPassed);
            objective.getScore("Current block: " + WordUtils.capitalizeFully(playerCurrentBlock.toString().replaceAll("_", " "))).setScore(1);
            objective.getScore("Rounds: " + this.roundsPassed + "/" + this.arena.getRounds()).setScore(2);
            objective.getScore("Found blocks: " + this.founds.get(player) + "/" + this.arena.getRounds()).setScore(3);
            objective.getScore("Time: " + DurationFormatUtils.formatDuration(this.secondsPassed, "mm:ss")).setScore(4);

            player.setScoreboard(board);
        });
    }

    private void generateBlocks() {
        ArrayList<Material> eligibleBlocks = new ArrayList<>();
        for (Material mat : Material.values()) {
            if (mat.isSolid() && mat.isBlock() && mat.isOccluding() && !SolidMaterials.listOfEndSolidMaterials().contains(mat) && !SolidMaterials.listOfNetherSolidMaterials().contains(mat)) {
                eligibleBlocks.add(mat);
            }
        }

        this.participatingPlayers.forEach(player -> {
            ArrayList<Material> playerMaterials = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                playerMaterials.add(MiscUtils.getRandomListElement(eligibleBlocks));
            }
            this.blocks.put(player, playerMaterials);
        });
    }

    private void nextRound() {
        this.roundSecondsPassed = 0;
        this.roundsPassed += 1;
        if (this.roundsPassed >= this.arena.getRounds()) {
            this.endGame();
        }
    }

    public void removePlayer(Player player) {
        player.setScoreboard(Objects.requireNonNull(this.manager).getNewScoreboard());
        this.getBlockFinder().getPlayers().remove(player);
        this.plugin.getGameManager().getPlayerGames().remove(player);
        this.blocks.remove(player);
        this.founds.remove(player);
        PaperLib.teleportAsync(player, this.oldLocations.remove(player));
        this.participatingPlayers.remove(player);
        if (this.participatingPlayers.size() == 1) {
            this.endGame();
        }
        if (this.participatingPlayers.size() == 0) {
            this.plugin.getGameManager().terminateGame(this, true);
        }
    }
}
