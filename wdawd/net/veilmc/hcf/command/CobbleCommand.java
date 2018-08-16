package net.veilmc.hcf.command;

import net.veilmc.hcf.utils.ConfigurationService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class CobbleCommand implements Listener, CommandExecutor{

	private static ArrayList cobbletoggle = new ArrayList();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return false;
		}

		Player p = (Player) sender;
		if(args.length == 0){
			if((!cobbletoggle.contains(p.getName()))){
				p.sendMessage(ConfigurationService.COBBLE_DISABLED);
				cobbletoggle.add(p.getName());
			}else if((cobbletoggle.contains(p.getName()))){
				cobbletoggle.remove(p.getName());
				p.sendMessage(ConfigurationService.COBBLE_ENABLED);
			}
		}

		return true;
	}

	@EventHandler
	public void onPlayerPickup(PlayerPickupItemEvent event){
		Material type = event.getItem().getItemStack().getType();

		if(type == Material.STONE || type == Material.COBBLESTONE){
			if(cobbletoggle.contains(event.getPlayer().getName())){
				event.setCancelled(true);
			}
		}
	}


	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onQuit(PlayerQuitEvent event){
		Player p = event.getPlayer();
		cobbletoggle.remove(p.getName());
	}

}
