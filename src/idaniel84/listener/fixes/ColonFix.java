package idaniel84.listener.fixes;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ColonFix implements Listener{

	@EventHandler
	public void onPlayerColonCommand(PlayerCommandPreprocessEvent e){
		if((e.getMessage().startsWith("/minecraft:")) || (e.getMessage().startsWith("bukkit:"))){
			e.setCancelled(true);
		}else if(((e.getMessage().startsWith("/ver")) || (e.getMessage().startsWith("/about"))) && (!e.getPlayer().isOp())){
			e.setCancelled(true);
		}
	}
}
