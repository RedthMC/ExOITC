package me.redth.exoitc.command;

import me.redth.exoitc.game.GamePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Leave extends SubCommand {
    public Leave() {
        super("leave", "leaves your current game", "oitc leave", null, true);
    }

    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        GamePlayer.leave(player);
    }
}
