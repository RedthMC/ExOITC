package me.redth.exoitc.command;

import me.redth.exoitc.config.Games;
import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.Game;
import org.bukkit.command.CommandSender;

public class SetPlayerLimit extends SubCommand {

    public SetPlayerLimit() {
        super("setplayerlimit", "sets the player limit of a game", "oitc setplayerlimit <id> <max> [min]", "oitc.admin", false);
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 1) {
            Messages.COMMAND_SPECIFY_ID.send(sender);
            return;
        }
        if (args.length == 2) {
            Messages.COMMAND_SPECIFY_NUMBER.send(sender);
            return;
        }
        String id = args[1];
        Game game = MainCommand.getGame(sender, id);
        if (game == null) return;

        try {
            game.maxPlayer = Integer.parseInt(args[2]);
        } catch (Exception e) {
            Messages.COMMAND_NAN.send(sender, args[2]);
            return;
        }

        if (args.length > 3) {
            try {
                game.minPlayer = Integer.parseInt(args[3]);
            } catch (Exception e) {
                Messages.COMMAND_NAN.send(sender, args[3]);
                return;
            }
        }

        Messages.COMMAND_SET_PLAYER_LIMIT.send(sender, id);
        Games.save();
    }
}
