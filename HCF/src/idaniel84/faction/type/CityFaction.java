package idaniel84.faction.type;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public class CityFaction extends ClaimableFaction implements ConfigurationSerializable{

    public CityFaction(){
        super("Ciudad");
        this.safezone = false;
    }

    public CityFaction(Map<String, Object> map){
        super(map);
    }

    @Override
    public String getDisplayName(CommandSender sender){
        return ChatColor.GOLD + this.getName().replace("Ciudad", "Ciudad");
    }

    @Override
    public boolean isDeathban(){
        return true;
    }
}
