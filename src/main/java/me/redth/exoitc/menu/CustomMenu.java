package me.redth.exoitc.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public abstract class CustomMenu extends InventoryView {
    protected final Player player;
    protected final Inventory inventory;

    public CustomMenu(Player player, String title, int rows) {
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 9 * rows, title);
    }

    public void display() {
        player.openInventory(this);
    }

    public abstract void onInteract(InventoryClickEvent e);

    public void onDrag(InventoryDragEvent e) {
    }

    public void onClose(InventoryCloseEvent e) {
    }

    @Override
    public Inventory getTopInventory() {
        return inventory;
    }

    @Override
    public Inventory getBottomInventory() {
        return player.getInventory();
    }

    @Override
    public HumanEntity getPlayer() {
        return player;
    }

    @Override
    public InventoryType getType() {
        return inventory.getType();
    }

}
