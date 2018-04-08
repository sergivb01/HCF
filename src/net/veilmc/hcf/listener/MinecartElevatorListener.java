package net.veilmc.hcf.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class MinecartElevatorListener
		implements Listener{
	@EventHandler
	public void onMinecart(VehicleEnterEvent event){
		if((!(event.getVehicle() instanceof Minecart)) || (!(event.getEntered() instanceof Player))){
			return;
		}
		Player p = (Player) event.getEntered();
		Location l = event.getVehicle().getLocation();
		Location loc = new Location(p.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
		Material m = loc.getBlock().getType();
		if((m.equals(Material.FENCE_GATE)) || (m.equals(Material.SIGN_POST))){
			event.setCancelled(true);
			p.teleport(teleportSpot(loc, loc.getBlockY(), 254));
		}
	}

	public Location teleportSpot(Location loc, int min, int max){
		for(int k = min; k < max; k++){
			Material m1 = new Location(loc.getWorld(), loc.getBlockX(), k, loc.getBlockZ()).getBlock().getType();
			Material m2 = new Location(loc.getWorld(), loc.getBlockX(), k + 1, loc.getBlockZ()).getBlock().getType();
			if((m1.equals(Material.AIR)) && (m2.equals(Material.AIR))){
				return new Location(loc.getWorld(), loc.getBlockX(), k, loc.getBlockZ());
			}
		}
		return new Location(loc.getWorld(), loc.getBlockX(), loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()), loc.getBlockZ());
	}
}
