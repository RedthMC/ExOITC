package me.redth.exoitc.config;

import me.redth.exoitc.ExOITC;
import me.redth.exoitc.util.sign.LeaderboardSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    public static Location lobbySpawn;

    public static void load() {
        ExOITC.getInstance().saveDefaultConfig();
        ExOITC.getInstance().reloadConfig();

        FileConfiguration config = ExOITC.getInstance().getConfig();

        lobbySpawn = (Location) config.get("lobby-spawn", Bukkit.getWorld("world").getSpawnLocation());


        Messages.load();
        Games.load();
        Stats.load();
        Hotbars.load();
        LeaderboardSign.load(config.getConfigurationSection("leaderboard-signs"));

        ExOITC.scheduleDelayedRepeating(() -> {
            Stats.save();
            Hotbars.save();
        }, 6000, 6000);
//        loadItem(config.getConfigurationSection("item"));
//        loadMenu(config.getConfigurationSection("menu"));

    }

    public static void setLobbySpawn(Location location) {
        lobbySpawn = location;
        ExOITC.getInstance().getConfig().set("lobby-spawn", lobbySpawn);
    }

    public static void save() {
        Stats.save();
        Hotbars.save();
    }

    public static void saveDefault() {
        ExOITC.getInstance().saveConfig();
    }

//    public static void loadItem(ConfigurationSection section) {
//        ItemBuilder.fromSection(section).buildHeldItem("menu_item").setOnLeftClick(player -> LobbyMenu.MAIN_MENU.display(player));
//    }
//
//    public static void loadMenu(ConfigurationSection section) {
//        MenuBuilder menuBuilder = new MenuBuilder("server_selector", section.getString("name"), section.getInt("rows"));
//        ConfigurationSection items = section.getConfigurationSection("items");
//        for (String child : items.getKeys(false)) {
//            ConfigurationSection childSection = items.getConfigurationSection(child);
//            MenuButton button = ItemBuilder.fromSection(childSection).buildButton(child).setOnLeftClick(player -> PlayerUtils.sendTo(player, childSection.getString("server")));
//            menuBuilder.set(childSection.getInt("slot"), button);
//        }
//    }

}
