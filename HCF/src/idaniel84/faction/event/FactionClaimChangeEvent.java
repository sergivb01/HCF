package idaniel84.faction.event;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import idaniel84.faction.claim.Claim;
import idaniel84.faction.event.cause.ClaimChangeCause;
import idaniel84.faction.type.ClaimableFaction;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Collection;

public class FactionClaimChangeEvent
		extends Event
		implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private final ClaimChangeCause cause;
	private final Collection<Claim> affectedClaims;
	private final ClaimableFaction claimableFaction;
	private final CommandSender sender;
	private boolean cancelled;

	public FactionClaimChangeEvent(CommandSender sender, ClaimChangeCause cause, Collection<Claim> affectedClaims, ClaimableFaction claimableFaction){
		Preconditions.checkNotNull((Object) sender, "CommandSender cannot be null");
		Preconditions.checkNotNull(cause, "Cause cannot be null");
		Preconditions.checkNotNull(affectedClaims, "Affected claims cannot be null");
		Preconditions.checkNotNull((Object) affectedClaims.isEmpty(), "Affected claims cannot be empty");
		Preconditions.checkNotNull((Object) claimableFaction, "ClaimableFaction cannot be null");
		this.sender = sender;
		this.cause = cause;
		this.affectedClaims = ImmutableList.copyOf(affectedClaims);
		this.claimableFaction = claimableFaction;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}

	public CommandSender getSender(){
		return this.sender;
	}

	public ClaimChangeCause getCause(){
		return this.cause;
	}

	public Collection<Claim> getAffectedClaims(){
		return this.affectedClaims;
	}

	public ClaimableFaction getClaimableFaction(){
		return this.claimableFaction;
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

