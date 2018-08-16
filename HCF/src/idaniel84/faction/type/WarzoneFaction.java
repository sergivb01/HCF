package idaniel84.faction.type;

import idaniel84.utils.ConfigurationService;
import org.bukkit.command.CommandSender;

import java.util.Map;

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

