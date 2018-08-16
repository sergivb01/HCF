package net.veilmc.hcf.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EntityLimitListener
		implements Listener{
	private static final int MAX_CHUNK_GENERATED_ENTITIES = 25;
	private static final int MAX_NATURAL_CHUNK_ENTITIES = 25;

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onCreatureSpawn(CreatureSpawnEvent event){
		if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT){
			return;
		}
		switch(event.getSpawnReason()){
			case NATURAL:{
				if(event.getLocation().getChunk().getEntities().length <= 25) break;
				event.setCancelled(true);
				break;
			}
			case CHUNK_GEN:{
				if(event.getLocation().getChunk().getEntities().length <= 25) break;
				event.setCancelled(true);
			}
		}
	}

}

