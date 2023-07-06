package me.redth.exoitc.command;

import me.redth.exoitc.config.Config;
import me.redth.exoitc.config.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetMainLobby extends SubCommand {
    public SetMainLobby() {
        super("setmainlobby", "sets the main lobby", "oitc setmainlobby", "oitc.admin", true);
    }

    public void execute(final CommandSender sender, final String[] args) {
        final Player player = (Player) sender;
        Config.setLobbySpawn(player.getLocation());
        Messages.COMMAND_MAIN_LOBBY_SET.send(player);
    }
}
