package me.redth.exoitc.listener;

import me.redth.exoitc.config.Config;
import me.redth.exoitc.game.audience.Audience;
import me.redth.exoitc.game.audience.DamageCallback;
import me.redth.exoitc.game.audience.GamePlayer;
import me.redth.exoitc.game.Game;
import me.redth.exoitc.game.editor.EditorMenu;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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

public class IngameListener implements Listener, DamageCallback {

    public static boolean shouldRestrict(Player player) {
        return player.getGameMode() != GameMode.CREATIVE || Audience.isWatching(player);
    }

    public DamageCallback gamePlayerElseAudience(Player player) {
        DamageCallback callback = GamePlayer.of(player);
        if (callback == null) callback = Audience.of(player);
        if (callback == null) callback = this;
        return callback;
    }

    @EventHandler
    public void noDrop(PlayerDropItemEvent e) {
        if (shouldRestrict(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void noPickup(PlayerPickupItemEvent e) {
        if (shouldRestrict(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void noBlockBreak(BlockBreakEvent e) {
        if (shouldRestrict(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void noUseBlock(PlayerInteractEvent e) {
        if (shouldRestrict(e.getPlayer())) e.setUseInteractedBlock(Event.Result.DENY);
    }

    @EventHandler
    public void noHunger(FoodLevelChangeEvent e) {
        if (shouldRestrict(((Player) e.getEntity()))) e.setFoodLevel(20);
    }

    @EventHandler
    public void onDamageHandle(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        DamageCallback callback = gamePlayerElseAudience(player);
        callback.onDamage(e);
    }

    @EventHandler
    public void clearArrow(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow) {
            e.getEntity().remove();
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        ((CraftPlayer) e.getPlayer()).getHandle().o(0); // clear arrow
        DamageCallback callback = gamePlayerElseAudience(e.getPlayer());
        e.setRespawnLocation(callback.getRespawnLocation());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        GamePlayer player = GamePlayer.of(e.getEntity());
        if (player == null) return;

        player.onDeath();
        e.setKeepInventory(true);
        e.setDeathMessage(null);
        e.setNewExp(0);
        e.setNewLevel(0);
        e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), 0.0f);

        Player killer = player.as().getKiller();
        if (killer == null) return;
        if (e.getEntity().equals(killer)) return;

        GamePlayer gameKiller = GamePlayer.of(killer);
        if (gameKiller == null) return;

        gameKiller.onKill(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Game.leave(e.getPlayer());
        EditorMenu menu = EditorMenu.editors.get(e.getPlayer().getUniqueId());
        if (menu != null) menu.save();
    }

    @Override
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.VOID || e.getCause() == EntityDamageEvent.DamageCause.SUICIDE) {
            e.getEntity().teleport(Config.getLobby());
        }
        e.setCancelled(true);
    }

    @Override
    public Location getRespawnLocation() {
        return Config.getLobby();
    }
}
