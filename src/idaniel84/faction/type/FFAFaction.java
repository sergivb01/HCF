package idaniel84.faction.type;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public class FFAFaction extends ClaimableFaction implements ConfigurationSerializable {

    public FFAFaction(){
        super("FFA");
        this.safezone = false;
    }

    public FFAFaction(Map<String, Object> map){
        super(map);
    }

    @Override
    public String getDisplayName(CommandSender sender){
        return ChatColor.GOLD + this.getName().replace("FFA", "FFA");
    }

    @Override
    public boolean isDeathban(){
        return true;
    }
}