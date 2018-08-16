package idaniel84.timer.type;

import idaniel84.HCF;
import idaniel84.combatlog.CombatLogListener;
import idaniel84.faction.LandMap;
import idaniel84.timer.PlayerTimer;
import idaniel84.timer.TimerRunnable;
import idaniel84.utils.ConfigurationService;
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class StuckTimer
		extends PlayerTimer
		implements Listener{
	private final ConcurrentMap<Object, Object> startedLocations;

	public StuckTimer(){
		super(ConfigurationService.STUCK_TIMER, TimeUnit.MINUTES.toMillis(2) + TimeUnit.SECONDS.toMillis(45), false);
		this.startedLocations = CacheBuilder.newBuilder().expireAfterWrite(this.defaultCooldown + 5000, TimeUnit.MILLISECONDS).build().asMap();
	}

	@Override
	public ChatColor getScoreboardPrefix(){
		return ConfigurationService.STUCK_COLOUR;
	}

	@Override
	public TimerRunnable clearCooldown(UUID uuid){
		TimerRunnable runnable = super.clearCooldown(uuid);
		if(runnable != null){
			this.startedLocations.remove(uuid);
			return runnable;
		}
		return null;
	}

	@Override
	public boolean setCooldown(@Nullable Player player, UUID playerUUID, long millis, boolean force){
		if(player != null && super.setCooldown(player, playerUUID, millis, force)){
			this.startedLocations.put(playerUUID, player.getLocation());
			return true;
		}
		return false;
	}

	private void checkMovement(Player player, Location from, Location to){
		UUID uuid = player.getUniqueId();
		if(this.getRemaining(uuid) > 0){
			if(from == null){
				this.clearCooldown(uuid);
				return;
			}
			int xDiff = Math.abs(from.getBlockX() - to.getBlockX());
			int yDiff = Math.abs(from.getBlockY() - to.getBlockY());
			int zDiff = Math.abs(from.getBlockZ() - to.getBlockZ());
			if(xDiff > 5 || yDiff > 5 || zDiff > 5){
				this.clearCooldown(uuid);
				player.sendMessage(ChatColor.RED + "You moved more than " + ChatColor.BOLD + 5 + ChatColor.RED + " blocks. " + this.getDisplayName() + ChatColor.RED + " timer ended.");
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event){
		if(event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()){
			return;
		}
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		if(this.getRemaining(uuid) > 0){
			Location from = (Location) this.startedLocations.get(uuid);
			this.checkMovement(player, from, event.getTo());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event){
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		if(this.getRemaining(uuid) > 0){
			Location from = (Location) this.startedLocations.get(uuid);
			this.checkMovement(player, from, event.getTo());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event){
		UUID uuid = event.getPlayer().getUniqueId();
		if(this.getRemaining(event.getPlayer().getUniqueId()) > 0){
			this.clearCooldown(uuid);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event){
		UUID uuid = event.getPlayer().getUniqueId();
		if(this.getRemaining(event.getPlayer().getUniqueId()) > 0){
			this.clearCooldown(uuid);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageEvent event){
		Player player;
		Entity entity = event.getEntity();
		if(entity instanceof Player && this.getRemaining(player = (Player) entity) > 0){
			player.sendMessage(ChatColor.RED + "You were damaged, " + this.getDisplayName() + ChatColor.RED + " timer ended.");
			this.clearCooldown(player);
		}
	}

	@Override
	public void onExpire(UUID userUUID){
		Player player = Bukkit.getPlayer(userUUID);
		if(player == null){
			return;
		}
		Location nearest = LandMap.getNearestSafePosition(player, player.getLocation(), 124);
		if(nearest == null){
			CombatLogListener.safelyDisconnect(player, ChatColor.RED + "Unable to find a safe location, you have been safely logged out.");
			player.sendMessage(ChatColor.RED + "No safe-location found.");
			return;
		}
		if(player.teleport(nearest, PlayerTeleportEvent.TeleportCause.PLUGIN)){
			player.sendMessage(ChatColor.YELLOW + this.getDisplayName() + ChatColor.YELLOW + " timer has teleported you to the nearest safe area.");
		}
	}

	public void run(Player player){
		long remainingMillis = this.getRemaining(player);
		if(remainingMillis > 0){
			player.sendMessage(this.getDisplayName() + ChatColor.BLUE + " timer is teleporting you in " + ChatColor.BOLD + HCF.getRemaining(remainingMillis, true, false) + ChatColor.BLUE + '.');
		}
	}
}

