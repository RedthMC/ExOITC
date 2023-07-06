package me.redth.exoitc.config;

import me.redth.exoitc.data.PlayerHotbar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.UUID;

public class Hotbars {
    private static final YamlConfig config = new YamlConfig("hotbars");
    private static boolean dirty;

    public static void load() {
        config.load();
    }

    public static void save() {
        if (!dirty) return;
        config.save();
    }

    public static ConfigurationSection get(UUID player) {
        YamlConfiguration yaml = config.config;
        return yaml.getConfigurationSection(player.toString());
    }


    public static void set(PlayerHotbar hotbar) {
        YamlConfiguration yaml = config.config;
        yaml.set(hotbar.uuid + ".sword", hotbar.sword);
        yaml.set(hotbar.uuid + ".bow", hotbar.bow);
        yaml.set(hotbar.uuid + ".arrow", hotbar.arrow);

        dirty = true;
    }
}
