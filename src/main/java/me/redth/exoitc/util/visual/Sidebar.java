package me.redth.exoitc.util.visual;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.data.PlayerStats;
import me.redth.exoitc.game.Game;
import me.redth.exoitc.game.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class Sidebar {
    private final Objective objective;
    private final List<String> lines = new ArrayList<>();

    public Sidebar(Objective objective) {
        this.objective = objective;
        objective.setDisplayName(Messages.SCOREBOARD_TITLE.get());
    }

    public void show() {
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void addLine(String added) {
        this.lines.add(added);
        objective.getScore(added).setScore(15 - this.lines.size());
    }

    public void custom(String entry, int score) {
        this.lines.add(entry);
        objective.getScore(entry).setScore(score);
    }

    public void addLines(String... added) {
        for (String line : added) {
            this.lines.add(line);
            objective.getScore(line).setScore(15 - this.lines.size());
        }
    }

    public static void lobby(Player player) {
        Sidebar sidebar = newSidebar(player, "lobby");
        PlayerStats stats = PlayerStats.get(player.getUniqueId());
        sidebar.addLines(
                Messages.SCOREBOARD_HEADER.get(),
                "§7§m--- --------------",
                Messages.STATS_GAMES.get(String.valueOf(stats.games)),
                " ",
                Messages.STATS_KILLS.get(String.valueOf(stats.kills)),
                Messages.STATS_DEATHS.get(String.valueOf(stats.deaths)),
                Messages.STATS_KDR.get(String.format("%.2f", (float) stats.kills / (float) stats.deaths)),
                "  ",
                Messages.STATS_WINS.get(String.valueOf(stats.wins)),
                Messages.STATS_LOSSES.get(String.valueOf(stats.losses)),
                Messages.STATS_WLR.get(String.format("%.2f", (float) stats.wins / (float) stats.losses)),
                "§7§m---- -------------",
                Messages.SCOREBOARD_FOOTER.get());
        sidebar.show();
    }

    public static void queue(GamePlayer player) {
        Sidebar sidebar = newSidebar(player.as(), "queue");
        Game game = player.getGame();
        sidebar.addLines(
                Messages.SCOREBOARD_HEADER.get(),
                "",
                Messages.SCOREBOARD_QUEUE_MAP.get(game.name),
                Messages.SCOREBOARD_QUEUE_PLAYERS.get(String.valueOf(game.players.size()), String.valueOf(game.maxPlayer)),
                ((game.players.size() < game.minPlayer) ? Messages.SCOREBOARD_QUEUE_WAITING : Messages.SCOREBOARD_QUEUE_STARTING).get(),
                " ",
                Messages.SCOREBOARD_FOOTER.get());
        sidebar.show();

    }

    public static void game(GamePlayer self) {
        Sidebar sidebar = newSidebar(self.as(), "game");
        sidebar.custom("", 30);
        for (GamePlayer player : self.getGame().players) {
            if (player.spectator) continue;
            Messages format = (player == self) ? Messages.SCOREBOARD_GAME_FORMAT_SELF : Messages.SCOREBOARD_GAME_FORMAT;
            sidebar.custom(format.get(player.as().getName(), String.valueOf(player.getKills())), player.getKills());
        }
        sidebar.custom(" ", -1);
        sidebar.custom(Messages.SCOREBOARD_FOOTER.get(), -2);
        sidebar.show();

    }

    public static Sidebar newSidebar(Player player, String id) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective obj = scoreboard.getObjective(id);
        if (obj != null) obj.unregister();
        obj = scoreboard.registerNewObjective(id, "dummy");
        return new Sidebar(obj);
    }

}
