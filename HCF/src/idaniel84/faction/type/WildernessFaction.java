package idaniel84.faction.type;

import java.util.Map;

import idaniel84.utils.ConfigurationService;
import org.bukkit.command.CommandSender;

public class WildernessFaction
		extends Faction{
	public WildernessFaction(){
		super("Wilderness");
	}

	public WildernessFaction(Map<String, Object> map){
		super(map);
	}

	@Override
	public String getDisplayName(CommandSender sender){
		return ConfigurationService.WILDERNESS_COLOUR + this.getName();
	}
}

