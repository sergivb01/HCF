package net.veilmc.hcf.faction.event;

import net.veilmc.hcf.faction.type.PlayerFaction;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerJoinFactionEvent
		extends FactionEvent
		implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private final UUID uniqueID;
	private boolean cancelled;
	private Optional<Player> player;

	public PlayerJoinFactionEvent(Player player, PlayerFaction playerFaction){
		super(playerFaction);
		Preconditions.checkNotNull((Object) player, "Player cannot be null");
		this.player = Optional.of(player);
		this.uniqueID = player.getUniqueId();
	}

	public PlayerJoinFactionEvent(UUID playerUUID, PlayerFaction playerFaction){
		super(playerFaction);
		Preconditions.checkNotNull((Object) playerUUID, "Player UUID cannot be null");
		this.uniqueID = playerUUID;
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

