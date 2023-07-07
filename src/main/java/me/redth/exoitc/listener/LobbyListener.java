package me.redth.exoitc.listener;

import me.redth.exoitc.game.GameKit;
import me.redth.exoitc.util.item.HeldItem;
import me.redth.exoitc.util.visual.menu.CustomMenu;
import me.redth.exoitc.util.sign.GameSign;
import me.redth.exoitc.util.sign.LeaderboardSign;
import me.redth.exoitc.util.visual.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.InventoryView;

public class LobbyListener implements Listener {

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
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!(e.getClickedBlock().getState() instanceof Sign)) return;
        GameSign.onSignClicked(e.getPlayer(), (Sign) e.getClickedBlock().getState());
    }

    @EventHandler
    public static void onMenuClicked(InventoryClickEvent e) {
        InventoryView view = e.getView();
        if (view instanceof CustomMenu)
            ((CustomMenu) view).onInteract(e);
    }


    @EventHandler
    public static void onMenuDragged(InventoryDragEvent e) {
        InventoryView view = e.getView();
        if (view instanceof CustomMenu)
            ((CustomMenu) view).onDrag(e);
    }

    @EventHandler
    public static void onMenuClose(InventoryCloseEvent e) {
        InventoryView view = e.getView();
        if (view instanceof CustomMenu)
            ((CustomMenu) view).onClose(e);
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        Sidebar.lobby(player);
        GameKit.lobby(player);
//        player.getInventory().setItem(1, null);

    }

    @EventHandler
    public void signPlaced(SignChangeEvent e) {
        GameSign.onSignPlaced(e);
        LeaderboardSign.onSignPlaced(e);
    }


}
