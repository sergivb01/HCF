package net.veilmc.hcf.command;

import net.md_5.bungee.api.ChatColor;
import net.veilmc.hcf.listener.DeathListener;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RefundCommand
		implements CommandExecutor{
	public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args){
		String Usage = ChatColor.RED + "/" + s + " <playerName> <reason>";
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "You must be a player");
			return true;
		}
		Player p = (Player) cs;
		if(args.length < 2){
			cs.sendMessage(Usage);
			return true;
		}
		if(Bukkit.getPlayer(args[0]) == null){
			p.sendMessage(ChatColor.RED + "Player must be online");
			return true;
		}
		Player target = Bukkit.getPlayer(args[0]);
		if(DeathListener.PlayerInventoryContents.containsKey(target.getUniqueId())){
			target.getInventory().setContents(DeathListener.PlayerInventoryContents.get(target.getUniqueId()));
			target.getInventory().setArmorContents(DeathListener.PlayerArmorContents.get(target.getUniqueId()));
			String reason = StringUtils.join(args, ' ', 1, args.length);

			Command.broadcastCommandMessage(p, ChatColor.YELLOW + "Returned " + target.getName() + "'s items for: " + reason);
			DeathListener.PlayerArmorContents.remove(target.getUniqueId());
			DeathListener.PlayerInventoryContents.remove(target.getUniqueId());
			p.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&c&lThis command will be deactivated soon, use command /death in future."));
			return true;
		}
		p.sendMessage(ChatColor.RED + "That player's inventory has already been rolledback!");

		return false;
	}
}

