package me.redth.exoitc.util.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HeldItem {
    public static final List<HeldItem> ITEM = new ArrayList<>();
    public static final Map<UUID, Long> LAST_CLICKED = new HashMap<>();
    private final ItemStack item;
    //    private ClickHandler onLeftClick;
    private final ClickHandler onRightClick;

    protected HeldItem(ItemStack item, ClickHandler onRightClick) {
        this.item = item;
        this.onRightClick = onRightClick;
    }

//    public HeldItem setOnLeftClick(ClickHandler action) {
//        onLeftClick = action;
//        return this;
//    }
//
//    public HeldItem setOnRightClick(ClickHandler action) {
//        onRightClick = action;
//        return this;
//    }

//    public void leftClick(Player player) {
//        if (onLeftClick == null) return;
//        onLeftClick.click(player);
//    }

    public void rightClick(Player player) {
        if (onRightClick == null) return;
        LAST_CLICKED.entrySet().removeIf(entry -> entry.getValue() + 500L < System.currentTimeMillis());
        if (LAST_CLICKED.containsKey(player.getUniqueId())) return;
        onRightClick.click(player);
        LAST_CLICKED.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public boolean is(ItemStack item) {
        return this.item.isSimilar(item);
    }

    public interface ClickHandler {
        void click(Player player);
    }

}
