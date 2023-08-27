package me.redth.exoitc.util.item;

import me.redth.exoitc.player.OITCPlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HeldItem {
    public static final List<HeldItem> ITEM = new ArrayList<>();
    private final ItemStack item;
    private final ClickHandler onRightClick;

    protected HeldItem(ItemStack item, ClickHandler onRightClick) {
        this.item = item;
        this.onRightClick = onRightClick;
    }

    public void rightClick(OITCPlayer player) {
        if (onRightClick == null) return;
        if (player.lastTimeClickedItem + 500L >= System.currentTimeMillis()) return;
        onRightClick.click(player);
        player.lastTimeClickedItem = System.currentTimeMillis();
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean is(ItemStack item) {
        return this.item.isSimilar(item);
    }

    public interface ClickHandler {
        void click(OITCPlayer player);
    }

}
