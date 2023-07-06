package me.redth.exoitc.data;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.redth.exoitc.config.Hotbars;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerHotbar {
    public static final LoadingCache<UUID, PlayerHotbar> CACHE = CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.MINUTES).build(new CacheLoader<UUID, PlayerHotbar>() {
        @Override
        public PlayerHotbar load(UUID uuid) {
            ConfigurationSection section = Hotbars.get(uuid);
            PlayerHotbar data = new PlayerHotbar(uuid);
            if (section == null) return data;
            data.sword = section.getInt("sword");
            data.bow = section.getInt("bow");
            data.arrow = section.getInt("arrow");
            return data;
        }
    });
    public final UUID uuid;
    public int sword = 0;
    public int bow = 1;
    public int arrow = 2;

    public PlayerHotbar(UUID uuid) {
        this.uuid = uuid;
    }

    public void saveHotbar(int sword, int bow, int arrow) {
        this.sword = sword;
        this.bow = bow;
        this.arrow = arrow;
        Hotbars.set(this);
    }
}
