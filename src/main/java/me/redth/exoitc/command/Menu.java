package me.redth.exoitc.command;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.GamePlayer;
import me.redth.exoitc.util.visual.menu.GamesMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Menu extends SubCommand {
    public Menu() {
        super("menu", "displays an overview of games", "oitc menu", null, true);
    }

    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (GamePlayer.isParticipating(player)) {
            Messages.PLAYER_ALREADY_INGAME.send(player);
            return;
        }
        new GamesMenu(player).display();
    }
}
