package idaniel84.faction.struct;

import idaniel84.utils.ConfigurationService;
import idaniel84.utils.ConfigurationService;
import idaniel84.utils.ConfigurationService;
import idaniel84.utils.ConfigurationService;
import net.veilmc.util.BukkitUtils;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public enum Relation{
	MEMBER(3),
	ALLY(2),
	ENEMY(1);

	private final int value;

	Relation(int value){
		this.value = value;
	}

	public int getValue(){
		return this.value;
	}

	public boolean isAtLeast(Relation relation){
		return this.value >= relation.value;
	}

	public boolean isAtMost(Relation relation){
		return this.value <= relation.value;
	}

	public boolean isMember(){
		return this == MEMBER;
	}

	public boolean isAlly(){
		return this == ALLY;
	}

	public boolean isEnemy(){
		return this == ENEMY;
	}

	public String getDisplayName(){
		switch(this){
			case ALLY:{
				return this.toChatColour() + "alliance";
			}
		}
		return this.toChatColour() + this.name().toLowerCase();
	}

	public ChatColor toChatColour(){
		switch(this){
			case MEMBER:{
				return ConfigurationService.TEAMMATE_COLOUR;
			}
			case ALLY:{
				return ConfigurationService.ALLY_COLOUR;
			}
		}
		return ConfigurationService.ENEMY_COLOUR;
	}

	public DyeColor toDyeColour(){
		return BukkitUtils.toDyeColor(this.toChatColour());
	}

}

