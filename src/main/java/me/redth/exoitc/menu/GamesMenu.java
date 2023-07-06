package me.redth.exoitc.menu;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.Game;
import me.redth.exoitc.item.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class GamesMenu extends CustomMenu {
    public GamesMenu(Player player) {
        super(player, Messages.MENU_GAME_TITLE.get(), Game.GAMES.size() / 9 + 1);
        for (Game game : Game.GAMES.values()) {
            inventory.addItem(getGameItem(game));
        }
    }

    @Override
    public void onInteract(InventoryClickEvent e) {
        e.setCancelled(true);
        if (e.getClickedInventory() != e.getInventory()) return;
        if (e.getCurrentItem() == null) return;
        if (!e.getCurrentItem().getItemMeta().hasLore()) return;
        String id = e.getCurrentItem().getItemMeta().getLore().get(0).substring(2);
        Game game = Game.GAMES.get(id);
        if (game != null) game.joinQueue(player);
    }

    public static ItemStack getGameItem(Game game) {
        ItemBuilder itemBuilder = new ItemBuilder(game.icon).setDamage(game.iconDamage);
        itemBuilder.setName(Messages.MENU_GAME_NAME.get(game.name));
        itemBuilder.setLore(
                "§0" + game.id,
                ((game.phase == 0) ? Messages.MENU_GAME_JOINABLE : Messages.MENU_GAME_IN_PROGRESS).get(),
                Messages.MENU_GAME_PLAYERS.get(String.valueOf(game.players.size()), String.valueOf(game.maxPlayer))
        );
        if (game.phase != 0) {
            itemBuilder.addEnchantment(Enchantment.DURABILITY, 1);
            itemBuilder.addFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return itemBuilder.build();
    }
}
