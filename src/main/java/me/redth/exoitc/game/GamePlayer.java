package me.redth.exoitc.game;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.redth.exoitc.ExOITC;
import me.redth.exoitc.config.Config;
import me.redth.exoitc.config.Messages;
import me.redth.exoitc.data.PlayerStats;
import me.redth.exoitc.util.visual.Sidebar;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.github.paperspigot.Title;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamePlayer {
    private static final Map<UUID, GamePlayer> inGameOrQueuePlayers = new HashMap<>();
    private final Game game;
    private final Player player;
    private final ItemStack[] prevInventory;
    private Location spawn;
    private int kills;
    private int killstreak;
    private int deaths;
    private boolean win;

    public GamePlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
        prevInventory = player.getInventory().getContents();
    }

    public void onQueue() {
        inGameOrQueuePlayers.put(player.getUniqueId(), this);
        player.teleport(game.queueLobby);
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

    public void onKill(GamePlayer killed) {
        kills++;
        killstreak++;
        player.setLevel(killstreak);

        ExOITC.scheduleDelayed(this::heal, 1L);
        addArrow();
        playSound(Sound.SUCCESSFUL_HIT, 1F);
        game.onKill(killed, this, kills >= 20, killstreak);
    }

    public boolean inGameOrQueue() {
        return game != null;
    }

    public Game getGame() {
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

    public boolean is(Player player) {
        return this.player.equals(player);
    }

    public Player as() {
        return player;
    }

    public static boolean inGameOrQueue(Player player) {
        return inGameOrQueuePlayers.containsKey(player.getUniqueId());
    }

    public static GamePlayer of(Player player) {
        if (player == null) return null;
        return inGameOrQueuePlayers.get(player.getUniqueId());
    }

    public static void leave(Player player) {
        GamePlayer gamePlayer = inGameOrQueuePlayers.remove(player.getUniqueId());
        if (gamePlayer == null) {
            Messages.PLAYER_NOT_PLAYING.send(player);
            return;
        }
        gamePlayer.getGame().leaveGame(gamePlayer);
    }

    public void onLobby() {
        heal();
        player.getInventory().setContents(prevInventory);
        player.updateInventory();
        player.teleport(Config.lobbySpawn);
        player.setLevel(0);
        player.setExp(0);
        inGameOrQueuePlayers.remove(player.getUniqueId());
        Sidebar.lobby(player);
        setNametag(true);
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

        }
//        Team team = player.getScoreboard().getEntryTeam(player.getName());
//        if (team == null) team = player.getScoreboard().getTeam("HIDE");
//        if (team == null) team = player.getScoreboard().registerNewTeam("HIDE");
//        for (GamePlayer gamePlayer : game.players) {
//            team.addEntry(gamePlayer.player.getName());
//        }
//        team.setNameTagVisibility(visible ? NameTagVisibility.ALWAYS : NameTagVisibility.NEVER);
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


}
