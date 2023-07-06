package me.redth.exoitc.command;

import me.redth.exoitc.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Join extends SubCommand {
    public Join() {
        super("join", "joins a game", "oitc join <id>", null, true);
    }

    public void execute(final CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Game game = MainCommand.getGame(sender, args[1]);
        if (game == null) return;
        game.joinQueue(player);
    }
}
