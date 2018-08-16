package idaniel84.timer.type;


import idaniel84.utils.ConfigurationService;
import idaniel84.utils.ConfigurationService;
import idaniel84.utils.ConfigurationService;
import idaniel84.HCF;
import idaniel84.combatlog.CombatLogListener;
import idaniel84.timer.PlayerTimer;
import idaniel84.utils.ConfigurationService;
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

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LogoutTimer
		extends PlayerTimer
		implements Listener{
	public LogoutTimer(){
		super(ConfigurationService.LOGOUT_TIMER, TimeUnit.SECONDS.toMillis(30), false);
	}

	@Override
	public ChatColor getScoreboardPrefix(){
		return ConfigurationService.LOGOUT_COLOUR;
	}

	private void checkMovement(Player player, Location from, Location to){
		if(from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()){
			return;
		}
		if(this.getRemaining(player) > 0){
			player.sendMessage(ChatColor.RED + "You moved a block, " + this.getDisplayName() + ChatColor.RED + " timer cancelled.");
			this.clearCooldown(player);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event){
		if(event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()){
			return;
		}
		this.checkMovement(event.getPlayer(), event.getFrom(), event.getTo());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event){
		this.checkMovement(event.getPlayer(), event.getFrom(), event.getTo());
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
		CombatLogListener.safelyDisconnect(player, ConfigurationService.LOGOUT_DISCONNECT);
	}

	public void run(Player player){
		long remainingMillis = this.getRemaining(player);
		if(remainingMillis > 0){
			player.sendMessage(ChatColor.YELLOW + "Logging out in: " + ChatColor.RED + HCF.getRemaining(remainingMillis, true));
			player.sendMessage(this.getDisplayName() + ChatColor.YELLOW + " timer is disconnecting you in " + ChatColor.RED + ChatColor.BOLD + HCF.getRemaining(remainingMillis, true, false) + ChatColor.BLUE + '.');
		}
	}
}

