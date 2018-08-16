package idaniel84.kothgame.argument;

import idaniel84.HCF;
import idaniel84.faction.type.Faction;
import idaniel84.kothgame.faction.EventFaction;
import net.veilmc.util.command.CommandArgument;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EventRenameArgument
		extends CommandArgument{
	private final HCF plugin;

	public EventRenameArgument(HCF plugin){
		super("rename", "Renames an event");
		this.plugin = plugin;
		this.permission = "idaniel84.command.event.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <oldName> <newName>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length < 3){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		Faction faction = this.plugin.getFactionManager().getFaction(args[2]);
		if(faction != null){
			sender.sendMessage(ChatColor.RED + "There is already a faction named " + args[2] + '.');
			return true;
		}
		faction = this.plugin.getFactionManager().getFaction(args[1]);
		if(!(faction instanceof EventFaction)){
			sender.sendMessage(ChatColor.RED + "There is not an event faction named '" + args[1] + "'.");
			return true;
		}
		String oldName = faction.getName();
		faction.setName(args[2], sender);
		sender.sendMessage(ChatColor.YELLOW + "Renamed event " + ChatColor.WHITE + oldName + ChatColor.YELLOW + " to " + ChatColor.WHITE + faction.getName() + ChatColor.YELLOW + '.');
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length != 2){
			return Collections.emptyList();
		}
		return this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map(Faction::getName).collect(Collectors.toList());
	}
}

