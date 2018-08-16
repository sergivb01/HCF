package idaniel84.crowbar;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Crowbar{
	public static final int MAX_SPAWNER_USES = 1;
	public static final int MAX_END_DRAGON_USES = 1;
	public static final int MAX_END_FRAME_USES = 5;
	public static final Material CROWBAR_TYPE = Material.DIAMOND_HOE;
	private static final String CROWBAR_NAME = ChatColor.GOLD.toString() + "Crowbar";
	private static final String END_DRAGON_USE_TAG = "End Dragon Uses";
	private static final String SPAWNER_USE_TAG = "Spawner Uses";
	private static final String END_FRAME_USE_TAG = "End Frame Uses";
	private static final String LORE_FORMAT = ChatColor.GRAY + "%1$s: " + ChatColor.YELLOW + "%2$s/%3$s";
	private final ItemStack stack = new ItemStack(CROWBAR_TYPE, 1);
	private int endFrameUses;
	private int endDragonUses;
	private int spawnerUses;
	private boolean needsMetaUpdate;

	public Crowbar(){
		this(1, 5, 1);
	}

	public Crowbar(int spawnerUses, int endFrameUses, int endDragonUses){
		Preconditions.checkArgument(spawnerUses > 0 || endFrameUses > 0, "Cannot create a crowbar with empty uses");
		this.setSpawnerUses(Math.min(1, spawnerUses));
		this.setEndDragonUses(Math.min(1, endDragonUses));
		this.setEndFrameUses(Math.min(5, endFrameUses));
	}

	public static Optional<Crowbar> fromStack(ItemStack stack){
		if(stack == null || !stack.hasItemMeta()){
			return Optional.absent();
		}
		ItemMeta meta = stack.getItemMeta();
		if(!(meta.hasDisplayName() && meta.hasLore() && meta.getDisplayName().equals(CROWBAR_NAME))){
			return Optional.absent();
		}
		Crowbar crowbar = new Crowbar();
		List loreList = meta.getLore();
		Iterator iterator = loreList.iterator();
		block0:
		while(iterator.hasNext()){
			String lore = (String) iterator.next();
			lore = ChatColor.stripColor(lore);
			int length = lore.length();
			for(int i = 0; i < length; ++i){
				char character = lore.charAt(i);
				if(!Character.isDigit(character)) continue;
				int amount = Integer.parseInt(String.valueOf(character));
				if(lore.startsWith("End Dragon Uses")){
					crowbar.setEndDragonUses(amount);
				}
				if(lore.startsWith("End Frame Uses")){
					crowbar.setEndFrameUses(amount);
					continue block0;
				}
				if(!lore.startsWith("Spawner Uses")) continue;
				crowbar.setSpawnerUses(amount);
				continue block0;
			}
		}
		return Optional.of(crowbar);
	}

	public int getEndDragonUses(){
		return this.endDragonUses;
	}

	public void setEndDragonUses(int uses){
		if(this.endDragonUses != uses){
			this.endDragonUses = Math.min(1, uses);
			this.needsMetaUpdate = true;
		}
	}

	public int getEndFrameUses(){
		return this.endFrameUses;
	}

	public void setEndFrameUses(int uses){
		if(this.endFrameUses != uses){
			this.endFrameUses = Math.min(5, uses);
			this.needsMetaUpdate = true;
		}
	}

	public int getSpawnerUses(){
		return this.spawnerUses;
	}

	public void setSpawnerUses(int uses){
		if(this.spawnerUses != uses){
			this.spawnerUses = Math.min(1, uses);
			this.needsMetaUpdate = true;
		}
	}

	public ItemStack getItemIfPresent(){
		Optional<ItemStack> optional = this.toItemStack();
		return optional.isPresent() ? optional.get() : new ItemStack(Material.AIR, 1);
	}

	public Optional<ItemStack> toItemStack(){
		if(this.needsMetaUpdate){
			double increment;
			double curDurability;
			double maxDurability = curDurability = (double) CROWBAR_TYPE.getMaxDurability();
			if(Math.abs((curDurability -= (increment = curDurability / 6.0) * (double) (this.spawnerUses + this.endFrameUses + this.endDragonUses)) - maxDurability) == 0.0){
				return Optional.absent();
			}
			ItemMeta meta = this.stack.getItemMeta();
			meta.setDisplayName(CROWBAR_NAME);
			meta.setLore(Arrays.asList(String.format(LORE_FORMAT, "Spawner Uses", this.spawnerUses, 1), String.format(LORE_FORMAT, "End Frame Uses", this.endFrameUses, 5), String.format(LORE_FORMAT, "End Dragon Uses", this.endDragonUses, 1)));
			this.stack.setItemMeta(meta);
			this.stack.setDurability((short) curDurability);
			this.needsMetaUpdate = false;
		}
		return Optional.of(this.stack);
	}
}

