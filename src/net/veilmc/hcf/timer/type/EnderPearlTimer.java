package net.veilmc.hcf.timer.type;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.hcf.timer.PlayerTimer;
import net.veilmc.hcf.timer.TimerRunnable;
import net.veilmc.util.Config;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.PacketPlayOutSetSlot;
import net.minecraft.server.v1_7_R4.PlayerInventory;
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class EnderPearlTimer
		extends PlayerTimer
		implements Listener{
	private static final long REFRESH_DELAY_TICKS = 2;
	private static final long REFRESH_DELAY_TICKS_18 = 20;
	private static final long EXPIRE_SHOW_MILLISECONDS = 1500;
	private final ConcurrentMap<Object, Object> itemNameFakes;
	private final JavaPlugin plugin;

	public EnderPearlTimer(JavaPlugin plugin){
		//super("Enderpearl", TimeUnit.SECONDS.toMillis(16));
		super(ConfigurationService.ENDERPEARL_TIMER, TimeUnit.SECONDS.toMillis(16));
		this.plugin = plugin;
		this.itemNameFakes = CacheBuilder.newBuilder().expireAfterWrite(this.defaultCooldown + 1500 + 5000, TimeUnit.MILLISECONDS).build().asMap();
	}

	@Override
	public ChatColor getScoreboardPrefix(){
		//return ChatColor.YELLOW.toString() + (Object)ChatColor.BOLD;
		return ConfigurationService.ENDERPEARL_COLOUR;
	}

	@Override
	public void load(final Config config){
		super.load(config);
		final Collection<UUID> cooldowned = this.cooldowns.keySet();
		for(final UUID uuid : cooldowned){
			final Player player = Bukkit.getPlayer(uuid);
			if(player == null){
				continue;
			}
			this.startDisplaying(player);
		}
	}


	@Override
	public void onExpire(UUID userUUID){
		super.onExpire(userUUID);
		Player player = Bukkit.getPlayer(userUUID);
		if(player != null){
			player.sendMessage(ConfigurationService.ENDERPEARL_COOLDOWN_EXPIRED);
		}
	}

	@Override
	public TimerRunnable clearCooldown(UUID playerUUID){
		TimerRunnable runnable = super.clearCooldown(playerUUID);
		if(runnable != null){
			this.itemNameFakes.remove(playerUUID);
			return runnable;
		}
		return null;
	}

	@Override
	public void clearCooldown(Player player){
		this.stopDisplaying(player);
		super.clearCooldown(player);
	}

	public void refund(Player player){
		player.getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.ENDER_PEARL, 1));
		this.clearCooldown(player);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProjectileLaunch(ProjectileLaunchEvent event){
		ProjectileSource source;
		EnderPearl enderPearl;
		Projectile projectile = event.getEntity();
		if(projectile instanceof EnderPearl && (source = (enderPearl = (EnderPearl) projectile).getShooter()) instanceof Player){
			Player shooter = (Player) source;
			long remaining = this.getRemaining(shooter);
			if(remaining > 0){
				shooter.sendMessage(ChatColor.RED + "You cannot use" + ChatColor.YELLOW + " Enderpearl" + ChatColor.RED + " for another " + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + '.');
				event.setCancelled(true);
				shooter.getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.ENDER_PEARL));
				return;
			}
			if(this.setCooldown(shooter, shooter.getUniqueId(), this.defaultCooldown, true)){
				this.startDisplaying(shooter);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event){
		this.clearCooldown(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event){
		this.clearCooldown(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerItemHeld(PlayerItemHeldEvent event){
		Player player = event.getPlayer();
		PearlNameFaker pearlNameFaker = (PearlNameFaker) this.itemNameFakes.get(player.getUniqueId());
		if(pearlNameFaker != null){
			int previousSlot = event.getPreviousSlot();
			org.bukkit.inventory.ItemStack item = player.getInventory().getItem(previousSlot);
			if(item == null){
				return;
			}
			pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(item), previousSlot);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onInventoryDrag(InventoryDragEvent event){
		HumanEntity humanEntity = event.getWhoClicked();
		if(humanEntity instanceof Player){
			Player player = (Player) humanEntity;
			PearlNameFaker pearlNameFaker = (PearlNameFaker) this.itemNameFakes.get(player.getUniqueId());
			if(pearlNameFaker == null){
				return;
			}
			for(Map.Entry entry : event.getNewItems().entrySet()){
				if(((Integer) entry.getKey()).intValue() != player.getInventory().getHeldItemSlot()) continue;
				pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(player.getItemInHand()), player.getInventory().getHeldItemSlot());
				break;
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onInventoryClick(InventoryClickEvent event){
		HumanEntity humanEntity = event.getWhoClicked();
		if(humanEntity instanceof Player){
			final Player player = (Player) humanEntity;
			PearlNameFaker pearlNameFaker = (PearlNameFaker) this.itemNameFakes.get(player.getUniqueId());
			if(pearlNameFaker == null){
				return;
			}
			int heldSlot = player.getInventory().getHeldItemSlot();
			if(event.getSlot() == heldSlot){
				pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(player.getItemInHand()), heldSlot);
			}else if(event.getHotbarButton() == heldSlot){
				pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(event.getCurrentItem()), event.getSlot());
				new BukkitRunnable(){

					public void run(){
						player.updateInventory();
					}
				}.runTask(this.plugin);
			}
		}
	}

	public void startDisplaying(Player player){
		if(this.getRemaining(player) > 0){
			PearlNameFaker pearlNameFaker = new PearlNameFaker(this, player);
			if(this.itemNameFakes.putIfAbsent(player.getUniqueId(), pearlNameFaker) == null){
				long ticks = ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() >= 47 ? 20 : 2;
				pearlNameFaker.runTaskTimerAsynchronously(this.plugin, ticks, ticks);
			}
		}
	}

	public void stopDisplaying(Player player){
		PearlNameFaker pearlNameFaker = (PearlNameFaker) this.itemNameFakes.remove(player.getUniqueId());
		if(pearlNameFaker != null){
			pearlNameFaker.cancel();
		}
	}

	public static class PearlNameFaker
			extends BukkitRunnable{
		private final PlayerTimer timer;
		private final Player player;

		public PearlNameFaker(PlayerTimer timer, Player player){
			this.timer = timer;
			this.player = player;
		}

		public void run(){
			org.bukkit.inventory.ItemStack stack = this.player.getItemInHand();
			if(stack != null && stack.getType() == Material.ENDER_PEARL){
				long remaining = this.timer.getRemaining(this.player);
				ItemStack item = CraftItemStack.asNMSCopy(stack);
				if(remaining > 0){
					item = item.cloneItemStack();
					item.c(ConfigurationService.ENDERPEARL_ITEM.replace("%time%", HCF.getRemaining(remaining, true, true)));
					//item.c((Object)ChatColor.YELLOW + "Enderpearl Cooldown: " + (Object)ChatColor.RED + HCF.getRemaining(remaining, true, true));
					this.setFakeItem(item, this.player.getInventory().getHeldItemSlot());
				}else{
					this.cancel();
				}
			}
		}

		public synchronized void cancel() throws IllegalStateException{
			super.cancel();
			this.setFakeItem(CraftItemStack.asNMSCopy(this.player.getItemInHand()), this.player.getInventory().getHeldItemSlot());
		}

		public void setFakeItem(ItemStack nms, int index){
			EntityPlayer entityPlayer = ((CraftPlayer) this.player).getHandle();
			if(index < PlayerInventory.getHotbarSize()){
				index += 36;
			}else if(index > 35){
				index = 8 - (index - 36);
			}
			entityPlayer.playerConnection.sendPacket(new PacketPlayOutSetSlot(entityPlayer.activeContainer.windowId, index, nms));
		}
	}

}

