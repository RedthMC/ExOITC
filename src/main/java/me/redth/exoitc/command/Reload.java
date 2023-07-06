package me.redth.exoitc.command;

import me.redth.exoitc.config.Config;
import me.redth.exoitc.config.Messages;
import org.bukkit.command.CommandSender;

public class Reload extends SubCommand {
    public Reload() {
        super("reload", "reloads all configuration files", "oitc reload", "oitc.admin", false);
    }

    public void execute(final CommandSender sender, final String[] args) {
        Config.load();
        Messages.COMMAND_RELOAD_CONFIG.send(sender);
    }
}
