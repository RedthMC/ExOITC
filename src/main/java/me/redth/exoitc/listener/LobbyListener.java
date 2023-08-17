package me.redth.exoitc.listener;

import me.redth.exoitc.config.Config;
import me.redth.exoitc.game.GameKit;
import me.redth.exoitc.util.item.HeldItem;
import me.redth.exoitc.util.menu.ClickableMenu;
import me.redth.exoitc.util.menu.ClosableMenu;
import me.redth.exoitc.util.menu.DraggableMenu;
import me.redth.exoitc.util.visual.Sidebar;
import org.bukkit.Bukkit;
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
                item.rightClick(e.getPlayer());
                e.setUseInteractedBlock(Event.Result.DENY);
                e.setUseItemInHand(Event.Result.DENY);
                return;
            }
        }
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
    }

}
