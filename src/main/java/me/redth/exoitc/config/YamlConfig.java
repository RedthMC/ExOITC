package me.redth.exoitc.config;

import me.redth.exoitc.ExOITC;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

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

    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
