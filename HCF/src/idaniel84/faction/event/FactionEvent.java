package idaniel84.faction.event;

import com.google.common.base.Preconditions;
import idaniel84.faction.type.Faction;
import org.bukkit.event.Event;

public abstract class FactionEvent
		extends Event{
	protected final Faction faction;

	public FactionEvent(Faction faction){
		this.faction = (Faction) Preconditions.checkNotNull((Object) faction, "Faction cannot be null");
	}

	FactionEvent(Faction faction, boolean async){
		super(async);
		this.faction = (Faction) Preconditions.checkNotNull((Object) faction, "Faction cannot be null");
	}

	public Faction getFaction(){
		return this.faction;
	}
}

