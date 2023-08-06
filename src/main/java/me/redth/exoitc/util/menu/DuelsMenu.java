package me.redth.exoitc.util.menu;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.duel.DuelRequest;
import me.redth.exoitc.game.Game;
import me.redth.exoitc.game.GamePhase;
import me.redth.exoitc.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class DuelsMenu extends CustomMenu {
    public static final ItemStack BACKGROUND = new ItemBuilder(Material.STAINED_GLASS_PANE).setDamage((short) 7).setName(" ").build();
    public static final ItemStack RANDOM = new ItemBuilder(Material.MAP).setName("§bRandom Map").build();

    private final Player accepter;

    public DuelsMenu(Player player, Player accepter) {
        super(player, "§cDuel: §3" + accepter.getName(), 6);
        this.accepter = accepter;
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
        inventory.addItem(RANDOM);
        for (Game game : Game.GAMES.values()) {
            if (!game.isDuel) continue;
            if (game.phase != GamePhase.QUEUE) continue;
            inventory.addItem(getGameItem(game));
        }
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!inventory.equals(e.getClickedInventory())) return;
        if (e.getCurrentItem() == null) return;
        if (!e.getCurrentItem().hasItemMeta()) return;
        if (!"§7§m------- ".equals(e.getCurrentItem().getItemMeta().getDisplayName())) {
            if ("§bRandom Map".equals(e.getCurrentItem().getItemMeta().getDisplayName())) {
                DuelRequest.request(null, player, accepter);
            }
            return;
        }
        if (!e.getCurrentItem().getItemMeta().hasLore()) return;
        String id = e.getCurrentItem().getItemMeta().getLore().get(1).split(" ")[1].replace("§", "");
        Game game = Game.GAMES.get(id);
        DuelRequest.request(game, player, accepter);
    }

    public static ItemStack getGameItem(Game game) {
        ItemBuilder itemBuilder = new ItemBuilder(game.icon).setDamage(game.iconDamage);
        itemBuilder.setName("§7§m------- ");
        itemBuilder.setLore(Messages.MENU_GAME_NAME.get(game.name), "§7§m------- " + game.id.replaceAll("(?!$)", "§"));
        return itemBuilder.build();
    }
}
