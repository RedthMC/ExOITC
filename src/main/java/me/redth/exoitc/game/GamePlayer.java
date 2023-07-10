package me.redth.exoitc.game;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.redth.exoitc.ExOITC;
import me.redth.exoitc.data.PlayerStats;
import me.redth.exoitc.util.visual.Sidebar;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class GamePlayer extends Participant {
    private Location spawn;
    private int kills;
    private int killstreak;
    private int deaths;
    private boolean win;
    public boolean spectator;

    public GamePlayer(Game game, Player player) {
        super(game, player);
    }

    public void onQueue() {
        player.setGameMode(GameMode.ADVENTURE);
        GameKit.queue(this);
    }

    public void onQueueNewPlayer() {
        Sidebar.queue(this);
    }

    public void onStart() {
        heal();
        player.teleport(spawn = getGame().takeRandomSpawn());
        player.setLevel(0);
        player.setExp(0);
        GameKit.apply(this);
        Sidebar.game(this);
        setNametag(false);
    }

    public void onDeath() {
        deaths++;
        killstreak = 0;
        player.setLevel(0);
        player.setExp(0);
        ExOITC.scheduleDelayed(() -> player.spigot().respawn(), 14L);
    }

    public void onKill(Participant killed) {
        if (!(killed instanceof GamePlayer)) return;
        kills++;
        killstreak++;
        player.setLevel(killstreak);

        heal();
        ExOITC.scheduleDelayed(this::heal, 5L);
        addArrow();
        playSound(Sound.SUCCESSFUL_HIT, 1F);
        game.onKill((GamePlayer) killed, this, kills >= 20, killstreak);
    }

    public void win() {
        win = true;
    }

    public boolean isWinner() {
        return win;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getKills() {
        return kills;
    }

    public int getKillStreak() {
        return killstreak;
    }

    public boolean is(Player player) {
        return this.player.equals(player);
    }

    public Player as() {
        return player;
    }

    public void onLobby() {
        if (!game.isDuel) PlayerStats.updateStats(this);
        setNametag(true);
        super.onLobby();
    }

    public void heal() {
        if (player.isDead()) return;
        player.setHealth(20);
        player.setExhaustion(20);
        player.setSaturation(20);
    }

    public void addArrow() {
        GameKit.arrow(this);
    }

    public Location getRespawnLocation() {
        heal();
        GameKit.apply(this);
        ((CraftPlayer) player).getHandle().o(0); // clear stuck arrows
        return (spawn = getGame().takeRandomSpawn(spawn));
    }

    public void setNametag(boolean visible) {
        try {
            TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
            if (tabPlayer == null) return;
            if (visible)
                TabAPI.getInstance().getNameTagManager().showNameTag(tabPlayer);
            else
                TabAPI.getInstance().getNameTagManager().hideNameTag(tabPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void leave() {
        game.leaveGame(this);
    }

    public static GamePlayer of(Player player) {
        Participant participant = Participant.of(player);
        if (participant instanceof GamePlayer) return (GamePlayer) participant;
        return null;
    }

    @Override
    public void onDamage(EntityDamageEvent e) {
        switch (e.getCause()) {
            case VOID:
            case SUICIDE:
                if (game.phase == 1) {
                    e.setDamage(1000D);
                    return;
                }
                player.teleport(game.queueLobby);
                break;
            case ENTITY_ATTACK:
            case PROJECTILE:
                if (game.phase == 1)
                    return;
        }
        e.setCancelled(true);
    }
}
