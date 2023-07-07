package me.redth.exoitc.game;

import me.redth.exoitc.ExOITC;
import me.redth.exoitc.config.Messages;
import me.redth.exoitc.data.PlayerStats;
import me.redth.exoitc.util.sign.GameSign;
import me.redth.exoitc.util.sign.LeaderboardSign;
import me.redth.exoitc.util.visual.Sidebar;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game {
    public static final Map<String, Game> GAMES = new HashMap<>();
    public static final Random random = new Random(0);
    public final String id;
    public String name;
    public Material icon = Material.WOOL;
    public short iconDamage = 0;
    public int minPlayer = 2;
    public int maxPlayer = 8;
    public Location queueLobby;
    public final List<Location> spawns = new ArrayList<>();
    public final List<Location> usableSpawn = new ArrayList<>();
    public final List<GamePlayer> players;
    public GamePlayer killstreaker;
    public int killstreak;
    public GameSign lobbySign;
    public int phase = 0; // 0 queue,1 start,2 end
    private int taskId = -1;
    private int countdown = 5;

    public Game(String id) {
        this.id = id;
        this.name = id;
        this.players = new ArrayList<>(maxPlayer);
    }

    public void joinQueue(Player player) {
        if (phase != 0) {
            Messages.GAME_IN_PROGRESS.send(player);
            return;
        }
        if (GamePlayer.inGameOrQueue(player)) {
            Messages.PLAYER_ALREADY_INGAME.send(player);
            return;
        }
        if (players.size() >= maxPlayer) {
            Messages.GAME_FULL.send(player);
            return;
        }
        if (countdown < 3) {
            Messages.GAME_IN_PROGRESS.send(player);
            return;
        }

        GamePlayer queuePlayer = new GamePlayer(this, player);
        players.add(queuePlayer);
        queuePlayer.onQueue();
        broadcast(Messages.GAME_JOIN, queuePlayer.as().getName(), String.valueOf(players.size()), String.valueOf(maxPlayer));
        if (lobbySign != null) lobbySign.update();

        for (GamePlayer gamePlayer : players) {
            gamePlayer.onQueueNewPlayer();
        }

        checkQueue();

    }

    public void checkQueue() {
        if (taskId != -1) return;
        if (players.size() < minPlayer) return;

        broadcast(Messages.GAME_COUNTDOWN_NOTE, "10");

        taskId = ExOITC.scheduleDelayedRepeating(() -> {
            if (countdown == 0) {
                taskId = ExOITC.cancelTask(taskId);
                start();
            } else {
                broadcast(Messages.GAME_COUNTDOWN_NOTE, String.valueOf(countdown));
                sound(Sound.NOTE_STICKS, 1F);
                title("§c" + countdown, "");
                countdown--;
            }
        }, 100L, 20L);
    }

    public void leaveGame(GamePlayer player) {
        if (!players.remove(player)) return;

        if (phase == 0) {
            checkCancelStarting();
        } else {
            checkCancelGame();
        }

        player.onLobby();
        Messages.PLAYER_LEAVE.send(player.as());
        broadcast(Messages.GAME_LEAVE, player.as().getName(), String.valueOf(players.size()), String.valueOf(maxPlayer));
        if (lobbySign != null) lobbySign.update();
    }

    public void checkCancelStarting() {
        if (taskId == -1) return;
        if (players.size() >= minPlayer) return;

        broadcast(Messages.GAME_COUNTDOWN_CANCELLED);

        taskId = ExOITC.cancelTask(taskId);
        countdown = 5;
    }


    public void checkCancelGame() {
        if (players.size() > 1) return;
        if (!players.isEmpty()) {
            finish(players.get(0));
        } else {
            if (taskId != -1) taskId = ExOITC.cancelTask(taskId);
            reset();
        }
    }

    public void addSpawn(Location spawn) {
        spawns.add(spawn);
        usableSpawn.add(spawn);
    }

    public void addSpawns(List<Location> spawn) {
        if (spawn == null) return;
        spawns.addAll(spawn);
        usableSpawn.addAll(spawn);
    }

    public void start() {
        if (phase != 0) return;

        phase = 1;

        if (lobbySign != null) lobbySign.update();

        title("", "");

        for (GamePlayer player : players) {
            player.onStart();
        }

        sound(Sound.LEVEL_UP, 2F);
    }

    public void finish(GamePlayer winner) {
        phase = 2;

        title(Messages.GAME_WIN.get(winner.as().getName()), "");
        sound(Sound.ANVIL_LAND, 1f);
        broadcast("");
        broadcast(Messages.GAME_RESULT_WINNER, winner.as().getName());
        broadcast("");
        broadcast(Messages.GAME_RESULT_KILLSTREAK, killstreaker.as().getName(), String.valueOf(killstreak));
        broadcast("");

        if (lobbySign != null) lobbySign.update();

        winner.win();
        taskId = ExOITC.scheduleDelayed(() -> {
            for (GamePlayer player : players) {
                PlayerStats.updateStats(player);
                player.onLobby();
            }
            title("", "");
            LeaderboardSign.update();
            reset();
            taskId = ExOITC.cancelTask(taskId);
        }, 100L);
    }

    public void reset() {
        killstreaker = null;
        killstreak = 0;
        usableSpawn.clear();
        usableSpawn.addAll(spawns);
        players.clear();
        countdown = 5;
        phase = 0;
    }

    public void broadcast(String str) {
        for (GamePlayer player : players) {
            player.as().sendMessage(str);
        }
    }

    public void broadcast(Messages message, String... args) {
        String str = message.get(args);
        for (GamePlayer player : players) {
            player.as().sendMessage(str);
        }
    }

    public void sound(Sound sound, float pitch) {
        for (GamePlayer player : players) {
            player.playSound(sound, pitch);
        }
    }

    public void title(String title, String subtitle) {
        for (GamePlayer player : players) {
            player.title(title, subtitle);
        }
    }


    public Location takeRandomSpawn() {
        int size = usableSpawn.size();
        int index = random.nextInt(size);
        return usableSpawn.remove(index);
    }

    public Location takeRandomSpawn(Location lastSpawn) {
        int size = usableSpawn.size();
        int index = random.nextInt(size);
        return usableSpawn.set(index, lastSpawn);
    }

    public void setLobby(Location location) {
        queueLobby = location;
    }

    public void onKill(GamePlayer killed, GamePlayer killer, boolean exceed20, int killstreak) {
        broadcast(Messages.GAME_KILL, killed.as().getName(), killer.as().getName());
        if (killstreak >= 3) broadcast(Messages.GAME_KILLSTREAK, killer.as().getName(), String.valueOf(killstreak));

        if (killstreaker == null) killstreaker = killer;
        else if (this.killstreak < killstreak) {
            killstreaker = killer;
            this.killstreak = killstreak;
        }


        if (exceed20) {
            finish(killer);
        }

        for (GamePlayer player : players) {
            Sidebar.game(player);
        }
    }
}
