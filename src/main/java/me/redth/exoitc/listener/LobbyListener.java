package me.redth.exoitc.listener;

import me.redth.exoitc.ExOITC;
import me.redth.exoitc.player.OITCPlayer;
import me.redth.exoitc.player.state.LobbyState;
import me.redth.exoitc.util.item.HeldItem;
import me.redth.exoitc.util.menu.ClickableMenu;
import me.redth.exoitc.util.menu.ClosableMenu;
import me.redth.exoitc.util.menu.DraggableMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class LobbyListener implements Listener {

    @EventHandler
    public static void onWeather(WeatherChangeEvent e) {
        e.setCancelled(e.toWeatherState());
    }

    @EventHandler
    public static void onItemClicked(PlayerInteractEvent e) {
        if (e.getItem() != null) {
            if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
            for (HeldItem item : HeldItem.ITEM) {
                if (!item.is(e.getItem())) continue;
                item.rightClick(OITCPlayer.get(e.getPlayer()));
                e.setUseInteractedBlock(Event.Result.DENY);
                e.setUseItemInHand(Event.Result.DENY);
                return;
            }
        }
        if (e.getAction() == Action.PHYSICAL) {
            switch (e.getClickedBlock().getType()) {
                case GOLD_PLATE:
                    jump(e.getPlayer(), 16, false);
                    break;
                case IRON_PLATE:
                    jump(e.getPlayer(), 8, true);
                    break;
            }
        }
    }

    public static void jump(Player player, int level, boolean boost) {
        boolean hadJump = player.hasPotionEffect(PotionEffectType.JUMP);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 15, level, false, false), true);
        if (hadJump) return;
        Runnable runnable = () -> {
            double motionY = 0.42 + (level + 1) * 0.1;
            Vector baseVelo = boost ? player.getLocation().getDirection() : player.getVelocity();
            player.setVelocity(baseVelo.setY(motionY));
            player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1.0F, 1.0F);
        };
        ExOITC.scheduleDelayed(runnable, 1L);
    }

    @EventHandler
    public static void onMenuClicked(InventoryClickEvent e) {
        InventoryView holder = e.getView();
        if (holder instanceof ClickableMenu)
            ((ClickableMenu) holder).onClick(e);
    }

    @EventHandler
    public static void onMenuDragged(InventoryDragEvent e) {
        InventoryView holder = e.getView();
        if (holder instanceof DraggableMenu)
            ((DraggableMenu) holder).onDrag(e);
    }

    @EventHandler
    public static void onMenuClosed(InventoryCloseEvent e) {
        InventoryView holder = e.getView();
        if (holder instanceof ClosableMenu)
            ((ClosableMenu) holder).onClose(e);
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        LobbyState.apply(OITCPlayer.get(player));
    }

}
