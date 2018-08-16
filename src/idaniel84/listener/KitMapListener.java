package idaniel84.listener;

import idaniel84.HCF;
import idaniel84.timer.event.TimerStartEvent;
import idaniel84.timer.type.PvpProtectionTimer;
import idaniel84.HCF;
import idaniel84.timer.event.TimerStartEvent;
import idaniel84.timer.type.PvpProtectionTimer;
import idaniel84.timer.event.TimerStartEvent;
import idaniel84.timer.type.PvpProtectionTimer;
import net.veilmc.base.kit.event.KitApplyEvent;
import idaniel84.timer.event.TimerStartEvent;
import idaniel84.timer.type.PvpProtectionTimer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class KitMapListener
		implements Listener{
	final HCF plugin;

	public KitMapListener(HCF plugin){
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event){
		event.setCancelled(true);
	}

	@EventHandler
	public void onTimer(TimerStartEvent e){
		if(e.getTimer() instanceof PvpProtectionTimer){
			this.plugin.getTimerManager().pvpProtectionTimer.clearCooldown(e.getUserUUID().get());
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		if(this.plugin.getTimerManager().pvpProtectionTimer.getRemaining(e.getPlayer()) >= 0){
			this.plugin.getTimerManager().pvpProtectionTimer.clearCooldown(e.getPlayer());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onKitApplyMonitor(KitApplyEvent event){
		Player player = event.getPlayer();
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
	}

}

