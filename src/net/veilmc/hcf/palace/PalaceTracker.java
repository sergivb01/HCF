package net.veilmc.hcf.palace;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.kothgame.CaptureZone;
import net.veilmc.hcf.kothgame.EventTimer;
import net.veilmc.hcf.kothgame.EventType;
import net.veilmc.hcf.kothgame.faction.EventFaction;
import net.veilmc.hcf.kothgame.tracker.EventTracker;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.hcf.utils.DateTimeFormats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class PalaceTracker
		implements EventTracker{
	public static final long DEFAULT_CAP_MILLIS1 = TimeUnit.MINUTES.toMillis(30L);
	private static final long MINIMUM_CONTROL_TIME_ANNOUNCE1 = TimeUnit.SECONDS.toMillis(25L);
	private final HCF plugin;

	public PalaceTracker(HCF plugin){
		this.plugin = plugin;
	}

	public EventType getEventType(){
		return EventType.PALACE;
	}

	public void tick(EventTimer eventTimer, EventFaction eventFaction){
		CaptureZone captureZone = ((PalaceFaction) eventFaction).getCaptureZone();
		long remainingMillis = captureZone.getRemainingCaptureMillis();
		if(remainingMillis <= 0L){
			this.plugin.getTimerManager().eventTimer.handleWinner(captureZone.getCappingPlayer());
			eventTimer.clearCooldown();
			return;
		}
		if(remainingMillis == captureZone.getDefaultCaptureMillis()){
			return;
		}
		int remainingSeconds = (int) (remainingMillis / 1000L);
		if((remainingSeconds > 0) && (remainingSeconds % 30 == 0)){
			Bukkit.broadcastMessage(ConfigurationService.BASECOLOUR + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.GOLD + "Someone is controlling " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + ". " + ChatColor.RED + '(' + DateTimeFormats.PALACE_FORMAT.format(remainingMillis) + ')');
		}
	}

	public void onContest(EventFaction eventFaction, EventTimer eventTimer){
		Bukkit.broadcastMessage(ConfigurationService.BASECOLOUR + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + eventFaction.getName() + ChatColor.GOLD + " can now be contested. " + ChatColor.RED + '(' + DateTimeFormats.PALACE_FORMAT.format(eventTimer.getRemaining1()) + ')');
	}

	public boolean onControlTake(Player player, CaptureZone captureZone){
		player.sendMessage(ChatColor.GOLD + "You are now in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
		return true;
	}

	public boolean onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction){
		player.sendMessage(ChatColor.GOLD + "You are no longer in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
		long remainingMillis = captureZone.getRemainingCaptureMillis();
		if((remainingMillis > 0L) && (captureZone.getDefaultCaptureMillis() - remainingMillis > MINIMUM_CONTROL_TIME_ANNOUNCE1)){
			Bukkit.broadcastMessage(ConfigurationService.BASECOLOUR + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GOLD + " has lost control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.' + ChatColor.RED + " (" + DateTimeFormats.PALACE_FORMAT.format(captureZone.getRemainingCaptureMillis()) + ')');
		}
		return true;
	}

	public void stopTiming(){
	}
}
