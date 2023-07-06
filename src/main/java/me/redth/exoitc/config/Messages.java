package me.redth.exoitc.config;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
    COMMAND_SET_PLAYER_LIMIT("command.player-limit", "id"),
    COMMAND_SET_DISPLAY_NAME("command.game.name", "name", "id"),
    COMMAND_RELOAD_CONFIG("command.reload-config"),
    COMMAND_SIGN_SET("command.sign-set", "id"),
    COMMAND_SPAWN_ADD("command.spawn-add", "id"),

    COMMAND_SPECIFY_ID("specify-game-id"),
    COMMAND_SPECIFY_NAME("specify-display-name"),
    COMMAND_SPECIFY_NUMBER("specify-number"),

    COMMAND_LEADERBOARD_SET("command.leaderboard-set"),
    COMMAND_MAIN_LOBBY_SET("command.main-lobby-set"),


    GAME_COUNTDOWN_CANCELLED("game.countdown.cancelled"),
    GAME_COUNTDOWN_NOTE("game.countdown.note", "seconds"),
    GAME_COUNTDOWN_SUBTITLE("game.countdown.subtitle"),

    GAME_FULL("game.full"),
    GAME_IN_PROGRESS("game.in-progress"),
    GAME_KILL("game.kill", "player", "killer"),
    GAME_KILLSTREAK("game.killstreak", "player", "streak"),
    GAME_WIN("game.win", "player"),
    GAME_JOIN("game.join", "player", "players", "max_players"),
    GAME_LEAVE("game.leave", "player", "players", "max_players"),
    GAME_SPECTATING("game.spectating", "player"),
    GAME_STOP_SPECTATING("game.stop-spectating", "player"),

    GAME_RESULT_WINNER("game.result.winner", "player"),
    GAME_RESULT_KILLSTREAK("game.result.killstreak", "player", "streak"),

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

    SIGN_GAME_NAME("sign.game.name", "game"),
    SIGN_GAME_JOINABLE("sign.game.joinable"),
    SIGN_GAME_IN_PROGRESS("sign.game.in-progress"),
    SIGN_GAME_PLAYERS("sign.game.players", "players", "max_players"),

    SIGN_LEADERBOARD_RANK("sign.leaderboard.rank", "rank"),
    SIGN_LEADERBOARD_NAME("sign.leaderboard.name", "name"),
    SIGN_LEADERBOARD_KILLS("sign.leaderboard.kills", "kills"),
    SIGN_LEADERBOARD_WINS("sign.leaderboard.wins", "wins"),

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
    SCOREBOARD_HEADER("scoreboard.header", "date") {
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
    SCOREBOARD_GAME_FORMAT_SELF("scoreboard.game.format-self", "player", "kills");

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
        player.sendMessage(get());
    }

    public void send(CommandSender player, String... objects) {
        player.sendMessage(get(objects));
    }

}
