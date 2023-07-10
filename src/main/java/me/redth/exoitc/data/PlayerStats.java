package me.redth.exoitc.data;

import me.redth.exoitc.config.Stats;
import me.redth.exoitc.game.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerStats {
    private static final Map<UUID, PlayerStats> MAP = new HashMap<>();
    public final UUID uuid;
    public String name;
    public int games;
    public int kills;
    public int deaths;
    public int wins;
    public int losses;

    public static PlayerStats get(UUID uuid) {
        return MAP.computeIfAbsent(uuid, PlayerStats::new);
    }

    private PlayerStats(UUID uuid) {
        this.uuid = uuid;
    }

    public static void load(UUID uuid, ConfigurationSection section) {
        PlayerStats data = new PlayerStats(uuid);
        if (section == null) return;
        data.name = (Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) ? Bukkit.getOfflinePlayer(uuid).getName() : section.getString("name");
        data.games = section.getInt("games");
        data.kills = section.getInt("kills");
        data.deaths = section.getInt("deaths");
        data.wins = section.getInt("wins");
        data.losses = section.getInt("losses", data.games - data.wins);

        MAP.put(uuid, data);
    }

    public static void updateStats(GamePlayer gamePlayer) {
        if (gamePlayer.getGame().phase == 0) return;
        PlayerStats stats = get(gamePlayer.as().getUniqueId());
        stats.games++;
        stats.kills += gamePlayer.getKills();
        stats.deaths += gamePlayer.getDeaths();
        if (gamePlayer.isWinner()) stats.wins++;
        else stats.losses++;
        Stats.set(stats);
    }

    public static List<PlayerStats> sorted() {
        List<PlayerStats> sorted = new ArrayList<>(MAP.values());
        sorted.sort(Comparator.comparingInt(stats -> -stats.kills));
        return sorted;
    }
}
