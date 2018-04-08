package net.veilmc.hcf.faction;

import net.veilmc.hcf.faction.claim.Claim;
import net.veilmc.hcf.faction.type.Faction;
import net.veilmc.hcf.faction.type.PlayerFaction;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface FactionManager{
	long MAX_DTR_REGEN_MILLIS = TimeUnit.HOURS.toMillis(3);
	String MAX_DTR_REGEN_WORDS = DurationFormatUtils.formatDurationWords((long) MAX_DTR_REGEN_MILLIS, (boolean) true, (boolean) true);

	Map<String, ?> getFactionNameMap();

	Collection<Faction> getFactions();

	Claim getClaimAt(Location var1);

	Claim getClaimAt(World var1, int var2, int var3);

	Faction getFactionAt(Location var1);

	Faction getFactionAt(Block var1);

	Faction getFactionAt(World var1, int var2, int var3);

	Faction getFaction(String var1);

	Faction getFaction(UUID var1);

	@Deprecated
	PlayerFaction getContainingPlayerFaction(String var1);

	PlayerFaction getPlayerFaction(Player var1);

	PlayerFaction getPlayerFaction(UUID var1);

	Faction getContainingFaction(String var1);

	boolean containsFaction(Faction var1);

	boolean createFaction(Faction var1);

	boolean createFaction(Faction var1, CommandSender var2);

	boolean removeFaction(Faction var1, CommandSender var2);

	void reloadFactionData();

	void saveFactionData();
}

