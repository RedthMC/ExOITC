package me.redth.exoitc.listener;

import me.redth.exoitc.game.Participant;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class IngameListener implements Listener {
    public static boolean inGame(Player player) {
        return Participant.isParticipating(player);
    }

    @EventHandler
    public void noDrop(PlayerDropItemEvent e) {
        if (inGame(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void noPickup(PlayerPickupItemEvent e) {
        if (inGame(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void noBlockBreak(BlockBreakEvent e) {
        if (inGame(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public static void noUseBlock(PlayerInteractEvent e) {
        if (inGame(e.getPlayer())) e.setUseInteractedBlock(Event.Result.DENY);
    }

    @EventHandler
    public void noHunger(FoodLevelChangeEvent e) {
        if (inGame(((Player) e.getEntity()))) e.setFoodLevel(20);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        if (!inGame(player)) return;
        Participant.of(player).onDamage(e);
    }

    @EventHandler
    public void clearArrow(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow) {
            e.getEntity().remove();
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (!inGame(e.getPlayer())) return;
        e.setRespawnLocation(Participant.of(e.getPlayer()).getRespawnLocation());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (!inGame(e.getEntity())) return;

        Participant player = Participant.of(e.getEntity());
        player.onDeath();
        e.setKeepInventory(true);
        e.setDeathMessage(null);
        e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), 0.0f);


        Player killer = player.as().getKiller();
        if (killer == null) return;
        if (e.getEntity().equals(killer)) return;
        if (!inGame(killer)) return;

        Participant gameKiller = Participant.of(killer);
        gameKiller.onKill(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Participant.leave(e.getPlayer());
    }
}
