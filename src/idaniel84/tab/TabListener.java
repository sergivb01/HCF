package idaniel84.tab;

import idaniel84.HCF;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TabListener implements Listener{
	private static HCF plugin;
	private static TUtils t;

	public TabListener(HCF plugin){
		Tab.setPlugin(plugin);
		this.t = new TUtils(plugin);
	}

	/*
		TODO:
			* Add listeners
			* Create player tab
			* Check different tab styles
			* Disable tab thing
			* Caching system (?)
	 */

	private static void update18(Player player){
		//61-80
		Tab tab = Tab.getByPlayer(player);
		if(tab.isClient18()){
			tab.setSlot(10, "&7For an optimal experience");
			tab.setSlot(11, "&fwe recommend the use of &4&l1.7");
		}
	}



	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();

		Tab.createTabList(player).setPlayerListHeaderFooter("My cool header", "My boring footer");

		for(int i = 1; i <= 80; i++){
			Tab.getByPlayer(player).setSlot(i, "Position " + i);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Tab.deleteTabList(event.getPlayer());
	}


}
