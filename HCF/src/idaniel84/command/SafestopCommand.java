package idaniel84.command;

import idaniel84.HCF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SafestopCommand
		implements CommandExecutor,
		TabCompleter{


	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		for(Player abc : Bukkit.getOnlinePlayers()){
			Bukkit.dispatchCommand(abc, "lobby");
		}
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "savedata");
		HCF.getPlugin().getUserManager().saveUserData();
		HCF.getPlugin().getFactionManager().saveFactionData();
		HCF.getPlugin().getEconomyManager().saveEconomyData();
		HCF.getPlugin().getEventManager().saveEventsData();
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "The server has been stopped by " + sender.getName());
		Bukkit.broadcastMessage(" ");
		Bukkit.shutdown();
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		return Collections.emptyList();
	}
}

