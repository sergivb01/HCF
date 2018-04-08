package net.veilmc.hcf.command.crate;

import net.veilmc.util.Config;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public abstract class Key{
	private String name;

	public Key(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}

	public void setName(String name){
		this.name = name;
	}

	public abstract ChatColor getColour();

	public String getDisplayName(){
		return this.getColour() + this.name;
	}

	public abstract ItemStack getItemStack();

	public void load(Config config){
	}

	public void save(Config config){
	}
}

