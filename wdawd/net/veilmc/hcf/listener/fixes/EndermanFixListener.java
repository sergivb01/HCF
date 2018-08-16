package net.veilmc.hcf.listener.fixes;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EndermanFixListener implements Listener{

	@EventHandler
	public void onEnderDamage(EntityDamageEvent e){
		if((e.getEntity() instanceof Player) && (e.getCause().equals(EntityType.ENDERMAN))){
			e.setCancelled(true);
		}
	}
}