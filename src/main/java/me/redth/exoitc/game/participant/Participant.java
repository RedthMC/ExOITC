package me.redth.exoitc.game.participant;

import me.redth.exoitc.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public interface Participant {

    String leaveMessage();

    String joinMessage();

    void onDamage(EntityDamageEvent e);

    Game getGame();

    Player getPlayer();

    String getFormattedName();
}
