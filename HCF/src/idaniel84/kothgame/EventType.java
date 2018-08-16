package idaniel84.kothgame;

import idaniel84.HCF;
import idaniel84.kothgame.tracker.ConquestTracker;
import idaniel84.kothgame.tracker.EventTracker;
import idaniel84.kothgame.tracker.KnockKothTracker;
import idaniel84.kothgame.tracker.KothTracker;
import idaniel84.palace.PalaceTracker;
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

