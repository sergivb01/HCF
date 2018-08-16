package idaniel84.timer.type;

import idaniel84.timer.PlayerTimer;
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import idaniel84.HCF;
import idaniel84.classes.PvpClass;
import idaniel84.timer.PlayerTimer;
import idaniel84.timer.TimerRunnable;
import idaniel84.utils.ConfigurationService;
import net.veilmc.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static org.bukkit.Bukkit.getScheduler;

public class PvpClassWarmupTimer
		extends PlayerTimer
		implements Listener{
	protected final ConcurrentMap<Object, Object> classWarmups;
	private final HCF plugin;

	public PvpClassWarmupTimer(HCF plugin){
		super(ConfigurationService.PVP_CLASS_WARMUP_TIMER, TimeUnit.SECONDS.toMillis(10), false);
		this.plugin = plugin;
		this.classWarmups = CacheBuilder.newBuilder().expireAfterWrite(this.defaultCooldown + 5000, TimeUnit.MILLISECONDS).build().asMap();
		if (ConfigurationService.VEILZ || ConfigurationService.FFA) return;
		runTaskTimer(() -> {
			for(Player player : Bukkit.getServer().getOnlinePlayers()){
				attemptEquip(player);
			}
		}, 10L, 10L);
	}

	public BukkitTask runTaskTimer(Runnable runnable, long start, long period){ // your dev will appreciate this later super clean way of doing things IMO
		return getScheduler().runTaskTimer(plugin, runnable, start, period);
	}

	@Override
	public void onDisable(Config config){
		super.onDisable(config);
		this.classWarmups.clear();
	}

	@Override
	public ChatColor getScoreboardPrefix(){
		return ConfigurationService.PVP_CLASS_WARMUP_COLOUR;
	}

	@Override
	public TimerRunnable clearCooldown(UUID playerUUID){
		TimerRunnable runnable = super.clearCooldown(playerUUID);
		if(runnable != null){
			this.classWarmups.remove(playerUUID);
			return runnable;
		}
		return null;
	}

	@Override
	public void onExpire(UUID userUUID){
		Player player = Bukkit.getPlayer(userUUID);
		if(player == null){
			return;
		}
		String className = (String) this.classWarmups.remove(userUUID);
		if(this.classWarmups.remove(userUUID) == null){
			return;
		}
		//Preconditions.checkNotNull((Object)className, "Attempted to equip a class for %s, but nothing was added", (Object[])new Object[]{player.getName()});
		this.plugin.getPvpClassManager().setEquippedClass(player, this.plugin.getPvpClassManager().getPvpClass(className));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerQuitEvent event){
		this.plugin.getPvpClassManager().setEquippedClass(event.getPlayer(), null);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event){
		if (ConfigurationService.VEILZ || ConfigurationService.FFA) return;
		this.attemptEquip(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEquipmentSet(EquipmentSetEvent event){
		HumanEntity humanEntity = event.getHumanEntity();
		if(humanEntity instanceof Player){
			this.attemptEquip((Player) humanEntity);
		}
	}

	//I have no clue why he did it like this but its really cancer
	private void attemptEquip(Player player){
		PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(player);

		if(equipped != null){
			if(equipped.isApplicableFor(player))
				return;

			this.plugin.getPvpClassManager().setEquippedClass(player, null);
		}

		PvpClass warmupClass = null;
		String warmup = (String) this.classWarmups.get(player.getUniqueId());

		if(warmup != null && !(warmupClass = this.plugin.getPvpClassManager().getPvpClass(warmup)).isApplicableFor(player))
			this.clearCooldown(player.getUniqueId());


		Collection<PvpClass> pvpClasses = this.plugin.getPvpClassManager().getPvpClasses();

		for(PvpClass pvpClass : pvpClasses){
			if(warmupClass == pvpClass || !pvpClass.isApplicableFor(player))
				continue;

			//can we run this on a test server im just kinda guessing this will work let me build
			this.classWarmups.put(player.getUniqueId(), pvpClass.getName());
			this.plugin.getPvpClassManager().setEquippedClass(player, pvpClass);
			this.setCooldown(player, player.getUniqueId(), pvpClass.getWarmupDelay(), false);

			break;
		}
	}

}

