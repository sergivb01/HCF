package idaniel84.timer.event;

import idaniel84.timer.GlobalTimer;
import idaniel84.timer.PlayerTimer;
import idaniel84.timer.Timer;
import com.google.common.base.Optional;

import java.util.UUID;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimerExtendEvent
		extends Event
		implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private final Optional<Player> player;
	private final Optional<UUID> userUUID;
	private final Timer timer;
	private final long previousDuration;
	private boolean cancelled;
	private long newDuration;

	public TimerExtendEvent(GlobalTimer timer, long previousDuration, long newDuration){
		this.player = Optional.absent();
		this.userUUID = Optional.absent();
		this.timer = timer;
		this.previousDuration = previousDuration;
		this.newDuration = newDuration;
	}

	public TimerExtendEvent(@Nullable Player player, UUID uniqueId, PlayerTimer timer, long previousDuration, long newDuration){
		this.player = Optional.fromNullable(player);
		this.userUUID = Optional.fromNullable(uniqueId);
		this.timer = timer;
		this.previousDuration = previousDuration;
		this.newDuration = newDuration;
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

	public long getPreviousDuration(){
		return this.previousDuration;
	}

	public long getNewDuration(){
		return this.newDuration;
	}

	public void setNewDuration(long newDuration){
		this.newDuration = newDuration;
	}

	public boolean isCancelled(){
		return this.cancelled;
	}

	public void setCancelled(boolean cancelled){
		this.cancelled = cancelled;
	}

	public HandlerList getHandlers(){
		return handlers;
	}
}

