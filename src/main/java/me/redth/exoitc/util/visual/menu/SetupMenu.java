package me.redth.exoitc.util.visual.menu;

import me.redth.exoitc.config.Games;
import me.redth.exoitc.game.Game;
import me.redth.exoitc.util.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;

public class SetupMenu extends CustomMenu {

    private final Game game;
    private String name;
    private int minPlayer;
    private int maxPlayer;
    private Material icon;
    private short iconDamage;
    private Location lobby;
    private List<Location> spawns;


    public SetupMenu(Player player, Game game) {
        super(player, "Setup" + game.name, 3);
        this.game = game;
        name = game.name;
        minPlayer = game.minPlayer;
        maxPlayer = game.maxPlayer;
        icon = game.icon;
        iconDamage = game.iconDamage;
        lobby = game.queueLobby;
        spawns = game.spawns;
        updateName();
        updateMinPlayers();
        updateMaxPlayers();
        updateIcon();
        inventory.setItem(11, new ItemBuilder(Material.ARMOR_STAND).setName("§bAdd spawn").build());
        inventory.setItem(12, new ItemBuilder(Material.NETHER_STAR).setName("§bSet lobby").build());
        inventory.setItem(26, new ItemBuilder(Material.EMERALD_BLOCK).setName("§aSave").build());
    }

    public void updateName() {
        inventory.setItem(3, new ItemBuilder(Material.NAME_TAG).setName("§bName: " + name).build());
    }

    public void updateMinPlayers() {
        inventory.setItem(5, new ItemBuilder(Material.DIAMOND_BLOCK).setName("§bMin Players: " + minPlayer).build());
    }

    public void updateMaxPlayers() {
        inventory.setItem(6, new ItemBuilder(Material.REDSTONE_BLOCK).setName("§bMax Players: " + maxPlayer).build());
    }

    public void updateIcon() {
        inventory.setItem(8, new ItemBuilder(icon).setDamage(iconDamage).setName("§bIcon").build());
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        switch (e.getSlot()) {
            case 3:
                new RenameScreen(this).display();
                break;
            case 5:
                if (e.getClick() == ClickType.LEFT) {
                    minPlayer++;
                } else if (e.getClick() == ClickType.RIGHT) {
                    minPlayer--;
                }
                updateMinPlayers();
                break;
            case 6:
                if (e.getClick() == ClickType.LEFT) {
                    maxPlayer++;
                } else if (e.getClick() == ClickType.RIGHT) {
                    maxPlayer--;
                }
                updateMaxPlayers();
                break;
            case 26:
                save();
        }
    }

    public void save() {
        game.name = name;
        game.minPlayer = minPlayer;
        game.maxPlayer = maxPlayer;
        game.icon = icon;
        game.iconDamage = iconDamage;
        game.queueLobby = lobby;
        game.spawns = spawns;
        Games.save();
        player.closeInventory();
    }

    public static class RenameScreen extends CustomMenu {
        private final SetupMenu parent;

        public RenameScreen(SetupMenu parent) {
            super(parent.player, "Anvil", InventoryType.ANVIL);
            this.parent = parent;
            inventory.setItem(0, new ItemBuilder(parent.icon).setDamage(parent.iconDamage).setName(parent.name).build());
        }

        @Override
        public void onClick(InventoryClickEvent e) {
            if (e.getSlotType() == InventoryType.SlotType.RESULT) {
                parent.name = e.getCurrentItem().getItemMeta().getDisplayName();
                parent.updateName();
                parent.display();
            }
        }
    }
}
