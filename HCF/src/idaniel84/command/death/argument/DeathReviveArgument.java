package idaniel84.command.death.argument;

import idaniel84.HCF;
import idaniel84.deathban.Deathban;
import idaniel84.user.FactionUser;
import net.veilmc.util.BukkitUtils;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DeathReviveArgument extends CommandArgument{
	private final HCF plugin;

	public DeathReviveArgument(final HCF plugin){
		super("revive", "Revive a deathbanned player");
		this.plugin = plugin;
		this.permission = "idaniel84.command.death.argument." + this.getName();
	}

	public String getUsage(final String label){
		return '/' + label + ' ' + this.getName() + " <playerName>";
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args){
		if(args.length < 2){
			sender.sendMessage(getUsage(command.getLabel()));
			return true;
		}
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
		if(!(target.hasPlayedBefore())){
			sender.sendMessage(ChatColor.RED + "Error: Player not found.");
			return true;
		}
		UUID targetUUID = target.getUniqueId();
		FactionUser factionTarget = HCF.getPlugin().getUserManager().getUser(targetUUID);
		Deathban deathban = factionTarget.getDeathban();
		if((deathban == null) || (!deathban.isActive())){
			sender.sendMessage(ChatColor.RED + "Error: Player is not deathbanned.");
			return true;
		}
		factionTarget.removeDeathban();
		Command.broadcastCommandMessage(sender, ChatColor.translateAlternateColorCodes('&', "&eYou have revived " + target.getName()));
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