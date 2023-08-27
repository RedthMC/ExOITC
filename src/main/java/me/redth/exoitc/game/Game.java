package me.redth.exoitc.game;

import me.redth.exoitc.ExOITC;
import me.redth.exoitc.config.Config;
import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.audience.Audience;
import me.redth.exoitc.game.audience.GamePlayer;
import me.redth.exoitc.game.mode.GameGoal;
import me.redth.exoitc.game.mode.GameProcess;
import me.redth.exoitc.player.OITCPlayer;
import me.redth.exoitc.player.state.LobbyState;
import me.redth.exoitc.player.state.State;
import me.redth.exoitc.util.sign.Leaderboard;
import me.redth.exoitc.util.visual.Sidebar;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Game implements Runnable, State {
    public static final Map<String, Game> GAMES = new HashMap<>();

    public final String id;
    public String name;
    public Material icon = Material.WOOL;
    public short iconDamage = 0;
    public int minPlayers = 2;
    public int maxPlayers = 8;
    public Location lobby;
    public List<Location> spawns = new ArrayList<>();
    public GameGoal goal = GameGoal.KILLS;

    public final List<OITCPlayer> audiences = new ArrayList<>();
    public GamePhase phase = GamePhase.QUEUE;
    private int queueTaskId = -1;
    private int countdown = 10;


    public Game(String id) {
        this.id = id;
        this.name = id;
    }

    public void join(OITCPlayer player) {
        if (!(player.state instanceof LobbyState)) {
            player.sendMessage(Messages.PLAYER_ALREADY_INGAME);
            return;
        }

        if (phase != GamePhase.QUEUE) {
            audiences.add(player);
            spectate(player);
            broadcast(Messages.GAME_SPECTATING, player.getFormattedName());
            return;
        }
        if (audiences.size() >= maxPlayers) {
            player.sendMessage(Messages.GAME_FULL);
            return;
        }

        audiences.add(player);
        queue(player);
        informQueue(Messages.GAME_JOIN, player.getFormattedName(), String.valueOf(audiences.size()), String.valueOf(maxPlayers));

        if (audiences.size() < minPlayers) return;

        countdown();
    }

    public void queue(OITCPlayer player) {
        player.player.teleport(lobby);
        player.player.setGameMode(GameMode.ADVENTURE);
        GameKit.queue(player);
    }

    public void spectate(OITCPlayer player) {
        player.player.teleport(lobby);

        PacketPlayOutPlayerInfo oldInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, ((CraftPlayer) player.player).getHandle());
        player.player.setGameMode(GameMode.SPECTATOR);
        player.sendPacket(new PacketPlayOutGameStateChange(3, 2));
        player.sendPacket(oldInfo);
        player.player.setAllowFlight(true);
        player.player.setFlying(true);

        GameKit.spectate(player);
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
        } else {
            informQueue(Messages.GAME_COUNTDOWN_NOTE, String.valueOf(countdown));
            playSound(Sound.NOTE_STICKS, 1F);
            sendTitle("§e" + countdown, "");
            countdown--;
        }
    }

    @Override
    public void onDamaged(OITCPlayer player, EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.VOID || e.getCause() == EntityDamageEvent.DamageCause.SUICIDE) {
            player.player.teleport(Config.getLobby());
        }
        e.setCancelled(true);
    }

    @Override
    public void onRemoved(OITCPlayer player) {
        audiences.remove(player);
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

    public void leave(OITCPlayer player) {
        if (!audiences.remove(player)) return;

        if (phase == GamePhase.QUEUE) {
            checkCancelStarting();
            informQueue(Messages.GAME_LEAVE, player.getFormattedName(), String.valueOf(audiences.size()), String.valueOf(maxPlayers));
        }

        LobbyState.apply(player);
        player.sendMessage(Messages.PLAYER_LEAVE);

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
        String str = Messages.PREFIX.get() + message.get(args);
        for (Audience audience : audiences) {
            audience.sendMessage(str);
            Sidebar.queue(this, audience.as());
        }
    }

    public void broadcast(Messages message, String... args) {
        String str = Messages.PREFIX.get() + message.get(args);
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
