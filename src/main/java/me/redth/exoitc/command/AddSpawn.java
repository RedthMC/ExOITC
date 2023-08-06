package me.redth.exoitc.command;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddSpawn extends SubCommand {
    public AddSpawn() {
        super("addspawn", "adds a spawn to an arena", "oitc addspawn <id>", "oitc.admin", true);
    }

    public void execute(final CommandSender sender, final String[] args) {
        final Player player = (Player) sender;
        if (args.length == 1) {
            MainCommand.specifyId(sender);
            return;
        }
        String id = args[1];
        Game game = MainCommand.getGame(sender, id);
        game.spawns.add(player.getLocation());
        Messages.COMMAND_SPAWN_ADD.send(player, id);
    }
}
