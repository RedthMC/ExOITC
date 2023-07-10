package me.redth.exoitc.game;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.data.PlayerHotbar;
import me.redth.exoitc.util.item.ItemBuilder;
import me.redth.exoitc.util.visual.menu.GamesMenu;
import me.redth.exoitc.util.visual.menu.HotbarMenu;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GameKit {
    public static final ItemStack STONE_SWORD = new ItemBuilder(Material.STONE_SWORD).setUnbreakable(true).build();
    public static final ItemStack BOW = new ItemBuilder(Material.BOW).setUnbreakable(true).addEnchantment(Enchantment.ARROW_DAMAGE, 255).build();
    public static final ItemStack ARROW = new ItemBuilder(Material.ARROW).build();

    public static final ItemStack LEAVE = new ItemBuilder(Material.INK_SACK).setDamage((short) 1).setName(Messages.LEAVE_ITEM.get()).build(Participant::leave);
    public static final ItemStack FORCE_START = new ItemBuilder(Material.DIAMOND).setName("§b強制開始").build(player -> {
        if (Participant.isParticipating(player)) {
            Participant.of(player).getGame().checkQueue();
        }
    });
    public static final ItemStack GAMES = new ItemBuilder(Material.BOW).setName(Messages.MENU_GAME_TITLE.get()).build(player -> new GamesMenu(player).display());
    public static final ItemStack HOTBAR = new ItemBuilder(Material.CHEST).setName(Messages.MENU_HOTBAR_TITLE.get()).build(player -> new HotbarMenu(player).display());


    public static void apply(GamePlayer player) {
        PlayerInventory inventory = player.as().getInventory();
        PlayerHotbar hotbar = PlayerHotbar.CACHE.getUnchecked(player.as().getUniqueId());
        inventory.clear();
        inventory.setItem(hotbar.sword, STONE_SWORD);
        inventory.setItem(hotbar.bow, BOW);
        inventory.setItem(hotbar.arrow, ARROW);
    }

    public static void arrow(GamePlayer player) {
        PlayerInventory inventory = player.as().getInventory();
        PlayerHotbar hotbar = PlayerHotbar.CACHE.getUnchecked(player.as().getUniqueId());
        if (inventory.getItem(hotbar.arrow) == null)
            inventory.setItem(hotbar.arrow, ARROW);
        else
            inventory.addItem(ARROW);
    }

    public static void queue(GamePlayer player) {
        PlayerInventory inventory = player.as().getInventory();
        inventory.clear();
        inventory.setItem(7, FORCE_START);
        inventory.setItem(8, LEAVE);
    }

    public static void spectate(Player player) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setItem(8, LEAVE);
    }

    public static void lobby(Player player) {
        PlayerInventory inventory = player.getInventory();
        inventory.setItem(2, GAMES);
        inventory.setItem(3, HOTBAR);
    }
}
