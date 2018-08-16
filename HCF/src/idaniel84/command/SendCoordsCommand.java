package idaniel84.command;

import idaniel84.HCF;
import idaniel84.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.HashMap;


public class SendCoordsCommand
		implements CommandExecutor{

	private final HCF plugin;
	public HashMap<String, Long> cooldowns = new HashMap<String, Long>();


	public SendCoordsCommand(HCF plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

		int cooldownTime = 15;
		if(cooldowns.containsKey(sender.getName())){
			long sLeft = ((cooldowns.get(sender.getName()) / 1000) + cooldownTime) - (System.currentTimeMillis() / 1000);
			if(sLeft > 0){
				sender.sendMessage(ChatColor.RED + "You must wait " + sLeft + " more seconds to use this command again.");
				return true;
			}
		}
		cooldowns.put(sender.getName(), System.currentTimeMillis());
		Player player = (Player) sender;
		PlayerFaction faction = this.plugin.getFactionManager().getPlayerFaction(player);
		if(faction == null){
			sender.sendMessage(ChatColor.RED + "You must be in a faction to use this command.");
			return true;
		}
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
		DecimalFormat df = new DecimalFormat("#");
		playerFaction.broadcast(ChatColor.translateAlternateColorCodes('&', "&a&lFACTION &eCoordinates of " + player.getName() + "&c " + df.format(player.getLocation().getX()) + ", " + df.format(player.getLocation().getY()) + ", " + df.format(player.getLocation().getZ())));
		return true;

	}


}
