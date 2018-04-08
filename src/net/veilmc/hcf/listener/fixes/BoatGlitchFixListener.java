package net.veilmc.hcf.listener.fixes;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;

public class BoatGlitchFixListener
		implements Listener{
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onVehicleCreate(VehicleCreateEvent event){
		Block belowBlock;
		Boat boat;
		Vehicle vehicle = event.getVehicle();
		if(vehicle instanceof Boat && (belowBlock = (boat = (Boat) vehicle).getLocation().add(0.0, -1.0, 0.0).getBlock()).getType() != Material.WATER && belowBlock.getType() != Material.STATIONARY_WATER){
			boat.remove();
		}
	}
}

