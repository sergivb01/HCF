package idaniel84.kothgame.argument;

import idaniel84.kothgame.EventTimer;
import idaniel84.HCF;
import idaniel84.kothgame.EventTimer;
import idaniel84.kothgame.faction.EventFaction;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EventCancelArgument
		extends CommandArgument{
	private final HCF plugin;

	public EventCancelArgument(HCF plugin){
		super("cancel", "Cancels a running event", new String[]{"stop", "end"});
		this.plugin = plugin;
		this.permission = "hcf.command.event.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
		EventFaction eventFaction = eventTimer.getEventFaction();
		if(!eventTimer.clearCooldown()){
			sender.sendMessage(ChatColor.RED + "There is not a running event.");
			return true;
		}
		Bukkit.broadcastMessage(sender.getName() + ChatColor.YELLOW + " has cancelled " + (eventFaction == null ? "the active event" : ChatColor.AQUA + eventFaction.getName() + ChatColor.YELLOW) + ".");
		return true;
	}
}

