package me.redth.exoitc.game;

import me.redth.exoitc.config.Config;
import me.redth.exoitc.config.Messages;
import me.redth.exoitc.util.visual.Sidebar;
import net.minecraft.server.v1_8_R3.Packet;
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

public class Participant {
    private static final Map<UUID, Participant> participants = new HashMap<>();
    protected final Game game;
    protected final Player player;
    protected final ItemStack[] prevInventory;

    public Participant(Game game, Player player) {
        this.game = game;
        this.player = player;
        prevInventory = player.getInventory().getContents();

        participants.put(player.getUniqueId(), this);
        player.teleport(game.queueLobby);
        init();
    }

    public void init() {
    }

    public void onQueueNewPlayer() {
    }

    public void onStart() {
    }

    public void onDeath() {
    }

    public void onKill(Participant killed) {
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

    public static boolean isParticipating(Player player) {
        return participants.containsKey(player.getUniqueId());
    }

    public static Participant of(Player player) {
        if (player == null) return null;
        return participants.get(player.getUniqueId());
    }

    public static void leave(Player player) {
        Participant participant = participants.remove(player.getUniqueId());
        if (participant == null) {
            Messages.PLAYER_NOT_PLAYING.send(player);
            return;
        }
        participant.leave();
    }

    public void leave() {
    }

    public void onLobby() {
        heal();
        player.getInventory().setContents(prevInventory);
        player.updateInventory();
        player.teleport(Config.lobbySpawn);
        player.setGameMode(GameMode.ADVENTURE);
        player.setLevel(0);
        player.setExp(0);
        Sidebar.lobby(player);
    }

    public void heal() {
    }

    public void playSound(Sound sound, float pitch) {
        player.playSound(player.getLocation(), sound, 1F, pitch);
    }

    public void title(String title, String subtitle) {
        player.sendTitle(new Title(title, subtitle));
    }

    public void sendPacket(Packet<?> packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }


    public void onDamage(EntityDamageEvent e) {
    }

    public Location getRespawnLocation() {
        return game.queueLobby;
    }
}
