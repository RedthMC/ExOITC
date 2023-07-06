package me.redth.exoitc.command;

import me.redth.exoitc.config.Messages;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public abstract class Command implements CommandBase, CommandExecutor {
    protected boolean playerOnly;
    protected List<SubCommand> commands;
    protected String name;
    protected String permissionNode;

    public Command(JavaPlugin plugin, String name, String permissionNode, boolean playerOnly) {
        this.commands = new ArrayList<>();
        this.name = name;
        this.permissionNode = permissionNode;
        this.playerOnly = playerOnly;
        plugin.getCommand(name).setExecutor(this);
    }

    public String getName() {
        return name;
    }

    public String getPermissionNode() {
        return permissionNode;
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }

    protected SubCommand getCommand(String name) {
        for (final SubCommand subCommand : commands) {
            if (subCommand.getName().equalsIgnoreCase(name)) {
                return subCommand;
            }
        }
        return null;
    }

    public List<SubCommand> getSubCommands() {
        return commands;
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player) && playerOnly) {
            Messages.COMMAND_INVALID_SENDER.send(sender);
            return true;
        }
        if (permissionNode != null && permissionNode.length() > 0 && !sender.hasPermission(permissionNode)) {
            Messages.COMMAND_NO_PERMISSION.send(sender);
            return true;
        }
        execute(sender, args);
        return true;
    }

}
