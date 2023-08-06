package me.redth.exoitc.game;

import me.redth.exoitc.ExOITC;
import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.audience.Audience;
import me.redth.exoitc.game.audience.GamePlayer;
import me.redth.exoitc.game.editor.EditorMenu;
import me.redth.exoitc.game.mode.GameGoal;
import me.redth.exoitc.game.mode.GameProcess;
import me.redth.exoitc.util.sign.Leaderboard;
import me.redth.exoitc.util.visual.Sidebar;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Game implements Runnable {
    public static final Map<String, Game> GAMES = new HashMap<>();

    public final String id;
    public String name;
    public Material icon = Material.WOOL;
    public short iconDamage = 0;
    public int minPlayers = 2;
    public int maxPlayers = 8;
    public boolean isDuel = false;
    public Location lobby;
    public List<Location> spawns = new ArrayList<>();
    public GameGoal goal = GameGoal.KILLS;

    public final List<Audience> audiences = new ArrayList<>();
    public GamePhase phase = GamePhase.QUEUE;
    private int queueTaskId = -1;
    private int countdown = 10;


    public Game(String id) {
        this.id = id;
        this.name = id;
    }

    public void join(Player player) {
        if (Audience.isWatching(player)) {
            Messages.PLAYER_ALREADY_INGAME.send(player);
            return;
        }
        if (EditorMenu.editors.containsKey(player.getUniqueId())) {
            Messages.PLAYER_ALREADY_INGAME.send(player);
            return;
        }
        if (phase != GamePhase.QUEUE) {
            Audience audience = new Audience(this, player);
            audiences.add(audience);
            audience.spectate();
            broadcast(Messages.GAME_SPECTATING, audience.as().getName());
//            Messages.GAME_IN_PROGRESS.send(player);
            return;
        }
        if (audiences.size() >= maxPlayers) {
            Messages.GAME_FULL.send(player);
            return;
        }
        Audience audience = new Audience(this, player);
        audiences.add(audience);
        audience.queue();

        informQueue(Messages.GAME_JOIN, player.getName(), String.valueOf(audiences.size()), isDuel ? "2" : String.valueOf(maxPlayers));

        if (audiences.size() < minPlayers) return;

        countdown();
    }

    public void countdown() {
        if (queueTaskId != -1) return;
        if (audiences.size() < 2) return;

        countdown = 10;

        queueTaskId = ExOITC.scheduleRepeating(this, 20L);
    }

    @Override
    public void run() {
        if (countdown == 0) {
            queueTaskId = ExOITC.cancelTask(queueTaskId);

            new GameProcess(this).start();
            phase = GamePhase.IN_PROGRESS;
            // start
        } else {
            informQueue(Messages.GAME_COUNTDOWN_NOTE, String.valueOf(countdown));
            playSound(Sound.NOTE_STICKS, 1F);
            sendTitle("§e" + countdown, "");
            countdown--;
        }
    }

    public static void leave(Player player) {
        Audience audience = Audience.allAudiences.remove(player.getUniqueId());
        if (audience == null) {
            Messages.PLAYER_NOT_PLAYING.send(player);
            return;
        }
        audience.getGame().leave(audience);

        GamePlayer gamePlayer = GamePlayer.inGamePlayers.remove(player.getUniqueId());
        if (gamePlayer != null) gamePlayer.getGame().leave(gamePlayer);
    }

    public void leave(Audience player) {
        if (!audiences.remove(player)) return;

        if (phase == GamePhase.QUEUE) {
            checkCancelStarting();
            informQueue(Messages.GAME_LEAVE, player.as().getName(), String.valueOf(audiences.size()), String.valueOf(maxPlayers));
        }

        player.lobby();
        Messages.PLAYER_LEAVE.send(player.as());

    }

    public void end() {
        for (Audience player : audiences) {
            player.lobby();
        }
        sendTitle("", "");
        Leaderboard.update();

        audiences.clear();
        phase = GamePhase.QUEUE;
    }

    public void checkCancelStarting() {
        if (queueTaskId == -1) return;
        if (audiences.size() >= minPlayers) return;

        informQueue(Messages.GAME_COUNTDOWN_CANCELLED);

        queueTaskId = ExOITC.cancelTask(queueTaskId);
    }


    public void informQueue(Messages message, String... args) {
        String str = message.get(args);
        for (Audience audience : audiences) {
            audience.sendMessage(str);
            Sidebar.queue(this, audience.as());
        }
    }

    public void broadcast(Messages message, String... args) {
        String str = message.get(args);
        for (Audience audience : audiences) {
            audience.sendMessage(str);
        }
    }

    public void broadcast(String message) {
        for (Audience audience : audiences) {
            audience.sendMessage(message);
        }
    }

    public void playSound(Sound sound, float pitch) {
        for (Audience audience : audiences) {
            audience.playSound(sound, pitch);
        }
    }

    public void sendTitle(String title, String subtitle) {
        for (Audience audience : audiences) {
            audience.sendTitle(title, subtitle);
        }
    }

    public void updateSidebar(Function<Audience, Sidebar> sidebarGenerator) {
        for (Audience audience : audiences) {
            sidebarGenerator.apply(audience).show();
        }
    }
}
