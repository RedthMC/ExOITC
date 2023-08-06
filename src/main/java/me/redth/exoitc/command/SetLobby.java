package me.redth.exoitc.command;

import me.redth.exoitc.config.Games;
import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLobby extends SubCommand {

    public SetLobby() {
        super("setlobby", "sets the waiting lobby for a game", "oitc setlobby <id>", "oitc.admin", true);
    }

    public void execute(final CommandSender sender, final String[] args) {
        Player player = (Player) sender;
        if (args.length == 1) {
            MainCommand.specifyId(sender);
            return;
        }

        String id = args[1];
        Game game = MainCommand.getGame(player, id);
        if (game == null) return;
        game.lobby = player.getLocation();
        Messages.COMMAND_SET_LOBBY.send(player, id);
        Games.save();
    }
}
