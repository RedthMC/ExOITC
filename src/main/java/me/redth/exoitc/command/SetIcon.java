package me.redth.exoitc.command;

import me.redth.exoitc.config.Games;
import me.redth.exoitc.config.Messages;
import me.redth.exoitc.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetIcon extends SubCommand {

    public SetIcon() {
        super("seticon", "sets the icon for a game", "oitc seticon <id>", "oitc.admin", true);
    }

    public void execute(final CommandSender sender, final String[] args) {
        Player player = (Player) sender;
        if (args.length == 1) {
            MainCommand.specifyId(sender);
            return;
        }

        String id = args[1];
        Game game = MainCommand.getGame(player, id);
        if (game == null) return;
        ItemStack itemStack = player.getItemInHand();
        if (itemStack == null) {
            Messages.COMMAND_SPECIFY_ITEM.send(player);
            return;
        }
        game.icon = itemStack.getType();
        game.iconDamage = itemStack.getDurability();
        Messages.COMMAND_SET_ICON.send(player);
        Games.save();
    }
}
