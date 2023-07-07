package me.redth.exoitc.util.visual.menu;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.data.PlayerHotbar;
import me.redth.exoitc.game.GameKit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class HotbarMenu extends CustomMenu {
    public HotbarMenu(Player player) {
        super(player, Messages.MENU_HOTBAR_TITLE.get(), 1);
        PlayerHotbar hotbar = PlayerHotbar.CACHE.getUnchecked(player.getUniqueId());
        inventory.setItem(hotbar.sword, GameKit.STONE_SWORD);
        inventory.setItem(hotbar.bow, GameKit.BOW);
        inventory.setItem(hotbar.arrow, GameKit.ARROW);
    }

    @Override
    public void onInteract(InventoryClickEvent e) {
        if (e.getClickedInventory() != e.getInventory()) e.setCancelled(true);
        if (e.getAction() != InventoryAction.PICKUP_ALL && e.getAction() != InventoryAction.PLACE_ALL && e.getAction() != InventoryAction.SWAP_WITH_CURSOR) e.setCancelled(true);
    }

    public void onDrag(InventoryDragEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent e) {
        int sword = inventory.first(Material.STONE_SWORD);
        int bow = inventory.first(Material.BOW);
        int arrow = inventory.first(Material.ARROW);

        player.setItemOnCursor(null);

        if (sword == -1 || bow == -1 || arrow == -1) {
            Messages.MENU_HOTBAR_ERROR.send(player);
            return;
        }
        PlayerHotbar.CACHE.getUnchecked(player.getUniqueId()).saveHotbar(sword, bow, arrow);
        Messages.MENU_HOTBAR_SAVED.send(player);
    }

}
