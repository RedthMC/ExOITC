package me.redth.exoitc.util.item;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        item = new ItemStack(material);
        meta = item.getItemMeta();
    }

    public ItemBuilder setCount(int count) {
        item.setAmount(count);
        return this;
    }

    public ItemBuilder setDamage(short damage) {
        item.setDurability(damage);
        return this;
    }

    public ItemBuilder setName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        meta.spigot().setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder setLore(String... lores) {
        meta.setLore(Arrays.asList(lores));
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addFlags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack build(HeldItem.ClickHandler onRightClick) {
        HeldItem.ITEM.add(new HeldItem(build(), onRightClick));
        return item;
    }

    public static ItemBuilder fromSection(ConfigurationSection section) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.valueOf(section.getString("material")));
        if (section.contains("count")) itemBuilder.setCount(section.getInt("count"));
        if (section.contains("damage")) itemBuilder.setDamage((short) section.getInt("damage"));
        if (section.contains("name")) itemBuilder.setName(section.getString("name"));
        if (section.contains("lore")) itemBuilder.setLore(section.getStringList("lore").toArray(new String[0]));
        return itemBuilder;
    }

}
