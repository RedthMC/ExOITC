// 
// Decompiled by Procyon v0.6.0
// 

package me.redth.exoitc.command;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Help extends SubCommand
{
    private SubCommand[] commands;
    
    public Help(final SubCommand[] commands) {
        super("help", "displays the help menu", "oitc help", null, false);
        this.commands = commands;
    }
    
    public void execute(final CommandSender sender, final String[] args) {
        sender.sendMessage(" ");
        sender.sendMessage("§e§l \u226b OITC Help Menu");
        sender.sendMessage(" ");
        for (final SubCommand subCommand : this.getCommands(false)) {
            sender.sendMessage(" \u25cf /" + subCommand.getUsage() + " §7" + subCommand.getDescription());
        }
        sender.sendMessage(" ");
        if (sender.hasPermission("oitc.admin")) {
            for (final SubCommand subCommand : this.getCommands(true)) {
                sender.sendMessage(" \u25cf /" + subCommand.getUsage() + " §7" + subCommand.getDescription());
            }
            sender.sendMessage(" ");
        }
    }
    
    private SubCommand[] getCommands(final boolean permission) {
        final List<SubCommand> list = new ArrayList<SubCommand>();
        for (final SubCommand subCommand : this.commands) {
            if (permission) {
                if (subCommand.getPermissionNode() != null && subCommand.getPermissionNode().length() > 0) {
                    list.add(subCommand);
                }
            }
            else if (subCommand.getPermissionNode() == null || subCommand.getPermissionNode().length() <= 0) {
                list.add(subCommand);
            }
        }
        return list.toArray(new SubCommand[list.size()]);
    }
}
