package idaniel84.faction.argument;

import idaniel84.HCF;
import idaniel84.faction.type.Faction;
import net.veilmc.util.command.CommandArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionShowArgument
		extends CommandArgument{
	private final HCF plugin;

	public FactionShowArgument(HCF plugin){
		super("show", "Get details about a faction.", new String[]{"i", "info", "who"});
		this.plugin = plugin;
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " [playerName|factionName]";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Faction namedFaction;
		Faction playerFaction = null;
		if(args.length < 2){
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
				return true;
			}
			namedFaction = this.plugin.getFactionManager().getPlayerFaction(((Player) sender).getUniqueId());
			if(namedFaction == null){
				sender.sendMessage(ChatColor.RED + "You are not in a faction.");
				return true;
			}
		}else{
			namedFaction = this.plugin.getFactionManager().getFaction(args[1]);
			playerFaction = this.plugin.getFactionManager().getFaction(args[1]);
			if(Bukkit.getPlayer(args[1]) != null){
				playerFaction = this.plugin.getFactionManager().getPlayerFaction(Bukkit.getPlayer(args[1]));
			}else if(Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()){
				playerFaction = this.plugin.getFactionManager().getPlayerFaction(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
			}
			if(namedFaction == null && playerFaction == null){
				sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
				return true;
			}
		}
		if(namedFaction != null){
			namedFaction.printDetails(sender);
		}
		if(!(playerFaction == null || namedFaction != null && namedFaction.equals(playerFaction))){
			playerFaction.printDetails(sender);
		}
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length != 2 || !(sender instanceof Player)){
			return Collections.emptyList();
		}
		if(args[1].isEmpty()){
			return null;
		}
		Player player = (Player) sender;
		ArrayList<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
		for(Player target : Bukkit.getOnlinePlayers()){
			if(!player.canSee(target) || results.contains(target.getName())) continue;
			results.add(target.getName());
		}
		return results;
	}
}

