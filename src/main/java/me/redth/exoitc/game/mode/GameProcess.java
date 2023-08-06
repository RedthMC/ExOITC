package me.redth.exoitc.game.mode;

import me.redth.exoitc.ExOITC;
import me.redth.exoitc.config.Messages;
import me.redth.exoitc.data.PlayerStats;
import me.redth.exoitc.game.Game;
import me.redth.exoitc.game.audience.Audience;
import me.redth.exoitc.game.audience.GamePlayer;
import me.redth.exoitc.util.visual.Sidebar;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GameProcess implements Runnable {
    private static final Random random = new Random(0);
    public final List<Location> spawns;
    public final List<GamePlayer> players;
    public Game game;
    public GamePlayer bestKillstreaker = null;
    public int bestKillstreak = 0;
    private int taskId = -1;
    private int seconds = 5;

    public GameProcess(Game queue) {
        this.game = queue;
        spawns = new ArrayList<>(queue.spawns);
        players = queue.audiences.stream().map(this::transform).collect(Collectors.toList());
    }

    public GamePlayer transform(Audience queue) {
        return new GamePlayer(this, queue);
    }

    public void leave(GamePlayer player) {
        if (!players.remove(player)) return;
        PlayerStats.updateStats(player);
        player.setNametag(true);
        spawns.add(player.spawn);

        checkCancelGame();

        game.broadcast(Messages.GAME_LEAVE, player.as().getName(), String.valueOf(players.size()), "");
    }

    public void checkCancelGame() {
        if (players.size() > 1) return;
        if (!players.isEmpty()) {
            finish(players.get(0));
        } else {
            finish(null);
        }
    }

    public void start() {
        game.sendTitle("", "");

        for (GamePlayer player : players) {
            player.onStart();
        }

        game.playSound(Sound.LEVEL_UP, 2F);

        taskId = ExOITC.scheduleRepeating(this, 20);
    }

    @Override
    public void run() {
        seconds++;
        game.updateSidebar(this::generateSidebar);
    }

    public Sidebar generateSidebar(Audience audience) {
        Sidebar sidebar = Sidebar.newSidebar(audience.as(), "game");
        sidebar.custom(String.format("§eTime: %02d:%02d", seconds / 60, seconds % 60), 99);
        sidebar.custom("", 98);
        for (GamePlayer player : players) {
            Messages format = player.is(audience) ? Messages.SCOREBOARD_GAME_FORMAT_SELF : Messages.SCOREBOARD_GAME_FORMAT;
            sidebar.custom(format.get(player.as().getName(), String.valueOf(getScore(player))), getScore(player));
        }
        sidebar.custom(" ", -1);
        sidebar.custom(Messages.SCOREBOARD_FOOTER.get(), -2);
        return sidebar;
    }

    public int getScore(GamePlayer player) {
        return game.goal == GameGoal.LIVES ? 3 - player.getDeaths() : player.getKills();
    }

    public void finish(GamePlayer winner) {
        if (winner != null) {
            game.sendTitle(Messages.GAME_WIN.get(winner.as().getName()), "");
            game.playSound(Sound.LEVEL_UP, 1f);
            game.broadcast("");
            game.broadcast(Messages.GAME_RESULT_WINNER, winner.as().getName());
            game.broadcast("");
            if (bestKillstreaker != null) {
                game.broadcast(Messages.GAME_RESULT_KILLSTREAK, bestKillstreaker.as().getName(), String.valueOf(bestKillstreak));
                game.broadcast("");
            }
            winner.win();
        }

        for (GamePlayer player : players) {
            PlayerStats.updateStats(player);
            player.remove();
        }

        game.updateSidebar(this::generateSidebar);
        ExOITC.scheduleDelayed(game::end, 100L);

        ExOITC.cancelTask(taskId);
    }

    public Location takeRandomSpawn(Location lastSpawn) {
        int size = spawns.size();
        int index = random.nextInt(size);
        return lastSpawn == null ? spawns.remove(index) : spawns.set(index, lastSpawn);
    }

    public void onDeath(GamePlayer killed) {
        if (game.goal != GameGoal.LIVES) return;

        if (killed.getDeaths() < 3) return;

        if (!players.remove(killed)) return;
        killed.remove();

        PlayerStats.updateStats(killed);

        spawns.add(killed.spawn);

        killed.spectate();
    }

    public void onKill(GamePlayer killed, GamePlayer killer) {
        game.broadcast(Messages.GAME_KILL, killed.as().getName(), killer.as().getName(), String.valueOf(killed.getKills()), String.valueOf(killer.getKills()));
        if (killer.getKillStreak() >= 3) game.broadcast(Messages.GAME_KILLSTREAK, killer.as().getName(), String.valueOf(killer.getKillStreak()));

        if (bestKillstreaker == null) bestKillstreaker = killer;
        else if (bestKillstreak < killer.getKillStreak()) {
            bestKillstreaker = killer;
            bestKillstreak = killer.getKillStreak();
        }

        if (game.goal == GameGoal.KILLS && killer.getKills() >= 20) {
            finish(killer);
        }

    }

}
