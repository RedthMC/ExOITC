package me.redth.exoitc.game.audience;

import me.redth.exoitc.config.Config;
import me.redth.exoitc.game.GameKit;
import me.redth.exoitc.game.Game;
import me.redth.exoitc.util.visual.Sidebar;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.github.paperspigot.Title;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Audience implements DamageCallback {
    public static final Map<UUID, Audience> allAudiences = new HashMap<>();
    protected final Game game;
    protected final Player player;
    protected final ItemStack[] prevInventory;

    public Audience(Game game, Player player) {
        this.game = game;
        this.player = player;
        prevInventory = player.getInventory().getContents();

        allAudiences.put(player.getUniqueId(), this);
    }

    public void queue() {
        player.teleport(game.lobby);
        player.setGameMode(GameMode.ADVENTURE);
        GameKit.queue(player);
    }

    public void spectate() {
        player.teleport(game.lobby);

        PacketPlayOutPlayerInfo oldInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, ((CraftPlayer) player).getHandle());
        player.setGameMode(GameMode.SPECTATOR);
        sendPacket(new PacketPlayOutGameStateChange(3, 0));
        sendPacket(oldInfo);
        player.setAllowFlight(true);
        player.setFlying(true);

        GameKit.spectate(player);
    }

    public boolean inGameOrQueue() {
        return game != null;
    }

    public Game getGame() {
        return game;
    }

    public boolean is(Player player) {
        return this.player.equals(player);
    }

    public Player as() {
        return player;
    }

    public static boolean isWatching(Player player) {
        return allAudiences.containsKey(player.getUniqueId());
    }

    public static Audience of(Player player) {
        if (player == null) return null;
        return allAudiences.get(player.getUniqueId());
    }

    public void lobby() {
        heal();
        player.getInventory().setContents(prevInventory);
        player.updateInventory();
        player.teleport(Config.getLobby());
        player.setGameMode(GameMode.ADVENTURE);
        player.setLevel(0);
        player.setExp(0);
        Sidebar.lobby(player);
        allAudiences.remove(player.getUniqueId());
    }

    public void heal() {
        if (player.isDead()) return;
        player.setHealth(20);
    }

    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    public void playSound(Sound sound, float pitch) {
        player.playSound(player.getLocation(), sound, 1F, pitch);
    }

    public void sendTitle(String title, String subtitle) {
        player.sendTitle(new Title(title, subtitle));
    }

    public void sendPacket(Packet<?> packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void onDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.VOID || e.getCause() == EntityDamageEvent.DamageCause.SUICIDE) {
            player.teleport(game.lobby);
        }
        e.setCancelled(true);
    }

    public Location getRespawnLocation() {
        return game.lobby;
    }
}
