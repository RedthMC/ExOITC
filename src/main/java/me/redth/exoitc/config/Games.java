package me.redth.exoitc.config;

import me.redth.exoitc.game.Game;
import me.redth.exoitc.util.GameSign;
import org.bukkit.Bukkit;
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
                game.name = section.getString("name", key);
                String[] split = section.getString("icon", "WOOL:0").split(":");
                game.icon = Material.valueOf(split[0]);
                game.iconDamage = Short.parseShort(split[1]);
                game.minPlayer = section.getInt("min-player", 2);
                game.maxPlayer = section.getInt("max-player", 12);
                game.setLobby((Location) section.get("lobby", Bukkit.getWorld("world").getSpawnLocation()));
                game.addSpawns((List<Location>) section.getList("spawns", Collections.emptyList()));
                GameSign.loadGameSign(game, (Location) section.get("sign"));
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
            yaml.set(game.id + ".min-player", game.minPlayer);
            yaml.set(game.id + ".max-player", game.maxPlayer);
            yaml.set(game.id + ".lobby", game.queueLobby);
            yaml.set(game.id + ".spawns", game.spawns);
            if (game.lobbySign != null) yaml.set(game.id + ".sign", game.lobbySign.sign);
        }

        config.save();
    }


}
