package me.redth.exoitc.command;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.Game;
import org.bukkit.command.CommandSender;

public class CreateGame extends SubCommand {
    public CreateGame() {
        super("create", "creates a new game", "oitc create <id>", "oitc.admin", false);
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            MainCommand.specifyId(sender);
            return;
        }
        String id = args[1];
        if (Game.GAMES.containsKey(id)) {
            Messages.COMMAND_GAME_EXISTS.send(sender, id);
            return;
        }
        Game.GAMES.put(id, new Game(id));
        Messages.COMMAND_GAME_CREATE.send(sender, id);
    }
}
