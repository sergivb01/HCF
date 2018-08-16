package idaniel84.command;

import idaniel84.utils.ConfigurationService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class TogglefdCommand implements CommandExecutor, TabCompleter{
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		boolean newStatus;
		if(!sender.hasPermission("idaniel84.command.diamonds")){
			sender.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
		ConfigurationService.DIAMOND_ORE_ALERTS = newStatus = !ConfigurationService.DIAMOND_ORE_ALERTS;
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bYou have " + (newStatus ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled") + "&b found diamond ore notifications."));
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command commanda, String label, String[] args){
		return Collections.emptyList();
	}
}
