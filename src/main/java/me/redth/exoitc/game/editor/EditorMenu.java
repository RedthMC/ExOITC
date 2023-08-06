package me.redth.exoitc.game.editor;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import me.redth.exoitc.config.Config;
import me.redth.exoitc.config.Games;
import me.redth.exoitc.game.Game;
import me.redth.exoitc.game.GameKit;
import me.redth.exoitc.game.editor.anvil.RenameAnvil;
import me.redth.exoitc.game.mode.GameGoal;
import me.redth.exoitc.util.item.ItemBuilder;
import me.redth.exoitc.util.menu.CustomMenu;
import me.redth.exoitc.util.visual.Sidebar;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.Vector3f;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EditorMenu extends CustomMenu {
    public static final ItemStack OPEN_MENU = new ItemBuilder(Material.DIAMOND).setName("§aSetup Menu").build(EditorMenu::displayIfExist);
    public static final ItemStack SAVE = new ItemBuilder(Material.EMERALD_BLOCK).setName("§aSave & Exit").build();
    public static final Map<UUID, EditorMenu> editors = new HashMap<>();
    public final IntObjectMap<Location> armorStands = new IntObjectHashMap<>();
    public final Game game;
    private String name;
    private Material icon;
    private short iconDamage;
    private int minPlayers;
    private int maxPlayers;
    private boolean isDuel;
    private Location lobby;
    private GameGoal goal;

    public EditorMenu(Player player, Game game) {
        super(player, "Setup" + game.id, 3);
        this.game = game;

        setNameAndIcon(game.name, game.icon, game.iconDamage);
        setMinPlayers(game.minPlayers);
        setMaxPlayers(game.maxPlayers);
        setLobby(game.lobby);
        setGoal(game.goal);
        initSpawns(game.spawns);

        inventory.setItem(17, SAVE);

        player.teleport(lobby);
        player.getInventory().addItem(OPEN_MENU);
        player.setGameMode(GameMode.CREATIVE);

        editors.put(player.getUniqueId(), this);

        PacketListener.register(this);

    }

    public EditorMenu(Player player, String newGameName) {
        super(player, "Setup" + newGameName, 3);
        this.game = new Game(newGameName);

        game.lobby = player.getLocation().add(0, 0.5, 0);

        setNameAndIcon(game.name, game.icon, game.iconDamage);
        setMinPlayers(game.minPlayers);
        setMaxPlayers(game.maxPlayers);
        setLobby(game.lobby);
        setGoal(game.goal);
        initSpawns(game.spawns);

        inventory.setItem(17, SAVE);

        player.getInventory().addItem(OPEN_MENU);
        player.setGameMode(GameMode.CREATIVE);

        editors.put(player.getUniqueId(), this);

        PacketListener.register(this);

    }

    public void setNameAndIcon(String name, Material icon, short iconDamage) {
        this.name = name;
        this.icon = icon;
        this.iconDamage = iconDamage;
        updateNameAndIcon();
    }

    public void setName(String name) {
        this.name = name;
        updateNameAndIcon();
    }

    public void setIcon(Material icon, short iconDamage) {
        this.icon = icon;
        this.iconDamage = iconDamage;
        updateNameAndIcon();
    }

    public void updateNameAndIcon() {
        inventory.setItem(13, new ItemBuilder(icon).setDamage(iconDamage).setName("§7Name: §f" + name).setLore("§eClick to Rename", "§eDrag an item here to change icon").build());
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        inventory.setItem(3, new ItemBuilder(Material.DIAMOND_BLOCK).setName("§7Min Players: §f" + minPlayers).setLore("§cLeft click to decrease", "§aRight click to increase").build());
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        inventory.setItem(5, new ItemBuilder(Material.REDSTONE_BLOCK).setName("§7Max Players: §f" + maxPlayers).setLore("§cLeft click to decrease", "§aRight click to increase").build());
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
        inventory.setItem(15, new ItemBuilder(Material.NETHER_STAR).setName("§7Set lobby").build());
    }

    public void setDuel(boolean isDuel) {
        this.isDuel = isDuel;
        inventory.setItem(23, new ItemBuilder(Material.DIAMOND_SWORD).setName("§7Is Duel: §f").build());
    }

    public void setGoal(GameGoal goal) {
        this.goal = goal;
        inventory.setItem(21, new ItemBuilder(Material.GOLD_BLOCK).setName("§7Goal: §f" + goal.name).build());
    }

    public void initSpawns(List<Location> spawns) {
        summonFakeArmorStands(spawns);
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        if (!inventory.equals(e.getClickedInventory())) return;
        e.setCancelled(true);
        switch (e.getSlot()) {
            case 13:
                if (getCursor() == null || getCursor().getType() == Material.AIR) {
                    new RenameAnvil(this).display();
                } else {
                    setIcon(getCursor().getType(), getCursor().getDurability());
                }
                break;
            case 3:
                if (e.getClick() == ClickType.LEFT) {
                    if (minPlayers <= 2) break;
                    setMinPlayers(minPlayers - 1);
                } else if (e.getClick() == ClickType.RIGHT) {
                    if (minPlayers >= maxPlayers) break;
                    setMinPlayers(minPlayers + 1);
                }
                break;
            case 5:
                if (e.getClick() == ClickType.LEFT) {
                    if (maxPlayers <= minPlayers) break;
                    setMaxPlayers(maxPlayers - 1);
                } else if (e.getClick() == ClickType.RIGHT) {
                    setMaxPlayers(maxPlayers + 1);
                }
                break;
            case 11:
                summonFakeArmorStands(player.getLocation());
                close();
                break;
            case 15:
                setLobby(player.getLocation().add(0, 0.5, 0));
                player.sendMessage("§aLobby is set!");
                close();
                break;
            case 21:
                setGoal(goal.next());
                break;
            case 17:
                save();
                player.sendMessage("§aSaved!");
                close();
                break;
        }
    }

    public void save() {
        game.name = name;
        game.minPlayers = minPlayers;
        game.maxPlayers = maxPlayers;
        game.icon = icon;
        game.iconDamage = iconDamage;
        game.isDuel = isDuel;
        game.spawns = Arrays.asList(armorStands.values(Location.class));
        game.lobby = lobby;
        game.goal = goal;

        editors.remove(player.getUniqueId());
        Game.GAMES.put(game.id, game);
        Games.save();

        removeFakeArmorStands();

        PacketListener.unregister(player);

        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(Config.getLobby());
        GameKit.lobby(player);
    }

    public void summonFakeArmorStands(List<Location> locations) {
        for (Location location : locations) {
            summonFakeArmorStands(location);
        }
    }

    public void summonFakeArmorStands(Location location) {
        EntityPlayer handle = ((CraftPlayer) player).getHandle();

        EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle());
        armorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        armorStand.setGravity(false);
        armorStand.setCustomName("§cLeft click to remove");
        armorStand.setCustomNameVisible(true);
        armorStand.setHeadPose(new Vector3f(location.getPitch(), 0, 0));
        handle.playerConnection.sendPacket(new PacketPlayOutSpawnEntity(armorStand, 78));
        handle.playerConnection.sendPacket(new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), false));

        armorStands.put(armorStand.getId(), location);
    }

    public void removeFakeArmorStands() {
        EntityPlayer handle = ((CraftPlayer) player).getHandle();
        handle.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(armorStands.keys()));
    }

    public static void displayIfExist(Player player) {
        EditorMenu menu = editors.get(player.getUniqueId());
        if (menu != null) menu.display();
    }

    @Override
    public void display() {
        inventory.setItem(11, new ItemBuilder(Material.ARMOR_STAND).setName("§7Add spawn").setCount(armorStands.size()).build());
        super.display();
    }

    public void attackedId(int id) {
        if (armorStands.remove(id) == null) return;
        EntityPlayer handle = ((CraftPlayer) player).getHandle();
        handle.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(id));
    }
}
