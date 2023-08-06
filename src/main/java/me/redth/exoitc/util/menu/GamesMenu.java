package me.redth.exoitc.util.menu;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.Game;
import me.redth.exoitc.game.GamePhase;
import me.redth.exoitc.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class GamesMenu extends CustomMenu {
    public static final ItemStack BACKGROUND = new ItemBuilder(Material.STAINED_GLASS_PANE).setDamage((short) 7).setName(" ").build();

    public GamesMenu(Player player) {
        super(player, Messages.MENU_GAME_TITLE.get(), 6);
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, BACKGROUND);
        }
        for (int i = 9; i < 45; i += 9) {
            inventory.setItem(i, BACKGROUND);
        }
        for (int i = 17; i < 53; i += 9) {
            inventory.setItem(i, BACKGROUND);
        }
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, BACKGROUND);
        }
        for (Game game : Game.GAMES.values()) {
            if (game.isDuel) continue;
            inventory.addItem(getGameItem(game));
        }
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!inventory.equals(e.getClickedInventory())) return;
        if (e.getCurrentItem() == null) return;
        if (!e.getCurrentItem().getItemMeta().hasLore()) return;
        String id = e.getCurrentItem().getItemMeta().getLore().get(3).split(" ")[1].replace("§", "");
        Game game = Game.GAMES.get(id);
        if (game != null) game.join(player);
    }

    public static ItemStack getGameItem(Game game) {
        ItemBuilder itemBuilder = new ItemBuilder(game.icon).setDamage(game.iconDamage);
        itemBuilder.setName("§7§m-------");
        itemBuilder.setLore(
                Messages.MENU_GAME_NAME.get(game.name),
                Messages.MENU_GAME_PLAYERS.get(String.valueOf(game.audiences.size()), String.valueOf(game.maxPlayers)),
                ((game.phase == GamePhase.QUEUE) ? Messages.MENU_GAME_JOINABLE : Messages.MENU_GAME_IN_PROGRESS).get(),
                "§7§m------- " + game.id.replaceAll("(?!$)", "§")
        );
        if (game.phase == GamePhase.IN_PROGRESS) {
            itemBuilder.addEnchantment(Enchantment.DURABILITY, 1);
            itemBuilder.addFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return itemBuilder.build();
    }
}
