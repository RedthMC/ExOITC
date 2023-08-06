package me.redth.exoitc.game.audience;

import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageEvent;

public interface DamageCallback {
    void onDamage(EntityDamageEvent e);

    Location getRespawnLocation();
}
