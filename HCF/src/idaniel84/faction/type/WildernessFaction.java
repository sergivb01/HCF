package idaniel84.faction.type;

import idaniel84.utils.ConfigurationService;
import org.bukkit.command.CommandSender;

import java.util.Map;

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

