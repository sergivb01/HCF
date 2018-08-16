package idaniel84.listener.fixes;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.github.paperspigot.event.block.BeaconEffectEvent;

public class BeaconStrengthFixListener
		implements Listener{

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPotionEffectAdd(BeaconEffectEvent event){
		PotionEffect effect = event.getEffect();
		if(effect.getAmplifier() > 1 && effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)){
			event.getPlayer().addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(), 0, effect.isAmbient()));
			event.setCancelled(true);
		}
	}


}

