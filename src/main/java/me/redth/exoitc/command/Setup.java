package me.redth.exoitc.command;

import me.redth.exoitc.game.Game;
import me.redth.exoitc.util.visual.menu.SetupMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Setup extends SubCommand {
    public Setup() {
        super("setup", "displays a setup menu", "oitc setup", null, true);
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            MainCommand.specifyId(sender);
            return;
        }

        Player player = (Player) sender;

        String id = args[1];
        Game game = MainCommand.getGame(player, id);
        if (game == null) return;

        new SetupMenu(player, game).display();
    }
}
