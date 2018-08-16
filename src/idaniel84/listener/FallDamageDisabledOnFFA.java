package idaniel84.listener;

import idaniel84.utils.ConfigurationService;
import idaniel84.utils.ConfigurationService;
import idaniel84.utils.ConfigurationService;
import idaniel84.utils.ConfigurationService;
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
