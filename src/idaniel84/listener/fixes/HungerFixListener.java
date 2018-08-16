package idaniel84.listener.fixes;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class HungerFixListener implements Listener{

	@EventHandler
	public void FixIt(FoodLevelChangeEvent e){
		if((e.getFoodLevel() < ((Player) e.getEntity()).getFoodLevel()) &&
				(new Random().nextInt(100) > 4)){
			e.setCancelled(true);
		}
	}
}