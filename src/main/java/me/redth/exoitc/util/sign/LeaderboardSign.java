package me.redth.exoitc.util.sign;

import me.redth.exoitc.ExOITC;
import me.redth.exoitc.config.Config;
import me.redth.exoitc.config.Messages;
import me.redth.exoitc.data.PlayerStats;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.SignChangeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardSign {
    private static final Map<Integer, Location> SIGNS = new HashMap<>();
    private static List<PlayerStats> statsSorted = new ArrayList<>();

    public static void update() {
        statsSorted = PlayerStats.sorted();
        for (Map.Entry<Integer, Location> sign : SIGNS.entrySet()) {
            update(sign.getKey(), sign.getValue());
        }
    }

    public static void update(int rank, Location location) {
        BlockState state = location.getBlock().getState();
        if (!(state instanceof Sign)) return;
        Sign sign1 = ((Sign) state);
        setText(rank, sign1.getLines());
        sign1.update();
    }

    public static void setText(int rank, String[] lines) {
        PlayerStats stats = (rank > statsSorted.size()) ? null : statsSorted.get(rank - 1);
        lines[0] = Messages.SIGN_LEADERBOARD_RANK.get(String.valueOf(rank));
        lines[1] = Messages.SIGN_LEADERBOARD_NAME.get(stats == null ? "" : stats.name);
        lines[2] = Messages.SIGN_LEADERBOARD_KILLS.get(stats == null ? "" : String.valueOf(stats.kills));
        lines[3] = Messages.SIGN_LEADERBOARD_WINS.get(stats == null ? "" : String.valueOf(stats.wins));
    }

    public static void load(ConfigurationSection section) {
        try {
            if (section == null) return;
            for (String key : section.getKeys(false)) {
                SIGNS.put(Integer.parseInt(key), (Location) section.get(key));

            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void onSignPlaced(SignChangeEvent e) {
        if (!"[OITC#]".equals(e.getLine(0))) return;

        try {
            int i = Integer.parseInt(e.getLine(1));
            SIGNS.put(i, e.getBlock().getLocation());
            save();
            setText(i, e.getLines());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void save() {
        Configuration config = ExOITC.getInstance().getConfig();
        for (Map.Entry<Integer, Location> sign : SIGNS.entrySet()) {
            config.set("leaderboard-signs." + sign.getKey(), sign.getValue());
        }
        Config.saveDefault();
    }

}
