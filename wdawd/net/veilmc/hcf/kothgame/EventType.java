package net.veilmc.hcf.kothgame;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.kothgame.tracker.ConquestTracker;
import net.veilmc.hcf.kothgame.tracker.EventTracker;
import net.veilmc.hcf.kothgame.tracker.KnockKothTracker;
import net.veilmc.hcf.kothgame.tracker.KothTracker;
import net.veilmc.hcf.palace.PalaceTracker;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;


public enum EventType{
	CONQUEST("Conquest", new ConquestTracker(HCF.getPlugin())), PALACE("Palace", new PalaceTracker((HCF.getPlugin()))), KOTH("Koth", new KothTracker(HCF.getPlugin())), KNOCK("Knock Koth", new KnockKothTracker(HCF.getPlugin()));

	private static final ImmutableMap<String, EventType> byDisplayName;

	static{
		ImmutableBiMap.Builder builder = new ImmutableBiMap.Builder();
		for(EventType eventType : EventType.values()){
			builder.put(eventType.displayName.toLowerCase(), eventType);
		}
		byDisplayName = builder.build();
	}

	private final EventTracker eventTracker;
	private final String displayName;

	EventType(String displayName, EventTracker eventTracker){
		this.displayName = displayName;
		this.eventTracker = eventTracker;
	}

	@Deprecated
	public static EventType getByDisplayName(String name){
		return (EventType) byDisplayName.get(name.toLowerCase());
	}

	public EventTracker getEventTracker(){
		return this.eventTracker;
	}

	public String getDisplayName(){
		return this.displayName;
	}
}

