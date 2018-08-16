package net.veilmc.hcf.faction.type;

import java.util.Map;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class GlowstoneFaction
		extends ClaimableFaction
		implements ConfigurationSerializable{
	public GlowstoneFaction(){
		super("Glowstone");
		this.safezone = false;
	}

	public GlowstoneFaction(Map<String, Object> map){
		super(map);
	}

	@Override
	public String getDisplayName(CommandSender sender){
		return ChatColor.GOLD + this.getName().replace("Glowstone", "Glowstone");
	}

	@Override
	public boolean isDeathban(){
		return true;
	}
}

