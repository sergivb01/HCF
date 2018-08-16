package idaniel84.faction.type;

import java.util.Map;

import idaniel84.utils.ConfigurationService;
import org.bukkit.command.CommandSender;

public class WarzoneFaction
		extends Faction{
	public WarzoneFaction(){
		super("Warzone");
	}

	public WarzoneFaction(Map<String, Object> map){
		super(map);
	}

	@Override
	public String getDisplayName(CommandSender sender){
		return ConfigurationService.WARZONE_COLOUR + this.getName();
	}
}

