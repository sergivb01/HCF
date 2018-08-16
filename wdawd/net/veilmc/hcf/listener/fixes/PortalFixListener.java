package net.veilmc.hcf.listener.fixes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PortalFixListener
		implements Listener{

	@EventHandler
	public void onClick(PlayerInteractEvent e){

		if(e.getClickedBlock() == null){
			return;
		}
		if(e.getClickedBlock().getType() == Material.PORTAL){
			Player s = e.getPlayer();
			s.sendMessage(ChatColor.LIGHT_PURPLE + "You activated your stuck timer by right clicking the Nether Portal.");
			Bukkit.dispatchCommand(s, "f stuck");
		}
	}
}
