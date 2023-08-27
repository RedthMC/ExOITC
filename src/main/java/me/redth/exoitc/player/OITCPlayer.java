package me.redth.exoitc.player;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.data.PlayerHotbar;
import me.redth.exoitc.data.PlayerStats;
import me.redth.exoitc.player.state.State;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.github.paperspigot.Title;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OITCPlayer {
    public static final Map<UUID, OITCPlayer> OITCPLAYERS = new HashMap<>();

    public final Player player;
    public State state;
    public PlayerStats stats;
    public PlayerHotbar hotbar;
    public long lastTimeClickedItem;

    private OITCPlayer(Player player) {
        this.player = player;
    }

    public static OITCPlayer get(Player player) {
        return OITCPLAYERS.computeIfAbsent(player.getUniqueId(), uuid -> new OITCPlayer(player));
    }

    public static OITCPlayer remove(Player player) {
        OITCPlayer oitcPlayer = OITCPLAYERS.remove(player.getUniqueId());
        if (oitcPlayer == null) return null;
        if (oitcPlayer.state != null) oitcPlayer.state.onRemoved(oitcPlayer);
        return oitcPlayer;
    }

    public static Collection<OITCPlayer> getAll() {
        return OITCPLAYERS.values();
    }

    public void onDamaged(EntityDamageEvent event) {
        if (state == null) return;
        state.onDamaged(this, event);
    }

    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    public void sendMessage(Messages message) {
        message.send(player);
    }

    public void playSound(Sound sound, float pitch) {
        player.playSound(player.getLocation(), sound, 1F, pitch);
    }

    public void sendTitle(String title, String subtitle) {
        player.sendTitle(new Title(title, subtitle, 0, 1, 0));
    }

    public void sendPacket(Packet<?> packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public String getFormattedName() {
        return Messages.getPlayerNameFormat(player);
    }

    public void heal() {
        if (player.isDead()) return;
        player.setHealth(20);
    }
}
