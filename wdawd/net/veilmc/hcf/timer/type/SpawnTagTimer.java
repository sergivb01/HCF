package net.veilmc.hcf.timer.type;

import net.veilmc.base.kit.event.KitApplyEvent;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.hcf.faction.event.PlayerClaimEnterEvent;
import net.veilmc.hcf.faction.event.PlayerJoinFactionEvent;
import net.veilmc.hcf.faction.event.PlayerLeaveFactionEvent;
import net.veilmc.hcf.timer.PlayerTimer;
import net.veilmc.hcf.timer.event.TimerClearEvent;
import net.veilmc.hcf.timer.event.TimerStartEvent;
import net.veilmc.hcf.visualise.VisualType;
import net.veilmc.util.BukkitUtils;
import com.google.common.base.Optional;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SpawnTagTimer
		extends PlayerTimer
		implements Listener{
	private static final long NON_WEAPON_TAG = 5000;
	private final HCF plugin;

	public SpawnTagTimer(HCF plugin){
		super(ConfigurationService.SPAWNTAG_TIMER, TimeUnit.SECONDS.toMillis(30));
		this.plugin = plugin;
	}

	@Override
	public ChatColor getScoreboardPrefix(){
		return ConfigurationService.SPAWNTAG_COLOUR;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onKitApply(KitApplyEvent event){
		long remaining;
		Player player = event.getPlayer();
		if(!event.isForce() && (remaining = this.getRemaining(player)) > 0){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You cannot apply kits whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onTimerStop(TimerClearEvent event){
		Optional<UUID> optionalUserUUID;
		if(event.getTimer().equals(this) && (optionalUserUUID = event.getUserUUID()).isPresent()){
			this.onExpire(optionalUserUUID.get());
		}
	}

	@Override
	public void onExpire(UUID userUUID){
		Player player = Bukkit.getPlayer(userUUID);
		if(player == null){
			return;
		}
		this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.SPAWN_BORDER, null);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onFactionJoin(PlayerJoinFactionEvent event){
		long remaining;
		Player player;
		Optional<Player> optional = event.getPlayer();
		if(optional.isPresent() && (remaining = this.getRemaining(player = optional.get())) > 0){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You cannot join factions whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + " remaining]");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onFactionLeave(PlayerLeaveFactionEvent event){
		Player player;
		Optional<Player> optional = event.getPlayer();
		if(optional.isPresent() && this.getRemaining(player = optional.get()) > 0){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You cannot join factions whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + " remaining]");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPreventClaimEnter(PlayerClaimEnterEvent event){
		if(event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT){
			return;
		}
		Player player = event.getPlayer();
		if(!event.getFromFaction().isSafezone() && event.getToFaction().isSafezone() && this.getRemaining(player) > 0){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You cannot enter " + event.getToFaction().getDisplayName(player) + ChatColor.RED + " whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + " remaining]");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
		if (ConfigurationService.FFA) {
			return;
		}
		Entity entity;
		Player attacker = BukkitUtils.getFinalAttacker(event, true);
		if(attacker != null && (entity = event.getEntity()) instanceof Player){
			Player attacked = (Player) entity;
			boolean weapon = event.getDamager() instanceof Arrow;
			if(!weapon){
				ItemStack stack = attacker.getItemInHand();
				weapon = stack != null && EnchantmentTarget.WEAPON.includes(stack);
			}
			long duration = weapon ? this.defaultCooldown : 30000;
			this.setCooldown(attacked, attacked.getUniqueId(), Math.max(this.getRemaining(attacked), duration), true);
			this.setCooldown(attacker, attacker.getUniqueId(), Math.max(this.getRemaining(attacker), duration), true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onTimerStart(TimerStartEvent event){
		Optional<Player> optional;
		if(event.getTimer().equals(this) && (optional = event.getPlayer()).isPresent()){
			Player player = optional.get();
			//  player.sendMessage(ConfigurationService.SPAWN_TAGGED.replace("%time%", DurationFormatUtils.formatDurationWords((long)event.getDuration(), (boolean)true, (boolean)true)));
			player.sendMessage(ChatColor.YELLOW + "You are now spawn-tagged for " + ChatColor.RED + DurationFormatUtils.formatDurationWords(event.getDuration(), true, true) + ChatColor.YELLOW + '.');
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event){
		this.clearCooldown(event.getPlayer().getUniqueId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPreventClaimEnterMonitor(PlayerClaimEnterEvent event){
		if(event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT && !event.getFromFaction().isSafezone() && event.getToFaction().isSafezone()){
			this.clearCooldown(event.getPlayer());
		}
	}
}

