// 
// Decompiled by Procyon v0.6.0
// 

package me.redth.exoitc.command;

import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MainCommand extends Command {
    public MainCommand(final JavaPlugin plugin) {
        super(plugin, "oitc", null, false);
        this.commands.add(new AddSpawn());
        this.commands.add(new CreateGame());
        this.commands.add(new Join());
        this.commands.add(new Leave());
        this.commands.add(new Menu());
        this.commands.add(new Reload());
        this.commands.add(new Stats());
        this.commands.add(new RemoveGame());
        this.commands.add(new SetPlayerLimit());
        this.commands.add(new SetName());
        this.commands.add(new SetIcon());
        this.commands.add(new SetLobby());
        this.commands.add(new SetMainLobby());
        this.commands.add(new Help(this.commands.toArray(new SubCommand[this.commands.size()])));
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            final SubCommand help = this.getCommand("help");
            if (help != null) {
                help.execute(sender, args);
            }
            return;
        }
        final SubCommand subCommand = this.getCommand(args[0]);
        if (subCommand == null) {
            Messages.COMMAND_INVALID_ARGS.send(sender);
            return;
        }
        if (subCommand.isPlayerOnly() && !(sender instanceof Player)) {
            Messages.COMMAND_INVALID_SENDER.send(sender);
            return;
        }
        final String permissionNode = subCommand.getPermissionNode();
        if (permissionNode != null && !permissionNode.isEmpty() && !sender.hasPermission(permissionNode)) {
            Messages.COMMAND_NO_PERMISSION.send(sender);
            return;
        }
        try {
            subCommand.execute(sender, args);
        } catch (final Exception e) {
            Messages.COMMAND_ERROR.send(sender);
            e.printStackTrace();
        }
    }

    public static void specifyId(CommandSender sender) {
        String ids = String.join(",", Game.GAMES.keySet());
        Messages.COMMAND_SPECIFY_ID.send(sender, ids);
    }

    public static Game getGame(CommandSender sender, String id) {
        Game game = Game.GAMES.get(id);
        if (game == null)
            Messages.COMMAND_GAME_NOT_EXISTS.send(sender, id);
        return game;
    }
}
