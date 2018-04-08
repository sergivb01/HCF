package net.veilmc.hcf.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import net.veilmc.hcf.HCF;

public class SotwListener implements Listener{
	private final HCF plugin;

	public SotwListener(final HCF plugin){
		super();
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityDamage(final EntityDamageEvent event){
		if(event.getEntity() instanceof Player && event.getCause() != EntityDamageEvent.DamageCause.SUICIDE && this.plugin.getSotwTimer().getSotwRunnable() != null){
			event.setCancelled(true);
		}
	}
}