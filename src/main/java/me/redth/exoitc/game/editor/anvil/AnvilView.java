package me.redth.exoitc.game.editor.anvil;

import me.redth.exoitc.util.item.ItemBuilder;
import me.redth.exoitc.game.editor.EditorMenu;
import me.redth.exoitc.util.menu.ClickableMenu;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryView;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class AnvilView extends CraftInventoryView implements ClickableMenu {
    private final EditorMenu editorMenu;

    public AnvilView(EditorMenu editorMenu, CraftInventoryView view) {
        super(view.getPlayer(), view.getTopInventory(), view.getHandle());
        this.editorMenu = editorMenu;
        getTopInventory().setItem(0, new ItemBuilder(Material.NAME_TAG).setName(editorMenu.game.name).build());
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (e.getSlotType() != InventoryType.SlotType.RESULT) return;

        editorMenu.setName(Optional.ofNullable(getItem(2))
                .map(ItemStack::getItemMeta)
                .map(ItemMeta::getDisplayName)
                .orElse(editorMenu.game.name));

        editorMenu.display();
    }
}
