// 
// Decompiled by Procyon v0.6.0
// 

package me.redth.exoitc.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface CommandBase {
    void execute(CommandSender sender, String[] args);

    String getName();

    String getPermissionNode();

    boolean isPlayerOnly();

    interface PlayerOnly extends CommandBase {
        void execute(Player sender, String[] args);
    }

    interface GameBased extends CommandBase {
        void execute(Player sender, String[] args);
    }
}
