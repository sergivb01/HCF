package net.veilmc.hcf.kothgame.argument;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.utils.DateTimeFormats;
import net.veilmc.hcf.kothgame.EventTimer;
import net.veilmc.hcf.kothgame.faction.EventFaction;
import net.veilmc.util.command.CommandArgument;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EventUptimeArgument
		extends CommandArgument{
	private final HCF plugin;

	public EventUptimeArgument(HCF plugin){
		super("uptime", "Check the uptime of an event");
		this.plugin = plugin;
		this.permission = "hcf.command.event.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		EventFaction eventFaction;
		EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
		if(eventTimer.getRemaining() <= 0){
			sender.sendMessage(ChatColor.RED + "There is not a running event.");
			return true;
		}
		sender.sendMessage(ChatColor.YELLOW + "Up-time of " + eventTimer.getName() + " timer" + ((eventFaction = eventTimer.getEventFaction()) == null ? "" : new StringBuilder().append(": ").append(ChatColor.BLUE).append('(').append(eventFaction.getDisplayName(sender)).append(ChatColor.BLUE).append(')').toString()) + ChatColor.YELLOW + " is " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(eventTimer.getUptime(), true, true) + ChatColor.YELLOW + ", started at " + ChatColor.GOLD + DateTimeFormats.HR_MIN_AMPM_TIMEZONE.format(eventTimer.getStartStamp()) + ChatColor.YELLOW + '.');
		return true;
	}
}

