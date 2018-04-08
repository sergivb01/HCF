package net.veilmc.hcf.kothgame.koth.argument;

import net.veilmc.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class KothShowArgument
		extends CommandArgument{
	public KothShowArgument(){
		super("show", "View the information on a koth");
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args[1].isEmpty()){
			sender.sendMessage(ChatColor.RED + "FAIL: No koth");
			return true;
		}
		Bukkit.dispatchCommand(sender, "f who " + args[1]);
		return true;
	}
}

