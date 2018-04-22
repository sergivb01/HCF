package net.veilmc.hcf.listener;

import net.veilmc.hcf.utils.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.material.EnderChest;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import net.veilmc.hcf.HCF;

public class WorldListener implements Listener{
	private final HCF plugin;

	public WorldListener(HCF plugin){
		this.plugin = plugin;
	}


	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		if((player.getWorld().getEnvironment() == World.Environment.NETHER) && ((event.getBlock().getState() instanceof CreatureSpawner)) && (!player.hasPermission("hcf.faction.protection.bypass"))){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You may not break spawners in the nether.");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event){
		Player player = event.getPlayer();
		if((player.getWorld().getEnvironment() == World.Environment.NETHER) && ((event.getBlock().getState() instanceof CreatureSpawner)) && (!player.hasPermission("hcf.faction.protection.bypass"))){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You may not place spawners in the nether.");
		}
	}


	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
	public void onEntityExplode(EntityExplodeEvent event){
		event.blockList().clear();
		if(event.getEntity() instanceof EnderDragon){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
	public void onBlockChange(BlockFromToEvent event){
		if(event.getBlock().getType() == Material.DRAGON_EGG){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityPortalEnter(EntityPortalEvent event){
		if(event.getEntity() instanceof EnderDragon){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBedEnter(PlayerBedEnterEvent event){
		//event.setCancelled(true); //TODO: Enable/Disable beds at nether
		//event.getPlayer().sendMessage(ChatColor.RED + "Beds are disabled on this server.");
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onWitherChangeBlock(EntityChangeBlockEvent event){
		Entity entity = event.getEntity();
		if(entity instanceof Wither || entity instanceof EnderDragon){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockFade(BlockFadeEvent event){
		switch(event.getBlock().getType()){
			case SNOW:
			case ICE:{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent event){
		event.setRespawnLocation(Bukkit.getWorld("world").getSpawnLocation().add(0.5, 0.0, 0.5));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerSpawn(PlayerSpawnLocationEvent event){
		Player player = event.getPlayer();
		if(!player.hasPlayedBefore()){
			if (!ConfigurationService.VEILZ) this.plugin.getEconomyManager().addBalance(player.getUniqueId(), 250);
			event.setSpawnLocation(Bukkit.getWorld("world").getSpawnLocation().add(0.5, 0.0, 0.5));
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onInventoryOpen(InventoryOpenEvent event){
		if(event.getInventory() instanceof EnderChest){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockIgnite(BlockIgniteEvent event){
		if(event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onCreatureSpawn(CreatureSpawnEvent event){
		if(event.getEntity() instanceof Squid){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		if((ConfigurationService.KIT_MAP) && event.getEntity().getKiller() != null){
			Player killer = event.getEntity().getKiller();
			if(killer != event.getEntity().getPlayer()){
				int mult = getMultiplier(killer);
				int eco = 100 * mult;
				plugin.getEconomyManager().addBalance(killer.getUniqueId(), eco);
				killer.sendMessage(ChatColor.GREEN + "You have gained $" + eco + " for killing " + ChatColor.WHITE + event.getEntity().getName() + ChatColor.GREEN + ". " + (mult != 1 ? ChatColor.GRAY + " (x" + mult + " multiplier" : ""));
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate key " + killer.getName() + " KillReward");
			}
		}
	}

	private int getMultiplier(Player player){
		for(int i = 4; i > 1; i--){
			if(player.hasPermission("balance.multiplayer." + i)){
				return i;
			}
		}
		return 1;
	}

}

