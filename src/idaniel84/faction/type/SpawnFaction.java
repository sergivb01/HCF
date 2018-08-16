package idaniel84.faction.type;

import java.util.Map;

import idaniel84.utils.ConfigurationService;
import idaniel84.utils.ConfigurationService;
import idaniel84.utils.ConfigurationService;
import idaniel84.utils.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class SpawnFaction
		extends ClaimableFaction
		implements ConfigurationSerializable{
	public SpawnFaction(){
		super("Spawn");
		this.safezone = true;
		for(World world : Bukkit.getWorlds()){
			World.Environment environment = world.getEnvironment();
			if(environment == World.Environment.THE_END) continue;
			double radius = ConfigurationService.SPAWN_RADIUS_MAP.get(world.getEnvironment());
//            this.addClaim(new Claim(this, new Location(world, radius, 0.0, radius), new Location(world, - radius, (double)world.getMaxHeight(), - radius)), null);
		}
	}

	public SpawnFaction(Map<String, Object> map){
		super(map);
	}

	@Override
	public boolean isDeathban(){
		return false;
	}
}

