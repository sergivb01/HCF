package idaniel84.timer.type;

import com.google.common.base.Optional;
import idaniel84.HCF;
import idaniel84.faction.claim.Claim;
import idaniel84.faction.event.FactionClaimChangedEvent;
import idaniel84.faction.event.PlayerClaimEnterEvent;
import idaniel84.faction.event.cause.ClaimChangeCause;
import idaniel84.faction.type.ClaimableFaction;
import idaniel84.faction.type.Faction;
import idaniel84.faction.type.PlayerFaction;
import idaniel84.faction.type.RoadFaction;
import idaniel84.timer.PlayerTimer;
import idaniel84.timer.TimerRunnable;
import idaniel84.timer.event.TimerClearEvent;
import idaniel84.timer.event.TimerStartEvent;
import idaniel84.utils.ConfigurationService;
import idaniel84.utils.DurationFormatter;
import idaniel84.visualise.VisualType;
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import net.veilmc.util.BukkitUtils;
import net.veilmc.util.Config;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class PvpProtectionTimer extends PlayerTimer implements Listener{
	private static final String PVP_COMMAND = "/pvp enable";
	private static final long ITEM_PICKUP_DELAY;
	private static final long ITEM_PICKUP_MESSAGE_DELAY = 1250L;
	private static final String ITEM_PICKUP_MESSAGE_META_KEY = "pickupMessageDelay";

	static{
		ITEM_PICKUP_DELAY = TimeUnit.SECONDS.toMillis(30L);
	}

	private final Set<UUID> legible;
	private final ConcurrentMap<Object, Object> itemUUIDPickupDelays;
	private final HCF plugin;

	public PvpProtectionTimer(final HCF plugin){
		super(ConfigurationService.PVPTIMER_TIMER, TimeUnit.MINUTES.toMillis(30L));
		this.legible = new HashSet<UUID>();
		this.plugin = plugin;
		this.itemUUIDPickupDelays = CacheBuilder.newBuilder().expireAfterWrite(PvpProtectionTimer.ITEM_PICKUP_DELAY + 5000L, TimeUnit.MILLISECONDS).build().asMap();
	}

	public ChatColor getScoreboardPrefix(){
		return ConfigurationService.PVPTIMER_COLOUR;
	}

	@Override
	public void onExpire(final UUID userUUID){
		final Player player = Bukkit.getPlayer(userUUID);
		if(player == null){
			return;
		}
		if(this.getRemaining(player) <= 0L){
			this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.CLAIM_BORDER, null);
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lYour PvP Protection has expired. PvP is now enabled."));
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onTimerStop(final TimerClearEvent event){
		if(event.getTimer().equals(this)){
			final Optional<UUID> optionalUserUUID = event.getUserUUID();
			if(optionalUserUUID.isPresent()){
				this.onExpire(optionalUserUUID.get());
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onClaimChange(final FactionClaimChangedEvent event){
		if(event.getCause() != ClaimChangeCause.CLAIM){
			return;
		}
		final Collection<Claim> claims = event.getAffectedClaims();
		for(final Claim claim : claims){
			final Collection<Player> players = claim.getPlayers();
			for(final Player player : players){
				if(this.getRemaining(player) > 0L){
					Location location = player.getLocation();
					location.setX(claim.getMinimumX() - 1);
					location.setY(0);
					location.setZ(claim.getMinimumZ() - 1);
					location = BukkitUtils.getHighestLocation(location, location);
					if(!player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)){
						continue;
					}
					player.sendMessage(ChatColor.RED + "Land was claimed where you were standing. As you still have your " + this.getName() + " timer, you were teleported away.");
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event){
		if(ConfigurationService.KIT_MAP){
			return;
		}
		Player player = event.getPlayer();
		if(setCooldown(player, player.getUniqueId(), this.defaultCooldown, true)){
			this.setPaused(player, player.getUniqueId(), true);
			//player.sendMessage(ChatColor.RED + "Once you leave Spawn your 30 minutes of " + this.getName() + ChatColor.RED + " will start.");
		}
	}

	@EventHandler
	public void onTimer(TimerStartEvent e){
		if(ConfigurationService.KIT_MAP){
			if(e.getTimer() instanceof PvpProtectionTimer){
				this.plugin.getTimerManager().pvpProtectionTimer.clearCooldown(e.getUserUUID().get());
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDeath(final PlayerDeathEvent event){
		if(ConfigurationService.KIT_MAP){
			return;
		}
		final Player player = event.getEntity();
		final World world = player.getWorld();
		final Location location = player.getLocation();
		final Iterator<ItemStack> iterator = event.getDrops().iterator();
		while(iterator.hasNext()){
			this.itemUUIDPickupDelays.put(world.dropItemNaturally(location, iterator.next()).getUniqueId(), System.currentTimeMillis() + PvpProtectionTimer.ITEM_PICKUP_DELAY);
			iterator.remove();
		}
		this.clearCooldown(player);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBucketEmpty(final PlayerBucketEmptyEvent event){
		final Player player = event.getPlayer();
		final long remaining = this.getRemaining(player);
		if(remaining > 0L){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You cannot empty buckets as your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockIgnite(final BlockIgniteEvent event){
		final Player player = event.getPlayer();
		if(player == null){
			return;
		}
		final long remaining = this.getRemaining(player);
		if(remaining > 0L){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You cannot ignite blocks as your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onItemPickup(final PlayerPickupItemEvent event){
		final Player player = event.getPlayer();
		final long remaining = this.getRemaining(player);
		if(remaining > 0L){
			final UUID itemUUID = event.getItem().getUniqueId();
			final Long delay = (Long) this.itemUUIDPickupDelays.get(itemUUID);
			if(delay == null){
				return;
			}
			final long millis = System.currentTimeMillis();
			if((delay - millis) > 0L){
				event.setCancelled(true);

				// Don't let the pickup event spam the player.
				List<MetadataValue> value = player.getMetadata(ITEM_PICKUP_MESSAGE_META_KEY);
				if(value != null && !value.isEmpty() && value.get(0).asLong() - millis <= 0L){
					player.setMetadata(ITEM_PICKUP_MESSAGE_META_KEY, new FixedMetadataValue(plugin, millis + ITEM_PICKUP_MESSAGE_DELAY));
					player.sendMessage(ChatColor.RED + "You cannot pick this item up for another " + ChatColor.BOLD + DurationFormatUtils.formatDurationWords(remaining, true, true) + ChatColor.RED + " as your " + getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
				}
			}else itemUUIDPickupDelays.remove(itemUUID);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent event){
		final Player player = event.getPlayer();
		final TimerRunnable runnable = this.cooldowns.get(player.getUniqueId());
		if(runnable != null && runnable.getRemaining() > 0L){
			runnable.setPaused(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerSpawnLocation(final PlayerSpawnLocationEvent event){
		final Player player = event.getPlayer();
		if(!player.hasPlayedBefore() && (!ConfigurationService.KIT_MAP)){
			if(!this.plugin.getEotwHandler().isEndOfTheWorld() && this.legible.add(player.getUniqueId())){
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou now have PvP Protection since you have died."));
			}
		}else if(this.isPaused(player) && this.getRemaining(player) > 0L && !this.plugin.getFactionManager().getFactionAt(event.getSpawnLocation()).isSafezone()){
			this.setPaused(player, player.getUniqueId(), false);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerClaimEnterMonitor(final PlayerClaimEnterEvent event){
		final Player player = event.getPlayer();
		if(event.getTo().getWorld().getEnvironment() == World.Environment.THE_END){
			this.clearCooldown(player);
			return;
		}
		final Faction toFaction = event.getToFaction();
		final Faction fromFaction = event.getFromFaction();
		if(fromFaction.isSafezone() && !toFaction.isSafezone()){
			if(this.legible.remove(player.getUniqueId())){
				this.setCooldown(player, player.getUniqueId());
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour PvP Protection Timer has started."));
				return;
			}
			if(this.getRemaining(player) > 0L){
				this.setPaused(player, player.getUniqueId(), false);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour PvP Protection Timer has started."));
			}
		}else if(!fromFaction.isSafezone() && toFaction.isSafezone() && this.getRemaining(player) > 0L){
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour PvP Protection Timer has been paused."));
			this.setPaused(player, player.getUniqueId(), true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerClaimEnter(final PlayerClaimEnterEvent event){
		final Player player = event.getPlayer();
		final Faction toFaction = event.getToFaction();
		final long remaining;
		if(toFaction instanceof ClaimableFaction && (remaining = this.getRemaining(player)) > 0L){
			final PlayerFaction playerFaction;
			if(event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT && toFaction instanceof PlayerFaction && (playerFaction = this.plugin.getFactionManager().getPlayerFaction(player)) != null && playerFaction.equals(toFaction)){
				// player.sendMessage(ChatColor.AQUA + "You have entered your own claim, therefore your " + this.getDisplayName() + ChatColor.AQUA + " has been removed.");
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bYou have entered your claim, meaning you no longer have PvP Protection."));
				this.clearCooldown(player);
				return;
			}
			if(!toFaction.isSafezone() && !(toFaction instanceof RoadFaction)){
				event.setCancelled(true);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot enter this claim while you have PvP Protection."));
				//  player.sendMessage(ChatColor.RED + "You cannot enter " + toFaction.getDisplayName(player) + ChatColor.RED + " whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]. " + "Use '" + ChatColor.GOLD + "/pvp enable" + ChatColor.RED + "' to remove this timer.");
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityDamageByEntity(final EntityDamageByEntityEvent event){
		final Entity entity = event.getEntity();
		if(entity instanceof Player){
			final Player attacker = BukkitUtils.getFinalAttacker(event, true);
			if(attacker == null){
				return;
			}
			final Player player = (Player) entity;
			long remaining;
			if((remaining = this.getRemaining(player)) > 0L){
				event.setCancelled(true);
				attacker.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + " still has PvP Protection."));
				//  attacker.sendMessage(ChatColor.RED + player.getName() + " has their " + this.getDisplayName() + ChatColor.RED + " timer for another " + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + '.');
				return;
			}
			if((remaining = this.getRemaining(attacker)) > 0L){
				event.setCancelled(true);
				attacker.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can not attack players while you have PvP Protection. Use &6/pvp enable &cto enable PvP"));
				// attacker.sendMessage(ChatColor.RED + "You cannot attack players whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]. Use '" + ChatColor.GOLD + "/pvp enable" + ChatColor.RED + "' to allow pvp.");
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPotionSplash(final PotionSplashEvent event){
		final ThrownPotion potion = event.getPotion();
		if(potion.getShooter() instanceof Player && BukkitUtils.isDebuff(potion)){
			for(final LivingEntity livingEntity : event.getAffectedEntities()){
				if(livingEntity instanceof Player && this.getRemaining((Player) livingEntity) > 0L){
					event.setIntensity(livingEntity, 0.0);
				}
			}
		}
	}

	public Set<UUID> getLegible(){
		return this.legible;
	}

	@Override
	public long getRemaining(final UUID playerUUID){
		return this.plugin.getEotwHandler().isEndOfTheWorld() ? 0L : super.getRemaining(playerUUID);
	}

	@Override
	public boolean setCooldown(@Nullable final Player player, final UUID playerUUID, final long duration, final boolean overwrite){
		return !this.plugin.getEotwHandler().isEndOfTheWorld() && super.setCooldown(player, playerUUID, duration, overwrite);
	}

	@Override
	public TimerRunnable clearCooldown(final UUID playerUUID){
		final TimerRunnable runnable = super.clearCooldown(playerUUID);
		if(runnable != null){
			this.legible.remove(playerUUID);
			return runnable;
		}
		return null;
	}

	@Override
	public void load(final Config config){
		super.load(config);
		final Object object = config.get("pvp-timer-legible");
		if(object instanceof List){
			this.legible.addAll((List<UUID>) object);
		}
	}

	@Override
	public void onDisable(final Config config){
		super.onDisable(config);
		config.set("pvp-timer-legible", new ArrayList<>(legible).toArray(new UUID[legible.size()]));
	}
}

