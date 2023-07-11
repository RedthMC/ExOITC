package me.redth.exoitc.command;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.Game;
import org.bukkit.command.CommandSender;

public abstract class GameSubc extends SubCommand {
    public GameSubc(String name, String description, String usage, String permissionNode, boolean playerOnly, String... aliases) {
        super(name, description, usage, permissionNode, playerOnly, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
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
    }

    public abstract void execute(Game game, CommandSender commandSender, String[] args);
}
