package idaniel84.command;

import idaniel84.utils.ConfigurationService;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TeamspeakCommand implements CommandExecutor{
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args){
		sender.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Teamspeak: " + ChatColor.YELLOW + ConfigurationService.TEAMSPEAK_IP);
		return true;
	}
}
