package idaniel84.listener;

import net.veilmc.base.kit.event.KitApplyEvent;
import idaniel84.HCF;
import idaniel84.utils.ConfigurationService;
import idaniel84.faction.type.Faction;
import idaniel84.faction.type.PlayerFaction;
import idaniel84.timer.event.TimerStartEvent;
import idaniel84.timer.type.PvpProtectionTimer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class KitListener
		implements Listener{
	private final HCF plugin;

	public KitListener(HCF plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onTimer(TimerStartEvent e){
		if(ConfigurationService.KIT_MAP && e.getTimer() instanceof PvpProtectionTimer){
			this.plugin.getTimerManager().pvpProtectionTimer.clearCooldown(e.getUserUUID().get());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onKitApply(KitApplyEvent event){
		PlayerFaction playerFaction;
		Player player = event.getPlayer();
		Location location = player.getLocation();
		Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
		if(!(factionAt.isSafezone() || (playerFaction = this.plugin.getFactionManager().getPlayerFaction(player)) != null && playerFaction.equals(factionAt))){
			player.sendMessage(ChatColor.RED + "Kits can only be applied in safe-zones or your own claims.");
			event.setCancelled(true);
		}
	}
}

