package idaniel84.faction.claim;

import idaniel84.HCF;
import idaniel84.faction.type.ClaimableFaction;
import idaniel84.faction.type.Faction;
import net.veilmc.util.GenericUtils;
import net.veilmc.util.cuboid.Cuboid;
import net.veilmc.util.cuboid.NamedCuboid;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class Claim
		extends NamedCuboid
		implements Cloneable,
		ConfigurationSerializable{
	private static final Random RANDOM = new Random();
	private final UUID claimUniqueID;
	private final UUID factionUUID;
	private final Map subclaims = new CaseInsensitiveMap();
	private Faction faction;
	private boolean loaded = false;

	public Claim(Map map){
		super(map);
		this.name = (String) map.get("name");
		this.claimUniqueID = UUID.fromString((String) map.get("claimUUID"));
		this.factionUUID = UUID.fromString((String) map.get("factionUUID"));
		for(Subclaim subclaim : GenericUtils.createList(map.get("subclaims"), Subclaim.class)){
			this.subclaims.put(subclaim.getName(), subclaim);
		}
	}


	public Claim(Faction faction, Location location){
		super(location, location);
		this.name = this.generateName();
		this.factionUUID = faction.getUniqueID();
		this.claimUniqueID = UUID.randomUUID();
	}

	public Claim(Faction faction, Location location1, Location location2){
		super(location1, location2);
		this.name = this.generateName();
		this.factionUUID = faction.getUniqueID();
		this.claimUniqueID = UUID.randomUUID();
	}

	public Claim(Faction faction, World world, int x1, int y1, int z1, int x2, int y2, int z2){
		super(world, x1, y1, z1, x2, y2, z2);
		this.name = this.generateName();
		this.factionUUID = faction.getUniqueID();
		this.claimUniqueID = UUID.randomUUID();
	}

	public Claim(Faction faction, Cuboid cuboid){
		super(cuboid);
		this.name = this.generateName();
		this.factionUUID = faction.getUniqueID();
		this.claimUniqueID = UUID.randomUUID();
	}

	public Map<String, Object> serialize(){
		Map map = super.serialize();
		map.put("name", this.name);
		map.put("claimUUID", this.claimUniqueID.toString());
		map.put("factionUUID", this.factionUUID.toString());
		map.put("subclaims", new ArrayList(this.subclaims.values()));
		return map;
	}

	private String generateName(){
		return String.valueOf(RANDOM.nextInt(899) + 100);
	}

	public UUID getClaimUniqueID(){
		return this.claimUniqueID;
	}

	public ClaimableFaction getFaction(){
		if(!this.loaded && this.faction == null){
			this.faction = HCF.getPlugin().getFactionManager().getFaction(this.factionUUID);
			this.loaded = true;
		}
		return this.faction instanceof ClaimableFaction ? (ClaimableFaction) this.faction : null;
	}

	public Collection<Subclaim> getSubclaims(){
		return this.subclaims.values();
	}

	public Subclaim getSubclaim(String name){
		return (Subclaim) this.subclaims.get(name);
	}

	public String getFormattedName(){
		return this.getName() + ": (" + this.worldName + ", " + this.x1 + ", " + this.y1 + ", " + this.z1 + ") - (" + this.worldName + ", " + this.x2 + ", " + this.y2 + ", " + this.z2 + ')';
	}

	public Claim clone(){
		return (Claim) super.clone();
	}


}

