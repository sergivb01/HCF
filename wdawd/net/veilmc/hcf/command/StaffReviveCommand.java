package net.veilmc.hcf.command;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.deathban.Deathban;
import net.veilmc.hcf.user.FactionUser;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class StaffReviveCommand
		implements CommandExecutor, TabCompleter{
	private final HCF plugin;

	public StaffReviveCommand(HCF plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(ConfigurationService.KIT_MAP){
			sender.sendMessage(ChatColor.RED + "There is no need for this command on VeilMC kitmap.");
			return false;
		}
		if(args.length < 1){
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
			return true;
		}
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
		if((!target.hasPlayedBefore()) && (!target.isOnline())){
			sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
			return true;
		}
		UUID targetUUID = target.getUniqueId();
		FactionUser factionTarget = HCF.getPlugin().getUserManager().getUser(targetUUID);
		Deathban deathban = factionTarget.getDeathban();
		if((deathban == null) || (!deathban.isActive())){
			sender.sendMessage(ChatColor.RED + target.getName() + " is not deathbanned.");
			return true;
		}
		factionTarget.removeDeathban();
		Command.broadcastCommandMessage(sender, ChatColor.translateAlternateColorCodes('&', "&eYou have revived " + target.getName()));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lThis command will be deactivated soon, use command /death in future."));
		return false;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length != 1){
			return Collections.emptyList();
		}
		List<String> results = new ArrayList();
		for(FactionUser factionUser : this.plugin.getUserManager().getUsers().values()){
			Deathban deathban = factionUser.getDeathban();
			if((deathban != null) && (deathban.isActive())){
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(factionUser.getUserUUID());
				String name = offlinePlayer.getName();
				if(name != null){
					results.add(name);
				}
			}
		}
		return BukkitUtils.getCompletions(args, results);
	}


}
