package me.redth.exoitc.game;

import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class Spectator extends Participant {
    public Spectator(Game game, Player player) {
        super(game, player);
    }

    @Override
    public void init() {
        PacketPlayOutPlayerInfo oldInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, ((CraftPlayer) player).getHandle());
        player.setGameMode(GameMode.SPECTATOR);
        sendPacket(new PacketPlayOutGameStateChange(3, 0));
        sendPacket(oldInfo);
        player.setAllowFlight(true);
        player.setFlying(true);

        GameKit.spectate(player);
    }


    @Override
    public void leave() {
        game.stopSpec(this);
    }

    @Override
    public void onDamage(EntityDamageEvent e) {
        player.teleport(game.queueLobby);
        e.setCancelled(true);
    }
}
