package idaniel84.faction.type;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import idaniel84.HCF;
import idaniel84.faction.event.FactionRenameEvent;
import idaniel84.faction.struct.Relation;
import idaniel84.utils.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Faction
		implements ConfigurationSerializable{
	protected final UUID uniqueID;
	public long lastRenameMillis;
	protected String name;
	protected long creationMillis;
	protected double dtrLossMultiplier = 1.0;
	protected double deathbanMultiplier = 1.0;
	protected boolean safezone;
	protected boolean locked;


	public Faction(String name){
		this.uniqueID = UUID.randomUUID();
		this.name = name;
	}

	public Faction(Map map){
		this.uniqueID = UUID.fromString((String) map.get("uniqueID"));
		this.name = (String) map.get("name");
		this.creationMillis = Long.parseLong((String) map.get("creationMillis"));
		this.lastRenameMillis = Long.parseLong((String) map.get("lastRenameMillis"));
		this.deathbanMultiplier = (Double) map.get("deathbanMultiplier");
		this.safezone = (Boolean) map.get("safezone");
	}

	public Map<String, Object> serialize(){
		LinkedHashMap map = Maps.newLinkedHashMap();
		map.put("uniqueID", this.uniqueID.toString());
		map.put("name", this.name);
		map.put("creationMillis", Long.toString(this.creationMillis));
		map.put("lastRenameMillis", Long.toString(this.lastRenameMillis));
		map.put("deathbanMultiplier", this.deathbanMultiplier);
		map.put("safezone", this.safezone);
		return map;
	}

	public UUID getUniqueID(){
		return this.uniqueID;
	}

	public String getName(){
		return this.name;
	}

	public boolean setName(String name){
		return this.setName(name, Bukkit.getConsoleSender());
	}

	public boolean setName(String name, CommandSender sender){
		if(this.name.equals(name)){
			return false;
		}
		FactionRenameEvent event = new FactionRenameEvent(this, sender, this.name, name);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()){
			return false;
		}
		this.lastRenameMillis = System.currentTimeMillis();
		this.name = name;
		return true;
	}

	public Relation getFactionRelation(Faction faction){
		if(faction == null){
			return Relation.ENEMY;
		}
		if(faction instanceof PlayerFaction){
			PlayerFaction playerFaction = (PlayerFaction) faction;
			if(playerFaction.equals(this)){
				return Relation.MEMBER;
			}
			if(playerFaction.getAllied().contains(this.uniqueID)){
				return Relation.ALLY;
			}
		}
		return Relation.ENEMY;
	}

	public Relation getRelation(CommandSender sender){
		if(!(sender instanceof Player)){
			return Relation.ENEMY;
		}
		Player player = (Player) sender;
		return this.getFactionRelation(HCF.getPlugin().getFactionManager().getPlayerFaction(player));
	}

	public String getDisplayName(CommandSender sender){
		return (this.safezone ? ConfigurationService.SAFEZONE_COLOUR : this.getRelation(sender).toChatColour()) + this.name;
	}

	public String getDisplayName(Faction other){
		return this.getFactionRelation(other).toChatColour() + this.name;
	}

	public void printDetails(CommandSender sender){
		sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
		sender.sendMessage("" + ' ' + this.getDisplayName(sender));
		sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
	}

	public boolean isDeathban(){
		return !this.safezone && this.deathbanMultiplier > 0.0;
	}

	public void setDeathban(boolean deathban){
		if(deathban != this.isDeathban()){
			this.deathbanMultiplier = deathban ? 1.0 : 0.0;
		}
	}

	public double getDeathbanMultiplier(){
		return this.deathbanMultiplier;
	}

	public void setDeathbanMultiplier(double deathbanMultiplier){
		Preconditions.checkArgument(deathbanMultiplier >= 0.0, "Deathban multiplier may not be negative");
		this.deathbanMultiplier = deathbanMultiplier;
	}

	public double getDtrLossMultiplier(){
		return this.dtrLossMultiplier;
	}

	public void setDtrLossMultiplier(double dtrLossMultiplier){
		this.dtrLossMultiplier = dtrLossMultiplier;
	}

	public boolean isLocked(){
		return this.locked;
	}

	public void setLocked(boolean locked){
		this.locked = locked;
	}

	public boolean isSafezone(){
		return this.safezone;
	}

	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof Faction)){
			return false;
		}
		Faction faction = (Faction) o;
		if(this.creationMillis != faction.creationMillis){
			return false;
		}
		if(this.lastRenameMillis != faction.lastRenameMillis){
			return false;
		}
		if(Double.compare(faction.dtrLossMultiplier, this.dtrLossMultiplier) != 0){
			return false;
		}
		if(Double.compare(faction.deathbanMultiplier, this.deathbanMultiplier) != 0){
			return false;
		}
		if(this.safezone != faction.safezone){
			return false;
		}
		if(!(this.uniqueID != null ? this.uniqueID.equals(faction.uniqueID) : faction.uniqueID == null)){
			return false;
		}
		return this.name != null ? this.name.equals(faction.name) : faction.name == null;
	}

	public int hashCode(){
		int result = this.uniqueID != null ? this.uniqueID.hashCode() : 0;
		result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
		result = 31 * result + (int) (this.creationMillis ^ this.creationMillis >>> 32);
		result = 31 * result + (int) (this.lastRenameMillis ^ this.lastRenameMillis >>> 32);
		long temp = Double.doubleToLongBits(this.dtrLossMultiplier);
		result = 31 * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(this.deathbanMultiplier);
		result = 31 * result + (int) (temp ^ temp >>> 32);
		result = 31 * result + (this.safezone ? 1 : 0);
		return result;
	}
}

