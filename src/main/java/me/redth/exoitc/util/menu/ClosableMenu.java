package me.redth.exoitc.util.menu;

import org.bukkit.event.inventory.InventoryCloseEvent;

public interface ClosableMenu {
    void onClose(InventoryCloseEvent e);
}
