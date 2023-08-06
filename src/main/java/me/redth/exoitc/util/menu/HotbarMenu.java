package me.redth.exoitc.util.menu;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.data.PlayerHotbar;
import me.redth.exoitc.game.GameKit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class HotbarMenu extends CustomMenu implements ClosableMenu, DraggableMenu {
    public HotbarMenu(Player player) {
        super(player, Messages.MENU_HOTBAR_TITLE.get(), 1);
        PlayerHotbar hotbar = PlayerHotbar.CACHE.getUnchecked(player.getUniqueId());
        inventory.setItem(hotbar.sword, GameKit.STONE_SWORD);
        inventory.setItem(hotbar.bow, GameKit.BOW);
        inventory.setItem(hotbar.arrow, GameKit.ARROW);
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        if (e.getClick() != ClickType.LEFT || (!inventory.equals(e.getClickedInventory()))) e.setCancelled(true);
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

    @Override
    public void onDrag(InventoryDragEvent e) {
        if (e.getRawSlots().stream().anyMatch(i -> i > 8)) e.setCancelled(true);
    }
}
