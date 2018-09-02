package idaniel84.listener;

import idaniel84.utils.ConfigurationService;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MobSpawnDisabledOnKitsListener implements Listener {

    @EventHandler
    private void mobSpawningDisabled(EntitySpawnEvent event) {
        if (ConfigurationService.KIT_MAP) {
            if (event.getEntity() instanceof Monster) {
                event.setCancelled(true);
            }
        }
    }
}
