package me.redth.exoitc.command;

import me.redth.exoitc.config.Games;
import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.Game;
import org.bukkit.command.CommandSender;

public class RemoveGame extends SubCommand {
    public RemoveGame() {
        super("remove", "removes a game and its arenas", "oitc remove <id>", "oitc.admin", false);
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 1) {
            MainCommand.specifyId(sender);
            return;
        }

        String id = args[1];
        Game game = Game.GAMES.remove(id);
        if (game == null) {
            Messages.COMMAND_GAME_NOT_EXISTS.send(sender, id);
            return;
        }
        Games.remove(game);
        Messages.COMMAND_GAME_REMOVE.send(sender, id);
    }
}
