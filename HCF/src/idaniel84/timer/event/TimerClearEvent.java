package idaniel84.timer.event;

import com.google.common.base.Optional;
import idaniel84.timer.PlayerTimer;
import idaniel84.timer.Timer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class TimerClearEvent
		extends Event{
	private static final HandlerList handlers = new HandlerList();
	private final Optional<UUID> userUUID;
	private final Timer timer;

	public TimerClearEvent(Timer timer){
		this.userUUID = Optional.absent();
		this.timer = timer;
	}

	public TimerClearEvent(UUID userUUID, PlayerTimer timer){
		this.userUUID = Optional.of(userUUID);
		this.timer = timer;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}

	public Optional<UUID> getUserUUID(){
		return this.userUUID;
	}

	public Timer getTimer(){
		return this.timer;
	}

	public HandlerList getHandlers(){
		return handlers;
	}
}

