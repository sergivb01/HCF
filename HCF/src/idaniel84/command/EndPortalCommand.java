package idaniel84.command;

import idaniel84.HCF;
import idaniel84.utils.ConfigurationService;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class EndPortalCommand implements CommandExecutor, Listener{
	private final String ITEM_DISPLAYNAME;
	private HCF plugin;
	private Map<UUID, LocationPair> playerSelections;

	public EndPortalCommand(final HCF plugin){
		this.plugin = plugin;
		this.ITEM_DISPLAYNAME = ChatColor.GREEN + "Endportal Maker";
		this.playerSelections = new HashMap<UUID, LocationPair>();
		if (!ConfigurationService.VEILZ || !ConfigurationService.FFA) this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onInteract(final PlayerInteractEvent e){
		final Player p = e.getPlayer();
		if(e.hasItem() && e.getClickedBlock() != null){
			final ItemStack itemStack = e.getItem();
			final Block b = e.getClickedBlock();
			if(itemStack.getItemMeta().hasDisplayName()
					&& itemStack.getItemMeta().getDisplayName().equals(this.ITEM_DISPLAYNAME)){
				final LocationPair locationPair = this.playerSelections.computeIfAbsent(e.getPlayer().getUniqueId(),
						func -> this.playerSelections.put(p.getUniqueId(), new LocationPair(null, null)));
				if(e.getAction() == Action.LEFT_CLICK_BLOCK){
					if(b.getType() != Material.ENDER_PORTAL_FRAME){
						e.getPlayer().sendMessage(ChatColor.RED + "You must select an end portal frame.");
						return;
					}
					locationPair.setFirstLoc(b.getLocation());
					e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully set the first location.");
				}else if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
					if(b.getType() != Material.ENDER_PORTAL_FRAME){
						e.getPlayer().sendMessage(ChatColor.RED + "You must select an end portal frame.");
						return;
					}
					if(locationPair.getFirstLoc() == null){
						e.getPlayer().sendMessage(ChatColor.RED
								+ "Please set the first location (by left clicking the end portal frame).");
						return;
					}
					locationPair.setSecondLoc(b.getLocation());
					e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully set the second location.");
					final Location firstLoc = locationPair.getFirstLoc();
					final Location secondLoc = locationPair.getSecondLoc();
					if(firstLoc.distance(secondLoc) > 6.0){
						e.getPlayer().sendMessage(ChatColor.RED + "You cannot create an end portal that big.");
						return;
					}
					if(firstLoc.getBlockY() != secondLoc.getBlockY()){
						e.getPlayer()
								.sendMessage(ChatColor.RED + "Make sure that the portals have the same elevation.");
						return;
					}
					final int minX = Math.min(firstLoc.getBlockX(), secondLoc.getBlockX());
					final int minY = Math.min(firstLoc.getBlockY(), secondLoc.getBlockY());
					final int minZ = Math.min(firstLoc.getBlockZ(), secondLoc.getBlockZ());
					final int maxX = Math.max(firstLoc.getBlockX(), secondLoc.getBlockX());
					final int maxY = Math.max(firstLoc.getBlockY(), secondLoc.getBlockY());
					final int maxZ = Math.max(firstLoc.getBlockZ(), secondLoc.getBlockZ());
					for(int x = minX; x <= maxX; ++x){
						for(int y = minY; y <= maxY; ++y){
							for(int z = minZ; z <= maxZ; ++z){
								final Block block = b.getWorld().getBlockAt(x, y, z);
								if(block.isEmpty()){
									block.setType(Material.ENDER_PORTAL);
								}
							}
						}
					}
					e.setCancelled(true);
					new BukkitRunnable(){
						public void run(){
							e.getPlayer().setItemInHand(null);
							e.getPlayer().updateInventory();
						}
					}.runTask(this.plugin);
					e.getPlayer().sendMessage(ChatColor.GREEN + "Created an end portal");
					this.playerSelections.remove(p.getUniqueId());
				}
			}
		}
	}

	@EventHandler
	public void onDrop(final PlayerDropItemEvent e){
		final ItemStack itemStack = e.getItemDrop().getItemStack();
		if(itemStack.getItemMeta().hasDisplayName()
				&& itemStack.getItemMeta().getDisplayName().equals(this.ITEM_DISPLAYNAME)){
			e.getItemDrop().remove();
		}
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent e){
		this.playerSelections.remove(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onKick(final PlayerKickEvent e){
		this.playerSelections.remove(e.getPlayer().getUniqueId());
	}

	public boolean onCommand(final CommandSender s, final Command c, final String alias, final String[] args){
		if (ConfigurationService.VEILZ) {
			s.sendMessage(ChatColor.RED + "You cannot use this command while Veilz is enabled.");
			return false;
		}
		if (ConfigurationService.FFA) {
			s.sendMessage(ChatColor.RED + "You cannot use this command while FFA is enabled.");
			return false;
		}
		if(!(s instanceof Player)){
			return true;
		}
		Player p = (Player) s;
		if(p.getInventory().firstEmpty() == -1){
			return true;
		}
		if(!p.hasPermission("idaniel84.command.endportal")){
			s.sendMessage(ChatColor.RED + "You do not have acces to this command.");
			return true;
		}
		final ItemStack portalMaker = new ItemStack(Material.GOLD_SWORD);
		final ItemMeta itemMeta = portalMaker.getItemMeta();
		itemMeta.setDisplayName(this.ITEM_DISPLAYNAME);
		portalMaker.setItemMeta(itemMeta);
		p.getInventory().addItem(portalMaker);
		return true;
	}

	private class LocationPair{
		private Location firstLoc;
		private Location secondLoc;

		public LocationPair(final Location firstLoc, final Location secondLoc){
			this.firstLoc = firstLoc;
			this.secondLoc = secondLoc;
		}

		public Location getFirstLoc(){
			return this.firstLoc;
		}

		public void setFirstLoc(final Location firstLoc){
			this.firstLoc = firstLoc;
		}

		public Location getSecondLoc(){
			return this.secondLoc;
		}

		public void setSecondLoc(final Location secondLoc){
			this.secondLoc = secondLoc;
		}
	}
}
