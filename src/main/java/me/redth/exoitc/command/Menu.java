package me.redth.exoitc.command;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.audience.Audience;
import me.redth.exoitc.util.menu.GamesMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Menu extends SubCommand {
    public Menu() {
        super("menu", "displays an overview of games", "oitc menu", null, true);
    }

    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (Audience.isWatching(player)) {
            Messages.PLAYER_ALREADY_INGAME.send(player);
            return;
        }
        new GamesMenu(player).display();
    }
}
