package idaniel84.classes.bard;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.sergivb01.event.PotionEffectExpiresEvent;
import idaniel84.HCF;
import idaniel84.classes.event.PvpClassUnequipEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.UUID;

public class BardRestorer implements Listener{
	private final Table<UUID, PotionEffectType, PotionEffect> restores;

	public BardRestorer(final HCF plugin){
		this.restores = HashBasedTable.create();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPvpClassUnequip(final PvpClassUnequipEvent event){
		this.restores.rowKeySet().remove(event.getPlayer().getUniqueId());
	}

	public void setRestoreEffect(final Player player, final PotionEffect effect){
		if(effect == null){
			return;
		}
		boolean shouldCancel = true;
		final Collection<PotionEffect> activeList = player.getActivePotionEffects();
		for(final PotionEffect active : activeList){
			if(active.getType().equals(effect.getType())){
				if(effect.getAmplifier() < active.getAmplifier()){
					return;
				}
				if(effect.getAmplifier() == active.getAmplifier() && effect.getDuration() < active.getDuration()){
					return;
				}
				this.restores.put(player.getUniqueId(), active.getType(), active);
				shouldCancel = false;
			}
		}
		player.addPotionEffect(effect, true);
		if(shouldCancel && effect.getDuration() > 100 && effect.getDuration() < BardClass.DEFAULT_MAX_DURATION){
			this.restores.remove(player.getUniqueId(), effect.getType());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPotionEffectExpire(final PotionEffectExpiresEvent event){
		final LivingEntity livingEntity = event.getEntity();
		if(livingEntity instanceof Player){
			final Player player = (Player) livingEntity;
			final PotionEffect previous = this.restores.remove(player.getUniqueId(), event.getEffect().getType());
			if(previous != null){
				event.setCancelled(true);
				new BukkitRunnable(){
					public void run(){
						player.addPotionEffect(previous, true);
					}
				}.runTask(HCF.getPlugin());
			}
		}
	}

}

