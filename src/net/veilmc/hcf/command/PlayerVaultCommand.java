package net.veilmc.hcf.command;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.utils.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerVaultCommand implements CommandExecutor{
	private final HCF plugin;

	public PlayerVaultCommand(HCF plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Twat.");
			return false;
		}

		if(!ConfigurationService.KIT_MAP && !ConfigurationService.VEILZ){
			sender.sendMessage(ChatColor.RED + "This command can be executed on Kits only.");
			return true;
		}

		Player player = (Player) sender;
		Player target = player;
		if(player.hasPermission("hcf.command.pv.others") && args.length != 0){
			target = Bukkit.getPlayer(args[0]);

			if(target == null){
				player.sendMessage(ChatColor.RED + "Uknown player.");
				return true;
			}
		}

		player.openInventory(target.getEnderChest());
		player.sendMessage(ChatColor.WHITE + "Opened " + ChatColor.AQUA.toString() + ChatColor.BOLD + (player.equals(target) ? "your" : target.getName() + "'s") + ChatColor.WHITE + " player vault.");

		return true;
	}
}