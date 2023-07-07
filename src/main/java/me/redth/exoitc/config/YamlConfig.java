package me.redth.exoitc.config;

import me.redth.exoitc.ExOITC;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class YamlConfig {
    public final String filename;
    public final File file;
    public YamlConfiguration config;

    public YamlConfig(String name) {
        filename = name + ".yml";
        file = new File(ExOITC.getInstance().getDataFolder(), filename);
    }

    public YamlConfiguration load() {
        ExOITC.getInstance().saveResource(filename, false);
        return config = YamlConfiguration.loadConfiguration(file);
    }

    public void serializeLocationIfPresent(String path, Consumer<Location> setter) {
        if (config.contains(path)) setter.accept((Location) config.get(path));
    }

    public void serializeIntIfPresent(String path, IntConsumer setter) {
        if (config.contains(path)) setter.accept(config.getInt(path));
    }

    public void serializeStringIfPresent(String path, Consumer<String> setter) {
        if (config.contains(path)) setter.accept(config.getString(path));
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
