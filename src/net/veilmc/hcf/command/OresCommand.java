package net.veilmc.hcf.command;

import net.veilmc.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class OresCommand implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args){
    /*    if (command.getName().equalsIgnoreCase("ores")) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "=========" + ChatColor.LIGHT_PURPLE +  " ORE STATS "+ ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + ChatColor.STRIKETHROUGH +"=========" );
            sender.sendMessage(ChatColor.GREEN + "Emerald Mined: " + ChatColor.GRAY + ((Player) sender).getStatistic(Statistic.MINE_BLOCK, Material.EMERALD_ORE));
            sender.sendMessage(ChatColor.AQUA + "Diamonds Mined: " + ChatColor.GRAY + ((Player) sender).getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE));
            sender.sendMessage(ChatColor.GOLD + "Gold Mined: " + ChatColor.GRAY + ((Player) sender).getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE));
            sender.sendMessage(ChatColor.RED + "Redstone Mined: " + ChatColor.GRAY + ((Player) sender).getStatistic(Statistic.MINE_BLOCK, Material.REDSTONE_ORE));
            sender.sendMessage(ChatColor.BLUE + "Lapis Mined: " + ChatColor.GRAY + ((Player) sender).getStatistic(Statistic.MINE_BLOCK, Material.LAPIS_ORE));
            sender.sendMessage(ChatColor.GRAY + "Iron Mined: " + ChatColor.GRAY + ((Player) sender).getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE));
            sender.sendMessage(ChatColor.DARK_GRAY + "Coal Mined: " + ChatColor.GRAY + ((Player) sender).getStatistic(Statistic.MINE_BLOCK, Material.COAL_ORE));
            return true;
        }*/
        if (args.length == 0) {
            Bukkit.dispatchCommand(sender, "ores " + sender.getName());
            return true;
        }

		OfflinePlayer target = Bukkit.getServer().getPlayer(args[0]);
		if(target == null || (!(target.hasPlayedBefore()))){
			sender.sendMessage(ChatColor.RED + "Error: Player has not played before.");
			return true;
		}
		sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lOre Statistics"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "Player: " + target.getName()));
		Integer diamonds = target.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE);
		if (diamonds < 250) sender.sendMessage("Miner level: 0 (Default effects)");
		if (diamonds >= 250 && diamonds < 500) sender.sendMessage("Miner level: 1");
		if (diamonds >= 500 && diamonds < 750) sender.sendMessage("Miner level: 2");
		if (diamonds >= 750 && diamonds < 1000) sender.sendMessage("Miner level: 3");
		if (diamonds >= 1000 && diamonds < 2000) sender.sendMessage("Miner level: 4");
		if (diamonds >= 2000) sender.sendMessage("Miner level: 5");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.BLUE + "Emerald Mined: " + ChatColor.WHITE + target.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.EMERALD_ORE));
		sender.sendMessage(ChatColor.BLUE + "Diamonds Mined: " + ChatColor.WHITE + target.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE));
		sender.sendMessage(ChatColor.BLUE + "Gold Mined: " + ChatColor.WHITE + target.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE));
		sender.sendMessage(ChatColor.BLUE + "Redstone Mined: " + ChatColor.WHITE + target.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.REDSTONE_ORE));
		sender.sendMessage(ChatColor.BLUE + "Lapis Mined: " + ChatColor.WHITE + target.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.LAPIS_ORE));
		sender.sendMessage(ChatColor.BLUE + "Iron Mined: " + ChatColor.WHITE + target.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE));
		sender.sendMessage(ChatColor.BLUE + "Coal Mined: " + ChatColor.WHITE + target.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.COAL_ORE));
		sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		return true;
	}
}
