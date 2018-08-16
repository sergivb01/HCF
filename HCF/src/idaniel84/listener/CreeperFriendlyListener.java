package idaniel84.listener;

import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class CreeperFriendlyListener implements Listener{

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onTargetEvent(EntityTargetEvent e){
		if((e.getEntity() instanceof Creeper)){
			e.setCancelled(true);
		}
	}
}
