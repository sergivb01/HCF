package net.veilmc.hcf.command;

import net.veilmc.hcf.HCF;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class HelpCommand implements CommandExecutor, TabCompleter{

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		for(String messages : HCF.getInstance().getConfig().getStringList("help")){
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages).replace("%OVERWORLD%", HCF.getPlugin().getServerHandler().getWorldBorder() + "")
					.replace("%NETHER%", HCF.getPlugin().getServerHandler().getNetherBorder() + "")
					.replace("%END%", HCF.getPlugin().getServerHandler().getEndBorder() + ""));
		}

		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		return Collections.emptyList();
	}
}

