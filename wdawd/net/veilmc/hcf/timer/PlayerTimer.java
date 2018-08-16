package net.veilmc.hcf.timer;

import net.veilmc.hcf.timer.event.TimerClearEvent;
import net.veilmc.hcf.timer.event.TimerExpireEvent;
import net.veilmc.hcf.timer.event.TimerExtendEvent;
import net.veilmc.hcf.timer.event.TimerPauseEvent;
import net.veilmc.hcf.timer.event.TimerStartEvent;
import com.google.common.base.Optional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

import net.veilmc.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public abstract class PlayerTimer
		extends Timer{
	private static final String COOLDOWN_PATH = "timer-cooldowns";
	protected final boolean persistable;
	protected final Map<UUID, TimerRunnable> cooldowns = new ConcurrentHashMap<UUID, TimerRunnable>();

	public PlayerTimer(String name, long defaultCooldown){
		this(name, defaultCooldown, true);
	}

	public PlayerTimer(String name, long defaultCooldown, boolean persistable){
		super(name, defaultCooldown);
		this.persistable = persistable;
	}

	public void onExpire(UUID userUUID){
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onTimerExpireLoadReduce(TimerExpireEvent event){
		Optional<UUID> optionalUserUUID;
		if(event.getTimer().equals(this) && (optionalUserUUID = event.getUserUUID()).isPresent()){
			UUID userUUID = optionalUserUUID.get();
			this.onExpire(userUUID);
			this.clearCooldown(userUUID);
		}
	}

	public void clearCooldown(Player player){
		this.clearCooldown(player.getUniqueId());
	}

	public TimerRunnable clearCooldown(UUID playerUUID){
		TimerRunnable runnable = this.cooldowns.remove(playerUUID);
		if(runnable != null){
			runnable.cancel();
			Bukkit.getPluginManager().callEvent(new TimerClearEvent(playerUUID, this));
			return runnable;
		}
		return null;
	}

	public void clearCooldowns(){
		for(UUID uuid : this.cooldowns.keySet()){
			this.clearCooldown(uuid);
		}
	}

	public boolean isPaused(Player player){
		return this.isPaused(player.getUniqueId());
	}

	public boolean isPaused(UUID playerUUID){
		TimerRunnable runnable = this.cooldowns.get(playerUUID);
		return runnable != null && runnable.isPaused();
	}

	public void setPaused(@Nullable Player player, UUID playerUUID, boolean paused){
		TimerRunnable runnable = this.cooldowns.get(playerUUID);
		if(runnable != null && runnable.isPaused() != paused){
			TimerPauseEvent event = new TimerPauseEvent(playerUUID, this, paused);
			Bukkit.getPluginManager().callEvent(event);
			if(!event.isCancelled()){
				runnable.setPaused(paused);
			}
		}
	}

	public boolean hasCooldown(Player player){
		return this.getRemaining(player) > 0;
	}

	public long getRemaining(Player player){
		return this.getRemaining(player.getUniqueId());
	}

	public long getRemaining(UUID playerUUID){
		TimerRunnable runnable = this.cooldowns.get(playerUUID);
		return runnable == null ? 0 : runnable.getRemaining();
	}

	public boolean setCooldown(@Nullable Player player, UUID playerUUID){
		return this.setCooldown(player, playerUUID, this.defaultCooldown, false);
	}

	public boolean setCooldown(@Nullable Player player, UUID playerUUID, long duration, boolean overwrite){
		TimerRunnable runnable = duration <= 0 ? this.clearCooldown(playerUUID) : this.cooldowns.get(playerUUID);
		if(runnable != null){
			long remaining = runnable.getRemaining();
			if(!overwrite && remaining > 0 && duration > remaining){
				return false;
			}
			TimerExtendEvent event = new TimerExtendEvent(player, playerUUID, this, remaining, duration);
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled()){
				return false;
			}
			runnable.setRemaining(duration);
		}else{
			Bukkit.getPluginManager().callEvent(new TimerStartEvent(player, playerUUID, this, duration));
			runnable = new TimerRunnable(playerUUID, this, duration);
		}
		this.cooldowns.put(playerUUID, runnable);
		return true;
	}

	@Override
	public void load(Config config){
		MemorySection section;
		if(!this.persistable){
			return;
		}
		String path = "timer-cooldowns." + this.name;
		Object object = config.get(path);
		if(object instanceof MemorySection){
			section = (MemorySection) object;
			long millis = System.currentTimeMillis();
			for(String id : section.getKeys(false)){
				long remaining = config.getLong(section.getCurrentPath() + '.' + id) - millis;
				if(remaining <= 0) continue;
				this.setCooldown(null, UUID.fromString(id), remaining, true);
			}
		}
		if((object = config.get(path = "timer-cooldowns." + this.name)) instanceof MemorySection){
			section = (MemorySection) object;
			for(String id2 : section.getKeys(false)){
				TimerRunnable timerRunnable = this.cooldowns.get(UUID.fromString(id2));
				if(timerRunnable == null) continue;
				timerRunnable.setPauseMillis(config.getLong(path + '.' + id2));
			}
		}
	}

	@Override
	public void onDisable(Config config){
		if(this.persistable){
			Set<Map.Entry<UUID, TimerRunnable>> entrySet = this.cooldowns.entrySet();
			LinkedHashMap<String, Long> pauseSavemap = new LinkedHashMap<String, Long>(entrySet.size());
			LinkedHashMap<String, Long> cooldownSavemap = new LinkedHashMap<String, Long>(entrySet.size());
			for(Map.Entry<UUID, TimerRunnable> entry : entrySet){
				String id = entry.getKey().toString();
				TimerRunnable runnable = entry.getValue();
				pauseSavemap.put(id, runnable.getPauseMillis());
				cooldownSavemap.put(id, runnable.getExpiryMillis());
			}
			config.set("timer-pauses." + this.name, pauseSavemap);
			config.set("timer-cooldowns." + this.name, cooldownSavemap);
		}
	}
}

