package idaniel84.command.crate;

import com.google.common.collect.Sets;
import idaniel84.HCF;
import idaniel84.command.crate.type.ConquestKey;
import idaniel84.command.crate.type.KnockKothKey;
import idaniel84.command.crate.type.KothKey;
import idaniel84.command.crate.type.PalaceKey;
import net.veilmc.util.InventoryUtils;
import net.veilmc.util.chat.Text;
import net.veilmc.util.chat.TextUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class KeyListener
		implements Listener{
	private final HCF plugin;

	private static Set<Key> keys;

	public KeyListener(HCF plugin){

		keys = Sets.newHashSet(new Key[]{new KothKey(), new PalaceKey(), new ConquestKey(), new KnockKothKey()});
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event){
		Key key = getKey(event.getItemInHand());
		if(key != null){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onInventoryClose(InventoryCloseEvent event){
		Inventory inventory = event.getInventory();
		Inventory topInventory = event.getView().getTopInventory();
		if(inventory != null && topInventory != null && topInventory.equals(inventory) && topInventory.getTitle().endsWith(" key.")){
			Player player = (Player) event.getPlayer();
			Location location = player.getLocation();
			World world = player.getWorld();
			boolean isEmpty = true;
			for(ItemStack stack : topInventory.getContents()){
				if(stack == null || stack.getType() == Material.AIR) continue;
				world.dropItemNaturally(location, stack);
				isEmpty = false;
			}
			if(!isEmpty){
				player.sendMessage(ChatColor.RED.toString() + "You closed your loot crate reward inventory, dropped on the ground for you.");
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onInventoryDrag(InventoryDragEvent event){
		Inventory inventory = event.getInventory();
		Inventory topInventory = event.getView().getTopInventory();
		if(inventory != null && topInventory != null && topInventory.equals(inventory) && topInventory.getTitle().endsWith(" key.")){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onInventoryClick(InventoryClickEvent event){
		Inventory clickedInventory = event.getClickedInventory();
		Inventory topInventory = event.getView().getTopInventory();
		if(clickedInventory == null || topInventory == null || !topInventory.getTitle().endsWith(" key.")){
			return;
		}
		InventoryAction action = event.getAction();
		if(!topInventory.equals(clickedInventory) && action == InventoryAction.MOVE_TO_OTHER_INVENTORY){
			event.setCancelled(true);
		}else if(topInventory.equals(clickedInventory) && (action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE || action == InventoryAction.PLACE_SOME)){
			event.setCancelled(true);
		}
	}

	private void decrementHand(Player player){
		ItemStack stack = player.getItemInHand();
		if(stack.getAmount() <= 1){
			player.setItemInHand(new ItemStack(Material.AIR, 1));
		}else{
			stack.setAmount(stack.getAmount() - 1);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack stack = event.getItem();
		if(action != Action.RIGHT_CLICK_BLOCK){
			return;
		}
		Key key = getKey(stack);
		if(key == null){
			return;
		}
		Block block = event.getClickedBlock();
		if(key instanceof EnderChestKey && block.getType() == Material.ENDER_CHEST){
			EnderChestKey enderChestKey = (EnderChestKey) key;
			boolean broadcastLoot = enderChestKey.getBroadcastItems();
			int rolls = enderChestKey.getRolls();
			int size = InventoryUtils.getSafestInventorySize(rolls);
			Inventory inventory = Bukkit.createInventory(player, size, enderChestKey.getName() + " key.");
			ItemStack[] loot = enderChestKey.getLoot();
			if(loot == null){
				player.sendMessage(ChatColor.RED + "That key has no loot defined, please inform an admin.");
				return;
			}
			ArrayList<ItemStack> finalLoot = new ArrayList<ItemStack>();
			Random random = this.plugin.getRandom();
			for(int i = 0; i < rolls; ++i){
				ItemStack item = loot[random.nextInt(loot.length)];
				if(item == null || item.getType() == Material.AIR) continue;
				finalLoot.add(item);
				inventory.setItem(i, item);
			}
			if(broadcastLoot){
				Text text = new Text();
				text.append(new Text(player.getName()).setColor(ChatColor.AQUA));
				text.append(new Text(" has obtained").setColor(ChatColor.YELLOW));
				text.append(TextUtils.joinItemList(finalLoot, ", ", true));
				text.append(new Text(" from a ").setColor(ChatColor.YELLOW));
				text.append(new Text(enderChestKey.getDisplayName()).setColor(enderChestKey.getColour()));
				text.append(new Text(" key.").setColor(ChatColor.YELLOW));
				text.broadcast();
			}
			Location location = block.getLocation();
			player.openInventory(inventory);
			player.playSound(location, Sound.LEVEL_UP, 1.0f, 1.0f);
			this.decrementHand(player);
			event.setCancelled(true);
		}
	}

	public static Key getKey(ItemStack stack){
		if(stack == null || !stack.hasItemMeta()){
			return null;
		}
		for(Key key : keys){
			ItemStack item = key.getItemStack();
			if(!item.getItemMeta().getDisplayName().equals(stack.getItemMeta().getDisplayName())) continue;
			return key;
		}
		return null;
	}

	public static Key getKey(String name){
		for(Key key : keys){
			if(!key.getName().equalsIgnoreCase(name)) continue;
			return key;
		}
		return null;
	}

	public static Set<Key> getKeys(){
		return keys;
	}
}

