package me.redth.exoitc.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.redth.exoitc.data.PlayerStats;
import me.redth.exoitc.util.sign.Leaderboard;
import org.bukkit.OfflinePlayer;

public class Expansion extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "exoitc";
    }

    @Override
    public String getAuthor() {
        return "Redth";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        try {
            String[] args = params.split("_");
            if ("topkills".equalsIgnoreCase(args[0])) {
                int rank = Integer.parseInt(args[1]) - 1;
                PlayerStats stats = Leaderboard.killsSorted.get(rank);
                return ("name".equalsIgnoreCase(args[2])) ? stats.name : String.format("%,d", stats.kills);
            }
            if ("topwins".equalsIgnoreCase(args[0])) {
                int rank = Integer.parseInt(args[1]) - 1;
                PlayerStats stats = Leaderboard.winsSorted.get(rank);
                return ("name".equalsIgnoreCase(args[2])) ? stats.name : String.format("%,d", stats.wins);
            }
        } catch (Exception e) {
            return "-";
        }
        return null;
    }
}

