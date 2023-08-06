package me.redth.exoitc.game.editor;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.Game;
import me.redth.exoitc.util.item.ItemBuilder;
import me.redth.exoitc.util.menu.CustomMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class EditListMenu extends CustomMenu {
    public static final ItemStack CREATE_NEW = new ItemBuilder(Material.STAINED_GLASS).setDamage((short) 5).setName("§aCreate new game").build();

    public EditListMenu(Player player) {
        super(player, "§aSetup", 6);

        inventory.addItem(CREATE_NEW);
        for (Game game : Game.GAMES.values()) {
            inventory.addItem(getGameItem(game));
        }
    }

    public static boolean isEmpty(Game game) {
        return game.audiences.isEmpty();
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!inventory.equals(e.getClickedInventory())) return;
        if (e.getRawSlot() == 0) {
            new EditorMenu(player, String.valueOf(Game.GAMES.size() + 1));
            close();
            return;
        }
        Optional.ofNullable(e.getCurrentItem())
                .map(EditListMenu::zeroToNull)
                .map(ItemStack::getItemMeta)
                .map(ItemMeta::getDisplayName)
                .map(Game.GAMES::remove)
                .ifPresent(game -> {
                    new EditorMenu(player, game);
                    close();
                });
//        if (e.getCurrentItem() == null) return;
//        if (e.getCurrentItem().getAmount() == 0) return;
//        if (!e.getCurrentItem().getItemMeta().hasDisplayName()) return;
//        String id = e.getCurrentItem().getItemMeta().getDisplayName();
//
//        Game game = Game.GAMES.remove(id);
//        if (game != null) new EditorMenu(player, game);
    }

    public static ItemStack getGameItem(Game game) {
        ItemBuilder itemBuilder = new ItemBuilder(game.icon)
                .setDamage(game.iconDamage)
                .setName(game.id)
                .setLore(
                        Messages.MENU_GAME_NAME.get(game.name),
                        isEmpty(game) ? "§aClick to Edit" : "§eHas Players!"
                );
        if (!isEmpty(game)) itemBuilder.setCount(0);
        return itemBuilder.build();
    }

    public static ItemStack zeroToNull(ItemStack item) {
        if (item == null) return null;
        if (item.getAmount() == 0) return null;
        return item;
    }
}
