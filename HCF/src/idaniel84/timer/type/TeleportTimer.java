package idaniel84.timer.type;

import idaniel84.HCF;
import idaniel84.utils.ConfigurationService;
import idaniel84.faction.FactionManager;
import idaniel84.faction.type.Faction;
import idaniel84.timer.PlayerTimer;
import idaniel84.timer.TimerRunnable;
import net.minecraft.util.com.google.common.cache.CacheBuilder;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportTimer
		extends PlayerTimer
		implements Listener{
	private final ConcurrentMap<Object, Object> destinationMap;
	private final HCF plugin;

	public TeleportTimer(HCF plugin){
		super(ConfigurationService.TELEPORT_TIMER, TimeUnit.SECONDS.toMillis(10), false);
		this.plugin = plugin;
		this.destinationMap = CacheBuilder.newBuilder().expireAfterWrite(60000, TimeUnit.MILLISECONDS).build().asMap();
	}

	public Object getDestination(Player player){
		return this.destinationMap.get(player.getUniqueId());
	}

	@Override
	public ChatColor getScoreboardPrefix(){
		return ConfigurationService.TELEPORT_COLOUR;
	}

	@Override
	public TimerRunnable clearCooldown(UUID uuid){
		TimerRunnable runnable = super.clearCooldown(uuid);
		if(runnable != null){
			this.destinationMap.remove(uuid);
			return runnable;
		}
		return null;
	}

	public int getNearbyEnemies(final Player player, final int distance){
		final FactionManager factionManager = this.plugin.getFactionManager();
		final Faction playerFaction = factionManager.getPlayerFaction(player.getUniqueId());
		int count = 0;
		final Collection<Entity> nearby = player.getNearbyEntities((double) distance, (double) distance, (double) distance);
		for(final Entity entity : nearby){
			if(entity instanceof Player){
				final Player target = (Player) entity;
				if(!target.canSee(player)){
					continue;
				}
				if(!player.canSee(target)){
					continue;
				}
				final Faction targetFaction;
				if(playerFaction != null && (targetFaction = factionManager.getPlayerFaction(target)) != null && targetFaction.equals(playerFaction)){
					continue;
				}
				++count;
			}
		}
		return count;
	}


	public boolean teleport(Player player, Location location, long millis, String warmupMessage, PlayerTeleportEvent.TeleportCause cause){
		boolean result;
		this.cancelTeleport(player, null);
		if(millis <= 0){
			result = player.teleport(location, cause);
			this.clearCooldown(player.getUniqueId());
		}else{
			UUID uuid = player.getUniqueId();
			player.sendMessage(warmupMessage);
			this.destinationMap.put(uuid, location.clone());
			this.setCooldown(player, uuid, millis, true);
			result = true;
		}
		return result;
	}

	public void cancelTeleport(Player player, String reason){
		UUID uuid = player.getUniqueId();
		if(this.getRemaining(uuid) > 0){
			this.clearCooldown(uuid);
			if(reason != null && !reason.isEmpty()){
				player.sendMessage(reason);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event){
		Location from = event.getFrom();
		Location to = event.getTo();
		if(from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()){
			return;
		}
		this.cancelTeleport(event.getPlayer(), ChatColor.YELLOW + "You moved a block, therefore cancelling your teleport.");
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageEvent event){
		Entity entity = event.getEntity();
		if(entity instanceof Player){
			this.cancelTeleport((Player) entity, ChatColor.YELLOW + "You took damage, therefore cancelling your teleport.");
		}
	}

	@Override
	public void onExpire(UUID userUUID){
		Player player = Bukkit.getPlayer(userUUID);
		if(player == null){
			return;
		}
		Location destination = (Location) this.destinationMap.remove(userUUID);
		if(destination != null){
			destination.getChunk();
			player.playEffect(player.getLocation().clone().add(0.5, 1.0, 0.5), Effect.ENDER_SIGNAL, 3);
			player.teleport(destination, PlayerTeleportEvent.TeleportCause.COMMAND);
		}
	}
}

