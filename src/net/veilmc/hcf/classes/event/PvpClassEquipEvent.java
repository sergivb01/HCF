package net.veilmc.hcf.classes.event;

import net.veilmc.hcf.classes.PvpClass;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PvpClassEquipEvent
		extends PlayerEvent{
	private static final HandlerList handlers = new HandlerList();
	private final PvpClass pvpClass;

	public PvpClassEquipEvent(Player player, PvpClass pvpClass){
		super(player);
		this.pvpClass = pvpClass;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}

	public PvpClass getPvpClass(){
		return this.pvpClass;
	}

	public HandlerList getHandlers(){
		return handlers;
	}
}

