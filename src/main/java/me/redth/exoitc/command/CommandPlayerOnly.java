package me.redth.exoitc.command;

import me.redth.exoitc.config.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface CommandPlayerOnly extends CommandBase {

    @Override
    default void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Messages.COMMAND_INVALID_SENDER.send(sender);
            return;
        }

        execute((Player) sender, args);
    }

    void execute(Player player, String[] args);

}
