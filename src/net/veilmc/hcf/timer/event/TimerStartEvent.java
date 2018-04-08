package net.veilmc.hcf.timer.event;

import net.veilmc.hcf.timer.PlayerTimer;
import net.veilmc.hcf.timer.Timer;
import com.google.common.base.Optional;

import java.util.UUID;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimerStartEvent
		extends Event{
	private static final HandlerList handlers = new HandlerList();
	private final Optional<Player> player;
	private final Optional<UUID> userUUID;
	private final Timer timer;
	private final long duration;

	public TimerStartEvent(Timer timer, long duration){
		this.player = Optional.absent();
		this.userUUID = Optional.absent();
		this.timer = timer;
		this.duration = duration;
	}

	public TimerStartEvent(@Nullable Player player, UUID uniqueId, PlayerTimer timer, long duration){
		this.player = Optional.fromNullable(player);
		this.userUUID = Optional.fromNullable(uniqueId);
		this.timer = timer;
		this.duration = duration;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}

	public Optional<Player> getPlayer(){
		return this.player;
	}

	public Optional<UUID> getUserUUID(){
		return this.userUUID;
	}

	public Timer getTimer(){
		return this.timer;
	}

	public long getDuration(){
		return this.duration;
	}

	public HandlerList getHandlers(){
		return handlers;
	}
}

