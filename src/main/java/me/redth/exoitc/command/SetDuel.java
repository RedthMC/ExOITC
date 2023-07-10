package me.redth.exoitc.command;

import me.redth.exoitc.config.Games;
import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SetDuel extends SubCommand {

    public SetDuel() {
        super("setduel", "sets a game to duel mode", "oitc setduel <id>", "oitc.admin", false);
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 1) {
            MainCommand.specifyId(sender);
            return;
        }

        String id = args[1];
        Game game = MainCommand.getGame(sender, id);
        if (game == null) return;

        game.isDuel = !game.isDuel;
        sender.sendMessage("duel set ok");

        Games.save();
    }
}
