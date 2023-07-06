package me.redth.exoitc.config;

import me.redth.exoitc.data.PlayerStats;
import me.redth.exoitc.util.LeaderboardSign;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.UUID;

public class Stats {
    private static final YamlConfig config = new YamlConfig("stats");
    private static boolean dirty;

    public static void load() {
        YamlConfiguration yaml = config.load();
        for (String key : yaml.getKeys(false)) {
            try {
                PlayerStats.load(UUID.fromString(key), yaml.getConfigurationSection(key));
            } catch (Exception e) {

            }
        }

        LeaderboardSign.update();
    }

    public static void save() {
        if (!dirty) return;
        config.save();
    }

    public static void set(PlayerStats stats) {
        YamlConfiguration yaml = config.config;
        yaml.set(stats.uuid + ".games", stats.games);
        yaml.set(stats.uuid + ".kills", stats.kills);
        yaml.set(stats.uuid + ".deaths", stats.deaths);
        yaml.set(stats.uuid + ".wins", stats.wins);
        yaml.set(stats.uuid + ".losses", stats.losses);

        dirty = true;
    }
}
