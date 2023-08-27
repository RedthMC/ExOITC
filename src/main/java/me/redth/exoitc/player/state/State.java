package me.redth.exoitc.player.state;

import me.redth.exoitc.player.OITCPlayer;
import org.bukkit.event.entity.EntityDamageEvent;

public interface State {
    void onDamaged(OITCPlayer player, EntityDamageEvent event);

    void onRemoved(OITCPlayer player);
}
