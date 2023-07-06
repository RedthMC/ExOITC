package me.redth.exoitc;

import me.redth.exoitc.command.MainCommand;
import me.redth.exoitc.config.Config;
import me.redth.exoitc.listener.IngameListener;
import me.redth.exoitc.listener.LobbyListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ExOITC extends JavaPlugin {
    private static ExOITC instance;

    public static ExOITC getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        Config.load();
        getServer().getPluginManager().registerEvents(new LobbyListener(), this);
        getServer().getPluginManager().registerEvents(new IngameListener(), this);
        getCommand("oitc").setExecutor(new MainCommand(this));
    }

    public void onDisable() {
        Config.save();
        instance = null;
    }

    public static int scheduleDelayed(Runnable task, long delayTicks) {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(instance, task, delayTicks);
    }

    public static int scheduleRepeating(Runnable task, long intervalTicks) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, task, 0L, intervalTicks);
    }

    public static int scheduleDelayedRepeating(Runnable task, long delayTicks, long intervalTicks) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, task, delayTicks, intervalTicks);
    }

    public static int cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(taskId);
        return -1;
    }

}
