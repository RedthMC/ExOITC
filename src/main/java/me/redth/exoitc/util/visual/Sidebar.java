package me.redth.exoitc.util.visual;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.data.PlayerStats;
import me.redth.exoitc.game.Game;
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
                Messages.STATS_GAMES.get(String.format("%,d", stats.games)),
                " ",
                Messages.STATS_KILLS.get(String.format("%,d", stats.kills)),
                Messages.STATS_DEATHS.get(String.format("%,d", stats.deaths)),
                Messages.STATS_KDR.get(String.format("%.2f", (float) stats.kills / (float) stats.deaths)),
                "  ",
                Messages.STATS_WINS.get(String.format("%,d", stats.wins)),
                Messages.STATS_LOSSES.get(String.format("%,d", stats.losses)),
                Messages.STATS_WLR.get(String.format("%.2f", (float) stats.wins / (float) stats.losses)),
                "§7§m---- -------------",
                Messages.SCOREBOARD_FOOTER.get());
        sidebar.show();
    }

    public static void queue(Game game, Player player) {
        Sidebar sidebar = newSidebar(player, "queue");
        sidebar.addLines(
                Messages.SCOREBOARD_HEADER.get(),
                "",
                Messages.SCOREBOARD_QUEUE_MAP.get(game.name),
                Messages.SCOREBOARD_QUEUE_PLAYERS.get(String.valueOf(game.audiences.size()), String.valueOf(game.maxPlayers)),
                ((game.audiences.size() < game.minPlayers) ? Messages.SCOREBOARD_QUEUE_WAITING : Messages.SCOREBOARD_QUEUE_STARTING).get(),
                " ",
                Messages.SCOREBOARD_FOOTER.get());
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
