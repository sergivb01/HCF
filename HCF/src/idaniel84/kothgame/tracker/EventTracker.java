package idaniel84.kothgame.tracker;

import idaniel84.kothgame.EventType;
import org.bukkit.entity.Player;

import idaniel84.kothgame.CaptureZone;
import idaniel84.kothgame.EventTimer;
import idaniel84.kothgame.faction.EventFaction;

@Deprecated
public interface EventTracker{
	EventType getEventType();

	void tick(EventTimer var1, EventFaction var2);

	void onContest(EventFaction var1, EventTimer var2);

	boolean onControlTake(Player var1, CaptureZone var2);

	boolean onControlLoss(Player var1, CaptureZone var2, EventFaction var3);

	void stopTiming();
}

