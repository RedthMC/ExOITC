package me.redth.exoitc.config;

import me.redth.exoitc.game.Game;
import me.redth.exoitc.game.mode.GameGoal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collections;
import java.util.List;

public class Games {
    private static final YamlConfig config = new YamlConfig("games");

    public static void load() {
        YamlConfiguration yaml = config.load();

        for (String key : yaml.getKeys(false)) {
            try {
                ConfigurationSection section = yaml.getConfigurationSection(key);
                Game game = new Game(key);
                if (section.contains("name")) game.name = section.getString("name", key);
                if (section.contains("icon")) setIcon(game, section.getString("icon"));
                if (section.contains("min-player")) game.minPlayers = section.getInt("min-player");
                if (section.contains("max-player")) game.maxPlayers = section.getInt("max-player");
                if (section.contains("duel")) game.isDuel = section.getBoolean("duel");
                if (section.contains("lobby")) game.lobby = ((Location) section.get("lobby"));
                if (section.contains("spawns")) game.spawns = ((List<Location>) section.getList("spawns"));
                if (section.contains("goal")) game.goal = GameGoal.valueOfCatch(section.getString("goal"));
                Game.GAMES.put(key, game);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void remove(Game game) {
        config.config.set(game.id, null);
        config.save();
    }

    public static void save() {
        YamlConfiguration yaml = config.config;

        for (Game game : Game.GAMES.values()) {
            yaml.set(game.id + ".name", game.name);
            yaml.set(game.id + ".icon", game.icon.toString() + ":" + game.iconDamage);
            yaml.set(game.id + ".min-player", game.minPlayers);
            yaml.set(game.id + ".max-player", game.maxPlayers);
            yaml.set(game.id + ".duel", game.isDuel);
            yaml.set(game.id + ".lobby", game.lobby);
            yaml.set(game.id + ".spawns", game.spawns);
            yaml.set(game.id + ".goal", game.goal.toString());
        }

        config.save();
    }

    private static void setIcon(Game game, String icon) {
        String[] split = icon.split(":");
        game.icon = Material.valueOf(split[0]);
        game.iconDamage = Short.parseShort(split[1]);
    }

}
