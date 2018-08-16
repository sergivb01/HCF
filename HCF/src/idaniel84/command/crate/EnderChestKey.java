package idaniel84.command.crate;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class EnderChestKey
		extends Key{
	private final ItemStack[] items = new ItemStack[100];
	private int rolls;

	public EnderChestKey(String name, int rolls){
		super(name);
		this.rolls = rolls;
	}

	public boolean getBroadcastItems(){
		return false;
	}

	public int getRolls(){
		return this.rolls;
	}

	public void setRolls(int rolls){
		this.rolls = rolls;
	}

	public ItemStack[] getLoot(){
		return Arrays.copyOf(this.items, this.items.length);
	}

	public void setupRarity(ItemStack stack, int percent){
		int currentItems = 0;
		for(ItemStack item : this.items){
			if(item == null || item.getType() == Material.AIR) continue;
			++currentItems;
		}
		int min = Math.min(100, currentItems + percent);
		for(int i = currentItems; i < min; ++i){
			this.items[i] = stack;
		}
	}

	@Override
	public ChatColor getColour(){
		return ChatColor.GOLD;
	}

	@Override
	public ItemStack getItemStack(){
		ItemStack stack = new ItemStack(Material.TRIPWIRE_HOOK, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(this.getColour() + this.getName() + " Key");
		meta.setLore((List) Lists.newArrayList((Object[]) new String[]{ChatColor.GRAY + "Click an Ender Chest in a safe claim to use this key."}));
		stack.setItemMeta(meta);
		return stack;
	}
}

