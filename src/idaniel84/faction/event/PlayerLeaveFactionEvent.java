package idaniel84.faction.event;

import idaniel84.faction.event.cause.FactionLeaveCause;
import idaniel84.faction.event.cause.FactionLeaveCause;
import idaniel84.faction.event.cause.FactionLeaveCause;
import idaniel84.faction.event.cause.FactionLeaveCause;
import idaniel84.faction.type.PlayerFaction;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerLeaveFactionEvent
		extends FactionEvent
		implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private final UUID uniqueID;
	private final FactionLeaveCause cause;
	private boolean cancelled;
	private Optional<Player> player;

	public PlayerLeaveFactionEvent(Player player, PlayerFaction playerFaction, FactionLeaveCause cause){
		super(playerFaction);
		Preconditions.checkNotNull((Object) player, "Player cannot be null");
		Preconditions.checkNotNull((Object) playerFaction, "Player faction cannot be null");
		Preconditions.checkNotNull((Object) "Leave cause cannot be null");
		this.player = Optional.of(player);
		this.uniqueID = player.getUniqueId();
		this.cause = cause;
	}

	public PlayerLeaveFactionEvent(UUID playerUUID, PlayerFaction playerFaction, FactionLeaveCause cause){
		super(playerFaction);
		Preconditions.checkNotNull((Object) playerUUID, "Player UUID cannot be null");
		Preconditions.checkNotNull((Object) playerFaction, "Player faction cannot be null");
		Preconditions.checkNotNull((Object) "Leave cause cannot be null");
		this.uniqueID = playerUUID;
		this.cause = cause;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}

	public Optional<Player> getPlayer(){
		if(this.player == null){
			this.player = Optional.fromNullable(Bukkit.getPlayer(this.uniqueID));
		}
		return this.player;
	}

	public UUID getUniqueID(){
		return this.uniqueID;
	}

	public FactionLeaveCause getLeaveCause(){
		return this.cause;
	}

	public HandlerList getHandlers(){
		return handlers;
	}

	public boolean isCancelled(){
		return this.cancelled;
	}

	public void setCancelled(boolean cancelled){
		this.cancelled = cancelled;
	}
}

