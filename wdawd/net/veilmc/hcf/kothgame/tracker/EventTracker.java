package net.veilmc.hcf.kothgame.tracker;

import net.veilmc.hcf.kothgame.EventType;
import org.bukkit.entity.Player;

import net.veilmc.hcf.kothgame.CaptureZone;
import net.veilmc.hcf.kothgame.EventTimer;
import net.veilmc.hcf.kothgame.faction.EventFaction;

@Deprecated
public interface EventTracker{
	EventType getEventType();

	void tick(EventTimer var1, EventFaction var2);

	void onContest(EventFaction var1, EventTimer var2);

	boolean onControlTake(Player var1, CaptureZone var2);

	boolean onControlLoss(Player var1, CaptureZone var2, EventFaction var3);

	void stopTiming();
}

