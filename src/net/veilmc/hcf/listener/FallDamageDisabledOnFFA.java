package net.veilmc.hcf.listener;

import net.veilmc.hcf.utils.ConfigurationService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FallDamageDisabledOnFFA implements Listener {

    @EventHandler
    public void falldamageDisabled(EntityDamageEvent event) {
        if(ConfigurationService.FFA) {
            if(event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
    }
}
