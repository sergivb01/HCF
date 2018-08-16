package net.veilmc.hcf.kothgame.faction;

import net.veilmc.hcf.faction.claim.Claim;
import net.veilmc.hcf.faction.type.ClaimableFaction;
import net.veilmc.hcf.faction.type.Faction;
import net.veilmc.hcf.kothgame.CaptureZone;
import net.veilmc.hcf.kothgame.EventType;
import net.veilmc.util.cuboid.Cuboid;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public abstract class EventFaction
		extends ClaimableFaction{
	public EventFaction(String name){
		super(name);
		this.setDeathban(true);
	}

	public EventFaction(Map<String, Object> map){
		super(map);
		this.setDeathban(true);
	}

	@Override
	public String getDisplayName(Faction faction){
		if(this.getEventType() == EventType.KOTH){
			return ChatColor.BLUE + "" + ChatColor.BOLD + this.getName() + " KOTH";
		}
		if (this.getEventType() == EventType.KNOCK) {
			return ChatColor.BLUE + "" + ChatColor.BOLD + this.getName() + " KNOCK KOTH";
		}
		return ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + this.getEventType().getDisplayName();
	}

	@Override
	public String getDisplayName(CommandSender sender){
		if(this.getEventType() == EventType.KOTH){
			return ChatColor.BLUE + "" + ChatColor.BOLD + this.getName() + " KOTH";
		}
		if (this.getEventType() == EventType.KNOCK){
			return ChatColor.BLUE + "" + ChatColor.BOLD + this.getName() + " KNOCK KOTH";
		}
		return ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + this.getEventType().getDisplayName();
	}

	public String getDisplayName1(Faction faction){
		if(getEventType() == EventType.PALACE){
			return ChatColor.LIGHT_PURPLE.toString() + getName() + ' ' + getEventType().getDisplayName();
		}
		return ChatColor.DARK_PURPLE + getEventType().getDisplayName();
	}


	public void setClaim(Cuboid cuboid, CommandSender sender){
		this.removeClaims(this.getClaims(), sender);
		Location min = cuboid.getMinimumPoint();
		min.setY(0);
		Location max = cuboid.getMaximumPoint();
		max.setY(256);
		this.addClaim(new Claim(this, min, max), sender);
	}

	public abstract EventType getEventType();

	public abstract List<CaptureZone> getCaptureZones();
}

