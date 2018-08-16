package idaniel84.command.spawn.argument;

import idaniel84.HCF;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TokenCheckArgument extends CommandArgument{
	private final HCF plugin;

	public TokenCheckArgument(final HCF plugin){
		super("check", "Check Tokens");
		this.plugin = plugin;
		this.permission = "hcf.command.token.argument." + this.getName();
	}

	public String getUsage(final String label){
		return '/' + label + ' ' + this.getName() + " [playerName]";
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args){
		OfflinePlayer target;
		if(args.length > 1){
			target = Bukkit.getOfflinePlayer(args[1]);
		}else{
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
				return true;
			}
			target = (OfflinePlayer) sender;
		}
		if(!target.hasPlayedBefore() && !target.isOnline()){
			sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
			return true;
		}
		final int targetLives = this.plugin.getUserManager().getUser(target.getUniqueId()).getSpawnTokens();
		sender.sendMessage(ChatColor.YELLOW + target.getName() + ChatColor.YELLOW + " has " + ChatColor.LIGHT_PURPLE + targetLives + ChatColor.YELLOW + ' ' + ((targetLives == 1) ? "token" : "tokens") + '.');
		return true;
	}

	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args){
		return (args.length == 2) ? null : Collections.emptyList();
	}
}