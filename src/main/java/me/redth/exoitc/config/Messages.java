package me.redth.exoitc.config;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public enum Messages {
    PREFIX("prefix"),

    COMMAND_NAN("command.nan", "text"),
    COMMAND_ERROR("command.error"),
    COMMAND_INVALID_ARGS("command.invalid-args"),
    COMMAND_INVALID_SENDER("command.invalid-sender"),
    COMMAND_NO_PERMISSION("command.invalid-sender"),

    COMMAND_GAME_CREATE("command.game.create", "id"),
    COMMAND_GAME_EXISTS("command.game.exists", "id"),
    COMMAND_GAME_NOT_EXISTS("command.game.not-exists", "id"),
    COMMAND_GAME_REMOVE("command.game.remove", "id"),
    COMMAND_SET_LOBBY("command.game.lobby", "id"),
    COMMAND_SET_ICON("command.game.icon", "id"),
    COMMAND_SET_PLAYER_LIMIT("command.game.player-limit", "id"),
    COMMAND_SET_DISPLAY_NAME("command.game.name", "name", "id"),
    COMMAND_RELOAD_CONFIG("command.reload-config"),
    COMMAND_SPAWN_ADD("command.game.spawn-add", "id"),

    COMMAND_SPECIFY_ID("command.specify-game-id", "ids"),
    COMMAND_SPECIFY_NAME("command.specify-display-name"),
    COMMAND_SPECIFY_NUMBER("command.specify-number"),
    COMMAND_SPECIFY_ITEM("command.specify-item"),

    COMMAND_MAIN_LOBBY_SET("command.main-lobby-set"),

    GAME_COUNTDOWN_CANCELLED("game.countdown.cancelled"),
    GAME_COUNTDOWN_NOTE("game.countdown.note", "seconds"),
    GAME_COUNTDOWN_SUBTITLE("game.countdown.subtitle"),

    GAME_FULL("game.full"),
    GAME_IN_PROGRESS("game.in-progress"),
    GAME_KILL("game.kill", "player", "killer", "player_kills", "killer_kills"),
    GAME_KILLSTREAK("game.killstreak", "player", "streak"),
    GAME_WIN("game.win", "player"),
    GAME_JOIN("game.join", "player", "players", "max_players"),
    GAME_LEAVE("game.leave", "player", "players", "max_players"),
    GAME_SPECTATING("game.spectating", "player"),
    GAME_STOP_SPECTATING("game.stop-spectating", "player"),

    GAME_RESULT_WINNER("game.result.winner", "player"),
    GAME_RESULT_KILLSTREAK("game.result.killstreak", "player", "streak"),

    LEAVE_ITEM("game.leave-item"),

    PLAYER_ALREADY_INGAME("player.ingame"),
    PLAYER_NOT_PLAYING("player.not-ingame"),
    PLAYER_LEAVE("player.leave-game"),

    MENU_GAME_TITLE("menu.game.title"),
    MENU_GAME_NAME("menu.game.name", "game"),
    MENU_GAME_JOINABLE("menu.game.joinable"),
    MENU_GAME_IN_PROGRESS("menu.game.in-progress"),
    MENU_GAME_PLAYERS("menu.game.players", "players", "max_players"),

    MENU_HOTBAR_TITLE("menu.hotbar.title"),
    MENU_HOTBAR_SAVED("menu.hotbar.saved"),
    MENU_HOTBAR_ERROR("menu.hotbar.error"),

    STATS_TITLE("stats.title", "player"),
    STATS_GAMES("stats.games", "games"),
    STATS_KILLS("stats.kills", "kills"),
    STATS_DEATHS("stats.deaths", "deaths"),
    STATS_KDR("stats.kdr", "kdr"),
    STATS_WINS("stats.wins", "wins"),
    STATS_LOSSES("stats.losses", "losses"),
    STATS_WLR("stats.wlr", "wlr"),

    SCOREBOARD_TITLE("scoreboard.title"),
    SCOREBOARD_DATE_FORMAT("scoreboard.date-format") {
        @Override
        public String get() {
            return new SimpleDateFormat(config.config.getString(path, path)).format(new Date());
        }
    },
    SCOREBOARD_HEADER("scoreboard.header") {
        @Override
        public String get() {
            return super.get().replace("%date%", SCOREBOARD_DATE_FORMAT.get());
        }
    },
    SCOREBOARD_FOOTER("scoreboard.footer"),

    SCOREBOARD_QUEUE_MAP("scoreboard.queue.map", "map"),
    SCOREBOARD_QUEUE_PLAYERS("scoreboard.queue.players", "players", "max_players"),
    SCOREBOARD_QUEUE_WAITING("scoreboard.queue.waiting"),
    SCOREBOARD_QUEUE_STARTING("scoreboard.queue.starting"),

    SCOREBOARD_GAME_FORMAT("scoreboard.game.format", "player", "kills"),
    SCOREBOARD_GAME_FORMAT_SELF("scoreboard.game.format-self", "player", "kills"),

    PLAYER_NAME_FORMAT("player-name-format", "player");

    private static final YamlConfig config = new YamlConfig("messages");
    protected final String path;
    protected final String[] args;

    Messages(String path, String... args) {
        this.path = path;
        this.args = args;
    }

    public static void load() {
        config.load();
    }

    public String get() {
        return ChatColor.translateAlternateColorCodes('&', config.config.getString(path, path));
    }

    public String get(String... objects) {
        String string = get();
        int i = 0;
        for (String arg : args) {
            if (i >= objects.length) break;
            string = string.replace("%" + arg + "%", objects[i]);
            i++;
        }
        return string;
    }

    public void send(CommandSender player) {
        player.sendMessage(PREFIX.get() + get());
    }

    public void send(CommandSender player, String... objects) {
        player.sendMessage(PREFIX.get() + get(objects));
    }

    public static String getPlayerNameFormat(OfflinePlayer player) {
        return ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, PLAYER_NAME_FORMAT.get())).replace("%player%", player.getName());
    }
}
