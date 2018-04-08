package net.veilmc.hcf.listener;

import net.veilmc.base.ServerHandler;
import net.veilmc.hcf.HCF;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BorderListener
		implements Listener{
	private static final int BORDER_OFFSET_TELEPORTS = 50;

	public static boolean isWithinBorder(Location location){
		int borderSize = location.getWorld().getEnvironment() == World.Environment.NORMAL ? HCF.getPlugin().getServerHandler().getWorldBorder() : HCF.getPlugin().getServerHandler().getNetherBorder();
		return Math.abs(location.getBlockX()) <= borderSize && Math.abs(location.getBlockZ()) <= borderSize;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onCreaturePreSpawn(CreatureSpawnEvent event){
		if(!BorderListener.isWithinBorder(event.getLocation())){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBucketEmpty(PlayerBucketFillEvent event){
		if(!BorderListener.isWithinBorder(event.getBlockClicked().getLocation())){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You cannot fill buckets past the border.");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBucketEmpty(PlayerBucketEmptyEvent event){
		if(!BorderListener.isWithinBorder(event.getBlockClicked().getLocation())){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You cannot empty buckets past the border.");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event){
		if(!BorderListener.isWithinBorder(event.getBlock().getLocation())){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks past the border.");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event){
		if(!BorderListener.isWithinBorder(event.getBlock().getLocation())){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You cannot break blocks past the border.");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerMove(PlayerMoveEvent event){
		Location from = event.getFrom();
		Location to = event.getTo();
		if(from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()){
			return;
		}
		if(!BorderListener.isWithinBorder(to) && BorderListener.isWithinBorder(from)){
			Player player = event.getPlayer();
			player.sendMessage(ChatColor.RED + "You cannot go past the border.");
			event.setTo(from);
			Entity vehicle = player.getVehicle();
			if(vehicle != null){
				vehicle.eject();
				vehicle.teleport(from);
				vehicle.setPassenger(player);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerPortal(PlayerPortalEvent event){
		Location to = event.getTo();
		if(!BorderListener.isWithinBorder(to)){
			PlayerTeleportEvent.TeleportCause cause = event.getCause();
			if(cause != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL || cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && BorderListener.isWithinBorder(event.getFrom())){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You cannot go past the border.");
			}else{
				World.Environment toEnvironment = to.getWorld().getEnvironment();
				if(toEnvironment != World.Environment.NORMAL){
					return;
				}
				int x = to.getBlockX();
				int z = to.getBlockZ();
				int borderSize = HCF.getPlugin().getServerHandler().getWorldBorder();
				boolean extended = false;
				if(Math.abs(x) > borderSize){
					to.setX(x > 0 ? borderSize - 50 : -borderSize + 50);
					extended = true;
				}
				if(Math.abs(z) > borderSize){
					to.setZ(z > 0 ? borderSize - 50 : -borderSize + 50);
					extended = true;
				}
				if(extended){
					to.add(0.5, 0.0, 0.5);
					event.setTo(to);
					event.getPlayer().sendMessage(ChatColor.RED + "This portals travel location was over the border. It has been moved inwards.");
				}
			}
		}
	}
}

