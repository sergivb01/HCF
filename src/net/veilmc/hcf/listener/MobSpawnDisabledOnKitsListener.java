package net.veilmc.hcf.listener;

import net.veilmc.hcf.utils.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MobSpawnDisabledOnKitsListener implements Listener {

    @EventHandler
    public void mobSpawningDisabled(EntitySpawnEvent event) {
        if (ConfigurationService.KIT_MAP) {
            if (event.getEntity() instanceof Monster) {
                event.setCancelled(true);
            }
        }
    }
}
