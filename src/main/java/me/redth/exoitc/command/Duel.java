package me.redth.exoitc.command;

import me.redth.exoitc.ExOITC;
import me.redth.exoitc.config.Messages;
import me.redth.exoitc.duel.DuelRequest;
import me.redth.exoitc.game.Game;
import me.redth.exoitc.util.visual.menu.DuelsMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Duel extends Command {

    public Duel() {
        super(ExOITC.getInstance(), "duel", null, true);
    }

    public void execute(final CommandSender sender, final String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage("specify a player");
            return;
        }
        if (Game.GAMES.values().stream().noneMatch(game -> game.isDuel)) {
            sender.sendMessage("no map D:");
            return;

        }

        String accepter = args[0];
        Player pa = Bukkit.getPlayer(accepter);
        if (pa == null) {
            Messages.COMMAND_ERROR.send(sender);
            return;
        }
        if (player.equals(pa)) {
            sender.sendMessage("wtf are u doing");
            return;
        }

        if (DuelRequest.accept(player, pa)) return;
        new DuelsMenu(player, pa).display();
    }
}
