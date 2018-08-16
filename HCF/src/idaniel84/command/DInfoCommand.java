package idaniel84.command;

import idaniel84.HCF;
import idaniel84.deathban.Deathban;
import idaniel84.user.FactionUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;

public class DInfoCommand
		implements CommandExecutor{
	private final HCF plugin;

	public DInfoCommand(HCF plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){


		if(args.length < 1){
			sender.sendMessage(ChatColor.RED + "Usage: /" + cmd.getName() + " <player>");
			return false;
		}
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
		if(!target.hasPlayedBefore()){
			sender.sendMessage(ChatColor.RED + "Player not found.");
			return false;
		}
		FactionUser t = HCF.getPlugin().getUserManager().getUser(target.getUniqueId());
		Deathban deathban = t.getDeathban();
		if((deathban == null) || (!deathban.isActive())){
			sender.sendMessage(ChatColor.RED + "Player is not deathbanned.");
			return false;
		}


		String remain = HCF.getRemaining(deathban.getRemaining(), true, false);
		Double x = deathban.getDeathPoint().getX();
		Double y = deathban.getDeathPoint().getY();
		Double z = deathban.getDeathPoint().getZ();
		DecimalFormat df = new DecimalFormat("##");
		sender.sendMessage(ChatColor.YELLOW + "Reason: " + ChatColor.LIGHT_PURPLE + deathban.getReason());
		sender.sendMessage(ChatColor.YELLOW + "Remaining: " + ChatColor.LIGHT_PURPLE + remain);
		sender.sendMessage(ChatColor.YELLOW + "Location:" + ChatColor.LIGHT_PURPLE + " x" + df.format(x) + ", y" + df.format(y) + ", z" + df.format(z));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lThis command will be deactivated soon, use command /death in future."));
		return true;
	}


}