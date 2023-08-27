package me.redth.exoitc.player.state;

import me.redth.exoitc.config.Config;
import me.redth.exoitc.game.GameKit;
import me.redth.exoitc.player.OITCPlayer;
import me.redth.exoitc.util.visual.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class LobbyState implements State {
    private static final LobbyState instance = new LobbyState();

    public static void apply(OITCPlayer oitcPlayer) {
        Player player = oitcPlayer.player;

        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExp(0);
        player.setLevel(0);
        player.spigot().setCollidesWithEntities(true);
        player.teleport(Config.getLobby());
        Sidebar.lobby(player);
        GameKit.lobby(player);

        oitcPlayer.state = instance;
    }

    @Override
    public void onDamaged(OITCPlayer player, EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.VOID || e.getCause() == EntityDamageEvent.DamageCause.SUICIDE) {
            player.player.teleport(Config.getLobby());
        }
        e.setCancelled(true);
    }

    @Override
    public void onRemoved(OITCPlayer player) {
    }
}
