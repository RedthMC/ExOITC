package me.redth.exoitc.util.visual.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class CustomMenu implements InventoryHolder {
    protected final Player player;
    protected final Inventory inventory;

    public CustomMenu(Player player, String title, int rows) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 9 * rows, title);
    }

    public CustomMenu(Player player, String title, InventoryType type) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, type, title);
    }

    public void display() {
        player.openInventory(inventory);
    }

    public abstract void onClick(InventoryClickEvent e);

    public void onClose(InventoryCloseEvent e) {
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
