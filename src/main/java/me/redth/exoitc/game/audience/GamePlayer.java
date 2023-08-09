package me.redth.exoitc.game.audience;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.redth.exoitc.ExOITC;
import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.GameKit;
import me.redth.exoitc.game.mode.GameProcess;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamePlayer implements DamageCallback {
    public static final Map<UUID, GamePlayer> inGamePlayers = new HashMap<>();
    private final GameProcess game;
    private final Audience player;
    public Location spawn;
    private int kills;
    private int killstreak;
    private int deaths;
    private boolean win;

    public GamePlayer(GameProcess system, Audience player) {
        this.game = system;
        this.player = player;

        inGamePlayers.put(player.as().getUniqueId(), this);
    }

    public void onStart() {
        heal();
        player.as().teleport(spawn = game.takeRandomSpawn(null));
        GameKit.apply(this);
//        Sidebar.game(this);
        setNametag(false);
    }

    public void onDeath() {
        deaths++;
        killstreak = 0;
        ExOITC.scheduleDelayed(() -> player.as().spigot().respawn(), 14L);
        game.onDeath(this);
    }

    public void onKill(GamePlayer killed) {
        kills++;
        killstreak++;
        player.as().setLevel(killstreak);

        heal();
        ExOITC.scheduleDelayed(this::heal, 5L);
        addArrow();
        playSound(Sound.SUCCESSFUL_HIT, 1F);
        game.onKill(killed, this);
    }

    public boolean is(Audience audience) {
        return player.equals(audience);
    }

    public static GamePlayer of(Player player) {
        if (player == null) return null;
        return inGamePlayers.get(player.getUniqueId());
    }

    public GameProcess getGame() {
        return game;
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

    public void heal() {
        player.heal();
    }

    public void addArrow() {
        GameKit.arrow(this);
    }

    public Location getRespawnLocation() {
        heal();
        GameKit.apply(this); // clear stuck arrows
        return (spawn = game.takeRandomSpawn(spawn));
    }

    public void setNametag(boolean visible) {
        try {
            TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.as().getUniqueId());
            if (tabPlayer == null) return;
            if (visible)
                TabAPI.getInstance().getNameTagManager().showNameTag(tabPlayer);
            else
                TabAPI.getInstance().getNameTagManager().hideNameTag(tabPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDamage(EntityDamageEvent e) {
        switch (e.getCause()) {
            case VOID:
            case SUICIDE:
                e.setDamage(1000D);
                break;
            case ENTITY_ATTACK:
                break;
            case PROJECTILE:
                EntityDamageByEntityEvent arrowEvent = (EntityDamageByEntityEvent) e;
                if (!(arrowEvent.getDamager() instanceof Arrow)) break;
                ProjectileSource shooter = ((Arrow) arrowEvent.getDamager()).getShooter();
                if (!as().equals(shooter)) break;
            default:
                e.setCancelled(true);
        }
    }

    public void playSound(Sound sound, float pitch) {
        player.playSound(sound, pitch);
    }

    public Player as() {
        return player.as();
    }

    public void remove() {
        inGamePlayers.remove(player.as().getUniqueId());
        setNametag(true);
    }

    public void spectate() {
        player.spectate();
    }

    public String getFormattedName() {
        return player.getFormattedName();
    }
}
