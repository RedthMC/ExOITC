package me.redth.exoitc.command;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.data.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class Stats extends SubCommand {

    public Stats() {
        super("stats", "get the stats of a player", "oitc stats <player>", null, false);
    }

    public void execute(CommandSender sender, final String[] args) {
        if (args.length == 1) {
            Messages.COMMAND_SPECIFY_NAME.send(sender);
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        if (!player.hasPlayedBefore()) {
            Messages.COMMAND_GAME_NOT_EXISTS.send(sender);
        }
        PlayerStats stats = PlayerStats.get(player.getUniqueId());

        Messages.STATS_TITLE.send(sender, Messages.getPlayerNameFormat(player));
        Messages.STATS_GAMES.send(sender, String.valueOf(stats.games));

        Messages.STATS_KILLS.send(sender, String.valueOf(stats.kills));
        Messages.STATS_DEATHS.send(sender, String.valueOf(stats.deaths));
        Messages.STATS_KDR.send(sender, String.format("%.2f", (float) stats.kills / (float) stats.deaths));

        Messages.STATS_WINS.send(sender, String.valueOf(stats.wins));
        Messages.STATS_LOSSES.send(sender, String.valueOf(stats.losses));
        Messages.STATS_WLR.send(sender, String.format("%.2f", (float) stats.wins / (float) stats.losses));
    }
}
