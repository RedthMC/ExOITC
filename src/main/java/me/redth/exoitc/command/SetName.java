package me.redth.exoitc.command;

import me.redth.exoitc.config.Games;
import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SetName extends SubCommand {

    public SetName() {
        super("setname", "sets the name for a game", "oitc setname <id> <name>", "oitc.admin", false);
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 1) {
            MainCommand.specifyId(sender);
            return;
        }
        if (args.length == 2) {
            Messages.COMMAND_SPECIFY_NAME.send(sender);
            return;
        }
        String id = args[1];
        Game game = MainCommand.getGame(sender, id);
        if (game == null) return;

        String name = ChatColor.translateAlternateColorCodes('&', args[2]);
        game.name = name;
        Messages.COMMAND_SET_DISPLAY_NAME.send(sender, name, id);

        Games.save();
    }
}
