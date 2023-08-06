package me.redth.exoitc.command;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.audience.Audience;
import me.redth.exoitc.game.editor.EditListMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Setup extends SubCommand {
    public Setup() {
        super("setup", "displays a setup menu", "oitc setup", "oitc.admin", true);
    }

    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (Audience.isWatching(player)) {
            Messages.PLAYER_ALREADY_INGAME.send(player);
            return;
        }
        new EditListMenu(player).display();
    }
}
