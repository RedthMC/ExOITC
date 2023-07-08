package me.redth.exoitc.util.sign;

import me.redth.exoitc.config.Games;
import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.Game;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

public class GameSign {
    public final Game game;
    public final Location sign;

    public GameSign(Game game, Location sign) {
        this.game = game;
        this.sign = sign;
        this.game.lobbySign = this;
    }

    public void update() {
        Sign sign1 = getSign();
        if (sign1 == null) return;
        setText(game, sign1.getLines());
        sign1.update(true);
    }

    public static void setText(Game game, String[] lines) {
        lines[1] = (game.phase == 0 ? Messages.SIGN_GAME_JOINABLE : Messages.SIGN_GAME_IN_PROGRESS).get();
        lines[0] = Messages.SIGN_GAME_NAME.get(game.name);
        lines[2] = Messages.SIGN_GAME_PLAYERS.get(String.valueOf(game.players.size()), String.valueOf(game.maxPlayer));
    }

    public static void loadGameSign(Game game, Location location) {
        if (location == null) return;
        if (game == null) return;
        new GameSign(game, location);
    }

    public Sign getSign() {
        BlockState state = sign.getBlock().getState();
        if (!(state instanceof Sign)) return null;
        return (Sign) state;
    }

    public static void onSignPlaced(SignChangeEvent e) {
        if (!"[OITC]".equals(e.getLine(0))) return;

        Game game = Game.GAMES.get(e.getLine(1));
        if (game == null) return;

        game.lobbySign = new GameSign(game, e.getBlock().getLocation());
        Games.save();

        setText(game, e.getLines());
    }

    public static void onSignClicked(Player player, Sign signClicked) {
        for (Game game : Game.GAMES.values()) {
            if (game.lobbySign == null) continue;
            if (!game.lobbySign.sign.equals(signClicked.getLocation())) continue;
            game.joinQueue(player);
            return;
        }
    }

}
